package airconditioner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.json.simple.JSONObject;

@Path("/airconditioner")
public class AirConditionerController {
    
    private static AirConditioner airConditioner = AirConditioner.getInstance(8888l, 17); 
    
    @GET
    @Path("/on")
    public JSONObject turnOn() {
        
        //No caso de ele estar desligado, liga-o, caso contrário, "envia" a temperatura atual;
        if (airConditioner.getTemperature() == 0) {
            airConditioner.setTemperature(17);
        } 
        
        JSONObject json = new JSONObject();
        json.put("device", airConditioner.getId());
        json.put("temperature",airConditioner.getTemperature());
        return json;
    }
    
    @GET
    @Path("/off")
    public JSONObject turnOff() {
        JSONObject json = new JSONObject();
        airConditioner.setTemperature(0);
        json.put("device", airConditioner.getId());
        json.put("temperature",airConditioner.getTemperature());
        return json;
    }
    
    @GET
    @Path("/getValue")
    public JSONObject getValue() {
        JSONObject json = new JSONObject();
        json.put("device", airConditioner.getId());
        json.put("temperature",airConditioner.getTemperature());
        return json;
    }
    
    @GET
    @Path("/regulate/{temperature}")
    public JSONObject regulateTemperature(@PathParam("temperature") double temperature) {
        JSONObject json = new JSONObject();
        airConditioner.setTemperature(temperature);
        json.put("device", airConditioner.getId());
        json.put("temperature",airConditioner.getTemperature());
        return json;
    }
    
}
