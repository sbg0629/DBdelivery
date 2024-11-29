import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomerPage {

    public CustomerPage() {
        JFrame customerFrame = new JFrame("고객 페이지");
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setSize(1200, 800);

        // 레이아웃 설정
        customerFrame.setLayout(new BorderLayout());

        // 타이틀 및 상단 영역
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("디비김치찜");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);

        // 메뉴 패널
        JPanel menuPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3열 레이아웃
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(Color.LIGHT_GRAY);

        // 데이터베이스에서 메뉴 가져오기
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/내 데이터베이스", "데이터베이스 아이디 ", "비밀번호");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, description, price, image_url FROM menu")) {

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                int price = rs.getInt("price");
                String imageUrl = rs.getString("image_url");

                // MenuItemPanel 생성 후 추가
                menuPanel.add(new MenuItemPanel(name, description, price, imageUrl));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(customerFrame, "데이터베이스 연결 오류: " + e.getMessage());
        }

        // 프레임에 추가
        customerFrame.add(titlePanel, BorderLayout.NORTH);
        customerFrame.add(menuPanel, BorderLayout.CENTER);

        customerFrame.setVisible(true);
    }
}
