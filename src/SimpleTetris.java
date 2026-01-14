import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

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

    // 7種類のミノの形状データ (3次元配列)
    private final int[][][] MINOS = {
            {{1, 1, 1, 1}},           // I型 (1番)
            {{2, 2}, {2, 2}},         // O型 (2番)
            {{0, 3, 0}, {3, 3, 3}},   // T型 (3番)
            {{4, 4, 0}, {0, 4, 4}},   // S型 (4番)
            {{0, 5, 5}, {5, 5, 0}},   // Z型 (5番)
            {{6, 0, 0}, {6, 6, 6}},   // J型 (6番)
            {{0, 0, 7}, {7, 7, 7}}    // L型 (7番)
    };

    private Color getColor(int type) {
        switch (type) {
            case 1: return Color.CYAN;   // I
            case 2: return Color.YELLOW; // O
            case 3: return new Color(128, 0, 128); // T (紫)
            case 4: return Color.RED;  // S
            case 5: return Color.GREEN;    // Z
            case 6: return Color.BLUE;   // J
            case 7: return Color.ORANGE; // L
            default: return Color.BLACK;
        }
    }

    // 確認用にL字ブロックに変更
    private int[][] minoShape = {
            {1, 0},
            {1, 0},
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
                    case KeyEvent.VK_LEFT:  moveMino(-1, 0); break;
                    case KeyEvent.VK_RIGHT: moveMino(1, 0);  break;
                    case KeyEvent.VK_DOWN:  moveMino(0, 1);  break;
                    // --- ここから追加 ---
                    case KeyEvent.VK_Z:     rotateMinoLeft();  break; // Zで左回転
                    case KeyEvent.VK_X:     rotateMinoRight(); break; // Xで右回転
                    // --- ここまで ---
                }
                repaint();
            }
        });

        // ゲームループ開始 (500ミリ秒ごとにactionPerformedを呼ぶ)
        timer = new Timer(500, this);
        timer.start();
        spawnMino(); // 最初のミノを生成
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

        // 1. 固定されたブロックを描画
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (board[y][x] > 0) {
                    g.setColor(getColor(board[y][x])); // 数字に合わせた色を取得
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
                }
            }
        }

        // 2. 現在落ちているブロックを描画
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] > 0) {
                    g.setColor(getColor(minoShape[y][x])); // 数字に合わせた色を取得
                    int drawX = (currentX + x) * TILE_SIZE;
                    int drawY = (currentY + y) * TILE_SIZE;
                    g.fillRect(drawX, drawY, TILE_SIZE - 1, TILE_SIZE - 1);
                }
            }
        }
    }

    // --- ヘルパーメソッド（判定や処理） ---

    // --- 回転用メソッド ---

    // 右回転 (時計回り)
    private void rotateMinoRight() {
        int[][] newShape = rotate(true);
        applyRotation(newShape);
    }

    // 左回転 (反時計回り)
    private void rotateMinoLeft() {
        int[][] newShape = rotate(false);
        applyRotation(newShape);
    }

    // 行列の入れ替え計算（回転の実体）
    private int[][] rotate(boolean clockwise) {
        int rows = minoShape.length;
        int cols = minoShape[0].length;
        int[][] newShape = new int[cols][rows]; // 行と列を入れ替えた新しい配列を作る

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (clockwise) {
                    // 右回転の計算
                    newShape[x][rows - 1 - y] = minoShape[y][x];
                } else {
                    // 左回転の計算
                    newShape[cols - 1 - x][y] = minoShape[y][x];
                }
            }
        }
        return newShape;
    }

    // 回転できるか判定して、OKなら適用する
    private void applyRotation(int[][] newShape) {
        int[][] oldShape = minoShape; // 失敗した時のために今の形を保存
        minoShape = newShape;         // いったん新しい形にする

        if (!canMove(currentX, currentY)) {
            // もし壁やブロックにめり込んだら、元に戻す
            minoShape = oldShape;
        }
    }
    // 移動可能かチェックするメソッド
    private boolean canMove(int newX, int newY) {
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] > 0) { // 0より大きい（ミノがある）場所をチェック
                    int targetX = newX + x;
                    int targetY = newY + y;

                    // 画面の左右、および「下」の境界線チェック
                    if (targetX < 0 || targetX >= COLS || targetY >= ROWS) {
                        return false; // 画面外なら移動不可
                    }
                    // すでに固定されたブロックとの衝突チェック
                    if (targetY >= 0 && board[targetY][targetX] > 0) {
                        return false; // ブロックがあれば移動不可
                    }
                }
            }
        }
        return true; // どこにもぶつからなければ移動OK
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
                if (minoShape[y][x] > 0) {
                    board[currentY + y][currentX + x] = minoShape[y][x]; // 1じゃなく数字を保存
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

    private void spawnMino() {
        Random rand = new Random();
        // 7種類の中からランダムに1つ選ぶ
        int index = rand.nextInt(MINOS.length);
        minoShape = MINOS[index];

        // 出現位置を中央上にリセット
        currentX = 4;
        currentY = 0;

        // もし出現した瞬間に動けなければゲームオーバー
        if (!canMove(currentX, currentY)) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
            // 盤面をリセット
            board = new int[ROWS][COLS];
            spawnMino();
            timer.start();
        }
    }
}