package com.amigoscode.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("age")).thenReturn(34);
        when(resultSet.getString("name")).thenReturn("test");
        when(resultSet.getString("email")).thenReturn("test@gmail.com");

        //When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        //Then
        Customer expected = new Customer(1, "test", "test@gmail.com", 34);

        assertThat(actual).isEqualTo(expected);
    }
}
