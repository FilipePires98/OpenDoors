package pt.ua.opendoors;

import java.sql.Timestamp;

class DataFromPersistence {

    private long store;
    private Timestamp beginTimestampStats;
    private Timestamp endTimestampStats;

    public DataFromPersistence() {
    }

    public Timestamp getBeginTimestampStats() {
        return beginTimestampStats;
    }

    public void setBeginTimestampStats(Timestamp beginTimestampStats) {
        this.beginTimestampStats = beginTimestampStats;
    }

    public Timestamp getEndTimestampStats() {
        return endTimestampStats;
    }

    public void setEndTimestampStats(Timestamp endTimestampStats) {
        this.endTimestampStats = endTimestampStats;
    }

    public long getStore() {
        return store;
    }

    public void setStore(long store) {
        this.store = store;
    }
}
