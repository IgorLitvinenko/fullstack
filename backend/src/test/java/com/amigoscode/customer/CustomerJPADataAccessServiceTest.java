package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();

        //Then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        int id = 1;

        //When
        underTest.selectCustomerById(id);

        //Then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = AbstractTestcontainers.getCustomer();

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = "test";

        //When
        underTest.existsCustomerWithEmail(email);

        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsPersonWithId() {
        //Given
        int id = 1;

        //When
        underTest.existsCustomerWithId(id);

        //Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = AbstractTestcontainers.getCustomer();

        //When
        underTest.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }
}
