package softuni.bg.bikeshop.service.impl;
;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softuni.bg.bikeshop.models.*;
import softuni.bg.bikeshop.repository.PictureRepository;
import softuni.bg.bikeshop.repository.ProductRepository;
import softuni.bg.bikeshop.repository.UserRepository;
import softuni.bg.bikeshop.service.ProductsService;
import java.security.Principal;
import java.util.*;

@Service
public class ProductsServiceImpl implements ProductsService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    public ProductsServiceImpl(ProductRepository productRepository, UserRepository userRepository, PictureRepository pictureRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public boolean addToFavourites(Long id, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty()){
            return false;
        }
        Product product = optionalProduct.get();
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if(optionalUser.isEmpty()){
            return false;
        }
        User user = optionalUser.get();
        if(user.getFavouriteProducts().contains(product)){
            return false;
        }
        user.getFavouriteProducts().add(product);
        product.setFavourite(true);

        userRepository.save(user);
        return true;
    }

    @Override
    public Set<Product> getFavourites(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            return new HashSet<>();
        }
        User user = optionalUser.get();
        return productRepository.getFavouritesListByUser(user);

    }

    @Override
    @Transactional
    public void removeFromFavourites(Long productId, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            return;
        }
        User user = optionalUser.get();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty()){
            return;
        }

        Product product = optionalProduct.get();
        if(!user.getFavouriteProducts().contains(product)){
            return;
        }
        user.getFavouriteProducts().remove(product);
        product.setFavourite(false);
    }

    @Override
    public List<Product> getAllCurrentUserProducts(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            return new ArrayList<>();
        }
        User user = optionalUser.get();
        return productRepository.getAllCurrentUserProducts(user);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId, String name) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty()){
            return false;
        }
        Product product = optionalProduct.get();
        Optional<User> optionalUser = userRepository.findByUsername(name);
        if(optionalUser.isEmpty()){
            return false;
        }
        User user = optionalUser.get();
        if(!user.getProducts().contains(product)){
            return false;
        }
        if(product.isFavourite()){
            return false;
        }
        user.getProducts().remove(product);
        pictureRepository.deleteAll(product.getPictures());

        productRepository.deleteById(productId);

        return true;
    }

    @Override
    public boolean editProduct(Long productId, String username) {

        return false;
    }
}
