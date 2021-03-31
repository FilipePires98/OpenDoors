import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class MessageCreator extends Thread {
    
    private static String BROKER="localhost:9092";
    private static String PERSISTENCE="localhost:8081";
    
    static{
        File file = new File("/IES24/24properties"); 
        Scanner sc=null; 
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
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
    }
    
    
    private static KafkaProducer<String, String> producer;
    
    private static Map<String, MyTimer> funcionaries = new HashMap();
    private static Map<String, MyTimer> clients = new HashMap();

    private static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "group24messages");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    static void processSensors(String jobj) {
        try {
            
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jobj);
            
            String loja;
            JSONArray pressJson;

            ProducerRecord<String, String> record;
            switch((int)(long)json.get("data_type")){
                case 4://sit_func
                    loja=((JSONObject)json.get("data")).get("store").toString();
                    pressJson = (JSONArray)((JSONObject)json.get("data")).get("pressure");
                    
                    for(int i=0; i<pressJson.size(); i++){
                        if(((long)pressJson.get(i))==1){
                            if(!funcionaries.containsKey(loja+"-"+i)){
                                MyTimer timer = new MyTimer(loja+"-"+i, "messagesf");
                                timer.start();
                                funcionaries.put(loja+"-"+i, timer);
                            }
                            
                        }else{
                            if(funcionaries.containsKey(loja+"-"+i)){
                                MyTimer timer=funcionaries.get(loja+"-"+i);
                                timer.ended();
                                clients.remove(loja+"-"+i);
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
                                MyTimer timer = new MyTimer(loja+"-"+i, "messagesc");
                                timer.start();
                                clients.put(loja+"-"+i, timer);
                            }
                            
                        }else{
                            if(clients.containsKey(loja+"-"+i)){
                                MyTimer timer=clients.get(loja+"-"+i);
                                timer.ended();
                                clients.remove(loja+"-"+i);
                            }
                        }
                    }
                    
                    break;
                
            }
            
        } catch (ParseException ex) {
            Logger.getLogger(DataProcessorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void alert(String id, String funcOrClient) {
        String cliorfunc;
        String descript;
        MyTimer timer = null;
        
        
        if(funcOrClient.equals("f")) {
            cliorfunc = "\"employee\"";
            descript = "\"Employee " + id.split("-")[1] + "sitted for to long!\"";
                if(funcionaries.containsKey(id)){
                timer = funcionaries.get(id);
                timer.ended();
                funcionaries.remove(id);
            }
        }
        else{
            cliorfunc = "\"client\"";
            descript = "\"Client " + id.split("-")[1] + " sitted for to long!\"";
            if(clients.containsKey(id)){
                timer = clients.get(id);
                timer.ended();
                clients.remove(id);
            }
        }
        
        
        
        JSONObject json = new JSONObject();
        json.put("data_type", 6);
        JSONObject objTemp = new JSONObject();
        objTemp.put((funcOrClient.equals("c"))?"client":"employee",id.split("-")[1]);
        objTemp.put("store",id.split("-")[0]);
        objTemp.put("description",descript);
        objTemp.put("time", new Timestamp((long) timer.time()).toString());
        json.put("data", objTemp);
        
        
        
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://"+PERSISTENCE+"/OpenDoors_Persistence-1.0/regist/regist")     //testar com persist
                                .request(MediaType.APPLICATION_JSON)
                                .post(Entity.json(json.toJSONString()));
            
            //datatype:6, data:{client:id, store:id, description:descr, time:time}
        
        //id=loja+"-"+id
        ProducerRecord<String, String> record = new ProducerRecord("24alerts",
                                        id.split("-")[0],//loja
                                        cliorfunc+id.split("-")[1]+",\"event\":"+descript+", \"time\":"+System.currentTimeMillis()+"}");
        producer.send(record);
    }

    static void processCount(String key, String value) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsondata = (JSONObject) parser.parse(value);
            if(((long)jsondata.get("count"))==5){
                
                Client client = ClientBuilder.newClient();
                Response response = client.target("http://"+PERSISTENCE+"/OpenDoors_Persistence-1.0/regist/regist")     //testar com persist
                                        .request(MediaType.APPLICATION_JSON)
                                        .post(Entity.json("{\"data_type\":6, \"data\":{\"time\":"+((long)jsondata.get("time"))+", \"description\":"+"\"Client number 5!\""+"}"));
                
                System.out.println("alert-"+response.toString());
                
                ProducerRecord<String, String> record = new ProducerRecord("24alerts",
                                        key,//loja
                                        "\"event\":"+"\"Client number 5!\""+", \"time\":"+System.currentTimeMillis()+"}");
                producer.send(record);
            }
            
        } catch (ParseException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void run() {
        producer=MessageCreator.createProducer();
        new MyKafkaConsumer("24sensors-data",BROKER, "messages1").start();
        new MyKafkaConsumer("24count_client",BROKER, "messages2").start();
        
    }


}
