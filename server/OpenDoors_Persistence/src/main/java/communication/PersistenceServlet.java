package communication;

import persistence.*;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import javax.persistence.*;
import javax.ws.rs.*;
import org.json.simple.*;
import org.json.simple.parser.*;

@Path("/regist")
public class PersistenceServlet {
    
    // Attributes
    
    private EntityManagerFactory emfactory;
    private EntityManager em;

    public PersistenceServlet() {
        EntityManager em = null;
        String url="";
        String username="";
        String password="";
        
        
        File file = new File("/IES24/24properties"); 
        Scanner sc=null; 
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        while (sc.hasNextLine()){
            String[] props = sc.nextLine().split("=");
            if(props[0].equals("db-url")){
                url=props[1];
            }
            else if (props[0].equals("db-username")){
                username=props[1];
            }
            else if (props[0].equals("db-password")){
                password=props[1];
            }
        }
        
        Map properties = new HashMap();
        properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:mysql://"+url);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        EntityManagerFactory emf=null;
        try {
            emf = Persistence.createEntityManagerFactory("OpenDoors_Persistence_PU", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        em = emf.createEntityManager();
    }
    /*
    Returns: 0 if success
             -1 if unable to parse json
             -2 if invalid data_type
             -3 if unable to store data
    */
    @Path("/regist")
    @POST
    public String save(String parameters) {
        JSONObject retval = new JSONObject();
        try {
            // Parse JSON:
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
        
            Integer data_type = Integer.valueOf(((Long)param.get("data_type")).toString());
            JSONObject data = (JSONObject) param.get("data");

            // Get/Persist Store:
            Long store = (Long)data.get("store");
            em.getTransaction().begin();
            Loja loja = em.find(Loja.class,store);
            if(loja==null) {
                loja = new Loja(store);
                em.persist(loja);
                em.getTransaction().commit();
                em.getTransaction().begin();
            }

            // Persist Data:
            Timestamp now = new Timestamp(System.currentTimeMillis());
            switch(data_type) {
                case 1: // luminosidade // {"data_type":1, "data":{"store":1,"light":{"visible":[100],"infrared":[0]}}}
                    JSONObject data_light = (JSONObject) data.get("light");
                    JSONArray visible = (JSONArray) data_light.get("visible");
                    JSONArray infrared = (JSONArray) data_light.get("infrared");
                    try{
                        for(int i=0; i<visible.size() ; i++) {
                            Luminosidade luz = new Luminosidade(loja, i+1, now, Integer.valueOf(((Long)visible.get(i)).toString()), Integer.valueOf(((Long)infrared.get(i)).toString()));
                            em.persist(luz);
                        }
                        em.getTransaction().commit();
                    } catch(Exception e) { 
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString(); 
                    }
                    break;
                case 2: // temperatura // {"data_type":2, "data":{"store":1,"temperature":[15.0]}}
                    JSONArray temperature = (JSONArray) data.get("temperature");
                    try{
                        for(int i=0; i<temperature.size(); i++) {
                            String aux = temperature.get(i).toString();
                            Double value;
                            if(aux.contains(".")) {
                                value = (Double)temperature.get(i);
                            } else {
                                value = (Long)temperature.get(i) + 0.0;
                            }
                            Temperatura temp = new Temperatura(loja, i+1, now, value);
                            em.persist(temp);
                        }
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString(); 
                    }
                    break;
                case 3: // entrada de clientes // {"data_type":3, "data":{"store":1}}
                    try{
                        Infravermelho movement = new Infravermelho(loja, now);
                        em.persist(movement);
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString(); 
                    }
                    break;
                case 4: // sentar em cadeiras // {"data_type":4, "data":{"store":1,"pressure":[1.1, 2.0]}}
                    JSONArray pressure = (JSONArray) data.get("pressure");
                    try{
                        for(int i=0; i<pressure.size(); i++) {
                            int id_cadeira = (int)((Double)pressure.get(i)*1);
                            int acao = (int)(((Double)pressure.get(i)-id_cadeira)*10);
                            Pressao sitting = new Pressao(loja, id_cadeira, now, acao);
                            em.persist(sitting);
                        }
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString();  
                    }
                    break;
                case 5: // novo empregado // {"data_type":5, "data":{"store":1,"cc":1234,"name":"Joaquim"}}
                    long cc = (long)data.get("cc");
                    String name = (String)data.get("name");
                    try{
                        Empregado employee = new Empregado(loja, cc, name);
                        em.persist(employee);
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString(); 
                    }
                    break;
                case 6: // evento // {"data_type":6, "data":{"store":1, "employee":1,"time":...,"description":"..."}}
                    Timestamp time = (Timestamp)data.get("time");
                    String entity;
                    Long id;
                    if(data.containsKey("employee")) { 
                        entity = "employee"; 
                        id = (Long)data.get(entity);
                    } else if (data.containsKey("client")) { 
                        entity = "client"; 
                        id = (Long)data.get(entity);
                    } else {
                        entity = "client"; 
                        id = null;
                    }
                    String description = (String)data.get("description");
                    try{
                        Evento event = new Evento(loja, time, entity, id, description);
                        em.persist(event);
                        em.getTransaction().commit();
                    } catch(Exception e) {
                        e.printStackTrace();
                        retval.put("status", -2);
                        return retval.toJSONString(); 
                    }
                    break;
                default:
                    retval.put("status", -1);
                    return retval.toJSONString(); 
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -3);
            return retval.toJSONString();
        }
        retval.put("status", 0);
        return retval.toJSONString();
    }
    
    @Path("/temperature")
    @POST
    public String getTemperature(String parameters) { 
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            Timestamp min = convertStringToTimestamp((String)param.get("min"));
            Timestamp max = convertStringToTimestamp((String)param.get("max"));
            List<Temperatura> current = em.createNamedQuery("findTemperature", Temperatura.class).setParameter(1,store).setParameter(2,min).setParameter(3,max).getResultList();
            if(!current.isEmpty()) {
                JSONArray time = new JSONArray();
                JSONArray temperature = new JSONArray();
                
                JSONArray currentTemperature = new JSONArray();
                Timestamp currentTime = current.get(0).getTempo();
                
                for(Temperatura t: current) {
                    
                    if((t.getTempo()).equals(currentTime)) {
                        currentTemperature.add(t.getTemperatura());
                    } else {
                        int avg_temperature = 0;
                        for(Object o: currentTemperature) {
                            avg_temperature += (Double)o;
                        }
                        avg_temperature = avg_temperature/currentTemperature.size();
                        
                        time.add(currentTime);
                        temperature.add(avg_temperature);
                        
                        currentTemperature.clear();
                        
                        currentTime = t.getTempo();
                        currentTemperature.add(t.getTemperatura());
                    }
                }
                
                retval.put("time", time.toString());
                retval.put("temperature", temperature);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        
        return retval.toJSONString();
    }
    
    @Path("/current/temperature")
    @POST
    public String getCurrentTemperature(String parameters) { // {"store":1}
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            List<Temperatura> current = em.createNamedQuery("findCurrentTemperature", Temperatura.class).setParameter(1,store).getResultList();
            if(!current.isEmpty()) {
                JSONArray sensor = new JSONArray();
                JSONArray temperature = new JSONArray();
                for(Temperatura t: current) {
                    sensor.add(t.getSensor());
                    temperature.add(t.getTemperatura());
                }
                retval.put("time", current.get(0).getTempo().toString());
                retval.put("sensor", sensor);
                retval.put("temperature", temperature);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/light")
    @POST
    public String getLight(String parameters) { //
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            Timestamp min = convertStringToTimestamp((String)param.get("min"));
            Timestamp max = convertStringToTimestamp((String)param.get("max"));
            List<Luminosidade> current = em.createNamedQuery("findLight", Luminosidade.class).setParameter(1,store).setParameter(2,min).setParameter(3,max).getResultList();
            if(!current.isEmpty()) {
                JSONArray time = new JSONArray();
                JSONArray visible = new JSONArray();
                JSONArray infrared = new JSONArray();
                
                JSONArray currentVisible = new JSONArray();
                JSONArray currentInfrared = new JSONArray();
                Timestamp currentTime = current.get(0).getTempo();
                for(Luminosidade l: current) {
                    if((l.getTempo()).equals(currentTime)) {
                        currentVisible.add(l.getVisivel());
                        currentInfrared.add(l.getInfravermelho());
                    } else {
                        int avg_visible = 0;
                        for(Object o: currentVisible) {
                            avg_visible += (Integer)o;
                        }
                        avg_visible = avg_visible/currentVisible.size();
                        int avg_infrared = 0;
                        for(Object o: currentInfrared) {
                            avg_infrared += (Integer)o;
                        }
                        avg_infrared = avg_infrared/currentInfrared.size();
                        
                        time.add(currentTime);
                        visible.add(avg_visible);
                        infrared.add(avg_infrared);
                        
                        currentVisible.clear();
                        currentInfrared.clear();
                        
                        currentTime = l.getTempo();
                        currentVisible.add(l.getVisivel());
                        currentInfrared.add(l.getInfravermelho());
                    }
                }
                retval.put("time", time.toString());
                retval.put("visible", visible);
                retval.put("infrared", infrared);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/current/light")
    @POST
    public String getCurrentLight(String parameters) { // {"store":1}
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            List<Luminosidade> current = em.createNamedQuery("findCurrentLight", Luminosidade.class).setParameter(1,store).getResultList();
            if(!current.isEmpty()) {
                JSONArray sensor = new JSONArray();
                JSONArray visible = new JSONArray();
                JSONArray infrared = new JSONArray();
                for(Luminosidade l: current) {
                    sensor.add(l.getSensor());
                    visible.add(l.getVisivel());
                    infrared.add(l.getInfravermelho());
                }
                retval.put("time", current.get(0).getTempo().toString());
                retval.put("sensor", sensor);
                retval.put("visible", visible);
                retval.put("infrared", infrared);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/clients")
    @POST
    public String getClients(String parameters) { //
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            Timestamp min = convertStringToTimestamp((String)param.get("min"));
            Timestamp max = convertStringToTimestamp((String)param.get("max"));
            List<Infravermelho> current = em.createNamedQuery("findClients", Infravermelho.class).setParameter(1,store).setParameter(2,min).setParameter(3,max).getResultList();
            if(!current.isEmpty()) {
                JSONArray time = new JSONArray();
                for(Infravermelho movement: current) {
                    time.add(movement.getTempo());
                }
                retval.put("time", time.toString());
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/employees/all")
    @GET
    public String getAllEmployeesInfo() { 
        JSONObject retval = new JSONObject();
        List<Empregado> current = em.createNamedQuery("findAllEmployees", Empregado.class).getResultList();
        if(!current.isEmpty()) {
            JSONArray cc = new JSONArray();
            JSONArray name = new JSONArray();
            for(Empregado e: current) {
                cc.add(e.getCC());
                name.add(e.getNome());
            }
            retval.put("cc", cc);
            retval.put("name", name);
        }
        return retval.toJSONString();
    }
    
    @Path("/employees/store")
    @POST
    public String getStoreEmployeesInfo(String parameters) { // {"store":1}
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            List<Empregado> current = em.createNamedQuery("findStoreEmployees", Empregado.class).setParameter(1,store).getResultList();
            if(!current.isEmpty()) {
                JSONArray cc = new JSONArray();
                JSONArray name = new JSONArray();
                for(Empregado e: current) {
                    cc.add(e.getCC());
                    name.add(e.getNome());
                }
                retval.put("cc", cc);
                retval.put("name", name);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/employees/unregist")
    @POST
    public String unregistEmployee(String parameters) { // {"cc":1234}
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long cc = (Long)param.get("cc");
            Empregado e = em.find(Empregado.class, cc);
            if(e != null) {
                em.getTransaction().begin();
                em.remove(e);
                em.getTransaction().commit();
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/events")
    @POST
    public String getEvents(String parameters) { //
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            Timestamp min = convertStringToTimestamp((String)param.get("min"));
            Timestamp max = convertStringToTimestamp((String)param.get("max"));
            List<Evento> current = em.createNamedQuery("findEvents", Evento.class).setParameter(1,store).setParameter(2,min).setParameter(3,max).getResultList();
            if(!current.isEmpty()) {
                JSONArray time = new JSONArray();
                JSONArray entity = new JSONArray();
                JSONArray id = new JSONArray();
                JSONArray description = new JSONArray();
                for(Evento e: current) {
                    time.add(e.getTempo());
                    entity.add(e.getEntity());
                    id.add(e.getID());
                    description.add(e.getDescription());
                }                
                retval.put("time", time.toString());
                retval.put("entity", entity);
                retval.put("id", id);
                retval.put("description", description);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    @Path("/current/events")
    @POST
    public String getCurrentEvents(String parameters) { // {"store":1}
        JSONObject retval = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject param = (JSONObject) parser.parse(parameters);
            Long store = (Long)param.get("store");
            List<Evento> current = em.createNamedQuery("findCurrentEvents", Evento.class).setParameter(1,store).getResultList();
            if(!current.isEmpty()) {
                JSONArray entity = new JSONArray();
                JSONArray id = new JSONArray();
                JSONArray description = new JSONArray();
                for(Evento e: current) {
                    entity.add(e.getEntity());
                    id.add(e.getID());
                    description.add(e.getDescription());
                }
                retval.put("time", current.get(0).getTempo().toString());
                retval.put("entity", entity);
                retval.put("id", id);
                retval.put("description", description);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            retval.put("status", -1);
            return retval.toJSONString();
        }
        return retval.toJSONString();
    }
    
    public static Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date date = (Date) formatter.parse(str_date);
            Timestamp timeStampDate = new Timestamp(date.getTime());
            return timeStampDate;
        } catch (java.text.ParseException ex) {
            Logger.getLogger(PersistenceServlet.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception :" + ex);
            return null;
        }
    }
}
