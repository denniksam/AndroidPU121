package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.icu.text.Collator;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import step.learning.androidpu121.orm.NbuRate;
import step.learning.androidpu121.orm.NbuRateResponse;

public class RatesActivity extends AppCompatActivity {
    private final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private NbuRateResponse nbuRateResponse ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rates );
        new Thread( this::loadUrlData ).start();
        findViewById( R.id.rates_btn_by_alpha ).setOnClickListener( this::byAlphaClick );
    }
    private void byAlphaClick( View view ) {
        if( nbuRateResponse == null || nbuRateResponse.getRates() == null ) {
            Toast.makeText( this, "No data", Toast.LENGTH_SHORT ).show();
            return;
        }
        nbuRateResponse.getRates().sort( (r1, r2) ->
                Collator.getInstance().compare( r1.getTxt(), r2.getTxt() ) ) ; // Comparator.comparing( NbuRate::getTxt ) );  // (r1, r2) -> r1.getTxt().compareTo( r2.getTxt() )
        showResponse() ;
    }

    private void loadUrlData() {
        try( InputStream stream = new URL( nbuRatesUrl ).openStream() ) {
            ByteArrayOutputStream builder = new ByteArrayOutputStream() ;
            byte[] buffer = new byte[1024 * 16] ;
            int receivedLength ;
            while( ( receivedLength = stream.read( buffer ) ) > 0 ) {
                builder.write( buffer, 0, receivedLength ) ;
            }
            nbuRateResponse = new NbuRateResponse(
                    new JSONArray( builder.toString() )
            ) ;
            runOnUiThread( this::showResponse ) ;
        }
        catch( IOException | JSONException ex ) {
            Log.e( "loadUrlData", Objects.requireNonNull( ex.getMessage() ) ) ;
        }
        catch( NetworkOnMainThreadException ignored ) {
            Log.e( "loadUrlData", getString( R.string.rates_ex_thread ) ) ;
        }
    }
    private void showResponse() {
        LinearLayout container = findViewById( R.id.rates_container ) ;
        container.removeAllViews();
        Drawable rateBg1 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_1
        ) ;
        Drawable rateBg2 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_2
        ) ;

        LinearLayout.LayoutParams rateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Margin - параметри контейнера (Layout), вони задають правила взаємного
        // розміщення елементів в одному контейнері
        rateParams.setMargins( 10, 5, 10, 5 );
        rateParams.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams horizontalMargin = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        horizontalMargin.setMargins( 7, 0, 7, 0 );
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lineParams.setMargins( 10, 5, 10, 5 );
        for( NbuRate nbuRate : nbuRateResponse.getRates() ) {
            LinearLayout line = new LinearLayout( this );
            TextView tv1 = new TextView( this ) ;
            tv1.setText( nbuRate.getCc() + " " ) ;
            tv1.setBackground( rateBg1 );
            tv1.setMinWidth( 40 );
            tv1.setPadding( 10, 5, 10, 5 ) ;
            tv1.setLayoutParams( rateParams );
            TextView tv2 = new TextView( this ) ;
            tv2.setText( nbuRate.getTxt() ) ;
            tv2.setLayoutParams( rateParams );
            tv2.setBackground( rateBg1 );
            tv2.setPadding( 10, 5, 10, 5 ) ;
            TextView tv3 = new TextView( this ) ;
            tv3.setText( nbuRate.getRate() + "" ) ;
            tv3.setBackground( rateBg1 );
            tv3.setPadding( 10, 5, 10, 5 ) ;
            tv3.setLayoutParams( rateParams );
            tv3.setMinWidth( 70 );
            line.setLayoutParams( lineParams );
            line.setBackground( rateBg2 );
            line.addView( tv1 );
            line.addView( tv2 );
            line.addView( tv3 );
            line.setTag( nbuRate );
            line.setOnClickListener( this::rateClick );
            container.addView( line );
            /*
            TextView tv = new TextView( this ) ;
            tv.setBackground( rateBg1 );
            tv.setText( String.format(
                    Locale.UK,
                    "%s (%s) %f грн",
                    nbuRate.getCc(),
                    nbuRate.getTxt(),
                    nbuRate.getRate()
            ) ) ;
            // Padding - властивість самого елемента, тоді як Margin - контейнера
            tv.setPadding( 10, 5, 10, 5 ) ;
            tv.setLayoutParams( rateParams );

            tv.setOnClickListener( this::rateClick );
            tv.setTag( nbuRate );
            container.addView( tv ) ;
             */
        }
    }
    private void rateClick( View view ) {
        NbuRate nbuRate = (NbuRate) view.getTag() ;
        /*
        Д.З. Реалізувати фільтрацію курсів: користувач вводить
        фрагмент тексту і натикає кнопку, залишаються тількі ті
        елементи, до складу яких входить шуканий фрагмент.
        ( порада - розділити колекції для відображення та
          повну завантажену колекцію shownRates[] = nbuRateResponse.getRates())
        [AU] [filter] --> AUD, XAU
         */
        Toast.makeText( this, nbuRate.getCc(), Toast.LENGTH_SHORT ).show();
    }

}
/*
Робота з Інтернет. АРІ.
На прикладі курсу валют.
1. Основу роботи з Інтернет становить об'єкт класу URL (схожий за
змістом з File). Створення об'єкту не спричиняє роботу з мережею,
лише з'являється сам об'єкт.
2. Мережна активність запускається спробою "відкриття" URL, зокрема,
openConnection() або openStream()
при цьому можна одержати NetworkOnMainThreadException, що свідчить
про спробу відкриття з'єднання з UI (основного) потоку. Це виключення
може не потрапити у catch, спостерігаємо у логері.
3. Підключення до Інтернет вимагає включеного WiFi (на емуляторі), а
також дозволу, відсутність якого призводить до
SecurityException: Permission denied (missing INTERNET permission?)
Даний дозвіл має бути зазначений у маніфесті
<uses-permission android:name="android.permission.INTERNET"/>
Інколи зміна дозволів вимагає перевстановлення застосунку на пристрій
4. Винесення роботи з мережею до окремого потоку призводить до
зворотної проблеми, пов'язаною з виключенням
CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
яка забороняє змінювати представлення (View) з іншого потоку.
Звертатись слід через делегування
runOnUiThread( () -> ... );
-------------------------------------------
5. ORM. Після одержання "сирих" даних їх слід "відобразити" (map) на
об'єкти та колекції платформи (мови програмування). Для цього створюють
класи, що відповідають структурі даних, та їх конструктори (фабрики),
які приймають JSON
 */