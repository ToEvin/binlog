package com.insistingon.binlog.application;


import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.BinlogPortalStarter;
import com.insistingon.binlog.autoconfig.BinlogPortalBootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({BinlogPortalBootConfig.class})
public class ApplicationStart implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(ApplicationStart.class);

    @Autowired
    BinlogPortalStarter binlogPortalStarter;
    @Autowired
    BinlogPortalBootConfig config;

    @Override
    public void run(String... args) throws Exception {
        if(!config.getEnable()){
            return;
        }
        try {
            binlogPortalStarter.start();
        } catch (BinlogPortalException e) {
            log.error(e.getMessage(), e);
        }
    }
}
