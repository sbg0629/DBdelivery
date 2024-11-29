import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterPage {
    private JFrame frame;

    public RegisterPage() {
        frame = new JFrame("회원가입");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());

        // 회원가입 패널
        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userTypeLabel = new JLabel("사용자 유형:");
        JComboBox<String> userTypeComboBox = new JComboBox<>(new String[]{"관리자", "고객"});
        JLabel usernameLabel = new JLabel("아이디:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton registerButton = new JButton("회원가입");

        registerPanel.add(userTypeLabel);
        registerPanel.add(userTypeComboBox);
        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(confirmPasswordLabel);
        registerPanel.add(confirmPasswordField);
        registerPanel.add(new JLabel());
        registerPanel.add(registerButton);

        // 회원가입 버튼 동작
        registerButton.addActionListener(e -> {
            String userType = userTypeComboBox.getSelectedItem().toString();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (password.equals(confirmPassword)) {
                if (registerUser(userType, username, password)) {
                    JOptionPane.showMessageDialog(frame, "회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // 회원가입 창 닫기
                    new LoginPage(); // 로그인 페이지 열기
                } else {
                    JOptionPane.showMessageDialog(frame, "회원가입 실패. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(registerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private boolean registerUser(String userType, String username, String password) {
        String tableName = "관리자".equals(userType) ? "admin_users" : "customer_users";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_user", "your_password");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + tableName + " (username, password) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
