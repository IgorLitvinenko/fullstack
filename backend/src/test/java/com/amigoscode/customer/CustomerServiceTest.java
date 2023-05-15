package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "test", "test@gmail.com", 42);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 1;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "test@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("test", email, 55);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        //Given
        String email = "test@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("test", email, 55);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email %s already exist".formatted(email));

        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdNotExistsWhileDeletingCustomer() {
        //Given
        int id = 1;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found".formatted(id));

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllPropertiesCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "test", "test@gmail.com", 42);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "test1", "test1@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        //When
        underTest.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> argCustomer = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argCustomer.capture());

        Customer capturedCustomer = argCustomer.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "test", "test@gmail.com", 42);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "test1", null, null);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> argCustomer = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argCustomer.capture());

        Customer capturedCustomer = argCustomer.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenEmailExistWhileUpdatingCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "test", "test@gmail.com", 42);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "test", "test1@gmail.com", 42);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(true);


        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email %s already exist".formatted(request.email()));

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenNothingToChangeWhileUpdatingCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "test", "test@gmail.com", 42);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                customer.getName(), customer.getEmail(), customer.getAge());

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data change found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }
}
