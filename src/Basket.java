import java.util.ArrayList;
import java.util.List;

public class Basket {
    private static final Basket instance = new Basket();
    private final List<BasketItem> items;

    private Basket() {
        items = new ArrayList<>();
    }

    public static Basket getInstance() {
        return instance;
    }

    // 아이템 추가
    public void addItem(MenuItem menuItem) {
        for (BasketItem item : items) {
            if (item.getMenuItem().getName().equals(menuItem.getName())) {
                item.incrementQuantity();
                return;
            }
        }
        items.add(new BasketItem(menuItem));
    }

    // 아이템 수량 증가
    public void incrementQuantity(BasketItem item) {
        item.incrementQuantity();
    }

    // 아이템 수량 감소
    public void decrementQuantity(BasketItem item) {
        item.decrementQuantity();
        if (item.getQuantity() <= 0) {
            items.remove(item);
        }
    }

    // 아이템 삭제
    public void removeItem(BasketItem item) {
        items.remove(item);
    }

    // 총 가격 계산
    public int calculateTotalPrice() {
        int total = 0;
        for (BasketItem item : items) {
            total += item.getMenuItem().getPrice() * item.getQuantity();
        }
        return total;
    }

    public List<BasketItem> getItems() {
        return items;
    }
}

// BasketItem 클래스
class BasketItem {
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
