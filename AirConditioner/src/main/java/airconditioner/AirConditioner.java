package airconditioner;

public class AirConditioner {
    
    private static long id;
    private static double temperature;

    
    private static AirConditioner single_instance = null; 
  
    private AirConditioner(long id, double temperature)
    { 
        this.id = id;
        this.temperature = temperature;
    } 
  
    public static AirConditioner getInstance(long id, double temperature) 
    { 
        if (single_instance == null) 
            single_instance = new AirConditioner(id, temperature); 
  
        return single_instance; 
    } 
    
    public long getId() {
        return id;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    
    
    
    
}
