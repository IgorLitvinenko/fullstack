package com.amigoscode.customer;

import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id %s not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        emailCheck(email);
        customerDao.insertCustomer(
                new Customer(customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        customerRegistrationRequest.age()));

    }

    public void deleteCustomerById(Integer id) {
        if (!customerDao.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException("customer with id %s not found".formatted(id));
        }
        customerDao.deleteCustomerById(id);
    }

//    public void updateCustomer(Integer customerId, CustomerRegistrationRequest updateRequest) {
//        Customer customer = getCustomer(customerId);
//        Customer result = new Customer(customer.getId(),
//                updateRequest.name(),
//                updateRequest.email(),
//                updateRequest.age());
//
//        if (customer.equals(result)) {
//            throw new RequestValidationException("no data change found");
//        } else {
//            if (updateRequest.age() != null) {
//                customer.setAge(updateRequest.age());
//            }
//            if (updateRequest.name() != null) {
//                customer.setName(updateRequest.name());
//            }
//            if (updateRequest.email() != null) {
//                emailCheck(updateRequest.email());
//                customer.setEmail(updateRequest.email());
//            }
//            customerDao.insertCustomer(customer);
//        }
//    }
    public void updateCustomer(Integer customerId, CustomerRegistrationRequest updateRequest) {
        Customer customer = getCustomer(customerId);
        boolean changes = false;

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            emailCheck(updateRequest.email());
            customer.setEmail(updateRequest.email());
            changes = true;
        }
        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }
        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }
        if (!changes) {
            throw new RequestValidationException("no data change found");
        }
        customerDao.updateCustomer(customer);
    }

    private void emailCheck(String email) {
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("Customer with email %s already exist".formatted(email));
        }
    }
}
