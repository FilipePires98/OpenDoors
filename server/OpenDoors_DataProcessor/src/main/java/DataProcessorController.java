
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class DataProcessorController {
    
    private static String BROKER="localhost:9092";
    private static String PERSISTENCE="localhost:8081";
    
    static{
        File file = new File("/IES24/24properties"); 
        Scanner sc=null; 
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProcessorController.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        while (sc.hasNextLine()){
            String[] props = sc.nextLine().split("=");
            if(props[0].equals("kafka-broker")){
                BROKER=props[1];
            }
            else if(props[0].equals("24persistence")){
                PERSISTENCE=props[1];
            }
        }
        System.out.println(BROKER);
    }
    
    private static KafkaProducer<String, String> producer;
    
    private static Map<String, MyTimer> funcionaries = new HashMap();
    private static Map<String, MyTimer> clients = new HashMap();
    
    private static boolean called=false;
    
    
    
    private static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "group24producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
    
    public static void process(String jobj){
        try {
            
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jobj);
            System.out.println(json);
            
            
            Client client = ClientBuilder.newClient();
            Response response = client.target("http://"+PERSISTENCE+"/OpenDoors_Persistence-1.0/regist/regist")     //testar com pesist
                                    .request(MediaType.APPLICATION_JSON)
                                    .post(Entity.json(json.toJSONString()));
            System.out.println(response);
            
            String loja;
            JSONArray pressJson;

            ProducerRecord<String, String> record;
            switch((int)(long)json.get("data_type")){
                case 1://light
                    //{"data_type":1, "data":{"store":1,"light":{"visible":[100,200],"infrared":[0,50]}}}
                    JSONArray visibleJson = (JSONArray)((JSONObject)((JSONObject)json.get("data")).get("light")).get("visible");
                    JSONArray infraredJson = (JSONArray)((JSONObject)((JSONObject)json.get("data")).get("light")).get("infrared");
                    
                    
                    double[] visible=new double[visibleJson.size()];
                    double[] infrared=new double[visibleJson.size()];
                    
                    for(int i=0; i<visibleJson.size(); i++){
                        if(String.valueOf(visibleJson.get(i)).contains(".")){
                            visible[i]=(double)visibleJson.get(i);
                        }else{
                            visible[i]=(double)((long)visibleJson.get(i));
                        }
                        if(String.valueOf(infraredJson.get(i)).contains(".")){
                            infrared[i]=(double)infraredJson.get(i);
                        }else{
                            infrared[i]=(double)((long)infraredJson.get(i));
                        }
                        
                    }
                    

                    record = new ProducerRecord("24light",
                            ((JSONObject)json.get("data")).get("store").toString(), 
                            "{\"visible\":"+Arrays.stream(visible).average().getAsDouble()+", \"infrared\":"+Arrays.stream(infrared).average().getAsDouble()+", \"time\":"+System.currentTimeMillis()+"}");
                    producer.send(record);
                    break;
                case 2://temperature
                    //{"data_type":2, "data":{"store":1,"temperature":[15]}}
                    JSONArray tempJson = (JSONArray)((JSONObject)json.get("data")).get("temperature");
                    
                    
                    
                    double[] temp=new double[tempJson.size()];
                    for(int i=0; i<tempJson.size(); i++){
                        if(String.valueOf(tempJson.get(i)).contains(".")){
                            temp[i]=(double)tempJson.get(i);
                        }
                        else{
                            temp[i]=(double)((long)tempJson.get(i));
                        }
                    }
                    
                    record = new ProducerRecord("24temperature",
                            ((JSONObject)json.get("data")).get("store").toString(),
                            "{\"temperature\": "+Arrays.stream(temp).average().getAsDouble()+", \"time\":"+System.currentTimeMillis()+"}");
                    producer.send(record);
                    break;
                case 3://client
                    record = new ProducerRecord("24client",
                            ((JSONObject)json.get("data")).get("store").toString(),
                            "{\"moved\":"+(double)((long)((JSONObject)json.get("data")).get("moviment"))+", \"time\":"+System.currentTimeMillis()+"}");
                    producer.send(record);
                    break;
                case 4://sit_func
                    loja=((JSONObject)json.get("data")).get("store").toString();
                    pressJson = (JSONArray)((JSONObject)json.get("data")).get("pressure");
                    
                    for(int i=0; i<pressJson.size(); i++){
                        if(((long)pressJson.get(i))==1){
                            if(!funcionaries.containsKey(loja+"-"+i)){
                                MyTimer timer = new MyTimer(loja+"-"+i, "processor");
                                timer.start();
                                funcionaries.put(loja+"-"+i, timer);
                            }
                            
                        }else{
                            if(funcionaries.containsKey(loja+"-"+i)){
                                MyTimer timer=funcionaries.get(loja+"-"+i);
                                timer.ended();
                                clients.remove(loja+"-"+i);
                                record = new ProducerRecord("24sit_employee",
                                        loja+i,
                                        "{\"sitted\":"+timer.time()+", \"time\":"+System.currentTimeMillis()+"}");
                                producer.send(record);
                            }
                        }
                    }
                    
                    break;
                case 5://sit_client
                    loja=((JSONObject)json.get("data")).get("store").toString();
                    pressJson = (JSONArray)((JSONObject)json.get("data")).get("pressure");
                    
                    for(int i=0; i<pressJson.size(); i++){
                        if(((long)pressJson.get(i))==1){
                            if(!clients.containsKey(loja+"-"+i)){
                                MyTimer timer = new MyTimer(loja+"-"+i, "processor");
                                timer.start();
                                clients.put(loja+"-"+i, timer);
                            }
                            
                        }else{
                            if(clients.containsKey(loja+"-"+i)){
                                MyTimer timer=clients.get(loja+"-"+i);
                                timer.ended();
                                clients.remove(loja+"-"+i);
                                record = new ProducerRecord("24sit_client",
                                        loja+i,
                                        "{\"sitted\":"+timer.time()+", \"time\":"+System.currentTimeMillis()+"}");
                                producer.send(record);
                            }
                        }
                    }
                    
                    break;
                
            }
            
            startStream();
            
        } catch (ParseException ex) {
            Logger.getLogger(DataProcessorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String... args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "group24controller");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        /*
        KafkaAdminClient ac = (KafkaAdminClient)AdminClient.create(props);
        DeleteTopicsResult dtr=ac.deleteTopics(Arrays.asList(
                "24avg_temperature", 
                "24avg_light", 
                "24count_client", 
                "24count_sit_employee", 
                "24count_sit_client",
                "24light",
                "24temperature",
                "24client",
                "24sit_employee",
                "24sit_client"), 
                new DeleteTopicsOptions());
        
        
        CreateTopicsResult ctr = ac.createTopics(Arrays.asList(new NewTopic("avgtemp",1,(short)1), 
                new NewTopic("24avg_light",1,(short)1), 
                new NewTopic("24count_client",1,(short)1), 
                new NewTopic("24count_sit_employee",1,(short)1), 
                new NewTopic("24count_sit_client",1,(short)1),
                new NewTopic("24light",1,(short)1),
                new NewTopic("24client",1,(short)1),
                new NewTopic("24sit_employee",1,(short)1),
                new NewTopic("24sit_client",1,(short)1),
                new NewTopic("24temperature",1,(short)1)),
                new CreateTopicsOptions());
        */
        
        producer=DataProcessorController.createProducer();
        new MyKafkaConsumer("24sensors-data",BROKER, "processor").start();
        new MessageCreator().start();

    }

    private static void startStream() {
        if (!called){
            called=true;
            new StreamProcessor().start();
        }
    }
}