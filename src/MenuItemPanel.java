import javax.swing.*;
import java.awt.*;

public class MenuItemPanel extends JPanel {

    public MenuItemPanel(String name, String description, int price, String imageUrl) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 메뉴 이미지
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 이미지 로드 (URL 기반)
        try {
            ImageIcon imageIcon = new ImageIcon(new java.net.URL(imageUrl));
            Image scaledImage = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            imageLabel.setText("이미지 없음");
        }

        // 메뉴 정보
        JLabel nameLabel = new JLabel("<html><b>" + name + "</b><br>" + description + "<br>₩" + price + "</html>");
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // "장바구니에 담기" 버튼
        JButton addButton = new JButton("장바구니에 담기");

        this.add(imageLabel, BorderLayout.NORTH);
        this.add(nameLabel, BorderLayout.CENTER);
        this.add(addButton, BorderLayout.SOUTH);
    }
}
