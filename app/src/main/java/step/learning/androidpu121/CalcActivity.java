package step.learning.androidpu121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {
    private final int MAX_DIGITS = 10 ;
    private TextView tvResult ;
    private TextView tvExpression ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_calc );

        tvResult = findViewById( R.id.calc_tv_result );
        tvResult.setText( R.string.calc_btn_0 ) ;
        tvExpression = findViewById( R.id.calc_tv_expression );
        tvExpression.setText( "" );
        
        findViewById( R.id.calc_btn_inverse )
                .setOnClickListener( this::inverseClick );

        for( int i = 0; i < 10; i++ ) {
            findViewById(
                getResources().getIdentifier(
                    "calc_btn_" + i,
                    "id",
                    getPackageName()
            ) ).setOnClickListener( this::digitClick );
        }
        findViewById( R.id.calc_btn_c ).setOnClickListener( this::clearClick );
    }
    /*
    При зміні конфігурації відбувається перестворення активності, через що
    втрачаються дані, введені на UI. З метою збереження цих даних задаються
    обробники відповідних подій.
     */
    @Override
    protected void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState( outState );
        outState.putCharSequence( "result", tvResult.getText() );
        outState.putCharSequence( "expression", tvExpression.getText() );
    }
    @Override
    protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState ) {
        super.onRestoreInstanceState( savedInstanceState );
        tvResult.setText( savedInstanceState.getCharSequence( "result" ) );
        tvExpression.setText( savedInstanceState.getCharSequence( "expression" ) );
    }

    private void inverseClick( View view ) {
        String str = tvResult.getText().toString() ;
        double arg = getResult( str ) ;
        if( arg == 0 ) {
            tvResult.setText( R.string.calc_div_zero_message );
        }
        else {
            str = "1 / " + str + " =" ;
            tvExpression.setText( str );
            showResult( 1 / arg );
        }
    }
    private void digitClick( View view ) {
        String str = tvResult.getText().toString() ;
        if( str.equals( getString( R.string.calc_btn_0 ) ) ) {
            str = "";
        }
        str += ( ( Button ) view ).getText();
        tvResult.setText( str );
    }
    private void clearClick( View view ) {
        tvResult.setText( R.string.calc_btn_0 ) ;
    }
    private void showResult( double res ) {
        String str = String.valueOf( res );
        if( str.length() > MAX_DIGITS ) {
            str = str.substring( 0, MAX_DIGITS ) ;
        }
        str = str.replaceAll( "0", getString( R.string.calc_btn_0 ) ) ;
        tvResult.setText( str );
    }
    private double getResult( String str ) {
        str = str.replaceAll( getString( R.string.calc_btn_0 ), "0" );
        str = str.replaceAll( getString( R.string.calc_btn_comma ), "." );
        return Double.parseDouble( str );
    }
}
/*
Д.З. Завершити роботу над проєктом "Калькулятор"
 */