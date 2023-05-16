package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper rowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                rowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + " " + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);

        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given
        int id = -1;

        //When
        Optional<Customer> customer = underTest.selectCustomerById(id);

        //Then
        assertThat(customer).isEmpty();
    }

    @Test
    void insertCustomer() {
        //Given
        underTest.insertCustomer(getCustomer());

        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void existsCustomerWithEmail() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();
        underTest.insertCustomer(customer);

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerWithEmailWillReturnFalseWhenDoesNotExists() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + " " + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        boolean result = underTest.existsCustomerWithId(id);

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void existCustomerWithIdWillReturnFalseWhenDoesNotExists() {
        //Given
        int id = -1;

        //When
        boolean actual = underTest.existsCustomerWithId(id);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        underTest.deleteCustomerById(id);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "test";

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(newName));
            assertThat(c.getAge().equals(customer.getAge()));
            assertThat(c.getEmail().equals(customer.getEmail()));
        });
    }

    @Test
    void updateCustomerEmail() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = FAKER.internet().safeEmailAddress() + " " + UUID.randomUUID();

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getAge().equals(customer.getAge()));
            assertThat(c.getEmail().equals(newEmail));
        });
    }

    @Test
    void updateCustomerAge() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = new Random().nextInt(20,85);

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getAge().equals(newAge));
            assertThat(c.getEmail().equals(customer.getEmail()));
        });
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = new Random().nextInt(20,85);
        String newEmail = FAKER.internet().safeEmailAddress() + " " + UUID.randomUUID();
        String newName = "test";

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);
        update.setAge(newAge);
        update.setName(newName);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValue(update);
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getAge().equals(customer.getAge()));
            assertThat(c.getEmail().equals(customer.getEmail()));
        });
    }
}
