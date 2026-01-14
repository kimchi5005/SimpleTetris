import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TitlePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SimpleTetris parentFrame;
    
    public TitlePanel(CardLayout cardLayout, JPanel mainPanel, SimpleTetris parentFrame) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 40));
        
        // タイトル部分
        JPanel titleArea = new JPanel();
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        titleArea.setOpaque(false);
        titleArea.setBorder(BorderFactory.createEmptyBorder(80, 0, 40, 0));
        
        JLabel titleLabel = new JLabel("SIMPLE TETRIS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("テトリスで遊ぼう！");
        subtitleLabel.setFont(new Font("Yu Gothic", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleArea.add(titleLabel);
        titleArea.add(Box.createRigidArea(new Dimension(0, 20)));
        titleArea.add(subtitleLabel);
        
        // ボタン部分
        JPanel buttonArea = new JPanel();
        buttonArea.setLayout(new BoxLayout(buttonArea, BoxLayout.Y_AXIS));
        buttonArea.setOpaque(false);
        buttonArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        
        // ボタンを作成
        JButton startButton = createMenuButton("ゲームスタート");
        JButton settingsButton = createMenuButton("設定");
        JButton exitButton = createMenuButton("終了");
        
        // ボタンのアクションを設定
        startButton.addActionListener(e -> {
            parentFrame.startNewGame();
        });
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "settings"));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonArea.add(startButton);
        buttonArea.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonArea.add(settingsButton);
        buttonArea.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonArea.add(exitButton);
        
        // 操作説明
        JPanel infoArea = new JPanel();
        infoArea.setOpaque(false);
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
        infoArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel infoLabel1 = new JLabel("矢印キー: 移動 / Z: 左回転 / X: 右回転");
        infoLabel1.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel1.setForeground(new Color(200, 200, 200));
        infoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel2 = new JLabel("F11: フルスクリーン切り替え / ESC: 一時停止");
        infoLabel2.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel2.setForeground(new Color(200, 200, 200));
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel3 = new JLabel("ウィンドウサイズはドラッグで自由に変更可能");
        infoLabel3.setFont(new Font("Yu Gothic", Font.PLAIN, 12));
        infoLabel3.setForeground(new Color(150, 150, 150));
        infoLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoArea.add(infoLabel1);
        infoArea.add(Box.createRigidArea(new Dimension(0, 5)));
        infoArea.add(infoLabel2);
        infoArea.add(Box.createRigidArea(new Dimension(0, 5)));
        infoArea.add(infoLabel3);
        
        add(titleArea, BorderLayout.NORTH);
        add(buttonArea, BorderLayout.CENTER);
        add(infoArea, BorderLayout.SOUTH);
    }
    
    // メニューボタンを作成するヘルパーメソッド
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Yu Gothic", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ボタンのスタイル
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 100));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        
        // ホバー効果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 120));
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 100));
                button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
            }
        });
        
        return button;
    }
}
