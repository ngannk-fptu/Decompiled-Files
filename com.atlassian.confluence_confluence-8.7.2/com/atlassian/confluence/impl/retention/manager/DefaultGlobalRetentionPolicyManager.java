/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.event.api.EventPublisher
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.event.events.retention.GlobalRetentionPolicyChangedEvent;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGlobalRetentionPolicyManager
implements GlobalRetentionPolicyManager {
    static final String GLOBAL_RETENTION_POLICY_KEY = "com.atlassian.confluence.impl.content.retentionrules:global-retention-policy";
    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalRetentionPolicyManager.class);
    private static final GlobalRetentionPolicy DEFAULT_POLICY = new GlobalRetentionPolicy();
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public DefaultGlobalRetentionPolicyManager(BandanaManager bandanaManager, EventPublisher eventPublisher) {
        this(bandanaManager, eventPublisher, new ObjectMapper());
    }

    protected DefaultGlobalRetentionPolicyManager(BandanaManager bandanaManager, EventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public GlobalRetentionPolicy getPolicy() {
        Object policy = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, GLOBAL_RETENTION_POLICY_KEY);
        logger.debug("Existing GlobalRetentionPolicy is: {}", policy);
        if (policy != null) {
            try {
                return (GlobalRetentionPolicy)this.objectMapper.readValue((String)policy, GlobalRetentionPolicy.class);
            }
            catch (IOException e) {
                logger.error("Error parsing GlobalRetentionPolicy: {}", policy, (Object)e);
            }
        }
        logger.debug("Returning default GlobalRetentionPolicy");
        return DEFAULT_POLICY;
    }

    @Override
    public void savePolicy(GlobalRetentionPolicy newPolicy) {
        newPolicy.setLastModifiedBy(this.getAuthenticatedUserKey());
        try {
            GlobalRetentionPolicy oldPolicy = this.getPolicy();
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, GLOBAL_RETENTION_POLICY_KEY, (Object)this.objectMapper.writeValueAsString((Object)newPolicy));
            this.auditChange(oldPolicy, newPolicy);
        }
        catch (IOException e) {
            logger.error("Error persisting global retention policy: {}", (Object)newPolicy, (Object)e);
        }
    }

    private void auditChange(GlobalRetentionPolicy oldPolicy, GlobalRetentionPolicy newPolicy) {
        this.eventPublisher.publish((Object)new GlobalRetentionPolicyChangedEvent(oldPolicy, newPolicy));
    }

    private String getAuthenticatedUserKey() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return user.getKey().getStringValue();
    }
}

