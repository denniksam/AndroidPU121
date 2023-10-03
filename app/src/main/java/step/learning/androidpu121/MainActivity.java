package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tvTitle ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        // Робота з елементами UI (Views) - тільки після setContentView
        tvTitle = findViewById( R.id.main_tv_title );
        Button btnViews = findViewById( R.id.main_btn_views );
        btnViews.setOnClickListener( this::btnViewsClick );
    }
    // обробники подій мають однаковий прототип
    private void btnViewsClick( View view ) {  // view - sender
        Intent intent = new Intent(
                this.getApplicationContext(),
                ViewsActivity.class ) ;
        startActivity( intent );
    }
}