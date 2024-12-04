package com.example.nonograms;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 8;
    private static final int HINT_AREA_SIZE = 3;
    private static final int INITIAL_LIFE = 3;

    private Life life;
    private TableLayout tableLayout;
    private TextView statusTextView;
    private ToggleButton toggleButton;
    private TextView timerTextView;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        LinearLayout lifeContainer = findViewById(R.id.lifeContainer); // 생명 UI 컨테이너
        statusTextView = findViewById(R.id.statusTextView);
        toggleButton = findViewById(R.id.toggleButton);
        timerTextView = findViewById(R.id.timerTextView);

        life = new Life(INITIAL_LIFE, lifeContainer, this);

        setupGame();
        startTimer(30000); // 30초 제한 시간

        findViewById(R.id.restartButton).setOnClickListener(v -> resetGame());

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            statusTextView.setText(isChecked ? "X 표시 모드" : "검정 사각형 찾기 모드");
        });
    }

    private void setupGame() {
        tableLayout.removeAllViews();
        Cell.resetNumBlackSquares();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int cellSize = Math.min(metrics.widthPixels, metrics.heightPixels) / (BOARD_SIZE + HINT_AREA_SIZE + 1);

        for (int i = 0; i < BOARD_SIZE; i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i < HINT_AREA_SIZE || j < HINT_AREA_SIZE) {
                    TextView hintView = createHintView(cellSize);
                    tableRow.addView(hintView);
                } else {
                    Cell cell = createCell(cellSize);
                    cell.setOnClickListener(v -> handleCellClick(cell));
                    tableRow.addView(cell);
                }
            }
            tableLayout.addView(tableRow);
        }

        updateHints();
    }

    private TextView createHintView(int cellSize) {
        TextView hintView = new TextView(this);
        hintView.setGravity(Gravity.CENTER);
        hintView.setTextSize(14);
        hintView.setTextColor(getResources().getColor(android.R.color.black));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(cellSize, cellSize);
        layoutParams.setMargins(5, 5, 5, 5);
        hintView.setLayoutParams(layoutParams);
        return hintView;
    }

    private Cell createCell(int cellSize) {
        Cell cell = new Cell(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(cellSize, cellSize);
        layoutParams.setMargins(5, 5, 5, 5);
        cell.setLayoutParams(layoutParams);
        return cell;
    }

    private void handleCellClick(Cell cell) {
        if (!cell.isClickable()) return;

        if (toggleButton.isChecked()) {
            cell.toggleX();
        } else {
            if (cell.markBlackSquare()) {
                statusTextView.setText("성공!");
                if (Cell.getNumBlackSquares() == 0) {
                    endGame("승리!");
                }
            } else {
                life.decreaseLife();
                statusTextView.setText("실패!");
                if (life.isGameOver()) {
                    endGame("패배!");
                }
            }
        }
    }

    private void updateHints() {
        for (int i = HINT_AREA_SIZE; i < BOARD_SIZE; i++) {
            updateHintForRowOrColumn(i, true);
            updateHintForRowOrColumn(i, false);
        }
    }

    private void updateHintForRowOrColumn(int index, boolean isRow) {
        int[] hints = calculateHints(index, isRow);
        for (int i = 0; i < HINT_AREA_SIZE; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(isRow ? index : i);
            TextView hintView = (TextView) row.getChildAt(isRow ? i : index);

            if (hints[i] == 0) {
                hintView.setText(""); // 빈 문자열
            } else {
                hintView.setText(String.valueOf(hints[i])); // 숫자 표시
            }
        }
    }

    private int[] calculateHints(int index, boolean isRow) {
        int[] hints = new int[HINT_AREA_SIZE];
        int count = 0, hintIndex = 0;

        for (int i = HINT_AREA_SIZE; i < BOARD_SIZE; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(isRow ? index : i);
            Cell cell = (Cell) row.getChildAt(isRow ? i : index);

            if (cell.isBlackSquare()) {
                count++;
            } else if (count > 0) {
                if (hintIndex < HINT_AREA_SIZE) hints[hintIndex++] = count;
                count = 0;
            }
        }
        if (count > 0 && hintIndex < HINT_AREA_SIZE) hints[hintIndex] = count;
        return hints;
    }

    private void resetGame() {
        life = new Life(INITIAL_LIFE, findViewById(R.id.lifeContainer), this);
        statusTextView.setText("새 게임 시작!");
        setupGame();
        startTimer(30000); // 30초 재시작
    }

    private void endGame(String message) {
        disableAllCells();
        if (timer != null) timer.cancel();

        new AlertDialog.Builder(this)
                .setTitle(message)
                .setMessage("다시 시작하시겠습니까?")
                .setPositiveButton("재시작", (dialog, which) -> resetGame())
                .setNegativeButton("나가기", (dialog, which) -> finish())
                .show();
    }

    private void disableAllCells() {
        for (int i = HINT_AREA_SIZE; i < BOARD_SIZE; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = HINT_AREA_SIZE; j < BOARD_SIZE; j++) {
                Cell cell = (Cell) row.getChildAt(j);
                cell.setClickable(false);
            }
        }
    }

    private void startTimer(long duration) {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("시간: " + millisUntilFinished / 1000 + "초");
            }

            @Override
            public void onFinish() {
                endGame("시간 종료!");
            }
        };
        timer.start();
    }
}
