package persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name="24Infravermelho")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findClients", 
        query="SELECT * FROM 24Infravermelho WHERE loja = ?1 AND tempo > ?2 AND tempo < ?3",
        resultClass=Infravermelho.class)
})
public class Infravermelho implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    @Id
    private Timestamp tempo;

    public Infravermelho() { }

    public Infravermelho(Loja loja, Timestamp tempo) {
        this.loja = loja;
        this.tempo = tempo;
    }

    public Long getLoja() { return loja.getId(); }
    public void setLoja(Loja loja) { this.loja = loja; }
    public Timestamp getTempo() { return tempo; }
    public void setTempo(Timestamp tempo) { this.tempo = tempo; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.loja);
        hash = 17 * hash + Objects.hashCode(this.tempo);
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
        final Infravermelho other = (Infravermelho) obj;
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
        return "Infravermelho{" + "loja=" + loja.getId() + ", tempo=" + tempo + '}';
    }
    
    
}
