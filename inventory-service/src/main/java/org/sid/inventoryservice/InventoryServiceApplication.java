package org.sid.inventoryservice;

import com.netflix.discovery.converters.Auto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GeneratorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.function.Supplier;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
		@Bean
		CommandLineRunner start(ProductRepository productRepository,
								RepositoryRestConfiguration restConfiguration){
		return args -> {
			restConfiguration.exposeIdsFor(Product.class);
			productRepository.save(new Product(null,"ordinateur",788,12));
			productRepository.save(new Product(null,"imprimante",88,129));
			productRepository.save(new Product(null,"smartphone",1288,1772));
			productRepository.findAll().forEach(product -> {
				System.out.println(product.getName());
			});

		};
		}

}

@Entity
@Data @AllArgsConstructor @NoArgsConstructor @ToString
class Product{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private double price;
	private double quantity;
}
@CrossOrigin("*")
@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product,Long>{

}
@CrossOrigin("*")
@RestController
class ProductController {
	@Autowired
	private StreamBridge streamBridge;
	@Autowired
	private ProductRepository productRepository;
	@GetMapping("/producer")
	public List<Product> publish(){
		List<Product> pages=productRepository.findAll();
		streamBridge.send("Product_Topic",pages);
		return pages;
	}
}

