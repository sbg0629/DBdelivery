import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerPage {

    // Basket 클래스
    static class Basket {
        private static final Basket instance = new Basket();
        private final List<BasketItem> items;

        private Basket() {
            items = new ArrayList<>();
        }

        public static Basket getInstance() {
            return instance;
        }

        public void addItem(MenuItem item) {
            for (BasketItem basketItem : items) {
                if (basketItem.getMenuItem().getName().equals(item.getName())) {
                    basketItem.incrementQuantity();
                    return;
                }
            }
            items.add(new BasketItem(item));
            JOptionPane.showMessageDialog(null, item.getName() + "이(가) 장바구니에 추가되었습니다.");
        }

        public void removeItem(BasketItem basketItem) {
            items.remove(basketItem);
        }

        public List<BasketItem> getItems() {
            return items;
        }

        public int calculateTotalPrice() {
            int total = 0;
            for (BasketItem basketItem : items) {
                total += basketItem.getMenuItem().getPrice() * basketItem.getQuantity();
            }
            return total;
        }
    }

    // BasketItem 클래스
    static class BasketItem {
        private final MenuItem menuItem;
        private int quantity;

        public BasketItem(MenuItem menuItem) {
            this.menuItem = menuItem;
            this.quantity = 1;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            quantity++;
        }

        public void decrementQuantity() {
            if (quantity > 0) {
                quantity--;
            }
        }
    }

    // MenuItem 클래스
    static class MenuItem {
        private final String name;
        private final String description;
        private final int price;

        public MenuItem(String name, String description, int price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getPrice() {
            return price;
        }
    }

    // MenuItemPanel 클래스
    static class MenuItemPanel extends JPanel {
        public MenuItemPanel(MenuItem menuItem) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            setPreferredSize(new Dimension(200, 150));

            // 메뉴 이름
            JLabel nameLabel = new JLabel(menuItem.getName());
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // 설명과 가격
            JLabel descLabel = new JLabel("<html><p style='text-align:center;'>" + menuItem.getDescription() + "</p></html>");
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel priceLabel = new JLabel(menuItem.getPrice() + "원");
            priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // "장바구니에 담기" 버튼
            JButton addToCartButton = new JButton("장바구니에 담기");
            addToCartButton.addActionListener(e -> Basket.getInstance().addItem(menuItem));

            // 구성 요소 추가
            add(nameLabel, BorderLayout.NORTH);
            add(descLabel, BorderLayout.CENTER);
            add(priceLabel, BorderLayout.SOUTH);
            add(addToCartButton, BorderLayout.PAGE_END);
        }
    }

    // Database 연결 유틸리티 클래스
    static class DatabaseConnection {
        private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        private static final String DB_USER = "c##son";
        private static final String DB_PASSWORD = "1234";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    // CustomerPage 생성자
    public CustomerPage() {
        JFrame customerFrame = new JFrame("고객 페이지");
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setSize(1200, 800);
        customerFrame.setLayout(new BorderLayout());

        // 상단 영역
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("🍲 디비김치찜");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton callButton = new JButton("가게 전화");
        JButton cartButton = new JButton("장바구니 보기");
        buttonPanel.add(callButton);
        buttonPanel.add(cartButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 메뉴 패널
        JPanel menuPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(Color.LIGHT_GRAY);

        List<MenuItem> menuItems = getMenuItemsFromDatabase();
        for (MenuItem item : menuItems) {
            menuPanel.add(new MenuItemPanel(item));
        }

        // 프레임에 추가
        customerFrame.add(topPanel, BorderLayout.NORTH);
        customerFrame.add(menuPanel, BorderLayout.CENTER);

        customerFrame.setVisible(true);

        // 장바구니 버튼 이벤트
        cartButton.addActionListener(e -> showCart(customerFrame));

        // 가게 전화 버튼 이벤트
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(customerFrame, "가게 전화 연결: 가게 망했습니다"));
    }

    private void showCart(JFrame parentFrame) {
        JFrame cartFrame = new JFrame("장바구니");
        cartFrame.setSize(400, 600);
        cartFrame.setLayout(new BorderLayout());

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        List<BasketItem> basketItems = Basket.getInstance().getItems();
        for (BasketItem basketItem : basketItems) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            int itemTotalPrice = basketItem.getMenuItem().getPrice() * basketItem.getQuantity();

            JLabel nameLabel = new JLabel(
                    basketItem.getMenuItem().getName() + " (" + basketItem.getQuantity() + "개, "
                            + itemTotalPrice + "원)");
            JButton incrementButton = new JButton("+");
            JButton decrementButton = new JButton("-");
            JButton removeButton = new JButton("삭제");

            incrementButton.addActionListener(e -> {
                basketItem.incrementQuantity();
                cartFrame.dispose();
                showCart(parentFrame);
            });

            decrementButton.addActionListener(e -> {
                basketItem.decrementQuantity();
                if (basketItem.getQuantity() <= 0) {
                    Basket.getInstance().removeItem(basketItem);
                }
                cartFrame.dispose();
                showCart(parentFrame);
            });

            removeButton.addActionListener(e -> {
                Basket.getInstance().removeItem(basketItem);
                cartFrame.dispose();
                showCart(parentFrame);
            });

            itemPanel.add(nameLabel);
            itemPanel.add(incrementButton);
            itemPanel.add(decrementButton);
            itemPanel.add(removeButton);
            cartPanel.add(itemPanel);
        }

        JLabel totalPriceLabel = new JLabel("총 가격: " + Basket.getInstance().calculateTotalPrice() + "원");
        totalPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalPriceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        cartFrame.add(totalPriceLabel, BorderLayout.SOUTH);
        cartFrame.add(new JScrollPane(cartPanel), BorderLayout.CENTER);

        cartFrame.setVisible(true);
    }

    private List<MenuItem> getMenuItemsFromDatabase() {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT name, description, price FROM menu_items";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                int price = resultSet.getInt("price");

                items.add(new MenuItem(name, description, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "메뉴 데이터를 불러오는데 실패했습니다.");
        }

        return items;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerPage::new);
    }
}
