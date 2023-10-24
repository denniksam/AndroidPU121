package step.learning.androidpu121.orm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse {
    private int status ;
    private List<ChatMessage> data ;

    public static ChatResponse fromJsonString( String input ) {
        ChatResponse chatResponse = new ChatResponse() ;
        try {
            JSONObject response = new JSONObject( input ) ;
            chatResponse.setStatus( response.getInt( "status" ) ) ;
            chatResponse.setData( new ArrayList<>() ) ;
            JSONArray data = response.getJSONArray( "data" ) ;
            for( int i = 0; i < data.length(); i++ ) {
                chatResponse.getData().add(
                        ChatMessage.fromJson( data.getJSONObject( i ) )
                ) ;
            }
        }
        catch( JSONException e ) {
            throw new RuntimeException( e );
        }
        return chatResponse ;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus( int status ) {
        this.status = status;
    }

    public List<ChatMessage> getData() {
        return data;
    }

    public void setData( List<ChatMessage> data ) {
        this.data = data;
    }
}
