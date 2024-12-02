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
            return items.stream()
                    .mapToInt(basketItem -> basketItem.getMenuItem().getPrice() * basketItem.getQuantity())
                    .sum();
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

    // Database 연결 유틸리티 클래스
    static class DatabaseConnection {
        private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        private static final String DB_USER = "c##BAEMIN";
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

        customerFrame.add(topPanel, BorderLayout.NORTH);
        customerFrame.add(menuPanel, BorderLayout.CENTER);

        customerFrame.setVisible(true);

        // 장바구니 버튼 이벤트
        cartButton.addActionListener(e -> showCart(customerFrame));

        // 가게 전화 버튼 이벤트
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(customerFrame, "가게 전화 연결: 가게 망했습니다"));
    }

    // 장바구니 보기 및 주문하기
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

        JButton orderButton = new JButton("주문하기");
        orderButton.addActionListener(e -> {
            String address = JOptionPane.showInputDialog(cartFrame, "배송 주소를 입력하세요:", "배송 주소 입력", JOptionPane.PLAIN_MESSAGE);

            if (address != null && !address.trim().isEmpty()) {
                saveOrderToDatabase(address, basketItems);
                JOptionPane.showMessageDialog(cartFrame, "주문이 완료되었습니다!\n배송 주소: " + address);
                Basket.getInstance().getItems().clear(); // 장바구니 초기화
                cartFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(cartFrame, "주소를 입력해주세요!", "주소 누락", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(orderButton);

        cartFrame.add(totalPriceLabel, BorderLayout.NORTH);
        cartFrame.add(new JScrollPane(cartPanel), BorderLayout.CENTER);
        cartFrame.add(buttonPanel, BorderLayout.SOUTH);

        cartFrame.setVisible(true);
    }

    private void saveOrderToDatabase(String address, List<BasketItem> basketItems) {
        String orderInsertQuery = "INSERT INTO 주문 (주문고유ID, 사용자고유ID, 상점고유ID, 상태, 주문시간, 배달예상시간) VALUES (?, ?, ?, ?, ?, ?)";

        // 주문 ID 생성
        String orderId = generateOrderId();
        String userId = "1"; // 로그인한 사용자의 사용자고유ID로 대체 필요
        String storeId = "1"; // 테스트용 상점 ID
        String status = "주문 완료";
        Timestamp orderTime = new Timestamp(System.currentTimeMillis());
        Timestamp estimatedDeliveryTime = new Timestamp(System.currentTimeMillis() + 3600 * 1000); // 1시간 후 예상

        try (Connection connection = DatabaseConnection.getConnection()) {
            // 주문 데이터 삽입
            try (PreparedStatement orderStatement = connection.prepareStatement(orderInsertQuery)) {
                orderStatement.setString(1, orderId);
                orderStatement.setString(2, userId);
                orderStatement.setString(3, storeId);
                orderStatement.setString(4, status);
                orderStatement.setTimestamp(5, orderTime);
                orderStatement.setTimestamp(6, estimatedDeliveryTime);
                orderStatement.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "주문이 성공적으로 저장되었습니다!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "주문 저장 중 오류가 발생했습니다.", "주문 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 주문 ID 생성 메서드
    private String generateOrderId() {
        return "ORDER" + System.currentTimeMillis();
    }

    // 데이터베이스에서 메뉴 항목 가져오기
    private List<MenuItem> getMenuItemsFromDatabase() {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT 이름 AS name, 가격 AS price FROM 제품"; // 적절히 매핑된 칼럼명 사용

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int price = resultSet.getInt("price");
                String description = "맛있는 음식입니다!"; // 디폴트 설명
                items.add(new MenuItem(name, description, price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // MenuItemPanel 클래스
    static class MenuItemPanel extends JPanel {
        public MenuItemPanel(MenuItem item) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel(item.getName());
            JLabel priceLabel = new JLabel(item.getPrice() + "원");

            JButton addToCartButton = new JButton("장바구니 담기");
            addToCartButton.addActionListener(e -> Basket.getInstance().addItem(item));

            JPanel topPanel = new JPanel(new GridLayout(2, 1));
            topPanel.add(nameLabel);
            topPanel.add(priceLabel);

            add(topPanel, BorderLayout.CENTER);
            add(addToCartButton, BorderLayout.SOUTH);
        }
    }

    // 메인 함수
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerPage::new);
    }
}
