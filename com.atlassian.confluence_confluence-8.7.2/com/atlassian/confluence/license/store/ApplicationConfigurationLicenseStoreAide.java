/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.license.store;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.license.store.ApplicationConfigurationLicenseStore;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@ParametersAreNonnullByDefault
@Internal
public class ApplicationConfigurationLicenseStoreAide
implements InitializingBean,
DisposableBean {
    private final ApplicationConfigurationLicenseStore store;
    private final EventPublisher publisher;

    public ApplicationConfigurationLicenseStoreAide(ApplicationConfigurationLicenseStore store, EventPublisher publisher) {
        this.store = (ApplicationConfigurationLicenseStore)Preconditions.checkNotNull((Object)store);
        this.publisher = (EventPublisher)Preconditions.checkNotNull((Object)publisher);
    }

    public void afterPropertiesSet() throws Exception {
        this.store.notifyPublisherAvailable(this.publisher);
    }

    public void destroy() throws Exception {
        this.store.notifyPublisherUnavailable();
    }
}

