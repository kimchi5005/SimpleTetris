import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SettingsPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameSettings settings;
    
    // キー設定用のラベル
    private JLabel keyLeftLabel;
    private JLabel keyRightLabel;
    private JLabel keyDownLabel;
    private JLabel keyRotateRightLabel;
    private JLabel keyRotateLeftLabel;
    
    // 音量スライダー
    private JSlider masterVolumeSlider;
    private JSlider bgmVolumeSlider;
    private JSlider seVolumeSlider;
    
    public SettingsPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.settings = GameSettings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 40));
        
        // タイトル
        JLabel titleLabel = new JLabel("設定");
        titleLabel.setFont(new Font("Yu Gothic", Font.BOLD, 32));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // タブパネル
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Yu Gothic", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(30, 30, 50));
        tabbedPane.setForeground(Color.WHITE);
        
        // 各タブを追加
        tabbedPane.addTab("キー設定", createKeyConfigPanel());
        tabbedPane.addTab("音量設定", createVolumePanel());
        tabbedPane.addTab("画質設定", createGraphicsPanel());
        
        // 下部のボタン
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(new Color(20, 20, 40));
        
        JButton saveButton = createStyledButton("保存");
        JButton backButton = createStyledButton("戻る");
        
        saveButton.addActionListener(e -> {
            settings.saveSettings();
            JOptionPane.showMessageDialog(this, "設定を保存しました", "保存完了", JOptionPane.INFORMATION_MESSAGE);
        });
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "title"));
        
        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // キー設定パネル
    private JPanel createKeyConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel infoLabel = new JLabel("変更したいキーのボタンをクリックして、新しいキーを押してください");
        infoLabel.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel.setForeground(Color.YELLOW);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // 各キーの設定行を追加
        panel.add(createKeyConfigRow("左移動:", settings.getKeyLeft(), key -> {
            settings.setKeyLeft(key);
            keyLeftLabel.setText(GameSettings.keyCodeToString(key));
        }));
        
        panel.add(createKeyConfigRow("右移動:", settings.getKeyRight(), key -> {
            settings.setKeyRight(key);
            keyRightLabel.setText(GameSettings.keyCodeToString(key));
        }));
        
        panel.add(createKeyConfigRow("下移動:", settings.getKeyDown(), key -> {
            settings.setKeyDown(key);
            keyDownLabel.setText(GameSettings.keyCodeToString(key));
        }));
        
        panel.add(createKeyConfigRow("右回転:", settings.getKeyRotateRight(), key -> {
            settings.setKeyRotateRight(key);
            keyRotateRightLabel.setText(GameSettings.keyCodeToString(key));
        }));
        
        panel.add(createKeyConfigRow("左回転:", settings.getKeyRotateLeft(), key -> {
            settings.setKeyRotateLeft(key);
            keyRotateLeftLabel.setText(GameSettings.keyCodeToString(key));
        }));
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    // キー設定の1行を作成
    private JPanel createKeyConfigRow(String label, int initialKey, KeySetter setter) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBackground(new Color(30, 30, 50));
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Yu Gothic", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setPreferredSize(new Dimension(120, 30));
        
        JLabel keyLabel = new JLabel(GameSettings.keyCodeToString(initialKey));
        keyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        keyLabel.setForeground(Color.CYAN);
        keyLabel.setPreferredSize(new Dimension(150, 30));
        
        // どのキーのラベルかを記録
        if (label.contains("左移動")) keyLeftLabel = keyLabel;
        else if (label.contains("右移動")) keyRightLabel = keyLabel;
        else if (label.contains("下移動")) keyDownLabel = keyLabel;
        else if (label.contains("右回転")) keyRotateRightLabel = keyLabel;
        else if (label.contains("左回転")) keyRotateLeftLabel = keyLabel;
        
        JButton changeButton = new JButton("変更");
        changeButton.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        styleButton(changeButton);
        
        changeButton.addActionListener(e -> {
            changeButton.setText("キーを押してください...");
            changeButton.setEnabled(false);
            
            // ダイアログを使ってキー入力を受け取る
            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "キー入力待ち", true);
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(changeButton);
            
            JLabel dialogLabel = new JLabel("新しいキーを押してください", SwingConstants.CENTER);
            dialogLabel.setFont(new Font("Yu Gothic", Font.BOLD, 16));
            dialog.add(dialogLabel);
            
            dialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent ke) {
                    setter.setKey(ke.getKeyCode());
                    changeButton.setText("変更");
                    changeButton.setEnabled(true);
                    dialog.dispose();
                }
            });
            
            // ダイアログを表示
            SwingUtilities.invokeLater(() -> {
                dialog.setVisible(true);
            });
        });
        
        row.add(nameLabel);
        row.add(keyLabel);
        row.add(changeButton);
        
        return row;
    }
    
    // 音量設定パネル
    private JPanel createVolumePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // マスター音量
        panel.add(createVolumeRow("マスター音量:", settings.getVolumeMaster(), slider -> {
            masterVolumeSlider = slider;
            slider.addChangeListener(e -> {
                settings.setVolumeMaster(slider.getValue() / 100f);
            });
        }));
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // BGM音量
        panel.add(createVolumeRow("BGM音量:", settings.getVolumeBGM(), slider -> {
            bgmVolumeSlider = slider;
            slider.addChangeListener(e -> {
                settings.setVolumeBGM(slider.getValue() / 100f);
            });
        }));
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // 効果音音量
        panel.add(createVolumeRow("効果音音量:", settings.getVolumeSE(), slider -> {
            seVolumeSlider = slider;
            slider.addChangeListener(e -> {
                settings.setVolumeSE(slider.getValue() / 100f);
            });
        }));
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    // 音量設定の1行を作成
    private JPanel createVolumeRow(String label, float initialVolume, SliderCreator creator) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.setBackground(new Color(30, 30, 50));
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Yu Gothic", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBackground(new Color(30, 30, 50));
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JSlider slider = new JSlider(0, 100, (int)(initialVolume * 100));
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(new Color(30, 30, 50));
        slider.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(String.format("%d%%", (int)(initialVolume * 100)));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(Color.CYAN);
        valueLabel.setPreferredSize(new Dimension(50, 30));
        
        slider.addChangeListener(e -> {
            valueLabel.setText(String.format("%d%%", slider.getValue()));
        });
        
        creator.create(slider);
        
        sliderPanel.add(slider, BorderLayout.CENTER);
        sliderPanel.add(valueLabel, BorderLayout.EAST);
        
        row.add(nameLabel);
        row.add(Box.createRigidArea(new Dimension(0, 5)));
        row.add(sliderPanel);
        
        return row;
    }
    
    // 画質設定パネル
    private JPanel createGraphicsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // 説明文
        JLabel titleLabel = new JLabel("表示設定");
        titleLabel.setFont(new Font("Yu Gothic", Font.BOLD, 24));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel infoLabel1 = new JLabel("■ ウィンドウサイズの変更");
        infoLabel1.setFont(new Font("Yu Gothic", Font.BOLD, 16));
        infoLabel1.setForeground(Color.WHITE);
        infoLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel2 = new JLabel("  ウィンドウの端をドラッグして自由にサイズ変更できます");
        infoLabel2.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel2.setForeground(new Color(200, 200, 200));
        infoLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(infoLabel1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(infoLabel2);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel infoLabel3 = new JLabel("■ フルスクリーン");
        infoLabel3.setFont(new Font("Yu Gothic", Font.BOLD, 16));
        infoLabel3.setForeground(Color.WHITE);
        infoLabel3.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel4 = new JLabel("  F11キーでフルスクリーンと通常モードを切り替えられます");
        infoLabel4.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel4.setForeground(new Color(200, 200, 200));
        infoLabel4.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(infoLabel3);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(infoLabel4);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel infoLabel5 = new JLabel("■ 今後の追加予定");
        infoLabel5.setFont(new Font("Yu Gothic", Font.BOLD, 16));
        infoLabel5.setForeground(Color.WHITE);
        infoLabel5.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel6 = new JLabel("  盤面のマス数変更などの機能を追加予定です");
        infoLabel6.setFont(new Font("Yu Gothic", Font.PLAIN, 14));
        infoLabel6.setForeground(new Color(200, 200, 200));
        infoLabel6.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(infoLabel5);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(infoLabel6);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    // ボタンのスタイル設定
    private void styleButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 100));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
    }
    
    // スタイル付きボタンを作成
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Yu Gothic", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(150, 50));
        styleButton(button);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 120));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 100));
            }
        });
        
        return button;
    }
    
    // 関数型インターフェース
    @FunctionalInterface
    interface KeySetter {
        void setKey(int keyCode);
    }
    
    @FunctionalInterface
    interface SliderCreator {
        void create(JSlider slider);
    }
}
