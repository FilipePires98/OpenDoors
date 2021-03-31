package persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name="24Pressao")
public class Pressao implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    @Id
    private int sensor;
    @Id
    private Timestamp tempo;
    private int valor;

    public Pressao() {
    }

    public Pressao(Loja loja, int sensor, Timestamp tempo, int valor) {
        this.loja = loja;
        this.sensor = sensor;
        this.tempo = tempo;
        this.valor = valor;
    }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
    public int getSensor() { return sensor; }
    public void setSensor(int sensor) { this.sensor = sensor; }
    public Timestamp getTempo() { return tempo; }
    public void setTempo(Timestamp tempo) { this.tempo = tempo; }
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.loja);
        hash = 53 * hash + this.sensor;
        hash = 53 * hash + Objects.hashCode(this.tempo);
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
        final Pressao other = (Pressao) obj;
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
        return "Pressao{" + "loja=" + loja.getId() + ", sensor=" + sensor + ", tempo=" + tempo + ", valor=" + valor + '}';
    }
}
