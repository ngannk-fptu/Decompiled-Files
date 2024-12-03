/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.confluence.license.BaseLicenseDetailsImpl;
import com.atlassian.sal.confluence.license.SalLicenseChangedEvent;
import java.util.Optional;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Internal
public class LicenseUpdateListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;

    public LicenseUpdateListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onConfluenceLicenseChanged(LicenceUpdatedEvent event) {
        ProductLicense productLicense = event.getLicense().getProductLicense(Product.CONFLUENCE);
        ProductLicense previousProductLicense = Optional.ofNullable(event.getPreviousLicense()).map(l -> l.getProductLicense(Product.CONFLUENCE)).orElse(null);
        if (productLicense instanceof ConfluenceLicense && (previousProductLicense == null || previousProductLicense instanceof ConfluenceLicense)) {
            BaseLicenseDetailsImpl currentLicense = new BaseLicenseDetailsImpl((ConfluenceLicense)productLicense);
            BaseLicenseDetails previousLicense = Optional.ofNullable(previousProductLicense).map(ConfluenceLicense.class::cast).map(BaseLicenseDetailsImpl::new).orElse(null);
            this.eventPublisher.publish((Object)new SalLicenseChangedEvent(currentLicense, previousLicense));
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

