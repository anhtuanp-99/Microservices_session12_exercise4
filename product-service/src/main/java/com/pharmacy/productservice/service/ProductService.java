package com.pharmacy.productservice.service;

import com.pharmacy.productservice.dto.request.ProductRequestDTO;
import com.pharmacy.productservice.dto.response.ProductResponseDTO;
import com.pharmacy.productservice.entity.Product;
import com.pharmacy.productservice.exception.ResourceNotFoundException;
import com.pharmacy.productservice.mapper.ProductMapper;
import com.pharmacy.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .build();

        Product saved = productRepository.save(product);
        log.info("Đã tạo product id={}", saved.getId());
        return productMapper.toResponseDTO(saved);
    }

    /**
     * Lấy chi tiết sản phẩm — CÓ CACHE.
     * <p>
     * Lần 1: MISS → query DB → lưu vào Redis key "product:products::1"
     * Lần 2+: HIT → trả về ngay từ Redis (<10ms)
     */
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        log.info(">>> [DB QUERY] Lấy product id={} từ DATABASE", id);

        // Mô phỏng query chậm 200ms — để thấy rõ cache hit nhanh hơn
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id));

        return productMapper.toResponseDTO(product);
    }

    /**
     * Lấy tất cả sản phẩm — không cache vì list lớn.
     */
    public List<ProductResponseDTO> getAllProducts() {
        log.info(">>> [DB QUERY] Lấy tất cả products từ DATABASE");
        return productMapper.toResponseDTOList(productRepository.findAll());
    }

    /**
     * Cập nhật sản phẩm — XÓA CACHE.
     * <p>
     * Dùng @CacheEvict thay vì @CachePut vì:
     *   - Update phức tạp → có thể không khớp data GET trả về.
     *   - Đảm bảo lần GET sau luôn lấy data mới nhất từ DB.
     * <p>
     * key = "#id": xóa đúng cache của product được update.
     */
    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());

        Product updated = productRepository.save(product);
        log.info("Đã cập nhật product id={}, cache đã bị xóa", id);
        return productMapper.toResponseDTO(updated);
    }

    /**
     * Xóa sản phẩm — XÓA CACHE.
     */
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "ID", id);
        }
        productRepository.deleteById(id);
        log.info("Đã xóa product id={}, cache đã bị xóa", id);
    }
}