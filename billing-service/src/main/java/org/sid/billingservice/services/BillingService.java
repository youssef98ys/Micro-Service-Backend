package org.sid.billingservice.services;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repositories.BillRepository;
import org.sid.billingservice.repositories.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class BillingService {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private ProductItemRepository productItemRepository;
    @Bean
    public Consumer<Customer> customerConsumer() {
        return (input)-> {
            System.out.println("Consumer working");
            Customer customer=input;
            billRepository.save(new Bill(null,new Date(),null,customer.getId(),null));
        };
    }
    @Bean
    public Consumer<List<Product>> productConsumer() {
        return (input)-> {
            System.out.println("Consumer Product working");
            List<Product> products=input;
            Bill bill=billRepository.findById(1L).get();
            products.forEach(p -> {
                ProductItem productItem=new ProductItem();
                productItem.setPrice(p.getPrice());
                productItem.setQuantity(1+new Random().nextInt(100));
                productItem.setProductId(p.getId());
                productItem.setBill(bill);
                productItemRepository.save(productItem);
            });
        };
    }
}
