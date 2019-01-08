package pt.ua.opendoors;

class Employee {

    private String eName;
    private long eCC;
    private long eLoja;

    public Employee(String eName, long eCC, long eLoja) {
        this.eName = eName;
        this.eCC = eCC;
        this.eLoja = eLoja;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public long geteCC() {
        return eCC;
    }

    public void seteCC(long eCC) {
        this.eCC = eCC;
    }

    public long geteLoja() {
        return eLoja;
    }

    public void seteLoja(long eLoja) {
        this.eLoja = eLoja;
    }
}
