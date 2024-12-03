/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  io.atlassian.util.concurrent.atomic.AtomicReference
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.joda.time.ReadableDuration
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.pac;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.LazyReferences;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.pac.ClientContext;
import com.atlassian.upm.core.pac.ClientContextFactory;
import io.atlassian.util.concurrent.ResettableLazyReference;
import io.atlassian.util.concurrent.atomic.AtomicReference;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClientContextFactory
implements ClientContextFactory {
    private static final Logger log = LoggerFactory.getLogger(BaseClientContextFactory.class);
    private static final Duration CONTEXT_UPDATE_INTERVAL = Duration.standardDays((long)1L);
    private final ApplicationProperties applicationProperties;
    private final ResettableLazyReference<ClientContext> context;
    private final AtomicReference<DateTime> nextContextUpdateDate;
    private final DefaultHostApplicationInformation hostApplicationInformation;

    public BaseClientContextFactory(ApplicationProperties applicationProperties, DefaultHostApplicationInformation hostApplicationInformation) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
        this.context = new ResettableLazyReference<ClientContext>(){

            protected ClientContext create() {
                return BaseClientContextFactory.this.createContext(false).build();
            }
        };
        this.nextContextUpdateDate = new AtomicReference((Object)new DateTime());
    }

    @Override
    public ClientContext getClientContext() {
        return this.getClientContext(false);
    }

    @Override
    public ClientContext getClientContext(boolean forceServerDataCollection) {
        if (forceServerDataCollection) {
            return this.createContext(true).build();
        }
        DateTime now = new DateTime();
        DateTime nextUpdate = now.plus((ReadableDuration)CONTEXT_UPDATE_INTERVAL);
        DateTime updated = (DateTime)this.nextContextUpdateDate.update(input -> now.isBefore((ReadableInstant)input) ? input : nextUpdate);
        if (updated == nextUpdate) {
            this.context.reset();
        }
        return LazyReferences.safeGet(this.context);
    }

    protected abstract String getClientType();

    protected ClientContext.Builder createContext(boolean forceServerDataCollection) {
        log.debug("Refreshing product license/user information");
        ClientContext.Builder builder = new ClientContext.Builder().clientType(this.getClientType()).productName(this.applicationProperties.getDisplayName()).productVersion(this.applicationProperties.getVersion());
        if (Sys.isAnalyticsConfiguredToSendServerInformation() || forceServerDataCollection) {
            builder.onDemand(false).hosting(this.hostApplicationInformation.getHostingType());
        }
        return builder;
    }
}

