package persistence;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="24Empregado")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findAllEmployees", 
        query="SELECT * FROM 24Empregado",
        resultClass=Empregado.class),
    @NamedNativeQuery(
        name="findStoreEmployees", 
        query="SELECT * FROM 24Empregado WHERE loja = ?1",
        resultClass=Empregado.class)
})
public class Empregado implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name="LOJA")
    private Loja loja;
    
    @Id
    private long cc;
    
    private String nome;

    public Empregado() {
    }
    
    public Empregado(Loja loja, long cc, String nome) {
        this.loja = loja;
        this.cc = cc;
        this.nome = nome;
    }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
    public long getCC() { return cc; }
    public void setCC(long cc) { this.cc = cc; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (this.cc ^ (this.cc >>> 32));
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
        final Empregado other = (Empregado) obj;
        if (this.cc != other.cc) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Empregado{" + "cc=" + cc + ", nome=" + nome + ", loja=" + loja + '}';
    }
}
