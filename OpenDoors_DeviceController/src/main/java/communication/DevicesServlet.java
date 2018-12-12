package communication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;

@Path("/devices")
public class DevicesServlet {
    
    // Light
    @GET
    @Path("/light/{id}/on")
    public JSONObject turnOnLight(@PathParam("id") long id) throws MalformedURLException {
        String url;
        if (id == 1l) {
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/off";
        } else{
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/on";
        }
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }

    @GET
    @Path("/light/{id}/off")
    public JSONObject turnOffLight(@PathParam("id") long id) throws MalformedURLException {
        String url;
        if (id == 1l) {
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/off";
        } else{
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/off";
        }
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/light/{id}/regulate")
    public JSONObject regulateLight(@PathParam("id") long id, @QueryParam("temperature") double temperature,@QueryParam("intensity") double intensity) throws MalformedURLException {
        String url;
        if (id == 1l) {
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/off";
        } else{
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/regulate/"+temperature+"/"+intensity;
        }
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/light/{id}/getValue")
    public JSONObject getLight(@PathParam("id") long id) throws MalformedURLException {
        String url;
        if (id == 1l) {
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/off";
        } else{
            url = "http://172.18.0.3:8080/VirtualLight-1.0/light/getValue";
        }
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    // Temperature
    
    @GET
    @Path("/temperature/on")
    public JSONObject turnOnTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://172.18.0.2:8080/AirConditioner-1.0/airconditioner/on").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/temperature/off")
    public JSONObject turnOffTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://172.18.0.2:8080/AirConditioner-1.0/airconditioner/off").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/temperature/regulate")
    public JSONObject regulateTemperature(@QueryParam("temperature") double temperature) throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://172.18.0.2:8080/AirConditioner-1.0/airconditioner/regulate/"+temperature).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/temperature/getValue")
    public JSONObject getTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://172.18.0.2:8080/AirConditioner-1.0/airconditioner/getValue").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
}
