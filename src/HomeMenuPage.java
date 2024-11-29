import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HomeMenuPage {
    public HomeMenuPage() {
        JFrame frame = new JFrame("내 정보");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫아도 메인 앱은 실행 중

        // 상단 타이틀
        JLabel titleLabel = new JLabel("내 정보", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // 메인 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 관리자 정보 가져오기
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_user", "your_password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM admin WHERE id = 1")) { // 관리자 ID 1번

            if (rs.next()) {
                // 관리자 정보 표시
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                JLabel nameLabel = new JLabel("이름: " + name);
                nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

                JLabel emailLabel = new JLabel("이메일: " + email);
                emailLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

                JLabel phoneLabel = new JLabel("전화번호: " + phone);
                phoneLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

                infoPanel.add(nameLabel);
                infoPanel.add(Box.createVerticalStrut(10)); // 간격 추가
                infoPanel.add(emailLabel);
                infoPanel.add(Box.createVerticalStrut(10));
                infoPanel.add(phoneLabel);
            } else {
                JLabel noDataLabel = new JLabel("관리자 정보를 불러올 수 없습니다.", SwingConstants.CENTER);
                noDataLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                infoPanel.add(noDataLabel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("정보 로드 중 오류가 발생했습니다.", SwingConstants.CENTER);
            errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            infoPanel.add(errorLabel);
        }

        frame.add(infoPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
