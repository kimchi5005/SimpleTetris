import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class SimpleTetris extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private boolean isFullScreen = false;
    private Rectangle normalBounds; // フルスクリーン前のウィンドウサイズを保存

    public SimpleTetris() {
        setTitle("Simple Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true); // リサイズ可能にする

        // CardLayoutで画面を切り替え
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 各画面を作成して追加
        TitlePanel titlePanel = new TitlePanel(cardLayout, mainPanel, this);
        SettingsPanel settingsPanel = new SettingsPanel(cardLayout, mainPanel);
        gamePanel = new GamePanel(cardLayout, mainPanel);

        mainPanel.add(titlePanel, "title");
        mainPanel.add(settingsPanel, "settings");
        mainPanel.add(gamePanel, "game");
        
        // 初期サイズを控えめに設定（どのモニターでも表示できるサイズ）
        mainPanel.setPreferredSize(new Dimension(800, 600));

        // 最初はタイトル画面を表示
        cardLayout.show(mainPanel, "title");

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        // F11キーでフルスクリーン切り替え
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                }
            }
        });
        
        // タイトル画面にフォーカスを当てる
        titlePanel.requestFocusInWindow();
    }
    
    // フルスクリーン切り替え
    public void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (!isFullScreen) {
            // 通常モード→フルスクリーンモード
            normalBounds = getBounds(); // 現在のサイズを保存
            dispose(); // ウィンドウを一時的に破棄
            setUndecorated(true); // タイトルバーを非表示
            setResizable(false); // リサイズ不可
            
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(this);
            } else {
                // フルスクリーン非対応の場合は最大化
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            
            setVisible(true);
            isFullScreen = true;
        } else {
            // フルスクリーンモード→通常モード
            if (device.getFullScreenWindow() == this) {
                device.setFullScreenWindow(null);
            }
            dispose();
            setUndecorated(false); // タイトルバーを表示
            setResizable(true); // リサイズ可能
            setBounds(normalBounds); // 元のサイズに戻す
            setVisible(true);
            isFullScreen = false;
        }
        
        // フォーカスを戻す
        requestFocus();
    }
    
    // ゲームを開始するメソッド
    public void startNewGame() {
        gamePanel.startGame();
        cardLayout.show(mainPanel, "game");
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        // Look and Feelを設定（オプション）
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // デフォルトのLook and Feelを使用
        }
        
        SwingUtilities.invokeLater(() -> new SimpleTetris());
    }
}
