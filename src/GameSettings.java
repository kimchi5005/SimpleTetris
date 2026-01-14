import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Properties;

public class GameSettings {
    // シングルトンパターン
    private static GameSettings instance;
    
    // キー設定
    private int keyLeft = KeyEvent.VK_LEFT;
    private int keyRight = KeyEvent.VK_RIGHT;
    private int keyDown = KeyEvent.VK_DOWN;
    private int keySoftDrop = KeyEvent.VK_SPACE;  // 今後の拡張用
    private int keyRotateRight = KeyEvent.VK_X;
    private int keyRotateLeft = KeyEvent.VK_Z;
    private int keyHold = KeyEvent.VK_C;  // 今後の拡張用（ホールド機能）
    
    // 音量設定（0.0～1.0）
    private float volumeMaster = 0.0f;  // 初期はミュート
    private float volumeBGM = 0.7f;
    private float volumeSE = 0.8f;
    
    // 画質設定
    private int tileSize = 30;  // ブロックのサイズ
    private int boardCols = 10; // 横のマス数
    private int boardRows = 20; // 縦のマス数
    private int screenWidth = 1920;  // 画面の幅
    private int screenHeight = 1080; // 画面の高さ
    
    // ゲーム設定
    private int gameSpeed = 500; // 落下速度（ミリ秒）
    
    private GameSettings() {
        loadSettings();
    }
    
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    // 設定をファイルから読み込む
    public void loadSettings() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("tetris_settings.properties")) {
            props.load(fis);
            
            keyLeft = Integer.parseInt(props.getProperty("keyLeft", String.valueOf(KeyEvent.VK_LEFT)));
            keyRight = Integer.parseInt(props.getProperty("keyRight", String.valueOf(KeyEvent.VK_RIGHT)));
            keyDown = Integer.parseInt(props.getProperty("keyDown", String.valueOf(KeyEvent.VK_DOWN)));
            keySoftDrop = Integer.parseInt(props.getProperty("keySoftDrop", String.valueOf(KeyEvent.VK_SPACE)));
            keyRotateRight = Integer.parseInt(props.getProperty("keyRotateRight", String.valueOf(KeyEvent.VK_X)));
            keyRotateLeft = Integer.parseInt(props.getProperty("keyRotateLeft", String.valueOf(KeyEvent.VK_Z)));
            keyHold = Integer.parseInt(props.getProperty("keyHold", String.valueOf(KeyEvent.VK_C)));
            
            volumeMaster = Float.parseFloat(props.getProperty("volumeMaster", "0.0"));
            volumeBGM = Float.parseFloat(props.getProperty("volumeBGM", "0.7"));
            volumeSE = Float.parseFloat(props.getProperty("volumeSE", "0.8"));
            
            tileSize = Integer.parseInt(props.getProperty("tileSize", "30"));
            boardCols = Integer.parseInt(props.getProperty("boardCols", "10"));
            boardRows = Integer.parseInt(props.getProperty("boardRows", "20"));
            gameSpeed = Integer.parseInt(props.getProperty("gameSpeed", "500"));
            screenWidth = Integer.parseInt(props.getProperty("screenWidth", "1920"));
            screenHeight = Integer.parseInt(props.getProperty("screenHeight", "1080"));
            
        } catch (IOException e) {
            // ファイルが無い場合はデフォルト値を使用
            System.out.println("設定ファイルが見つかりません。デフォルト設定を使用します。");
        }
    }
    
    // 設定をファイルに保存
    public void saveSettings() {
        Properties props = new Properties();
        
        props.setProperty("keyLeft", String.valueOf(keyLeft));
        props.setProperty("keyRight", String.valueOf(keyRight));
        props.setProperty("keyDown", String.valueOf(keyDown));
        props.setProperty("keySoftDrop", String.valueOf(keySoftDrop));
        props.setProperty("keyRotateRight", String.valueOf(keyRotateRight));
        props.setProperty("keyRotateLeft", String.valueOf(keyRotateLeft));
        props.setProperty("keyHold", String.valueOf(keyHold));
        
        props.setProperty("volumeMaster", String.valueOf(volumeMaster));
        props.setProperty("volumeBGM", String.valueOf(volumeBGM));
        props.setProperty("volumeSE", String.valueOf(volumeSE));
        
        props.setProperty("tileSize", String.valueOf(tileSize));
        props.setProperty("boardCols", String.valueOf(boardCols));
        props.setProperty("boardRows", String.valueOf(boardRows));
        props.setProperty("gameSpeed", String.valueOf(gameSpeed));
        props.setProperty("screenWidth", String.valueOf(screenWidth));
        props.setProperty("screenHeight", String.valueOf(screenHeight));
        
        try (FileOutputStream fos = new FileOutputStream("tetris_settings.properties")) {
            props.store(fos, "Tetris Game Settings");
            System.out.println("設定を保存しました。");
        } catch (IOException e) {
            System.err.println("設定の保存に失敗しました: " + e.getMessage());
        }
    }
    
    // キーコードを文字列に変換
    public static String keyCodeToString(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }
    
    // ゲッター・セッター
    public int getKeyLeft() { return keyLeft; }
    public void setKeyLeft(int key) { this.keyLeft = key; }
    
    public int getKeyRight() { return keyRight; }
    public void setKeyRight(int key) { this.keyRight = key; }
    
    public int getKeyDown() { return keyDown; }
    public void setKeyDown(int key) { this.keyDown = key; }
    
    public int getKeySoftDrop() { return keySoftDrop; }
    public void setKeySoftDrop(int key) { this.keySoftDrop = key; }
    
    public int getKeyRotateRight() { return keyRotateRight; }
    public void setKeyRotateRight(int key) { this.keyRotateRight = key; }
    
    public int getKeyRotateLeft() { return keyRotateLeft; }
    public void setKeyRotateLeft(int key) { this.keyRotateLeft = key; }
    
    public int getKeyHold() { return keyHold; }
    public void setKeyHold(int key) { this.keyHold = key; }
    
    public float getVolumeMaster() { return volumeMaster; }
    public void setVolumeMaster(float volume) { 
        this.volumeMaster = Math.max(0.0f, Math.min(1.0f, volume)); 
    }
    
    public float getVolumeBGM() { return volumeBGM; }
    public void setVolumeBGM(float volume) { 
        this.volumeBGM = Math.max(0.0f, Math.min(1.0f, volume)); 
    }
    
    public float getVolumeSE() { return volumeSE; }
    public void setVolumeSE(float volume) { 
        this.volumeSE = Math.max(0.0f, Math.min(1.0f, volume)); 
    }
    
    public int getTileSize() { return tileSize; }
    public void setTileSize(int size) { this.tileSize = size; }
    
    public int getBoardCols() { return boardCols; }
    public void setBoardCols(int cols) { this.boardCols = cols; }
    
    public int getBoardRows() { return boardRows; }
    public void setBoardRows(int rows) { this.boardRows = rows; }
    
    public int getGameSpeed() { return gameSpeed; }
    public void setGameSpeed(int speed) { this.gameSpeed = speed; }
    
    public int getScreenWidth() { return screenWidth; }
    public void setScreenWidth(int width) { this.screenWidth = width; }
    
    public int getScreenHeight() { return screenHeight; }
    public void setScreenHeight(int height) { this.screenHeight = height; }
}
