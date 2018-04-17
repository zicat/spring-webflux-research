package name.zicat.spring.webflux.research.model;

public class ProductInfo {

    private int productId;

    public ProductInfo(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
