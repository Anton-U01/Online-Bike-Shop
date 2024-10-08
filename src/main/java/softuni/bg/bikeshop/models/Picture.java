package softuni.bg.bikeshop.models;

import jakarta.persistence.*;

@Entity
@Table
public class Picture extends BaseEntity{
    private String title;
    @Column(nullable = false,columnDefinition = "LONGTEXT")
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


}
