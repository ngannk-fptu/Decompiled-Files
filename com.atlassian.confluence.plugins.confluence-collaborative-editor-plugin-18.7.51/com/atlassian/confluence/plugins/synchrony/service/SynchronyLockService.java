/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.plugins.synchrony.service.http.InvalidJwtTokenException;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyLockRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyRequestExecutor;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyUnlockRequest;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component(value="synchrony-lock-service")
public class SynchronyLockService {
    private final SynchronyConfigurationManager configurationManager;
    private final SynchronyJsonWebTokenGenerator tokenGenerator;
    private final SynchronyRequestExecutor requestExecutor;

    public SynchronyLockService(SynchronyConfigurationManager configurationManager, SynchronyJsonWebTokenGenerator tokenGenerator, SynchronyRequestExecutor requestExecutor) {
        this.configurationManager = configurationManager;
        this.tokenGenerator = tokenGenerator;
        this.requestExecutor = requestExecutor;
    }

    void lockContent(String lockId, Collection<Long> contentIds, Long timeout) {
        this.lockContentWithRetry(new SynchronyLockRequest(this.getLockingUrl(lockId), this.tokenGenerator.createAdminToken(), contentIds, timeout));
    }

    void lockContent(String lockId, Long timeout) {
        this.lockContentWithRetry(new SynchronyLockRequest(this.getLockingUrl(lockId), this.tokenGenerator.createAdminToken(), timeout));
    }

    private void lockContentWithRetry(SynchronyLockRequest lockRequest) {
        try {
            this.requestExecutor.execute(lockRequest);
        }
        catch (InvalidJwtTokenException e) {
            this.configurationManager.retrievePublicKey();
            this.requestExecutor.execute(lockRequest);
        }
    }

    void unlockContent(String lockId) {
        try {
            this.requestExecutor.execute(new SynchronyUnlockRequest(this.getLockingUrl(lockId), this.tokenGenerator.createAdminToken()));
        }
        catch (InvalidJwtTokenException e) {
            this.configurationManager.retrievePublicKey();
            this.requestExecutor.execute(new SynchronyUnlockRequest(this.getLockingUrl(lockId), this.tokenGenerator.createAdminToken()));
        }
    }

    private String getLockingUrl(String lockId) {
        return this.configurationManager.getInternalServiceUrl() + "/hub-lock-state?lock-id=" + lockId;
    }
}

