/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ConfigurationProperties(prefix = "spring.jdbc")
public class JdbcConfiguration {
    private String driverName;
    private String driverUrl;
    private String username;
    private String password;

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverUrl() {
        return driverUrl;
    }

    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean(destroyMethod = "")
    public DataSource getDataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(getDriverName());
        dataSource.setUrl(getDriverUrl());
        dataSource.setUsername(getUsername());
        dataSource.setPassword(getPassword());
        dataSource.setInitialSize(2);
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(20);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}