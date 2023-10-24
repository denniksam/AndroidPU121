package step.learning.androidpu121.orm;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

// ORM відповіді НБУ на запит курсів валют
public class NbuRateResponse {
    private final List<NbuRate> rates ;

    public NbuRateResponse( JSONArray jsonArray ) throws JSONException {
        rates = new ArrayList<>() ;
        int length = jsonArray.length() ;
        for( int i = 0; i < length; i++ ) {
            rates.add( new NbuRate( jsonArray.getJSONObject( i ) ) ) ;
        }
    }

    public List<NbuRate> getRates() {
        return rates;
    }
}
