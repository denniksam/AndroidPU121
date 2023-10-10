package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );

        findViewById( R.id.game_layout ).setOnTouchListener(
                new OnSwipeListener( GameActivity.this ) {
                    @Override
                    public void onSwipeBottom() {
                        Toast.makeText(   // повідомлення, що з'являється та зникає з часом
                                GameActivity.this,
                                "onSwipeBottom",
                                Toast.LENGTH_SHORT   // час відображення повідомлення
                        ).show();
                    }
                    @Override
                    public void onSwipeLeft() {
                        Toast.makeText( GameActivity.this, "onSwipeLeft", Toast.LENGTH_SHORT ).show();
                    }
                    @Override
                    public void onSwipeRight() {
                        Toast.makeText( GameActivity.this, "onSwipeRight", Toast.LENGTH_SHORT ).show();
                    }
                    @Override
                    public void onSwipeTop() {
                        Toast.makeText( GameActivity.this, "onSwipeTop", Toast.LENGTH_SHORT ).show();
                    }
                } );
    }
}
/*
Д.З. Реалізувати верстку головної активності для гри 2048
 */