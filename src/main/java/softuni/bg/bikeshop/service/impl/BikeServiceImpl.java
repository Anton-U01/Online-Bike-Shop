package softuni.bg.bikeshop.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.bg.bikeshop.models.Bike;
import softuni.bg.bikeshop.models.BikeType;
import softuni.bg.bikeshop.models.User;
import softuni.bg.bikeshop.models.dto.AddBikeDto;
import softuni.bg.bikeshop.repository.ProductRepository;
import softuni.bg.bikeshop.repository.UserRepository;
import softuni.bg.bikeshop.service.BikeService;

import java.security.Principal;
import java.util.Optional;

@Service
public class BikeServiceImpl implements BikeService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public BikeServiceImpl(ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean add(AddBikeDto addBikeDto, Principal principal) {
        Bike bike = modelMapper.map(addBikeDto, Bike.class);
        BikeType bikeType = BikeType.valueOf(addBikeDto.getType());
        bike.setType(bikeType);
        Optional<User> optional = userRepository.findByUsername(principal.getName());
        if (optional.isEmpty()) {
            return false;
        }
        User seller = optional.get();
        bike.setSeller(seller);

        productRepository.saveAndFlush(bike);
        return true;
    }

}