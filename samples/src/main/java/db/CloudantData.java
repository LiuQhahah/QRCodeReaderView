package db;

/**
 * Created by root on 17-11-22.
 */

public class CloudantData {

    private String id;
    private String key;

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public Doc doc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public class Doc{
        public Payload getPayload() {
            return payload;
        }

        public void setPayload(Payload payload) {
            this.payload = payload;
        }

        public Payload payload;

    }
    public class Payload{
        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }

        public D d;

    }
    public class D{

        public String temp;
        public String humidity;

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }
    }

}
