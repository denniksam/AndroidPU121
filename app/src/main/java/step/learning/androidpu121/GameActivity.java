package step.learning.androidpu121;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
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
    private Animation spawnCellAnimation ;
    private Animation collapseCellsAnimation ;
    private MediaPlayer spawnSound ;
    private static final String bestScoreFilename = "best_score" ;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );

        spawnSound = MediaPlayer.create( GameActivity.this, R.raw.jump_00 ) ;
        tvScore = findViewById( R.id.game_tv_score ) ;
        // завантажуємо анімацію
        spawnCellAnimation = AnimationUtils.loadAnimation(
                GameActivity.this,
                R.anim.game_spawn_cell
        ) ;
        // ініціалізуємо анімацію
        spawnCellAnimation.reset() ;

        collapseCellsAnimation = AnimationUtils.loadAnimation(
                GameActivity.this,
                R.anim.game_collapse_cells
        ) ;
        collapseCellsAnimation.reset();

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

        TableLayout tableLayout = findViewById( R.id.game_table ) ;
        tableLayout.post( () -> {   // відкладений запуск (на кадр прорисовки)
            int margin = 7 ;
            int w = this.getWindow().getDecorView().getWidth() - 2 * margin;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( w, w ) ;
            layoutParams.setMargins( 7, 50, 7, 50 );
            layoutParams.gravity = Gravity.CENTER ;
            tableLayout.setLayoutParams( layoutParams ) ;
        } ) ;
        tableLayout.setOnTouchListener(
                new OnSwipeListener( GameActivity.this ) {
                    @Override
                    public void onSwipeBottom() {
                        processMove( MoveDirection.BOTTOM );
                    }
                    @Override
                    public void onSwipeLeft() {
                        processMove( MoveDirection.LEFT );
                    }
                    @Override
                    public void onSwipeRight() {
                       processMove( MoveDirection.RIGHT );
                    }
                    @Override
                    public void onSwipeTop() {
                        processMove( MoveDirection.TOP );
                    }
                } );

        startNewGame() ;
    }

    private void saveBestScore() {

    }
    private void loadBestScore() {

    }
    private void startNewGame() {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                cells[i][j] = 0 ;
            }
        }
        score = 0 ;
        // cells[1][1] = 8;
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

        // призначаємо анімацію появи для View даної комірки
        tvCells[x][y].startAnimation( spawnCellAnimation ) ;
        // програємо звук
        // spawnSound.start();

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

        // Resource with placeholder (%d in resource for number)
        tvScore.setText( getString( R.string.game_tv_score, score ) );
    }

    private void processMove( MoveDirection direction ) {
        if( move( direction ) ) {
            spawnCell();
            showField();
            if( ! isGameFail() ) {  // немає більше ходів
                showFailDialog() ;
            }
        }
        else {
            Toast.makeText( GameActivity.this,
                    R.string.game_toast_no_move, Toast.LENGTH_SHORT ).show();
        }
    }

    private void showFailDialog() {
        new AlertDialog.Builder( this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setTitle( R.string.game_over )
                .setMessage( R.string.game_over_dialog )
                .setCancelable( false )
                .setPositiveButton( R.string.game_over_yes,
                        ( DialogInterface dialog, int whichButton ) -> startNewGame()
                )
                .setNegativeButton( R.string.game_over_no,
                        ( DialogInterface dialog, int whichButton ) -> finish()   // закрити активність
                )
                .setNeutralButton( R.string.game_over_undo,
                        ( DialogInterface dialog, int whichButton ) ->
                                dialog.dismiss()
                )
                .show();
    }

    private boolean move( MoveDirection direction ) {
        switch( direction ) {
            case BOTTOM: return moveBottom() ;
            case LEFT: return moveLeft() ;
            case RIGHT: return moveRight() ;
            case TOP: return moveTop() ;
        }
        return false ;
    }

    private boolean moveLeft() {
        boolean result = false ;
        // всі комірки (з числами)            [2024]   [2222]   [2022]
        // переміщуємо ліворуч одна-до-одної  [2240]   [2222]   [2220]
        // перевіряємо та колапсуємо          [4-40]   [4-4-]   [4-20]
        // пересуваємо після колапсу          [4400]   [4400]   [4200]

        // 1) пересування: оскільки невідомо відразу на скільки позицій буде пересування
        // [0020] [2020] [0202] будемо повторювати переміщення доки ситуація не зміниться
        boolean needRepeat ;
        for( int i = 0; i < N; i++ ) {  // цикл по рядах
            // проробляємо і-й ряд
            do {
                needRepeat = false;
                for( int j = 0; j < N - 1; j++ ) {
                    if( cells[ i ][ j ] == 0 && cells[ i ][ j + 1 ] != 0 ) {
                        cells[ i ][ j ] = cells[ i ][ j + 1 ];
                        cells[ i ][ j + 1 ] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while( needRepeat ) ;

            // 2) collapse
            for( int j = 0; j < N - 1; j++ ) {  // [2248]
                if( cells[ i ][ j ] != 0 &&
                        cells[ i ][ j ] == cells[ i ][ j + 1 ] ) {
                    cells[ i ][ j ] *= 2 ;

                    // призначаємо анімацію злиття для "збільшеної" комірки
                    tvCells[ i ][ j ].startAnimation( collapseCellsAnimation ) ;

                    score += cells[ i ][ j ] ;
                    // [4248] пересунути на місце зколапсованої комірки всі правіші
                    for( int k = j+1; k < N-1; k++ ) {
                        cells[ i ][ k ] = cells[ i ][ k + 1 ] ;
                    }
                    // [4488] на місце самої правої ставимо 0
                    cells[ i ][ N-1 ] = 0;  // [4480]
                    result = true;
                }
            }
        }
        return result ;
    }

    private boolean moveRight() {
        boolean result = false;
        boolean needRepeat ;
        for( int i = 0; i < N; i++ ) {  // цикл по рядах
            do {
                needRepeat = false;
                for( int j = N-1; j > 0; j-- ) {
                    if( cells[ i ][ j ] == 0 && cells[ i ][ j - 1 ] != 0 ) {
                        cells[ i ][ j ] = cells[ i ][ j - 1 ];
                        cells[ i ][ j - 1 ] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while( needRepeat ) ;

            for( int j = N-1; j > 0; j-- ) {  // [8422]
                if( cells[ i ][ j ] != 0 &&
                        cells[ i ][ j ] == cells[ i ][ j - 1 ] ) {
                    cells[ i ][ j ] *= 2 ;
                    score += cells[ i ][ j ] ;
                    // [8424] пересунути на місце зколапсованої комірки
                    for( int k = j-1; k > 0; k-- ) {
                        cells[ i ][ k ] = cells[ i ][ k - 1 ] ;
                    }
                    // [8844] на місце крайньої ставимо 0
                    cells[ i ][ 0 ] = 0;  // [0844]
                    result = true;
                }
            }
        }
        return result ;
    }

    private boolean moveTop() {
        boolean result = false;
        boolean needRepeat;
        for( int j = 0; j < N; j++ ) {
            do {
                needRepeat = false;
                for( int i = 0; i < N - 1; i++ ) {
                    if( cells[ i ][ j ] == 0 && cells[ i + 1 ][ j ] != 0 ) {
                        cells[ i ][ j ] = cells[ i + 1 ][ j ];
                        cells[ i + 1 ][ j ] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while( needRepeat );
            for( int i = 0; i < N - 1; i++ ) {
                if( cells[ i ][ j ] != 0 && cells[ i ][ j ] == cells[ i + 1 ][ j ] ) {
                    cells[ i ][ j ] *= 2;
                    //Animation
                    tvCells[ i ][ j ].startAnimation( collapseCellsAnimation );
                    score += cells[ i ][ j ];
                    for( int k = j + 1; k < N - 1; k++ ) {
                        cells[ k ][ j ] = cells[ k + 1 ][ j ];
                    }
                    cells[ N - 1 ][ j ] = 0;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean moveBottom() {
        boolean result = false;
        boolean needRepeat;
        for( int j = 0; j < N; j++ ) {
            do {
                needRepeat = false;
                for( int i = N - 1; i > 0; i-- ) {
                    if( cells[ i ][ j ] == 0 && cells[ i - 1 ][ j ] != 0 ) {
                        cells[ i ][ j ] = cells[ i - 1 ][ j ];
                        cells[ i - 1 ][ j ] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while( needRepeat );
            for( int i = N - 1; i > 0; i-- ) {
                if( cells[ i ][ j ] != 0 && cells[ i ][ j ] == cells[ i - 1 ][ j ] ) {
                    cells[ i ][ j ] *= 2;
                    tvCells[ i ][ j ].startAnimation( collapseCellsAnimation );
                    score += cells[ i ][ j ];
                    for( int k = i - 1; k > 0; k-- ) {
                        cells[ k ][ j ] = cells[ k - 1 ][ j ];
                    }
                    cells[ 0 ][ j ] = 0;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean isGameFail() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    return false ;
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N-1; j++) {
                if (cells[i][j] == cells[i][j + 1]) {
                    return false ;
                }
            }
        }
        for (int i = 0; i < N-1; i++) {
            for (int j = 0; j < N; j++)  {
                if (cells[i][j] == cells[i + 1][j]) {
                    return false ;
                }
            }
        }
        return true ;
    }

    private enum MoveDirection {
        BOTTOM,
        LEFT,
        RIGHT,
        TOP
    }
}
/*
Анімації (double-anim) - плавні переходи числових параметрів
між початковим та кінцевим значеннями. Закладаються декларативно (у xml)
та проробляються ОС.
Створюємо ресурсну папку (anim, назва важлива)
у ній - game_spawn_cell.xml (див. коментарі у ньому)
Завантажуємо анімацію (onCreate)  та ініціалізуємо її
Призначаємо (викликаємо) анімацію при появі комірки (див. spawnCell)
 */
/*
Car{
    @WHERE(" IS NOT NULL ")
    cond,
    @WHERE(" = 'robot' " )
    automat,
    ... }
Request{ cond=true, }
sql = SELECT * FROM cars WHERE is_sold = true

        for( Field field : Car.class.getDeclaredFields() ) {
            String par = field.getName() // cond, automat,
        for( par : {"cond", "automat"})
            if( req.getParameter(par) != null ) {
              sql += " AND " + par + field.getAnnotation( WHERE.class )
            }
        }
        if( req.getParameter("min-price") != null ) {
              sql += " AND price >= " + req.getParameter("min-price")
            }

        SELECT * FROM cars WHERE is_sold = true AND cond IS NOT NULL AND automat IS NOT NULL
 */