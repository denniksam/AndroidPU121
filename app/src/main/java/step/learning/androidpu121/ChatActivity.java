package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import step.learning.androidpu121.orm.ChatMessage;
import step.learning.androidpu121.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private final List<ChatMessage> chatMessages = new ArrayList<>() ;
    private EditText etNik ;
    private EditText etMessage ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        new Thread( this::loadChatMessages ).start() ;
        chatContainer = findViewById( R.id.chat_layout_container ) ;
        etNik = findViewById( R.id.chat_et_nik ) ;
        etMessage = findViewById( R.id.chat_et_message ) ;
        findViewById( R.id.chat_btn_send ).setOnClickListener( this::sendButtonClick ) ;
    }
    private void sendButtonClick( View view ) {
        final String nik = etNik.getText().toString() ;
        final String message = etMessage.getText().toString() ;
        if( nik.isEmpty() ) {
            Toast.makeText( this, "Введіть нік у чаті", Toast.LENGTH_SHORT ).show();
            etNik.requestFocus() ;
            return ;
        }
        if( message.isEmpty() ) {
            Toast.makeText( this, "Введіть повідомлення", Toast.LENGTH_SHORT ).show();
            etMessage.requestFocus() ;
            return ;
        }
        new Thread( () -> sendChatMessage( nik, message ) ).start() ;
    }
    private void sendChatMessage( String nik, String message ) {
        try {
            // Надсилаємо POST-запит, це відбувається у декілька кроків
            // 1. Налаштовуємо з'єднання
            URL url = new URL( chatUrl ) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            connection.setDoOutput( true ) ;   // запит матиме тіло (Output)
            connection.setDoInput( true ) ;    // від запиту очікується відповідь
            connection.setRequestMethod( "POST" ) ;
            // заголовки встановлюються як RequestProperty
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" ) ;
            connection.setRequestProperty( "Accept", "*/*" ) ;
            connection.setChunkedStreamingMode( 0 ) ;  // не розділяти на блоки (один потік)

            // 2. Формуємо запит - пишемо до Output
            OutputStream connectionOutput = connection.getOutputStream() ;
            String body = String.format( "author=%s&msg=%s", nik, message ) ;
            connectionOutput.write( body.getBytes( StandardCharsets.UTF_8 ) ) ;
            connectionOutput.flush() ;  // надсилання запиту
            connectionOutput.close() ;  // закриваємо, або вживаємо try(resource)

            // 3. Одержуємо відповідь - перевіряємо статус та читаємо Input
            int statusCode = connection.getResponseCode() ;
            if( statusCode == 201 ) {  // відповідь-успіх, тут тіла не буде
                Log.d( "sendChatMessage", "Request OK" ) ;
            }
            else {  // відповідь-помилка, повідомлення про неї у тілі
                InputStream connectionInput = connection.getInputStream() ;
                String errorMessage = streamToString( connectionInput ) ;
                Log.d( "sendChatMessage",
                        "Request failed with code " + statusCode + " " + errorMessage ) ;
            }
            connection.disconnect() ;
            // у разі успішного надсилання завантажуємо повідомлення
            if( statusCode == 201 ) {
                loadChatMessages() ;
            }
        }
        catch( Exception ex ) {
            Log.d( "sendChatMessage", ex.getMessage() ) ;
        }
    }
    private void showChatMessages() {
        for( ChatMessage chatMessage : chatMessages ) {
            chatContainer.addView( chatMessageView( chatMessage ) ) ;
        }
    }
    private View chatMessageView( ChatMessage chatMessage ) {
        LinearLayout messageContainer = new LinearLayout( ChatActivity.this ) ;
        messageContainer.setOrientation( LinearLayout.VERTICAL ) ;
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ) ;
        messageContainer.setLayoutParams( containerParams ) ;

        TextView tv = new TextView( ChatActivity.this ) ;
        tv.setText( chatMessage.getAuthor() ) ;
        tv.setTypeface( null, Typeface.BOLD ) ;
        messageContainer.addView( tv ) ;

        tv = new TextView( ChatActivity.this ) ;
        tv.setText( chatMessage.getText() ) ;
        tv.setTypeface( null, Typeface.ITALIC ) ;
        messageContainer.addView( tv ) ;

        return messageContainer ;
    }
    private void loadChatMessages() {
        try( InputStream inputStream = new URL( chatUrl ).openStream() ) {
            ChatResponse chatResponse = ChatResponse.fromJsonString(
                    streamToString( inputStream ) ) ;

            // Перевіряємо на нові повідомлення, оновлюємо (за потреби) колекцію chatMessages
            boolean wasNewMessages = false ;
            chatResponse.getData().sort( Comparator.comparing( ChatMessage::getDate ) ) ;
            for( ChatMessage message : chatResponse.getData() ) {
                if( chatMessages.stream().noneMatch( m -> m.getId().equals( message.getId() ) ) ) {
                    // це нове повідомлення (немає у колекції)
                    chatMessages.add( message ) ;
                    wasNewMessages = true ;
                }
            }
            if( wasNewMessages ) {
                runOnUiThread( this::showChatMessages );
            }
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
/*
Додати до блоку з ніком ще один елемент - іконку, яка буде символізувати прихід
нового повідомлення (дзвоник, літачок, конверт...). При надходженні нового повідомлення
(wasNewMessages) програвати звук та анімувати цю іконку.
 */