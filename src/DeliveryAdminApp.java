import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DeliveryAdminApp {
    private JFrame frame;

    public DeliveryAdminApp() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("내 배달 앱 - 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // 전체 레이아웃 설정
        frame.setLayout(new BorderLayout());

        // 상단 네비게이션 바
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navBar.setBackground(Color.DARK_GRAY);
        navBar.setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel("🍴 내 배달 앱");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JButton homeButton = new JButton("내 정보");
        JButton adminButton = new JButton("관리자");
        JButton reviewButton = new JButton("리뷰");
        JButton settingsButton = new JButton("설정");
        JButton customerButton = new JButton("고객");

        // "고객" 버튼 클릭 이벤트
        customerButton.addActionListener(e -> new CustomerPage());
        reviewButton.addActionListener(e -> new ReviewPage());
        homeButton.addActionListener(e -> new HomeMenuPage());
        settingsButton.addActionListener(e -> new SettingsPage());

        navBar.add(titleLabel);
        navBar.add(Box.createHorizontalStrut(30));
        navBar.add(homeButton);
        navBar.add(adminButton);
        navBar.add(reviewButton);
        navBar.add(settingsButton);
        navBar.add(customerButton);

        // 메인 컨텐츠 영역
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 255));

        // 주문 관리 패널
        JPanel orderPanel = createOrderManagementPanel();

        // 배달 관리 패널
        JPanel deliveryPanel = createSectionPanel("배달 관리", "SELECT * FROM 배달", "배송 추적", "자세한 내용 보기");

        // 메뉴 & 보유 패널
        JPanel menuPanel = createSectionPanel("메뉴 & 보유", "SELECT * FROM 제품", "재고 수정");

        // 메인 패널에 각 섹션 추가
        mainPanel.add(orderPanel);
        mainPanel.add(deliveryPanel);
        mainPanel.add(menuPanel);

        // 프레임에 컴포넌트 추가
        frame.add(navBar, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    public void show() {
        frame.setVisible(true);
    }

    private JPanel createOrderManagementPanel() {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(new Color(240, 240, 255));
        sectionPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 255), 2));

        JLabel titleLabel = new JLabel("주문 관리", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        sectionPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        itemListPanel.setBackground(new Color(240, 240, 255));

        String query = "SELECT 주문고유ID, 사용자고유ID, 상태, 주문시간, 배달예상시간 FROM 주문";
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##BAEMIN", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String orderId = rs.getString("주문고유ID");
                String userId = rs.getString("사용자고유ID");
                String status = rs.getString("상태");
                String orderTime = rs.getString("주문시간");
                String deliveryTime = rs.getString("배달예상시간");

                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                itemPanel.setBackground(Color.WHITE);

                // 주문 정보 표시
                JLabel orderLabel = new JLabel("<html>주문 ID: " + orderId + "<br>사용자 ID: " + userId +
                        "<br>상태: " + status + "<br>주문 시간: " + orderTime +
                        "<br>배달 예상 시간: " + deliveryTime + "</html>");

                // "주문 수락" 버튼
                JButton acceptButton = new JButton("주문 수락");
                acceptButton.addActionListener(e -> updateOrderStatus(orderId, "수락됨"));

                // "주문 거절" 버튼
                JButton rejectButton = new JButton("주문 거절");
                rejectButton.addActionListener(e -> updateOrderStatus(orderId, "거절됨"));

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(acceptButton);
                buttonPanel.add(rejectButton);

                itemPanel.add(orderLabel, BorderLayout.CENTER);
                itemPanel.add(buttonPanel, BorderLayout.SOUTH);

                itemListPanel.add(itemPanel);
                itemListPanel.add(Box.createVerticalStrut(5)); // 항목 간 간격
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sectionPanel.add(new JScrollPane(itemListPanel), BorderLayout.CENTER);
        return sectionPanel;
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        if ("거절됨".equals(newStatus)) {
            String deleteQuery = "DELETE FROM 주문 WHERE 주문고유ID = ?";
            try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##BAEMIN", "1234");
                 PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                pstmt.setString(1, orderId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "주문 ID " + orderId + "이(가) 거절되어 삭제되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "주문 삭제 중 오류가 발생했습니다.");
            }
        } else if ("수락됨".equals(newStatus)) {
            String updateQuery = "UPDATE 주문 SET 상태 = ? WHERE 주문고유ID = ?";
            try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##BAEMIN", "1234");
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setString(1, newStatus);
                pstmt.setString(2, orderId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "주문 ID " + orderId + "의 상태가 '" + newStatus + "'로 업데이트되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "주문 상태 업데이트 중 오류가 발생했습니다.");
            }
        }

        // UI 새로고침
        frame.dispose(); // 현재 프레임 닫고
        new DeliveryAdminApp().show(); // 새로고침
    }

    private static JPanel createSectionPanel(String title, String query, String... buttonLabels) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(new Color(240, 240, 255));
        sectionPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 255), 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        sectionPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        itemListPanel.setBackground(new Color(240, 240, 255));

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "c##BAEMIN", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int index = 0;
            while (rs.next()) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                itemPanel.setBackground(Color.WHITE);

                String itemData = rs.getString(2); // 두 번째 열 데이터 가져옴
                JLabel itemLabel = new JLabel("<html>" + itemData.replace("\n", "<br>") + "</html>");
                JButton actionButton = new JButton(buttonLabels[index % buttonLabels.length]);

                itemPanel.add(itemLabel, BorderLayout.CENTER);
                itemPanel.add(actionButton, BorderLayout.EAST);

                itemListPanel.add(itemPanel);
                itemListPanel.add(Box.createVerticalStrut(5)); // 항목 간 간격 추가
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sectionPanel.add(itemListPanel, BorderLayout.CENTER);
        return sectionPanel;
    }

    public static void main(String[] args) {
        DeliveryAdminApp app = new DeliveryAdminApp();
        app.show();
    }
}