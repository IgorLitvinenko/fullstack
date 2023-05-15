package com.amigoscode;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TestContainersTest extends AbstractTestcontainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        assertThat(postgreSQLContainer.isCreated()).isTrue();
    }

//    @Test
//    void canStartMySQLDB() {
//        assertThat(mySQLContainer.isRunning()).isTrue();
//        assertThat(mySQLContainer.isCreated()).isTrue();
//    }
}
