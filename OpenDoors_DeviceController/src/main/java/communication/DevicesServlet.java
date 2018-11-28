package communication;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/devices")
public class DevicesServlet {
    
    // Light
    
    @GET
    @Path("/light/on")
    public void turnOnLight() { }
    
    @GET
    @Path("/light/off")
    public void turnOffLight() { }
    
    @POST
    @Path("/light/regulate")
    public void regulateLight(JsonObject param) {}
    
    // Temperature
    
    @GET
    @Path("/temperature/on")
    public void turnOnTemperature() { }
    
    @GET
    @Path("/temperature/off")
    public void turnOffTemperature() { }
    
    @POST
    @Path("/temperature/regulate")
    public void regulateTemperature(JsonObject param) {}
    
}
