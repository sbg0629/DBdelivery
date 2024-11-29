import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReviewPage {
    public ReviewPage() {
        JFrame frame = new JFrame("리뷰 관리");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫아도 메인 앱은 실행 중

        // 상단 타이틀
        JLabel titleLabel = new JLabel("고객 리뷰 관리", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // 리뷰 목록 패널
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(reviewPanel);

        // 데이터베이스 연결 및 리뷰 데이터 가져오기
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_user", "your_password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reviews")) {

            while (rs.next()) {
                // 데이터베이스에서 리뷰 데이터를 가져오기
                int reviewId = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                String reviewText = rs.getString("review");
                String reviewDate = rs.getString("date");

                // 각 리뷰 패널 생성
                JPanel singleReviewPanel = new JPanel(new BorderLayout());
                singleReviewPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 255), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                singleReviewPanel.setBackground(Color.WHITE);

                // 리뷰 정보 표시
                JLabel reviewLabel = new JLabel("<html><b>" + customerName + " (" + reviewDate + ")</b><br>" + reviewText + "</html>");
                reviewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

                // 삭제 및 답변 버튼
                JButton deleteButton = new JButton("삭제");
                JButton replyButton = new JButton("답변");

                // 삭제 버튼 동작
                deleteButton.addActionListener(e -> {
                    deleteReview(reviewId);
                    frame.dispose();
                    new ReviewPage(); // 화면 갱신
                });

                // 답변 버튼 동작
                replyButton.addActionListener(e -> {
                    replyToReview(customerName, reviewId);
                });

                // 버튼 패널
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(replyButton);
                buttonPanel.add(deleteButton);

                singleReviewPanel.add(reviewLabel, BorderLayout.CENTER);
                singleReviewPanel.add(buttonPanel, BorderLayout.SOUTH);

                reviewPanel.add(singleReviewPanel);
                reviewPanel.add(Box.createVerticalStrut(10)); // 간격 추가
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 리뷰 삭제 메서드
    private void deleteReview(int reviewId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_user", "your_password");
             Statement stmt = conn.createStatement()) {

            String query = "DELETE FROM reviews WHERE id = " + reviewId;
            stmt.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "리뷰가 삭제되었습니다!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "리뷰 삭제 중 오류가 발생했습니다.");
        }
    }

    // 리뷰 답변 메서드
    private void replyToReview(String customerName, int reviewId) {
        String reply = JOptionPane.showInputDialog(null, customerName + " 고객님께 답변하기:");
        if (reply != null && !reply.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_user", "your_password");
                 Statement stmt = conn.createStatement()) {

                String query = "UPDATE reviews SET reply = '" + reply + "' WHERE id = " + reviewId;
                stmt.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "답변이 등록되었습니다!");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "답변 등록 중 오류가 발생했습니다.");
            }
        }
    }
}
