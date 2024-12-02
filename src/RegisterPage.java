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
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // 회원가입 패널
        JPanel registerPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userIdLabel = new JLabel("사용자 고유 ID:");
        JTextField userIdField = new JTextField();
        JLabel usernameLabel = new JLabel("아이디:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordField = new JPasswordField();
        JLabel phoneLabel = new JLabel("연락처:");
        JTextField phoneField = new JTextField();
        JLabel addressLabel = new JLabel("주소:");
        JTextField addressField = new JTextField();

        JButton registerButton = new JButton("회원가입");
        JButton backButton = new JButton("뒤로가기");

        registerPanel.add(userIdLabel);
        registerPanel.add(userIdField);
        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(phoneLabel);
        registerPanel.add(phoneField);
        registerPanel.add(addressLabel);
        registerPanel.add(addressField);
        registerPanel.add(registerButton);
        registerPanel.add(backButton);

        // 회원가입 버튼 동작
        registerButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();
            String address = addressField.getText();

            if (registerUser(userId, username, password, phone, address)) {
                JOptionPane.showMessageDialog(frame, "회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // 회원가입 창 닫기
                new LoginPage(); // 로그인 페이지 열기
            } else {
                JOptionPane.showMessageDialog(frame, "회원가입 실패. 입력 정보를 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose(); // 회원가입 창 닫기
            new LoginPage(); // 로그인 페이지 열기
        });

        frame.add(registerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private boolean registerUser(String userId, String username, String password, String phone, String address) {
        String sql = "INSERT INTO 사용자 (사용자고유ID, ID, 비밀번호, 연락처, 주소, 가입날짜) VALUES (?, ?, ?, ?, ?, SYSDATE)";
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##BAEMIN", "1234");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, address);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
