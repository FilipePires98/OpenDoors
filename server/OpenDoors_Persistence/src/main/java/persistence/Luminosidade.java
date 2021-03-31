package persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name="24Luminosidade")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findCurrentLight", 
        query="SELECT * FROM 24Luminosidade WHERE loja = ?1 AND tempo=(SELECT MAX(tempo) FROM 24Luminosidade)",
        resultClass=Luminosidade.class),
    @NamedNativeQuery(
        name="findLight", 
        query="SELECT * FROM 24Luminosidade WHERE loja = ?1 AND tempo > ?2 AND tempo < ?3",
        resultClass=Luminosidade.class)
})
public class Luminosidade implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    @Id
    private int sensor;
    @Id
    private Timestamp tempo;
    private int visivel;
    private int infravermelho;

    public Luminosidade() { }

    public Luminosidade(Loja loja, int sensor, Timestamp tempo, int visivel, int infravermelho) {
        this.loja = loja;
        this.sensor = sensor;
        this.tempo = tempo;
        this.visivel = visivel;
        this.infravermelho = infravermelho;
    }

    public Long getLoja() { return loja.getId(); }
    public void setLoja(Loja loja) { this.loja = loja; }
    public int getSensor() { return sensor; }
    public void setSensor(int sensor) { this.sensor = sensor; }
    public Timestamp getTempo() { return tempo; }
    public void setTempo(Timestamp tempo) { this.tempo = tempo; }
    public int getVisivel() { return visivel; }
    public void setVisivel(int visivel) { this.visivel = visivel; }
    public int getInfravermelho() { return infravermelho; }
    public void setInfravermelho(int infravermelho) { this.infravermelho = infravermelho; }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.loja);
        hash = 89 * hash + this.sensor;
        hash = 89 * hash + Objects.hashCode(this.tempo);
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
        final Luminosidade other = (Luminosidade) obj;
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
        return "Luminosidade{" + "loja=" + loja.getId() + ", sensor=" + sensor + ", tempo=" + tempo + ", visivel=" + visivel + ", infravermelho=" + infravermelho + '}';
    }
}
