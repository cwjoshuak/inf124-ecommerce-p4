
public class Item {
    public Shoe shoe;
    public String color;
    public String colorIndex;
    public int size;
    public int quantity;

    public Item(Shoe sh, String sc, String index, int sz, int qty){
        this.shoe = sh;
        this.color = sc;
        this.colorIndex = index;
        this.size = sz;
        this.quantity = qty;
    }

    @Override
    public String toString() {
        return "Item {" +
                "Shoe =" + shoe + '\'' +
                ", Color ='" + color + '\'' +
                ", Size ='" + size + '\'' +
                ", Quantity ='" + quantity + '\'' +
                '}';
    }

}
