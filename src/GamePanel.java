import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // --- 設定値 (GameSettingsから取得) ---
    private GameSettings settings;
    private int TILE_SIZE;
    private int COLS;
    private int ROWS;

    // --- ゲームの状態 ---
    private Timer timer;
    private int[][] board;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // 現在落ちているブロックの情報
    private int currentX = 4;
    private int currentY = 0;

    // 7種類のミノの形状データ
    private final int[][][] MINOS = {
            {{1, 1, 1, 1}},           // I型
            {{2, 2}, {2, 2}},         // O型
            {{0, 3, 0}, {3, 3, 3}},   // T型
            {{4, 4, 0}, {0, 4, 4}},   // S型
            {{0, 5, 5}, {5, 5, 0}},   // Z型
            {{6, 0, 0}, {6, 6, 6}},   // J型
            {{0, 0, 7}, {7, 7, 7}}    // L型
    };

    private Color getColor(int type) {
        switch (type) {
            case 1: return Color.CYAN;
            case 2: return Color.YELLOW;
            case 3: return new Color(128, 0, 128);
            case 4: return Color.RED;
            case 5: return Color.GREEN;
            case 6: return Color.BLUE;
            case 7: return Color.ORANGE;
            default: return Color.BLACK;
        }
    }

    private int[][] minoShape;

    public GamePanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.settings = GameSettings.getInstance();
        
        // 設定から値を取得
        TILE_SIZE = settings.getTileSize();
        COLS = settings.getBoardCols();
        ROWS = settings.getBoardRows();
        board = new int[ROWS][COLS];

        // 最小サイズを設定（リサイズ可能にするため固定サイズは設定しない）
        setMinimumSize(new Dimension(COLS * 20, ROWS * 20));
        setBackground(Color.BLACK);
        setFocusable(true);

        // キー操作のリスナーを追加（設定に対応）
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                
                // 設定されたキーと比較
                if (keyCode == settings.getKeyLeft()) {
                    moveMino(-1, 0);
                } else if (keyCode == settings.getKeyRight()) {
                    moveMino(1, 0);
                } else if (keyCode == settings.getKeyDown()) {
                    moveMino(0, 1);
                } else if (keyCode == settings.getKeyRotateRight()) {
                    rotateMinoRight();
                } else if (keyCode == settings.getKeyRotateLeft()) {
                    rotateMinoLeft();
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    // ESCキーでタイトルに戻る
                    pauseGame();
                    int result = JOptionPane.showConfirmDialog(
                        GamePanel.this,
                        "タイトル画面に戻りますか？",
                        "一時停止",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        resetGame();
                        cardLayout.show(mainPanel, "title");
                    } else {
                        resumeGame();
                    }
                }
                
                repaint();
            }
        });

        // ゲームループ開始（最初は停止状態）
        timer = new Timer(settings.getGameSpeed(), this);
        // timer.start(); // 自動開始しない
        // spawnMino(); // 自動開始しない
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (canMove(currentX, currentY + 1)) {
            currentY++;
        } else {
            fixMino();
            checkLines();
            spawnMino();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // ウィンドウサイズに合わせてタイルサイズを動的に計算
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // アスペクト比を保ちながらタイルサイズを計算
        int tileWidth = panelWidth / COLS;
        int tileHeight = panelHeight / ROWS;
        int dynamicTileSize = Math.min(tileWidth, tileHeight);
        
        // ゲームエリアを中央に配置するためのオフセットを計算
        int offsetX = (panelWidth - (COLS * dynamicTileSize)) / 2;
        int offsetY = (panelHeight - (ROWS * dynamicTileSize)) / 2;

        // 固定されたブロックを描画
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (board[y][x] > 0) {
                    g.setColor(getColor(board[y][x]));
                    g.fillRect(
                        offsetX + x * dynamicTileSize, 
                        offsetY + y * dynamicTileSize, 
                        dynamicTileSize - 1, 
                        dynamicTileSize - 1
                    );
                }
            }
        }

        // 現在落ちているブロックを描画
        if (minoShape != null) {
            for (int y = 0; y < minoShape.length; y++) {
                for (int x = 0; x < minoShape[0].length; x++) {
                    if (minoShape[y][x] > 0) {
                        g.setColor(getColor(minoShape[y][x]));
                        int drawX = offsetX + (currentX + x) * dynamicTileSize;
                        int drawY = offsetY + (currentY + y) * dynamicTileSize;
                        g.fillRect(drawX, drawY, dynamicTileSize - 1, dynamicTileSize - 1);
                    }
                }
            }
        }
        
        // グリッド線を描画
        g.setColor(new Color(40, 40, 40));
        for (int i = 0; i <= ROWS; i++) {
            g.drawLine(
                offsetX, 
                offsetY + i * dynamicTileSize, 
                offsetX + COLS * dynamicTileSize, 
                offsetY + i * dynamicTileSize
            );
        }
        for (int i = 0; i <= COLS; i++) {
            g.drawLine(
                offsetX + i * dynamicTileSize, 
                offsetY, 
                offsetX + i * dynamicTileSize, 
                offsetY + ROWS * dynamicTileSize
            );
        }
    }

    // 右回転
    private void rotateMinoRight() {
        int[][] newShape = rotate(true);
        applyRotation(newShape);
    }

    // 左回転
    private void rotateMinoLeft() {
        int[][] newShape = rotate(false);
        applyRotation(newShape);
    }

    // 行列の入れ替え計算
    private int[][] rotate(boolean clockwise) {
        int rows = minoShape.length;
        int cols = minoShape[0].length;
        int[][] newShape = new int[cols][rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (clockwise) {
                    newShape[x][rows - 1 - y] = minoShape[y][x];
                } else {
                    newShape[cols - 1 - x][y] = minoShape[y][x];
                }
            }
        }
        return newShape;
    }

    // 回転できるか判定して適用
    private void applyRotation(int[][] newShape) {
        int[][] oldShape = minoShape;
        minoShape = newShape;

        if (!canMove(currentX, currentY)) {
            minoShape = oldShape;
        }
    }

    // 移動可能かチェック
    private boolean canMove(int newX, int newY) {
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] > 0) {
                    int targetX = newX + x;
                    int targetY = newY + y;

                    if (targetX < 0 || targetX >= COLS || targetY >= ROWS) {
                        return false;
                    }
                    if (targetY >= 0 && board[targetY][targetX] > 0) {
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

    // ブロックを固定
    private void fixMino() {
        for (int y = 0; y < minoShape.length; y++) {
            for (int x = 0; x < minoShape[0].length; x++) {
                if (minoShape[y][x] > 0) {
                    board[currentY + y][currentX + x] = minoShape[y][x];
                }
            }
        }
    }

    // ラインチェック
    private void checkLines() {
        for (int y = ROWS - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < COLS; x++) {
                if (board[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int row = y; row > 0; row--) {
                    System.arraycopy(board[row - 1], 0, board[row], 0, COLS);
                }
                board[0] = new int[COLS];
                y++;
            }
        }
    }

    // 新しいミノを生成
    private void spawnMino() {
        Random rand = new Random();
        int index = rand.nextInt(MINOS.length);
        minoShape = MINOS[index];

        currentX = COLS / 2 - 1;
        currentY = 0;

        if (!canMove(currentX, currentY)) {
            timer.stop();
            int result = JOptionPane.showConfirmDialog(
                this,
                "Game Over!\nもう一度プレイしますか？",
                "ゲームオーバー",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                resetGame();
                timer.start();
            } else {
                cardLayout.show(mainPanel, "title");
            }
        }
    }
    
    // ゲームをリセット
    private void resetGame() {
        board = new int[ROWS][COLS];
        currentX = COLS / 2 - 1;
        currentY = 0;
        spawnMino();
    }
    
    // ゲームを一時停止
    public void pauseGame() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    // ゲームを再開
    public void resumeGame() {
        if (timer != null) {
            timer.start();
        }
    }
    
    // ゲームを開始
    public void startGame() {
        resetGame();
        if (timer != null) {
            timer.start();
        }
        requestFocus();
    }
}
