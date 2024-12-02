import javax.swing.*;
import java.awt.*;

public class MenuItemPanel extends JPanel {
    private MenuItem menuItem;

    public MenuItemPanel(MenuItem menuItem) {
        this.menuItem = menuItem;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setPreferredSize(new Dimension(200, 300));

        // 텍스트 정보
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(menuItem.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        JLabel descLabel = new JLabel("<html><p style='width:150px;'>" + menuItem.getDescription() + "</p></html>");
        JLabel priceLabel = new JLabel(menuItem.getPrice() + "원");

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(priceLabel);

        // "장바구니에 담기" 버튼
        JButton addToCartButton = new JButton("장바구니에 담기");
        addToCartButton.addActionListener(e -> Basket.getInstance().addItem(menuItem));

        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(addToCartButton);

        add(textPanel, BorderLayout.CENTER);
    }
}
