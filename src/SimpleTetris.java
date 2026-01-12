import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleTetris extends JFrame {

    public SimpleTetris() {
        setTitle("Simple Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ゲーム画面を追加
        add(new GamePanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SimpleTetris();
    }
}

class GamePanel extends JPanel implements ActionListener {
    // --- 設定値 ---
    private final int TILE_SIZE = 30; // ブロックの大きさ
    private final int COLS = 10;      // 横のマス数
    private final int ROWS = 20;      // 縦のマス数

    // --- ゲームの状態 ---
    private Timer timer;
    // 落ちきったブロックを記録する地図 (0:なし, 1:ブロックあり)
    private int[][] board = new int[ROWS][COLS];

    // 現在落ちているブロックの情報
    private int currentX = 4; // 開始位置(横)
    private int currentY = 0; // 開始位置(縦)

    // テスト用の四角いブロック形状
    private final int[][] minoShape = {
            {1, 1},
            {1, 1}
    };

    public GamePanel() {
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true); // キー入力を受け付けるようにする

        // キー操作のリスナーを追加
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  moveMino(-1, 0); break; // 左へ
                    case KeyEvent.VK_RIGHT: moveMino(1, 0);  break; // 右へ
                    case KeyEvent.VK_DOWN:  moveMino(0, 1);  break; // 下へ加速
                }
                repaint(); // 画面を再描画
            }
        });

        // ゲームループ開始 (500ミリ秒ごとにactionPerformedを呼ぶ)
        timer = new Timer(500, this);
        timer.start();
    }

    // --- ゲームのメインループ (心臓部) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // 下に移動できるかチェック
        if (canMove(currentX, currentY + 1)) {
            currentY++; // 移動できるなら下へ
        } else {
            // 移動できない＝床や他のブロックに当たった
            fixMino();     // ブロックを固定
            checkLines();  // ラインが揃ったか確認
            spawnMino();   // 新しいブロックを出す
        }
        repaint(); // 画面更新
    }

    // --- 描画処理 ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. 固定されたブロック（背景）を描画
        g.setColor(Color.GRAY);
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (board[y][x] == 1) {
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE); // 枠線
                }
            }
        }

        // 2. 現在落ちているブロックを描画
        g.setColor(Color.CYAN);
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] == 1) {
                    int drawX = (currentX + x) * TILE_SIZE;
                    int drawY = (currentY + y) * TILE_SIZE;
                    g.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    // --- ヘルパーメソッド（判定や処理） ---

    // 移動可能かチェックするメソッド
    private boolean canMove(int newX, int newY) {
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] == 1) {
                    int targetX = newX + x;
                    int targetY = newY + y;

                    // 1. 壁の外に出ていないか？
                    if (targetX < 0 || targetX >= COLS || targetY >= ROWS) {
                        return false;
                    }
                    // 2. すでにブロックがある場所ではないか？
                    if (targetY >= 0 && board[targetY][targetX] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 移動処理
    private void moveMino(int dx, int dy) {
        if (canMove(currentX + dx, currentY + dy)) {
            currentX += dx;
            currentY += dy;
        }
    }

    // ブロックをフィールドに固定する
    private void fixMino() {
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] == 1) {
                    board[currentY + y][currentX + x] = 1;
                }
            }
        }
    }

    // ラインが揃ったかチェックして消す
    private void checkLines() {
        for (int y = ROWS - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < COLS; x++) {
                if (board[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            // 揃っていたら行を消して、上の行をずらす
            if (full) {
                for (int row = y; row > 0; row--) {
                    System.arraycopy(board[row - 1], 0, board[row], 0, COLS);
                }
                // 一番上の行を空にする
                board[0] = new int[COLS];
                y++; // 同じ行を再チェック（落ちてきた行も揃っている可能性があるため）
            }
        }
    }

    // 新しいブロックを上に出現させる
    private void spawnMino() {
        currentX = 4;
        currentY = 0;
        // 本来はここでゲームオーバー判定を行う
    }
}