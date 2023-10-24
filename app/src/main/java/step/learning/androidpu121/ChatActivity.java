package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import step.learning.androidpu121.orm.ChatMessage;
import step.learning.androidpu121.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private final List<ChatMessage> chatMessages = new ArrayList<>() ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        chatContainer = findViewById( R.id.chat_layout_container ) ;

        new Thread( this::loadChatMessages ).start() ;
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
    /*
    Д.З. Створити дизайн для відображення повідомлень двох типів:
    "свої" - вирівнювання праворуч, свої кольори, рамка НЕ скруглена у правому-нижньому куті
    "інші" - по лівому краю, нескруглений кут лівий-нижній
    Додати виведення часу повідомлення (*) в інтелектуальній формі: якщо сьогодні то
    тільки час, якщо на день раніше - "учора", інакше повна дата-час
    Для випробування вивести різні стилі через один
     */

    private void loadChatMessages() {
        try( InputStream inputStream = new URL( chatUrl ).openStream() ) {
            ChatResponse chatResponse = ChatResponse.fromJsonString(
                    streamToString( inputStream ) ) ;

            // Перевіряємо на нові повідомлення, оновлюємо (за потреби) колекцію chatMessages
            boolean wasNewMessages = false ;
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