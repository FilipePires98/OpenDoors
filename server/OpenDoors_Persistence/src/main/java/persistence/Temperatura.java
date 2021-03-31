package persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name="24Temperatura")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findCurrentTemperature", 
        query="SELECT * FROM 24Temperatura WHERE loja = ?1 AND tempo=(SELECT MAX(tempo) FROM 24Temperatura)",
        resultClass=Temperatura.class),
    @NamedNativeQuery(
        name="findTemperature", 
        query="SELECT * FROM 24Temperatura WHERE loja = ?1 AND tempo > ?2 AND tempo < ?3",
        resultClass=Temperatura.class)
})
public class Temperatura implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    @Id
    private int sensor;
    @Id
    private Timestamp tempo;
    private double temperatura;

    public Temperatura() { }

    public Temperatura(Loja loja, int sensor, Timestamp tempo, double temperatura) {
        this.loja = loja;
        this.sensor = sensor;
        this.tempo = tempo;
        this.temperatura = temperatura;
    }
    
    public Long getLoja() { return loja.getId(); }
    public void setLoja(Loja loja) { this.loja = loja; }
    public int getSensor() { return sensor; }
    public void setSensor(int sensor) { this.sensor = sensor; }
    public Timestamp getTempo() { return tempo; }
    public void setTempo(Timestamp tempo) { this.tempo = tempo; }
    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.loja);
        hash = 67 * hash + this.sensor;
        hash = 67 * hash + Objects.hashCode(this.tempo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Temperatura other = (Temperatura) obj;
        if (this.sensor != other.sensor) {
            return false;
        }
        if (!Objects.equals(this.loja, other.loja)) {
            return false;
        }
        if (!Objects.equals(this.tempo, other.tempo)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Temperatura{" + "loja=" + loja.getId() + ", sensor=" + sensor + ", tempo=" + tempo + ", temperatura=" + temperatura + '}';
    }
    
}
