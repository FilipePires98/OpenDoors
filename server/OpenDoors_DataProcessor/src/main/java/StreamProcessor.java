import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
 
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
 
public class StreamProcessor extends Thread{
    
    private static String BROKER="localhost:9092";
    
    static{
        File file = new File("/IES24/24properties"); 
        Scanner sc=null; 
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StreamProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        while (sc.hasNextLine()){
            String[] props = sc.nextLine().split("=");
            if(props[0].equals("kafka-broker")){
                BROKER=props[1];
            }
        }
    }
    
    

    private synchronized double getJson(String val, String field){
        double a;
        try {
            a = ((double)((JSONObject)(new JSONParser()).parse(val)).get(field));
        } catch (ParseException ex) {
            a=0;
        }
        return a;
    }   
    
    @Override
    public void run() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "group24streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass());

        StreamsBuilder builder = new StreamsBuilder();


        KStream<String, String> temperatures = builder.stream("24temperature",Consumed.with(Serdes.String(), Serdes.String()));
        temperatures.print(Printed.toSysOut());
        KStream<String, Double> auxtemp = temperatures.map((key,value) -> KeyValue.pair(key, this.getJson(value, "temperature")));
        KTable<Windowed<String>, Double> sumtemp = auxtemp.groupByKey().windowedBy(TimeWindows.of(Duration.ofMinutes(2))).reduce((aggValue, newValue) -> aggValue.doubleValue() + newValue.doubleValue() );
        KTable<Windowed<String>, Long> counttemp = auxtemp.groupByKey().windowedBy(TimeWindows.of(Duration.ofMinutes(2))).count();
        KTable<Windowed<String>, Double> joint = counttemp.join(sumtemp,(countValue, sumValue) -> sumValue / countValue);
        sumtemp.toStream().print(Printed.toSysOut());
        counttemp.toStream().print(Printed.toSysOut());
        KStream<String, String> endt = joint.toStream((key, value)-> key.key()).map((key,value)-> KeyValue.pair(key, "{\"average\":"+value+", \"time\":"+System.currentTimeMillis()+"}"));
        joint.toStream().print(Printed.toSysOut());
        endt.to("24avg_temperature", Produced.with(Serdes.String(), Serdes.String()));


        KStream<String, String> lights = builder.stream("24light",Consumed.with(Serdes.String(), Serdes.String()));
        lights.print(Printed.toSysOut());   
        KStream<String, Double> auxlight = lights.map((key,value) -> KeyValue.pair(key, this.getJson(value, "visible")));
        KTable<Windowed<String>, Double> sumlight = auxlight.groupByKey().windowedBy(TimeWindows.of(Duration.ofMinutes(2))).reduce((aggValue, newValue) -> aggValue + newValue);
        KTable<Windowed<String>, Long> countlight = auxlight.groupByKey().windowedBy(TimeWindows.of(Duration.ofMinutes(2))).count();
        KTable<Windowed<String>, Double> joinl = countlight.leftJoin(sumlight,(countValue, sumValue) -> sumValue / countValue);
        sumlight.toStream().print(Printed.toSysOut());
        countlight.toStream().print(Printed.toSysOut());
        joinl.toStream((key, value)-> key.key()).print(Printed.toSysOut());
        KStream<String, String> endl = joinl.toStream((key, value)-> key.key()).map((key,value)-> KeyValue.pair(key, "{\"average\":"+value+", \"time\":"+System.currentTimeMillis()+"}"));
        endl.to("24avg_light", Produced.with(Serdes.String(), Serdes.String()));


        KStream<String, String> clients = builder.stream("24client",Consumed.with(Serdes.String(), Serdes.String()));
        clients.print(Printed.toSysOut());
        KStream<String, Double> auxclient = clients.map((key,value) -> KeyValue.pair(key, this.getJson(value, "moved")));
        KTable<Windowed<String>, Long> countclient = auxclient.groupBy((key,value)->key).windowedBy(TimeWindows.of(Duration.ofMinutes(2))).count();
        countclient.toStream((key, value)-> key.key()).print(Printed.toSysOut());
        KStream<String, String> endc = countclient.toStream((key, value)-> key.key()).map((key,value)-> KeyValue.pair(key, "{\"count\":"+value+", \"time\":"+System.currentTimeMillis()+"}"));
        endc.to("24count_client", Produced.with(Serdes.String(), Serdes.String()));


        KStream<String, String> sitfunc = builder.stream("24sit_employee",Consumed.with(Serdes.String(), Serdes.String()));
        sitfunc.print(Printed.toSysOut());
        KStream<String, Double> auxsitfunc = sitfunc.map((key,value) -> KeyValue.pair(key, this.getJson(value, "sitted")));
        KTable<Windowed<String>, Double> sumsitfunc = auxsitfunc.groupBy((key,value)->key).windowedBy(TimeWindows.of(Duration.ofMinutes(2))).reduce((aggValue, newValue) -> aggValue + newValue);
        sumsitfunc.toStream((key, value)-> key.key()).print(Printed.toSysOut());
        KStream<String, String> endsf = sumsitfunc.toStream((key, value)-> key.key()).map((key,value)-> KeyValue.pair(key, "{\"sum\":"+value+", \"time\":"+System.currentTimeMillis()+"}"));
        endsf.to("24count_sit_employee", Produced.with(Serdes.String(), Serdes.String()));


        KStream<String, String> sitclient = builder.stream("24sit_client",Consumed.with(Serdes.String(), Serdes.String()));
        sitclient.print(Printed.toSysOut());
        KStream<String, Double> auxsitclient = sitclient.map((key,value) -> KeyValue.pair(key, this.getJson(value, "sitted")));
        KTable<Windowed<String>, Double> sumsitclient = auxsitclient.groupBy((key,value)->key).windowedBy(TimeWindows.of(Duration.ofMinutes(2))).reduce((aggValue, newValue) -> aggValue + newValue);
        sumsitclient.toStream((key, value)-> key.key()).print(Printed.toSysOut());
        KStream<String, String> endsc = sumsitclient.toStream((key, value)-> key.key()).map((key,value)-> KeyValue.pair(key, "{\"sum\":"+value+", \"time\":"+System.currentTimeMillis()+"}"));
        endsc.to("24count_sit_client", Produced.with(Serdes.String(), Serdes.String()));


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.cleanUp();
        streams.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down...");
                streams.close();
            }
        }));
    }
 
}