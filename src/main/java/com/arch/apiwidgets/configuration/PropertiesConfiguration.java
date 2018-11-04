/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class PropertiesConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfiguration.class);
    private static final String ACTIVE_PROFILE_NAME = "spring.profiles.active";
    private static final String TOMCAT_BASE_NAME = "catalina.base";

    /**
     * Support loading external application.properties file when production deployment.
     *
     * @return {@link PropertySourcesPlaceholderConfigurer}
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new ClassPathResource("application.properties"));
        String base = System.getProperty(TOMCAT_BASE_NAME);
        if (StringUtils.hasText(base)) {
            String external = base + "/conf/application.properties";
            File file = new File(external);
            if (file.exists() && !file.isDirectory()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("using external conf: %s", external));
                }
                resources.add(new FileSystemResource(external));
            } else {
                logger.error(String.format("external conf: %s not exist", external));
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("not running from Tomcat.");
        }
        String profile = System.getProperty(ACTIVE_PROFILE_NAME);
        if (StringUtils.hasText(profile)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The active profile is: %s", profile));
            }
            resources.add(new ClassPathResource(String.format("application-%s.properties", profile)));
            resources.add(new FileSystemResource(String.format("%s/conf/application-%s.properties", base, profile)));
        }

        final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        // https://www.baeldung.com/properties-with-spring
        //pspc.setLocalOverride(true);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        pspc.setIgnoreResourceNotFound(true);
        pspc.setLocations(resources.toArray(new Resource[resources.size()]));

        return pspc;
    }
}
