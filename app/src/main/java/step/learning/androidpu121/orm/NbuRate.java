package step.learning.androidpu121.orm;

import org.json.JSONException;
import org.json.JSONObject;

public class NbuRate {
    private int r030;
    private String txt;
    private double rate;
    private String cc;
    private String exchangedate;

    public NbuRate( JSONObject jsonObject ) throws JSONException {
        setR030( jsonObject.getInt( "r030" ) ) ;
        setTxt( jsonObject.getString( "txt" ) ) ;
        setRate( jsonObject.getDouble( "rate" ) ) ;
        setCc( jsonObject.getString( "cc" ) ) ;
        setExchangedate( jsonObject.getString( "exchangedate" ) ) ;
    }
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put( "r030", getR030() );
            jsonObject.put( "txt",  getTxt()  );
            jsonObject.put( "rate", getRate() );
            jsonObject.put( "cc",   getCc()   );
            jsonObject.put( "exchangedate", getExchangedate() );
        }
        catch( JSONException e ) {
            throw new RuntimeException( e );
        }
        return jsonObject ;
    }

    public int getR030() {
        return r030;
    }

    public void setR030( int r030 ) {
        this.r030 = r030;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt( String txt ) {
        this.txt = txt;
    }

    public double getRate() {
        return rate;
    }

    public void setRate( double rate ) {
        this.rate = rate;
    }

    public String getCc() {
        return cc;
    }

    public void setCc( String cc ) {
        this.cc = cc;
    }

    public String getExchangedate() {
        return exchangedate;
    }

    public void setExchangedate( String exchangedate ) {
        this.exchangedate = exchangedate;
    }
}
