package persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name="24Evento")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findCurrentEvents", 
        query="SELECT * FROM 24Evento WHERE loja = ?1 AND tempo=(SELECT MAX(tempo) FROM 24Evento)",
        resultClass=Evento.class),
    @NamedNativeQuery(
        name="findEvents", 
        query="SELECT * FROM 24Evento WHERE loja = ?1 AND tempo > ?2 AND tempo < ?3",
        resultClass=Evento.class)
})
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    
    @Id
    private Timestamp tempo;
    
    private String entity;
    private Long id;
    private String description;

    public Evento() {
    }

    public Evento(Loja loja, Timestamp tempo, String entity, Long id, String description) {
        this.loja = loja;
        this.tempo = tempo;
        this.entity = entity;
        this.id = id;
        this.description = description;
    }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
    public Timestamp getTempo() { return tempo; }
    public void setTempo(Timestamp tempo) { this.tempo = tempo; }
    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }
    public Long getID() { return id; }
    public void setID(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.loja);
        hash = 29 * hash + Objects.hashCode(this.tempo);
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
        final Evento other = (Evento) obj;
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
        if (id==null) {
            return "Evento{" + "loja=" + loja + ", tempo=" + tempo + ", entity=" + entity + ", description=" + description + '}';
        } 
        return "Evento{" + "loja=" + loja + ", tempo=" + tempo + ", entity=" + entity + ", id=" + id + ", description=" + description + '}';
    }
}
