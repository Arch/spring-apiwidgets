/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfiguration {
    private int database;
    private String password;
    private String nodes;
    private String master;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration conf = new RedisSentinelConfiguration();
        conf.setMaster(getMaster());
        for (String hostAndPort : StringUtils.commaDelimitedListToSet(getNodes())) {
            String[] args = StringUtils.split(hostAndPort, ":");
            Assert.notNull(args, "HostAndPort need to be separated by ':'");
            Assert.isTrue(args.length == 2, "Host and Port String needs to specified as host:port");
            conf.sentinel(args[0], Integer.valueOf(args[1]));
        }

        conf.setDatabase(getDatabase());
        conf.setPassword(RedisPassword.of(getPassword()));
        return new JedisConnectionFactory(conf);
    }
}