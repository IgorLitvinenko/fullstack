package com.amigoscode.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
@RequiredArgsConstructor
@Slf4j
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper rowMapper;

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT * FROM customer
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sql = """
                SELECT * FROM customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;

        int result = jdbcTemplate.update(sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge());

        log.info("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String sql = """
                SELECT count(id) FROM customer WHERE name = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        String sql = """
                SELECT count(id) FROM customer WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        String sql = """
                DELETE FROM customer WHERE id = ?
                """;
        int update = jdbcTemplate.update(sql, id);
        log.info("delete customer with id %s ".formatted(id) + " " + update);
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
            log.info("update customer name result = " + result);
        }
        if (update.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
            log.info("update customer age result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId()
            );
            log.info("update customer email result = " + result);
        }
    }
}
