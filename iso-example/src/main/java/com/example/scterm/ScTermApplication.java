package com.example.scterm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
@EnableJms
public class ScTermApplication {

    private static Logger LOG = LoggerFactory.getLogger(ScTermApplication.class);

    @Autowired
    BuildProperties buildProperties;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ScTermApplication.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        LOG.info("----------------------------------------");
        LOG.info("Name:     {}", buildProperties.getName());
        LOG.info("Group:    {}", buildProperties.getGroup());
        LOG.info("Artifact: {}", buildProperties.getArtifact());
        LOG.info("Version:  {}", buildProperties.getVersion());
        LOG.info("Time:     {}", buildProperties.getTime());
        LOG.info("Profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        LOG.info("----------------------------------------");
    }

}
