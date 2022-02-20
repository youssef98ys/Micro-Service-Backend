package org.sid.billingservice.web;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repositories.BillRepository;
import org.sid.billingservice.repositories.ProductItemRepository;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;
@CrossOrigin("*")
@RestController
public class BillingRestController {
    private BillRepository  billRepository;
    private ProductItemRepository productItemRepository;
    private ProductItemRestClient productItemRestClient;
    private CustomerRestClient customerRestClient;

    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, ProductItemRestClient productItemRestClient, CustomerRestClient customerRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.productItemRestClient = productItemRestClient;
        this.customerRestClient = customerRestClient;
    }

    @GetMapping(path = "/fullBill/{id}")
    public Bill getBill(@PathVariable(name = "id") Long id){
        Bill bill=billRepository.findById(id).get();
        Customer customer=customerRestClient.getCustomerById(bill.getCustomerId());
        bill.setCustomer(customer);
        PagedModel<Product> productPagedModel=productItemRestClient.pageProducts();
        bill.getProductItems().forEach(productItem -> {
            Product product=productItemRestClient.getProductById(productItem.getProductId());
            productItem.setProduct(product);
        });
        return bill;
    }
}
