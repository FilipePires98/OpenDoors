package communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private static String TRADFRI="deti-engsoft-01.ua.pt:8081";
    private static String VIRTUALLIGHT="localhost:4000";
    private static String AIRCONDITIONER="localhost:8000";
    
    
    static{
        File file = new File("/IES24/24properties"); 
        Scanner sc=null; 
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DevicesServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        while (sc.hasNextLine()){
            String[] props = sc.nextLine().split("=");
            if(props[0].equals("tradfri")){
                TRADFRI=props[1];
            }
            else if(props[0].equals("virtual-light")){
                VIRTUALLIGHT=props[1];
            }
            else if(props[0].equals("virtual-airconditioner")){
                AIRCONDITIONER=props[1];
            }
        }
    }
    
    private HashMap<Long, String[]> devices = new HashMap<>();
    
    public DevicesServlet(){
        devices.put(1l, new String[]{
            "http://"+TRADFRI+"/light/luzteste/switch/on",
            "http://"+TRADFRI+"/light/luzteste/switch/off",
            "http://"+TRADFRI+"/light/luzteste/luminosity/",
            "http://"+TRADFRI+"/light/luzteste/color/"});
        devices.put(2l, new String[]{
            "http://"+VIRTUALLIGHT+"/VirtualLight-1.0/light/on",
            "http://"+VIRTUALLIGHT+"/VirtualLight-1.0/light/off",
            "http://"+VIRTUALLIGHT+"/VirtualLight-1.0/light/luminosity/",
            "http://"+VIRTUALLIGHT+"/VirtualLight-1.0/light/color/"
        });
    }
    
    // Light
    
    @GET
    @Path("/light/{id}/on")
    public JSONObject turnOnLight(@PathParam("id") long id) throws MalformedURLException {
        String url = devices.get(id)[0];
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/light/{id}/off")
    public JSONObject turnOffLight(@PathParam("id") long id) throws MalformedURLException {
        String url = devices.get(id)[1];
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/light/{id}/luminosity/{luminosity}")
    public JSONObject changeLuminosityLight(@PathParam("id") long id,@PathParam("luminosity") int luminosity) throws MalformedURLException {
        String url = devices.get(id)[2]+luminosity;
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/light/{id}/color/{color_num}")
    public JSONObject changeColorLight(@PathParam("id") long id, @PathParam("color_num") int color_num) throws MalformedURLException {
        String[] colors = new String[] {"light_pink","pink","saturated_pink","saturated_purple","light_purple","light_blue","blue","lime","yellow","peach","dark_peach","warm_amber"};
        String url= devices.get(id)[3]+colors[color_num-1];
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL(url).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    
    // Temperature
    
    @GET
    @Path("/airconditioner/on")
    public JSONObject turnOnTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://"+AIRCONDITIONER+"/AirConditioner-1.0/airconditioner/on").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/airconditioner/off")
    public JSONObject turnOffTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://"+AIRCONDITIONER+"/AirConditioner-1.0/airconditioner/off").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/airconditioner/regulate")
    public JSONObject regulateTemperature(@QueryParam("temperature") double temperature) throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://"+AIRCONDITIONER+"/AirConditioner-1.0/airconditioner/regulate/"+temperature).toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
    
    @GET
    @Path("/airconditioner/getValue")
    public JSONObject getTemperature() throws MalformedURLException {
        JSONObject response = ClientBuilder.newClient()
                .target(URI.create(new URL("http://"+AIRCONDITIONER+"/AirConditioner-1.0/airconditioner/getValue").toExternalForm()))
                .request(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);
        return response;
    }
}
