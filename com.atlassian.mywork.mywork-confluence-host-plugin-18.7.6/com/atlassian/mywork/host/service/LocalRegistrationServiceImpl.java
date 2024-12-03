/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.model.Registration$RegistrationId
 *  com.atlassian.mywork.service.ClientRegistrationService
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.host.dao.RegistrationDao;
import com.atlassian.mywork.host.event.ActiveObjectsInitializedEvent;
import com.atlassian.mywork.host.event.RegistrationChangedEvent;
import com.atlassian.mywork.host.service.ActiveObjectsController;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.mywork.host.util.concurrent.LockedTaskExecutor;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.service.ClientRegistrationService;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class LocalRegistrationServiceImpl
implements LocalRegistrationService,
LifecycleAware,
InitializingBean,
DisposableBean {
    public static String PLUGIN_KEY = "com.atlassian.mywork.mywork-confluence-host-plugin";
    private static final Long DEFAULT_LOCK_TIMEOUT = Long.parseLong(System.getProperty("confluence.mywork.lock.timeout", "5000"));
    private static final Logger log = LoggerFactory.getLogger(LocalRegistrationServiceImpl.class);
    public static final String ID_HOST = "";
    private final AtomicBoolean isReadyToInitialize = new AtomicBoolean(false);
    private final List<Runnable> startUpActions;
    private final RegistrationDao registrationDao;
    private final ClientRegistrationService clientRegistrationService;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private final Cache<CacheKey, Date> lastModifiedCache;
    private final LockedTaskExecutor lockedTaskExecutor;
    private final ActiveObjectsController activeObjectsController;
    private final PluginEventManager pluginEventManager;

    public LocalRegistrationServiceImpl(RegistrationDao registrationDao, @ComponentImport ClientRegistrationService clientRegistrationService, TransactionTemplate transactionTemplate, EventPublisher eventPublisher, @ComponentImport ClusterLockService clusterLockService, @ComponentImport CacheManager cacheManager, ActiveObjectsController activeObjectsController, @ComponentImport PluginEventManager pluginEventManager) {
        this.registrationDao = registrationDao;
        this.clientRegistrationService = clientRegistrationService;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.lastModifiedCache = cacheManager.getCache("registration-service-shared-data");
        this.lockedTaskExecutor = new LockedTaskExecutor((Lock)clusterLockService.getLockForName(this.getClass().getName()));
        this.startUpActions = new ArrayList<Runnable>(1);
        this.activeObjectsController = activeObjectsController;
        this.pluginEventManager = pluginEventManager;
    }

    public void register(Iterable<Registration> registrations) {
        this.lockedTaskExecutor.executeUnderLock(() -> this.registerInternal(registrations), (Supplier<String>)((Supplier)() -> "registering " + registrations), (long)DEFAULT_LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void registerInternal(Iterable<Registration> registrations) {
        this.transactionTemplate.execute(() -> {
            for (Registration registration : registrations) {
                this.register(registration, new Date());
            }
            return null;
        });
    }

    @Nonnull
    private Optional<Date> lastModified() {
        return Optional.ofNullable((Date)this.lastModifiedCache.get((Object)CacheKey.KEY));
    }

    private void setLastModified(Date date) {
        this.lastModifiedCache.put((Object)CacheKey.KEY, (Object)DateUtils.truncate((Date)date, (int)13));
    }

    protected void register(Registration registration, Date date) {
        if (registration == null) {
            return;
        }
        this.lockedTaskExecutor.executeUnderLock(() -> this.registerInternal(registration, date), (Supplier<String>)((Supplier)() -> "registering " + registration + " for " + date), (long)DEFAULT_LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void registerInternal(Registration registration, Date date) {
        Registration old = this.registrationDao.get(registration.getId());
        if (old == null || !old.equals((Object)registration)) {
            this.registrationDao.set(registration);
            this.setLastModified(date);
            this.eventPublisher.publish((Object)new RegistrationChangedEvent(this, registration));
        }
        log.debug("Registered {}", (Object)registration.getAppId());
    }

    @Override
    public Option<Pair<List<Registration>, Date>> getAll(Date ifModifiedSince) {
        Date date = this.getLastModified();
        if (ifModifiedSince.compareTo(date) < 0) {
            return Option.some((Object)Pair.pair(this.registrationDao.getAll(), (Object)date));
        }
        return Option.none();
    }

    @Override
    public Date getLastModified() {
        Optional<Date> last = this.lastModified();
        if (last.isPresent()) {
            return last.get();
        }
        return this.lockedTaskExecutor.executeUnderLock(this::getLastModifiedInternal, "getting last modified date", (long)DEFAULT_LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private Date getLastModifiedInternal() {
        if (!this.lastModified().isPresent()) {
            Date lastModified = this.registrationDao.getMostRecentUpdate();
            this.setLastModified(lastModified);
            return lastModified;
        }
        return this.lastModified().get();
    }

    @Override
    public String getCacheValue(Locale locale) {
        return locale.toString() + "-" + this.getLastModified().getTime();
    }

    @EventListener
    public void onGlobalSettingsChangedEvent(GlobalSettingsChangedEvent event) {
        if (!event.getOldDomainName().equals(event.getNewDomainName())) {
            this.register(this.clientRegistrationService.createRegistrations());
        }
    }

    @Override
    public Registration get(Registration.RegistrationId id) {
        return this.registrationDao.get(id);
    }

    public void onStart() {
        this.isReadyToInitialize.compareAndSet(false, true);
        this.initConfluenceHostPlugin();
    }

    public void onStop() {
    }

    public void afterPropertiesSet() {
        this.pluginEventManager.register((Object)this);
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.pluginEventManager.unregister((Object)this);
        this.eventPublisher.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginStarted(PluginEnabledEvent pluginEnabledEvent) {
        String startUpPluginKey = pluginEnabledEvent.getPlugin().getKey();
        if (startUpPluginKey.equals(PLUGIN_KEY)) {
            this.startUpActions.add(() -> {
                log.debug("{} plugin started.", (Object)pluginEnabledEvent.getPlugin().getName());
                this.activeObjectsController.monitorInitialization();
            });
            this.initConfluenceHostPlugin();
        }
    }

    @EventListener
    public void onActiveObjectsInitialized(ActiveObjectsInitializedEvent event) {
        long startInitializedTime = System.currentTimeMillis();
        ImmutableList registrations = ImmutableList.copyOf((Iterable)this.clientRegistrationService.createRegistrations());
        this.transactionTemplate.execute(() -> this.lambda$onActiveObjectsInitialized$6((List)registrations));
        log.debug("onActiveObjectsInitialized executed with the cluster lock for: {}", (Object)(System.currentTimeMillis() - startInitializedTime));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initConfluenceHostPlugin() {
        if (this.isReadyToInitialize.get()) {
            List<Runnable> list = this.startUpActions;
            synchronized (list) {
                for (Runnable action : this.startUpActions) {
                    action.run();
                }
                this.startUpActions.clear();
            }
        }
    }

    private /* synthetic */ Object lambda$onActiveObjectsInitialized$6(List registrations) {
        this.setLastModified(this.registrationDao.getMostRecentUpdate());
        for (Registration registration : registrations) {
            this.register(registration, new Date());
        }
        return null;
    }

    private static enum CacheKey {
        KEY;

    }
}

