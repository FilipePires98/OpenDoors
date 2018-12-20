package pt.ua.virtuallight.light;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.json.simple.JSONObject;

@Path("/light")
public class LightController {
    
    private static Light light = Light.getInstance(2l, 100, "lime"); 
    
    @GET
    @Path("/on")
    public JSONObject turnOn() {
        
        if (light.getLuminosity() == 0) {
            light.setLuminosity(100);
        }
        
        if (light.getColor() == null){
            light.setColor("lime");
        }
        
        JSONObject json = new JSONObject();
        json.put("device",light.getId());
        json.put("luminosity",light.getLuminosity());
        json.put("color", light.getColor());
        return json;
    }
    
    @GET
    @Path("/off")
    public JSONObject turnOff() {
        JSONObject json = new JSONObject();
        light.setLuminosity(0);
        light.setColor(null);
        json.put("device",light.getId());
        json.put("luminosity",light.getLuminosity());
        json.put("color", light.getColor());
        return json;
    }
    
    @GET
    @Path("/luminosity/{luminosity}")
    public JSONObject changeLuminosity(@PathParam("luminosity") int luminosity) {
        JSONObject json = new JSONObject();
        light.setLuminosity(luminosity);
        json.put("device",light.getId());
        json.put("luminosity",light.getLuminosity());
        json.put("color", light.getColor());
        return json;
    }
    
    @GET
    @Path("color/{color}")
    public JSONObject changeColor(@PathParam("color") String color) {
        JSONObject json = new JSONObject();
        light.setColor(color);
        json.put("device",light.getId());
        json.put("luminosity",light.getLuminosity());
        json.put("color", light.getColor());
        return json;
    }
    
}
