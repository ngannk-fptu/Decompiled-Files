/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.authentication.api.config.IdpConfigService
 *  com.atlassian.plugins.authentication.api.config.SsoConfigService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.annotations.VisibleForTesting
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.troubleshooting.stp.properties.appenders.SsoDataFetcher;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class SsoConfigDataAppender
extends RootLevelSupportDataAppender {
    private static final String IDP_CONFIG_SERVICE_CLASS_NAME = "com.atlassian.plugins.authentication.api.config.IdpConfigService";
    private Optional<SsoDataFetcher> ssoDataFetcher = Optional.empty();
    private final BundleContext bundleContext;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SsoConfigDataAppender(BundleContext bundleContext, TransactionTemplate transactionTemplate) {
        this.bundleContext = bundleContext;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void addSupportData(SupportDataBuilder supportBuilder) {
        Optional<SsoDataFetcher> maybeSsoDataFetcher = this.tryToCreateSsoFetcherService();
        if (maybeSsoDataFetcher.isPresent()) {
            SupportDataBuilder categorizedBuilder = supportBuilder.addCategory("stp.properties.sso");
            this.transactionTemplate.execute(() -> {
                ((SsoDataFetcher)maybeSsoDataFetcher.get()).addGenericConfigData(categorizedBuilder);
                ((SsoDataFetcher)maybeSsoDataFetcher.get()).addSpecificConfigData(categorizedBuilder);
                return null;
            });
        }
    }

    @VisibleForTesting
    synchronized Optional<SsoDataFetcher> tryToCreateSsoFetcherService() {
        if (!this.isIdpConfigServiceClassPresent()) {
            return Optional.empty();
        }
        ServiceReference idpConfigServiceServiceReference = this.bundleContext.getServiceReference(IDP_CONFIG_SERVICE_CLASS_NAME);
        ServiceReference ssoConfigServiceServiceReference = this.bundleContext.getServiceReference("com.atlassian.plugins.authentication.api.config.SsoConfigService");
        if (idpConfigServiceServiceReference != null && ssoConfigServiceServiceReference != null) {
            if (this.ssoDataFetcher.isPresent()) {
                return this.ssoDataFetcher;
            }
            try {
                IdpConfigService idpConfigService = (IdpConfigService)this.bundleContext.getService(idpConfigServiceServiceReference);
                SsoConfigService ssoConfigService = (SsoConfigService)this.bundleContext.getService(ssoConfigServiceServiceReference);
                this.setSsoDataFetcher(new SsoDataFetcher(idpConfigService, ssoConfigService));
                return this.ssoDataFetcher;
            }
            catch (BeanInitializationException beanInitializationException) {}
        } else {
            this.clearSsoDataFetcher();
        }
        return this.ssoDataFetcher;
    }

    @VisibleForTesting
    boolean isIdpConfigServiceClassPresent() {
        return ClassUtils.isPresent((String)IDP_CONFIG_SERVICE_CLASS_NAME, (ClassLoader)this.getClass().getClassLoader());
    }

    void setSsoDataFetcher(SsoDataFetcher ssoDataFetcher) {
        this.ssoDataFetcher = Optional.ofNullable(ssoDataFetcher);
    }

    void clearSsoDataFetcher() {
        this.ssoDataFetcher = Optional.empty();
    }
}

