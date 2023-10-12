package step.learning.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int N = 4;
    private final int[][] cells = new int[N][N] ;
    private final TextView[][] tvCells = new TextView[N][N] ;
    private final Random random = new Random() ;

    private int score ;
    private TextView tvScore ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );

        // Збираємо посилання на комірки ігрового поля
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                tvCells[i][j] = findViewById(
                   getResources().getIdentifier(
                           "game_cell_" + i + j,
                           "id",
                           getPackageName()
                   )
                ) ;
            }
        }

        findViewById( R.id.game_table ).setOnTouchListener(
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

        spawnCell() ;
        spawnCell() ;
        showField() ;
    }

    /**
     * Поява нового числа на полі
     * @return чи додалось число (є вільні комірки)
     */
    private boolean spawnCell() {
        // оскільки не відомо де і скільки порожніх комірок, шукаємо їх всі
        List<Integer> freeCellIndexes = new ArrayList<>() ;
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
               if( cells[i][j] == 0 ) {  // ознака порожньої комірки
                   // складаємо її координати в один індекс і зберігаємо
                   freeCellIndexes.add( 10 * i + j );
               }
            }
        }
        // перевіряємо чи є взагалі порожні комірки
        int cnt = freeCellIndexes.size() ;
        if( cnt == 0 ) {  // немає порожніх - не можна додавати
            return false ;
        }
        // генеруємо випадковий індекс в межах довжини масиву
        int randIndex = random.nextInt( cnt ) ;
        // вилучаємо "зібраний" індекс комірки
        int randCellIndex = freeCellIndexes.get( randIndex ) ;
        // розділяємо його на координати ( 10 * i + j )
        int x = randCellIndex / 10 ;
        int y = randCellIndex % 10 ;
        // генеруємо випадкове число: 2 (з імовірністю 0.9) чи 4 (0.1)
        cells[x][y] =
                random.nextInt(10) == 0   // умова "один з 10"
                ? 4    // цей блок з імовірністю 1 / 10
                : 2 ;  // цей в інших випадках
        return true ;
    }

    /**
     * Показ поля - відображення числових даних на View та
     * підбір стилів у відповідності до значення числа
     */
    private void showField() {
        // Особливість - деякі параметри "на льоту" можна змінювати
        // через стилі, але не всі, для деяких доводиться подавати
        // окремі інструкції
        Resources resources = getResources() ;
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                // встановлюємо текст - зображення числа у масиві
                tvCells[i][j].setText( String.valueOf( cells[i][j] ) );

                // змінюємо стиль у відповідності значення cells[i][j]
                tvCells[i][j].setTextAppearance(
                        resources.getIdentifier(
                                "game_cell_" + cells[i][j],
                                "style",
                                getPackageName()
                        )
                );

                // окремо змінюємо background, через стиль ігнорується
                tvCells[i][j].setBackgroundColor(
                        resources.getColor(
                                resources.getIdentifier(
                                        "game_bg_" + cells[i][j],
                                        "color",
                                        getPackageName()
                                ),
                                getTheme()
                        )
                );
            }
        }
    }
}
