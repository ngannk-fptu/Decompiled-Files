/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.SplitStartupPluginSystemLifecycle
 *  com.atlassian.sal.spi.HostContextAccessor
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  com.google.common.base.Throwables
 *  org.apache.struts2.ServletActionContext
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.impl.setup.DelegatingBootstrapConfigurer;
import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.setup.actions.ConfluenceSetupPersister;
import com.atlassian.confluence.tenant.SystemTenant;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.SplitStartupPluginSystemLifecycle;
import com.atlassian.sal.spi.HostContextAccessor;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.XsrfTokenGenerator;
import com.google.common.base.Throwables;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

public abstract class AbstractSetupAction
extends ConfluenceActionSupport {
    public static final String DEMO_CONTENT_ZIP_FILE = "demo-site.zip";
    protected static final String SETUP_QUICK = "quick-setup";
    protected static final String SETUP_CLUSTER = "cluster-setup";
    protected static final String SETUP_CUSTOM = "custom-setup";
    protected static final String SETUP_CLUSTER_TO_STANDALONE = "cluster-to-standalone";
    protected static final String SETUP_USER_INTERNAL = "internal-user";
    protected static final String SETUP_USER_JAACS = "jaacs-user";
    protected static final String SETUP_DB_EMBEDDED = "input-embedded-db";
    protected static final String SETUP_DB_STANDARD = "input-standard-db";
    protected static final String SETUP_DB_DATASOURCE = "setupdatasource";
    protected static final String SETUP_DB_CONN_TYPE = "setupdb";
    protected static final String SETUP_DB_CLUSTER_CONN_TYPE = "setupdbcluster";
    protected static final String SETUP_DB_TYPE_STANDARD = "database-type-standard";
    protected static final String SETUP_DB_TYPE_DATASOURCE = "database-type-datasource";
    protected static final String SETUP_CONTENT_BLANK = "blank";
    protected static final String SETUP_CONTENT_DEMO = "demo";
    protected static final String SETUP_CONTENT_IMPORT = "import";
    protected static final String SETUP_SESSION_ID_KEY = "setup-session-id";
    protected static final String SETUP_PATHS = "setup-paths";
    protected static final String SETUP_SKIP_TO_NEXT_STEP = "skipToNextStep";
    protected static final String SETUP_SKIP_TO_NEXT_STEP_CLUSTER = "skipToNextStepCluster";
    private ConfluenceSetupPersister setupPersister;
    private ContainerManager containerManager;
    private LicenseService licenseService;
    private ConfluenceSidManager bootstrapSidManager;
    private XsrfTokenGenerator xsrfTokenGenerator;
    private BootstrapConfigurer bootstrapConfigurer;

    public void setSetupPersister(ConfluenceSetupPersister setupPersister) {
        this.setupPersister = setupPersister;
    }

    public ConfluenceSetupPersister getSetupPersister() {
        if (this.setupPersister == null) {
            this.setupPersister = (ConfluenceSetupPersister)BootstrapConfigurer.getBootstrapConfigurer().getSetupPersister();
        }
        return this.setupPersister;
    }

    @Override
    public boolean isPermitted() {
        return !GeneralUtil.isSetupComplete();
    }

    @Override
    protected BootstrapManager getBootstrapManager() {
        return (BootstrapManager)BootstrapUtils.getBootstrapManager();
    }

    protected BootstrapConfigurer bootstrapConfigurer() {
        if (this.bootstrapConfigurer != null) {
            return this.bootstrapConfigurer;
        }
        return new DelegatingBootstrapConfigurer(this.getBootstrapManager());
    }

    @Override
    public Locale getLocale() {
        ApplicationConfiguration applicationConfig;
        String localeString;
        ApplicationContext bootstrapContext = BootstrapUtils.getBootstrapContext();
        if (bootstrapContext != null && (localeString = (String)(applicationConfig = (ApplicationConfiguration)bootstrapContext.getBean("applicationConfig")).getProperty((Object)"confluence.setup.locale")) != null) {
            return LocaleParser.toLocale(localeString);
        }
        return LocaleManager.DEFAULT_LOCALE;
    }

    @Deprecated(forRemoval=true)
    protected void transitionFromColdToTenantedState() throws ConfigurationException {
        this.transitionFromColdToVacantState();
        this.bootstrapConfigurer().setProperty("hibernate.setup", "true");
        this.transitionFromVacantToTenantedState();
    }

    @Deprecated(forRemoval=true)
    protected void transitionFromColdToVacantState() throws ConfigurationException {
        try {
            ThreadLocalTenantGate.withoutTenantPermit(Executors.callable(() -> {
                ContainerContext containerContext = this.getContainerManager().getContainerContext();
                containerContext.refresh();
                SetupContext.destroy();
                this.retrieveSingletonFromMainContext(SplitStartupPluginSystemLifecycle.class).get().earlyStartup();
            })).call();
        }
        catch (Exception e) {
            Throwables.propagateIfInstanceOf((Throwable)e, ConfigurationException.class);
            throw new ConfigurationException(e.getMessage(), (Throwable)e);
        }
    }

    @Deprecated(forRemoval=true)
    protected void transitionFromVacantToTenantedState() {
        this.performLateStartup();
    }

    protected void performEarlyStartup() {
        ContainerContext containerContext = this.getContainerManager().getContainerContext();
        containerContext.refresh();
        SetupContext.destroy();
        this.retrieveSingletonFromMainContext(SplitStartupPluginSystemLifecycle.class).get().earlyStartup();
    }

    protected void performLateStartup() {
        this.retrieveSingletonFromMainContext(SystemTenant.class).get().arrived();
        this.retrieveSingletonFromMainContext(SplitStartupPluginSystemLifecycle.class).get().lateStartup();
    }

    @Deprecated
    protected <T> Maybe<T> getSingletonFromMainContext(Class<T> type) {
        ContainerContext containerContext = this.getContainerManager().getContainerContext();
        if (containerContext == null) {
            return MaybeNot.becauseOf("illegal access, main context is not (yet) initialised", new Object[0]);
        }
        HostContextAccessor hostContextAccessor = (HostContextAccessor)containerContext.getComponent((Object)"hostContextAccessor");
        if (hostContextAccessor == null) {
            return MaybeNot.becauseOf("no bean with id \"hostContextAccessor\" in main context", new Object[0]);
        }
        Map componentsOfType = hostContextAccessor.getComponentsOfType(type);
        if (componentsOfType.size() == 0) {
            return MaybeNot.becauseOf("no bean of type \"%s\" in main context", type);
        }
        if (componentsOfType.size() > 1) {
            return MaybeNot.becauseOf("multiple beans of type \"%s\" in main context", type);
        }
        return Option.some(componentsOfType.values().iterator().next());
    }

    protected <T> Optional<T> retrieveSingletonFromMainContext(Class<T> type) {
        return FugueConversionUtil.toOptional(this.getSingletonFromMainContext(type));
    }

    public ContainerManager getContainerManager() {
        return this.containerManager == null ? ContainerManager.getInstance() : this.containerManager;
    }

    public void setContainerManager(ContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public String getSetupSessionId() {
        String sessionId = (String)ServletActionContext.getRequest().getSession().getAttribute(SETUP_SESSION_ID_KEY);
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            ServletActionContext.getRequest().getSession().setAttribute(SETUP_SESSION_ID_KEY, (Object)sessionId);
        }
        return sessionId;
    }

    public void setBootstrapSidManager(ConfluenceSidManager bootstrapSidManager) throws ConfigurationException {
        this.bootstrapSidManager = bootstrapSidManager;
    }

    public String getXsrfToken() {
        return this.xsrfTokenGenerator.generateToken(this.getCurrentRequest());
    }

    public XsrfTokenGenerator getXsrfTokenGenerator() {
        return this.xsrfTokenGenerator;
    }

    public void setXsrfTokenGenerator(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }

    public String getServerId() {
        if (this.bootstrapSidManager == null) {
            return null;
        }
        try {
            return this.bootstrapSidManager.getSid();
        }
        catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public String getSupportEntitlementNumber() {
        ConfluenceLicense confluenceLicense;
        if (this.licenseService == null) {
            return null;
        }
        try {
            confluenceLicense = this.licenseService.retrieve();
        }
        catch (LicenseException e) {
            return null;
        }
        return confluenceLicense.getSupportEntitlementNumber();
    }

    public void setBootstrapConfigurer(BootstrapConfigurer bootstrapConfigurer) {
        this.bootstrapConfigurer = bootstrapConfigurer;
    }
}

