/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.jwt.JwtIssuer
 *  com.atlassian.jwt.SigningAlgorithm
 *  com.atlassian.jwt.internal.security.SecretGenerator
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.internal.security.SecretGenerator;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceNotificationJwtIssuer
implements JwtIssuer {
    private static final long LOCK_TIMEOUT_MS = 30000L;
    private static final String CONFLUENCE_ISSUER = "confluence_notifications";
    private static final String CONFLUENCE_JWT_SECRET_KEY = "confluence.jwt.secret.key";
    private static final String CONFLUENCE_SETUP_SERVER_ID = "confluence.setup.server.id";
    private static final String SHARED_SECRET_KEY_LOCK_NAME = "shared.secret.key.lock.name";
    private static final Logger log = LoggerFactory.getLogger(ConfluenceNotificationJwtIssuer.class);
    private final ApplicationConfiguration applicationConfiguration;
    private final BandanaManager bandanaManager;
    private final ClusterLockService clusterLockService;
    private String secretKey = "";

    public ConfluenceNotificationJwtIssuer(ApplicationConfiguration applicationConfiguration, BandanaManager bandanaManager, ClusterLockService clusterLockService) {
        this.applicationConfiguration = applicationConfiguration;
        this.bandanaManager = bandanaManager;
        this.clusterLockService = clusterLockService;
    }

    public String getName() {
        return CONFLUENCE_ISSUER + this.applicationConfiguration.getProperty((Object)CONFLUENCE_SETUP_SERVER_ID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getSharedSecret() {
        if (StringUtils.isEmpty((CharSequence)this.secretKey)) {
            ClusterLock lock = this.clusterLockService.getLockForName(SHARED_SECRET_KEY_LOCK_NAME);
            boolean locked = false;
            try {
                locked = lock.tryLock(30000L, TimeUnit.MILLISECONDS);
                if (locked) {
                    this.secretKey = (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, CONFLUENCE_JWT_SECRET_KEY);
                    if (StringUtils.isEmpty((CharSequence)this.secretKey)) {
                        this.secretKey = this.createSharedSecretKey();
                        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, CONFLUENCE_JWT_SECRET_KEY, (Object)this.secretKey);
                    }
                } else {
                    log.warn("Could not obtain lock with key | {} | within | {} | milliseconds", (Object)SHARED_SECRET_KEY_LOCK_NAME, (Object)30000L);
                }
            }
            catch (InterruptedException e) {
                log.warn("Interrupted while waiting for lock with key | {} |", (Object)SHARED_SECRET_KEY_LOCK_NAME);
            }
            finally {
                if (locked) {
                    lock.unlock();
                }
            }
        }
        return this.secretKey;
    }

    private String createSharedSecretKey() {
        return SecretGenerator.generateUrlSafeSharedSecret((SigningAlgorithm)SigningAlgorithm.HS256);
    }
}

