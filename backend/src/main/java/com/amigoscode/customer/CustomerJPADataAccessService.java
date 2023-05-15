package com.amigoscode.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public void updateCustomer(Customer update) {
        customerRepository.save(update);
    }
}
