/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.Scheduler
 *  org.quartz.SchedulerException
 *  org.quartz.SchedulerFactory
 *  org.quartz.impl.RemoteScheduler
 *  org.quartz.impl.SchedulerRepository
 *  org.quartz.impl.StdSchedulerFactory
 *  org.quartz.simpl.SimpleThreadPool
 *  org.quartz.spi.JobFactory
 */
package org.springframework.scheduling.quartz;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.RemoteScheduler;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;
import org.springframework.scheduling.quartz.ResourceLoaderClassLoadHelper;
import org.springframework.scheduling.quartz.SchedulerAccessor;
import org.springframework.scheduling.quartz.SchedulerContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class SchedulerFactoryBean
extends SchedulerAccessor
implements FactoryBean<Scheduler>,
BeanNameAware,
ApplicationContextAware,
InitializingBean,
DisposableBean,
SmartLifecycle {
    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";
    public static final int DEFAULT_THREAD_COUNT = 10;
    private static final ThreadLocal<ResourceLoader> configTimeResourceLoaderHolder = new ThreadLocal();
    private static final ThreadLocal<Executor> configTimeTaskExecutorHolder = new ThreadLocal();
    private static final ThreadLocal<DataSource> configTimeDataSourceHolder = new ThreadLocal();
    private static final ThreadLocal<DataSource> configTimeNonTransactionalDataSourceHolder = new ThreadLocal();
    @Nullable
    private SchedulerFactory schedulerFactory;
    private Class<? extends SchedulerFactory> schedulerFactoryClass = StdSchedulerFactory.class;
    @Nullable
    private String schedulerName;
    @Nullable
    private Resource configLocation;
    @Nullable
    private Properties quartzProperties;
    @Nullable
    private Executor taskExecutor;
    @Nullable
    private DataSource dataSource;
    @Nullable
    private DataSource nonTransactionalDataSource;
    @Nullable
    private Map<String, ?> schedulerContextMap;
    @Nullable
    private String applicationContextSchedulerContextKey;
    @Nullable
    private JobFactory jobFactory;
    private boolean jobFactorySet = false;
    private boolean autoStartup = true;
    private int startupDelay = 0;
    private int phase = Integer.MAX_VALUE;
    private boolean exposeSchedulerInRepository = false;
    private boolean waitForJobsToCompleteOnShutdown = false;
    @Nullable
    private String beanName;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private Scheduler scheduler;

    @Nullable
    public static ResourceLoader getConfigTimeResourceLoader() {
        return configTimeResourceLoaderHolder.get();
    }

    @Nullable
    public static Executor getConfigTimeTaskExecutor() {
        return configTimeTaskExecutorHolder.get();
    }

    @Nullable
    public static DataSource getConfigTimeDataSource() {
        return configTimeDataSourceHolder.get();
    }

    @Nullable
    public static DataSource getConfigTimeNonTransactionalDataSource() {
        return configTimeNonTransactionalDataSourceHolder.get();
    }

    public void setSchedulerFactory(SchedulerFactory schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
    }

    public void setSchedulerFactoryClass(Class<? extends SchedulerFactory> schedulerFactoryClass) {
        this.schedulerFactoryClass = schedulerFactoryClass;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public void setQuartzProperties(Properties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setNonTransactionalDataSource(DataSource nonTransactionalDataSource) {
        this.nonTransactionalDataSource = nonTransactionalDataSource;
    }

    public void setSchedulerContextAsMap(Map<String, ?> schedulerContextAsMap) {
        this.schedulerContextMap = schedulerContextAsMap;
    }

    public void setApplicationContextSchedulerContextKey(String applicationContextSchedulerContextKey) {
        this.applicationContextSchedulerContextKey = applicationContextSchedulerContextKey;
    }

    public void setJobFactory(JobFactory jobFactory) {
        this.jobFactory = jobFactory;
        this.jobFactorySet = true;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Override
    public int getPhase() {
        return this.phase;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }

    public void setExposeSchedulerInRepository(boolean exposeSchedulerInRepository) {
        this.exposeSchedulerInRepository = exposeSchedulerInRepository;
    }

    public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.dataSource == null && this.nonTransactionalDataSource != null) {
            this.dataSource = this.nonTransactionalDataSource;
        }
        if (this.applicationContext != null && this.resourceLoader == null) {
            this.resourceLoader = this.applicationContext;
        }
        this.scheduler = this.prepareScheduler(this.prepareSchedulerFactory());
        try {
            this.registerListeners();
            this.registerJobsAndTriggers();
        }
        catch (Exception ex) {
            try {
                this.scheduler.shutdown(true);
            }
            catch (Exception ex2) {
                this.logger.debug((Object)"Scheduler shutdown exception after registration failure", (Throwable)ex2);
            }
            throw ex;
        }
    }

    private SchedulerFactory prepareSchedulerFactory() throws SchedulerException, IOException {
        SchedulerFactory schedulerFactory = this.schedulerFactory;
        if (schedulerFactory == null) {
            schedulerFactory = BeanUtils.instantiateClass(this.schedulerFactoryClass);
            if (schedulerFactory instanceof StdSchedulerFactory) {
                this.initSchedulerFactory((StdSchedulerFactory)schedulerFactory);
            } else if (this.configLocation != null || this.quartzProperties != null || this.taskExecutor != null || this.dataSource != null) {
                throw new IllegalArgumentException("StdSchedulerFactory required for applying Quartz properties: " + schedulerFactory);
            }
        }
        return schedulerFactory;
    }

    private void initSchedulerFactory(StdSchedulerFactory schedulerFactory) throws SchedulerException, IOException {
        Properties mergedProps = new Properties();
        if (this.resourceLoader != null) {
            mergedProps.setProperty("org.quartz.scheduler.classLoadHelper.class", ResourceLoaderClassLoadHelper.class.getName());
        }
        if (this.taskExecutor != null) {
            mergedProps.setProperty("org.quartz.threadPool.class", LocalTaskExecutorThreadPool.class.getName());
        } else {
            mergedProps.setProperty("org.quartz.threadPool.class", SimpleThreadPool.class.getName());
            mergedProps.setProperty(PROP_THREAD_COUNT, Integer.toString(10));
        }
        if (this.configLocation != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Loading Quartz config from [" + this.configLocation + "]"));
            }
            PropertiesLoaderUtils.fillProperties(mergedProps, this.configLocation);
        }
        CollectionUtils.mergePropertiesIntoMap(this.quartzProperties, mergedProps);
        if (this.dataSource != null) {
            mergedProps.putIfAbsent("org.quartz.jobStore.class", LocalDataSourceJobStore.class.getName());
        }
        if (this.schedulerName != null) {
            mergedProps.setProperty("org.quartz.scheduler.instanceName", this.schedulerName);
        } else {
            String nameProp = mergedProps.getProperty("org.quartz.scheduler.instanceName");
            if (nameProp != null) {
                this.schedulerName = nameProp;
            } else if (this.beanName != null) {
                mergedProps.setProperty("org.quartz.scheduler.instanceName", this.beanName);
                this.schedulerName = this.beanName;
            }
        }
        schedulerFactory.initialize(mergedProps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Scheduler prepareScheduler(SchedulerFactory schedulerFactory) throws SchedulerException {
        if (this.resourceLoader != null) {
            configTimeResourceLoaderHolder.set(this.resourceLoader);
        }
        if (this.taskExecutor != null) {
            configTimeTaskExecutorHolder.set(this.taskExecutor);
        }
        if (this.dataSource != null) {
            configTimeDataSourceHolder.set(this.dataSource);
        }
        if (this.nonTransactionalDataSource != null) {
            configTimeNonTransactionalDataSourceHolder.set(this.nonTransactionalDataSource);
        }
        try {
            Scheduler scheduler = this.createScheduler(schedulerFactory, this.schedulerName);
            this.populateSchedulerContext(scheduler);
            if (!this.jobFactorySet && !(scheduler instanceof RemoteScheduler)) {
                this.jobFactory = new AdaptableJobFactory();
            }
            if (this.jobFactory != null) {
                if (this.applicationContext != null && this.jobFactory instanceof ApplicationContextAware) {
                    ((ApplicationContextAware)this.jobFactory).setApplicationContext(this.applicationContext);
                }
                if (this.jobFactory instanceof SchedulerContextAware) {
                    ((SchedulerContextAware)this.jobFactory).setSchedulerContext(scheduler.getContext());
                }
                scheduler.setJobFactory(this.jobFactory);
            }
            Scheduler scheduler2 = scheduler;
            return scheduler2;
        }
        finally {
            if (this.resourceLoader != null) {
                configTimeResourceLoaderHolder.remove();
            }
            if (this.taskExecutor != null) {
                configTimeTaskExecutorHolder.remove();
            }
            if (this.dataSource != null) {
                configTimeDataSourceHolder.remove();
            }
            if (this.nonTransactionalDataSource != null) {
                configTimeNonTransactionalDataSourceHolder.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, @Nullable String schedulerName) throws SchedulerException {
        boolean overrideClassLoader;
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        boolean bl = overrideClassLoader = this.resourceLoader != null && this.resourceLoader.getClassLoader() != threadContextClassLoader;
        if (overrideClassLoader) {
            currentThread.setContextClassLoader(this.resourceLoader.getClassLoader());
        }
        try {
            SchedulerRepository repository;
            SchedulerRepository schedulerRepository = repository = SchedulerRepository.getInstance();
            synchronized (schedulerRepository) {
                Scheduler existingScheduler = schedulerName != null ? repository.lookup(schedulerName) : null;
                Scheduler newScheduler = schedulerFactory.getScheduler();
                if (newScheduler == existingScheduler) {
                    throw new IllegalStateException("Active Scheduler of name '" + schedulerName + "' already registered in Quartz SchedulerRepository. Cannot create a new Spring-managed Scheduler of the same name!");
                }
                if (!this.exposeSchedulerInRepository) {
                    SchedulerRepository.getInstance().remove(newScheduler.getSchedulerName());
                }
                Scheduler scheduler = newScheduler;
                return scheduler;
            }
        }
        finally {
            if (overrideClassLoader) {
                currentThread.setContextClassLoader(threadContextClassLoader);
            }
        }
    }

    private void populateSchedulerContext(Scheduler scheduler) throws SchedulerException {
        if (this.schedulerContextMap != null) {
            scheduler.getContext().putAll(this.schedulerContextMap);
        }
        if (this.applicationContextSchedulerContextKey != null) {
            if (this.applicationContext == null) {
                throw new IllegalStateException("SchedulerFactoryBean needs to be set up in an ApplicationContext to be able to handle an 'applicationContextSchedulerContextKey'");
            }
            scheduler.getContext().put(this.applicationContextSchedulerContextKey, (Object)this.applicationContext);
        }
    }

    protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
        if (startupDelay <= 0) {
            this.logger.info((Object)"Starting Quartz Scheduler now");
            scheduler.start();
        } else {
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Will start Quartz Scheduler [" + scheduler.getSchedulerName() + "] in " + startupDelay + " seconds"));
            }
            Thread schedulerThread = new Thread(){

                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(startupDelay);
                    }
                    catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (SchedulerFactoryBean.this.logger.isInfoEnabled()) {
                        SchedulerFactoryBean.this.logger.info((Object)("Starting Quartz Scheduler now, after delay of " + startupDelay + " seconds"));
                    }
                    try {
                        scheduler.start();
                    }
                    catch (SchedulerException ex) {
                        throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
                    }
                }
            };
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    @Override
    public Scheduler getScheduler() {
        Assert.state(this.scheduler != null, "No Scheduler set");
        return this.scheduler;
    }

    @Override
    @Nullable
    public Scheduler getObject() {
        return this.scheduler;
    }

    @Override
    public Class<? extends Scheduler> getObjectType() {
        return this.scheduler != null ? this.scheduler.getClass() : Scheduler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void start() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                this.startScheduler(this.scheduler, this.startupDelay);
            }
            catch (SchedulerException ex) {
                throw new SchedulingException("Could not start Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public void stop() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                this.scheduler.standby();
            }
            catch (SchedulerException ex) {
                throw new SchedulingException("Could not stop Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public boolean isRunning() throws SchedulingException {
        if (this.scheduler != null) {
            try {
                return !this.scheduler.isInStandbyMode();
            }
            catch (SchedulerException ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void destroy() throws SchedulerException {
        if (this.scheduler != null) {
            this.logger.info((Object)"Shutting down Quartz Scheduler");
            this.scheduler.shutdown(this.waitForJobsToCompleteOnShutdown);
        }
    }
}

