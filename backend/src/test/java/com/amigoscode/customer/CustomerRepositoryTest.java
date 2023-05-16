package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        //Given
        Customer customer = getCustomer();
        underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail(customer.getEmail());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerByEmailFailsWhenEmailNotPresent() {
        //Given
        Customer customer = getCustomer();
        underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail("");

        //Then
        assertThat(actual).isFalse();

    }

    @Test
    void existsCustomerById() {
        //Given
        Customer customer = getCustomer();
        underTest.save(customer);

        int id = underTest.findAll().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerByIdFailsWhenIdNotPresent() {
        //Given
        Customer customer = getCustomer();
        underTest.save(customer);
        int id = -1;

        //When
        boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isFalse();

    }
}
