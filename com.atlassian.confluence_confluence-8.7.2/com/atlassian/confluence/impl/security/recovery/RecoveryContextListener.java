/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security.recovery;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.security.recovery.RecoveryUtil;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.johnson.Johnson;
import com.atlassian.spring.container.ContainerManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecoveryContextListener
implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(RecoveryContextListener.class);

    public void contextInitialized(ServletContextEvent event) {
        if (!this.shouldCheckRecovery()) {
            return;
        }
        if (RecoveryUtil.isRecoveryMode()) {
            log.info("Confluence is running under recovery mode.");
            ConfluenceUserDao userDao = (ConfluenceUserDao)ContainerManager.getComponent((String)"confluenceUserDao");
            ConfluenceUser user = userDao.findByUsername("recovery_admin");
            if (user == null) {
                user = new ConfluenceUserImpl("recovery_admin", "recovery_admin", "");
                userDao.create(user);
                log.info("Recovery admin user is created.");
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

    private boolean shouldCheckRecovery() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        return bootstrapManager != null && bootstrapManager.isSetupComplete() && ContainerManager.isContainerSetup() && !this.johnsonHasErrors();
    }

    private boolean johnsonHasErrors() {
        return JohnsonUtils.eventExists(Johnson.getEventContainer(), JohnsonEventPredicates.blocksStartup());
    }
}

