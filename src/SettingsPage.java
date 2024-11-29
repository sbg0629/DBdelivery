import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsPage {
    private JFrame frame;
    private JPanel mainPanel;
    private JComboBox<String> themeComboBox;
    private JComboBox<String> languageComboBox;
    private JCheckBox enableNotification;

    public SettingsPage() {
        frame = new JFrame("설정");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫아도 메인 앱은 유지

        // 메인 패널
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 테마 변경 섹션
        JPanel themePanel = createThemePanel();

        // 언어 설정 섹션
        JPanel languagePanel = createLanguagePanel();

        // 알림 설정 섹션
        JPanel notificationPanel = createNotificationPanel();

        // 완료 버튼 섹션
        JPanel buttonPanel = createButtonPanel();

        // 모든 섹션을 메인 패널에 추가
        mainPanel.add(themePanel);
        mainPanel.add(languagePanel);
        mainPanel.add(notificationPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // 간격 추가
        mainPanel.add(buttonPanel);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createThemePanel() {
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setBorder(BorderFactory.createTitledBorder("테마 설정"));

        JLabel themeLabel = new JLabel("테마 선택:");
        String[] themes = {"밝은 모드", "어두운 모드"};
        themeComboBox = new JComboBox<>(themes);

        themeComboBox.addActionListener(e -> {
            String selectedTheme = (String) themeComboBox.getSelectedItem();
            applyTheme(selectedTheme);
        });

        themePanel.add(themeLabel);
        themePanel.add(themeComboBox);
        return themePanel;
    }

    private JPanel createLanguagePanel() {
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setBorder(BorderFactory.createTitledBorder("언어 설정"));

        JLabel languageLabel = new JLabel("언어:");
        String[] languages = {"한국어", "English"};
        languageComboBox = new JComboBox<>(languages);

        languageComboBox.addActionListener(e -> {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            applyLanguage(selectedLanguage);
        });

        languagePanel.add(languageLabel);
        languagePanel.add(languageComboBox);
        return languagePanel;
    }

    private JPanel createNotificationPanel() {
        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notificationPanel.setBorder(BorderFactory.createTitledBorder("알림 설정"));

        enableNotification = new JCheckBox("알림 활성화");
        enableNotification.setSelected(true);

        notificationPanel.add(enableNotification);
        return notificationPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("설정 완료");
        saveButton.addActionListener(e -> {
            saveSettings();
            frame.dispose(); // 창 닫기
        });

        buttonPanel.add(saveButton);
        return buttonPanel;
    }

    private void applyTheme(String theme) {
        if (theme.equals("밝은 모드")) {
            mainPanel.setBackground(Color.WHITE);
        } else if (theme.equals("어두운 모드")) {
            mainPanel.setBackground(Color.DARK_GRAY);
        }
        mainPanel.repaint();
    }

    private void applyLanguage(String language) {
        Locale locale = language.equals("English") ? Locale.ENGLISH : Locale.KOREAN;
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        // 예: 언어에 따라 레이블 변경 (추가 구현 가능)
        JOptionPane.showMessageDialog(frame, "언어가 " + language + "로 변경되었습니다.");
    }

    private void saveSettings() {
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        boolean notificationsEnabled = enableNotification.isSelected();

        // 설정 저장 로직 추가 가능 (ex. 파일 저장, 데이터베이스 저장 등)
        JOptionPane.showMessageDialog(frame, "설정이 저장되었습니다.\n" +
                "테마: " + selectedTheme + "\n" +
                "언어: " + selectedLanguage + "\n" +
                "알림: " + (notificationsEnabled ? "활성화" : "비활성화"));
    }
}
