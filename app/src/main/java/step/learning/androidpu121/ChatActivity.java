package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private TextView tvTitle ;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        tvTitle = findViewById( R.id.chat_tv_title ) ;

        new Thread( this::loadChatMessages ).start() ;
    }
    private void loadChatMessages() {
        try( InputStream inputStream = new URL( chatUrl ).openStream() ) {
            String body = streamToString( inputStream ) ;
            runOnUiThread(
                    () -> tvTitle.setText( body )
            );
        }
        catch( NetworkOnMainThreadException ignored ) {
            Log.e( "loadChatMessages", "NetworkOnMainThreadException" ) ;
        }
        catch( MalformedURLException ex ) {
            Log.e( "loadChatMessages", "URL parse error: " + ex.getMessage() ) ;
        }
        catch( IOException ex ) {
            Log.e( "loadChatMessages", "IO error: " + ex.getMessage() ) ;
        }
    }

    private String streamToString( InputStream inputStream ) throws IOException {
        ByteArrayOutputStream builder = new ByteArrayOutputStream() ;
        byte[] buffer = new byte[4096] ;
        int bytesReceived ;
        while( ( bytesReceived = inputStream.read( buffer ) ) > 0 ) {
            builder.write( buffer, 0 , bytesReceived ) ;
        }
        return builder.toString() ;
    }
}