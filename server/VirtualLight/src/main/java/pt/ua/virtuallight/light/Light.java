package pt.ua.virtuallight.light;

public class Light {
    
    private static long id;
    private static int luminosity;
    private static String color;

    
    private static Light single_instance = null; 
  
    private Light(long id, int luminosity, String color)
    { 
        this.id = id;
        this.luminosity = luminosity;
        this.color = color;
    } 
  
    public static Light getInstance(long id, int luminosity, String color) 
    { 
        if (single_instance == null) 
            single_instance = new Light(id, luminosity, color); 
  
        return single_instance; 
    } 
    
    public long getId() {
        return this.id;
    }

    public int getLuminosity() {
        return this.luminosity;
    }

    public void setLuminosity(int luminosity) {
        this.luminosity = luminosity;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    
    
}
