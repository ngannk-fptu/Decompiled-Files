/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEntity$Builder
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.core.impl.broker.AuditBroker;
import com.atlassian.audit.core.spi.service.AuditMethodProvider;
import com.atlassian.audit.core.spi.service.BaseUrlProvider;
import com.atlassian.audit.core.spi.service.ClusterNodeProvider;
import com.atlassian.audit.core.spi.service.CurrentUserProvider;
import com.atlassian.audit.core.spi.service.IpAddressProvider;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SessionBasedAuditService
implements AuditService {
    private final AuditBroker broker;
    private final CurrentUserProvider currentUserProvider;
    private final IpAddressProvider ipAddressProvider;
    private final AuditMethodProvider methodProvider;
    private final BaseUrlProvider baseUrlProvider;
    private final ClusterNodeProvider nodeProvider;

    public SessionBasedAuditService(AuditBroker broker, CurrentUserProvider currentUserProvider, IpAddressProvider ipAddressProvider, AuditMethodProvider methodProvider, BaseUrlProvider baseUrlProvider, ClusterNodeProvider nodeProvider) {
        this.broker = broker;
        this.currentUserProvider = currentUserProvider;
        this.ipAddressProvider = ipAddressProvider;
        this.methodProvider = methodProvider;
        this.baseUrlProvider = baseUrlProvider;
        this.nodeProvider = nodeProvider;
    }

    public void audit(@Nonnull AuditEvent event) {
        AuditEntity.Builder builder = AuditEntity.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)Optional.ofNullable(event.getArea()).orElse(CoverageArea.ECOSYSTEM), (CoverageLevel)event.getLevel(), (String)event.getCategoryI18nKey(), (String)event.getActionI18nKey()).build()).affectedObjects(event.getAffectedObjects()).changedValues(event.getChangedValues()).extraAttributes(event.getExtraAttributes()).timestamp(Instant.now()).author(this.determineAuthor()).source(this.determineSource()).method(this.determineMethod()).system(this.determineSystem()).node(this.determineNode());
        if (this.ipAddressProvider.forwarderIpAddress().isPresent()) {
            builder.extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.forwarder", (String)this.ipAddressProvider.forwarderIpAddress().get()).build());
        }
        this.broker.audit(builder.build());
    }

    @Nullable
    public String determineNode() {
        return this.nodeProvider.currentNodeId().orElse(null);
    }

    @Nonnull
    public String determineSystem() {
        return this.baseUrlProvider.currentBaseUrl();
    }

    @Nonnull
    public String determineMethod() {
        return this.methodProvider.currentMethod();
    }

    @Nullable
    public String determineSource() {
        return this.ipAddressProvider.remoteIpAddress();
    }

    @Nonnull
    public AuditAuthor determineAuthor() {
        return this.currentUserProvider.currentUser();
    }
}

