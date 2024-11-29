import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage {
    private JFrame frame;

    public LoginPage() {
        frame = new JFrame("로그인");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());

        // 로그인 패널
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userTypeLabel = new JLabel("사용자 유형:");
        JComboBox<String> userTypeComboBox = new JComboBox<>(new String[]{"관리자", "고객"});
        JLabel usernameLabel = new JLabel("아이디:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("로그인");
        JButton registerButton = new JButton("회원가입");

        loginPanel.add(userTypeLabel);
        loginPanel.add(userTypeComboBox);
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        // 로그인 버튼 동작
        loginButton.addActionListener(e -> {
            String userType = userTypeComboBox.getSelectedItem().toString();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(userType, username, password)) {
                JOptionPane.showMessageDialog(frame, "로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // 로그인 창 닫기
                if ("관리자".equals(userType)) {
                    new DeliveryAdminApp().show(); // 관리자 페이지 열기
                } else {
                    new CustomerPage(); // 고객 전용 페이지 열기
                }
            } else {
                JOptionPane.showMessageDialog(frame, "로그인 실패. 아이디 또는 비밀번호를 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 회원가입 버튼 동작
        registerButton.addActionListener(e -> {
            frame.dispose(); // 로그인 창 닫기
            new RegisterPage(); // 회원가입 페이지 열기
        });

        frame.add(loginPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private boolean authenticate(String userType, String username, String password) {
        String tableName = "관리자".equals(userType) ? "admin_users" : "customer_users";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/내 데이터베이스", "데이터베이스 아이디 ", "비밀번호");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
