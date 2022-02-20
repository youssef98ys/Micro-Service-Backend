package org.sid.customerservice.web;

import org.sid.customerservice.entities.Customer;
import org.sid.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin("*")
@RestController
public class CustomerController {
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private CustomerRepository customerRepository;
    @GetMapping("/producer/{id}")
    public Customer publish(@PathVariable Long id){
        Customer c = customerRepository.findById(id).get();
        streamBridge.send("Customer_Topic",c);
        return c;
    }
}
