/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

public class ScheduledAnnotationBeanPostProcessor
implements ScheduledTaskHolder,
MergedBeanDefinitionPostProcessor,
DestructionAwareBeanPostProcessor,
Ordered,
EmbeddedValueResolverAware,
BeanNameAware,
BeanFactoryAware,
ApplicationContextAware,
SmartInitializingSingleton,
ApplicationListener<ContextRefreshedEvent>,
DisposableBean {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Object scheduler;
    @Nullable
    private StringValueResolver embeddedValueResolver;
    @Nullable
    private String beanName;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private ApplicationContext applicationContext;
    private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
    private final Map<Object, Set<ScheduledTask>> scheduledTasks = new IdentityHashMap<Object, Set<ScheduledTask>>(16);

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setScheduler(Object scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.nonAnnotatedClasses.clear();
        if (this.applicationContext == null) {
            this.finishRegistration();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            this.finishRegistration();
        }
    }

    private void finishRegistration() {
        if (this.scheduler != null) {
            this.registrar.setScheduler(this.scheduler);
        }
        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, SchedulingConfigurer> beans2 = ((ListableBeanFactory)this.beanFactory).getBeansOfType(SchedulingConfigurer.class);
            ArrayList<SchedulingConfigurer> configurers = new ArrayList<SchedulingConfigurer>(beans2.values());
            AnnotationAwareOrderComparator.sort(configurers);
            for (SchedulingConfigurer configurer : configurers) {
                configurer.configureTasks(this.registrar);
            }
        }
        if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set to find scheduler by type");
            try {
                this.registrar.setTaskScheduler(this.resolveSchedulerBean(this.beanFactory, TaskScheduler.class, false));
            }
            catch (NoUniqueBeanDefinitionException ex) {
                this.logger.debug("Could not find unique TaskScheduler bean", ex);
                try {
                    this.registrar.setTaskScheduler(this.resolveSchedulerBean(this.beanFactory, TaskScheduler.class, true));
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("More than one TaskScheduler bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex.getBeanNamesFound());
                    }
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.logger.debug("Could not find default TaskScheduler bean", ex);
                try {
                    this.registrar.setScheduler(this.resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, false));
                }
                catch (NoUniqueBeanDefinitionException ex2) {
                    this.logger.debug("Could not find unique ScheduledExecutorService bean", ex2);
                    try {
                        this.registrar.setScheduler(this.resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, true));
                    }
                    catch (NoSuchBeanDefinitionException ex3) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("More than one ScheduledExecutorService bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex2.getBeanNamesFound());
                        }
                    }
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    this.logger.debug("Could not find default ScheduledExecutorService bean", ex2);
                    this.logger.info("No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
                }
            }
        }
        this.registrar.afterPropertiesSet();
    }

    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> schedulerType, boolean byName) {
        if (byName) {
            T scheduler = beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, schedulerType);
            if (this.beanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory)this.beanFactory).registerDependentBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, this.beanName);
            }
            return scheduler;
        }
        if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder<T> holder = ((AutowireCapableBeanFactory)beanFactory).resolveNamedBean(schedulerType);
            if (this.beanName != null && beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory)beanFactory).registerDependentBean(holder.getBeanName(), this.beanName);
            }
            return holder.getBeanInstance();
        }
        return beanFactory.getBean(schedulerType);
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) {
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) {
        if (bean2 instanceof AopInfrastructureBean) {
            return bean2;
        }
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean2);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set> annotatedMethods = MethodIntrospector.selectMethods(targetClass, method -> {
                Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
                return !scheduledMethods.isEmpty() ? scheduledMethods : null;
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
                }
            } else {
                annotatedMethods.forEach((method, scheduledMethods) -> scheduledMethods.forEach(scheduled -> this.processScheduled((Scheduled)scheduled, (Method)method, bean2)));
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processScheduled(Scheduled scheduled, Method method, Object bean2) {
        try {
            String fixedRateString;
            long fixedRate;
            String fixedDelayString;
            long fixedDelay;
            String cron;
            Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @Scheduled");
            Method invocableMethod = AopUtils.selectInvocableMethod(method, bean2.getClass());
            ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(bean2, invocableMethod);
            boolean processedSchedule = false;
            String errorMessage = "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";
            LinkedHashSet<ScheduledTask> tasks = new LinkedHashSet<ScheduledTask>(4);
            long initialDelay = scheduled.initialDelay();
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText(initialDelayString)) {
                Assert.isTrue(initialDelay < 0L, "Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                if (StringUtils.hasLength(initialDelayString)) {
                    try {
                        initialDelay = ScheduledAnnotationBeanPostProcessor.parseDelayAsLong(initialDelayString);
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into long");
                    }
                }
            }
            if (StringUtils.hasText(cron = scheduled.cron())) {
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength(cron)) {
                    Assert.isTrue(initialDelay == -1L, "'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    TimeZone timeZone = StringUtils.hasText(zone) ? StringUtils.parseTimeZoneString(zone) : TimeZone.getDefault();
                    tasks.add(this.registrar.scheduleCronTask(new CronTask((Runnable)runnable, new CronTrigger(cron, timeZone))));
                }
            }
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            if ((fixedDelay = scheduled.fixedDelay()) >= 0L) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
            }
            if (StringUtils.hasText(fixedDelayString = scheduled.fixedDelayString())) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength(fixedDelayString)) {
                    Assert.isTrue(!processedSchedule, errorMessage);
                    processedSchedule = true;
                    try {
                        fixedDelay = ScheduledAnnotationBeanPostProcessor.parseDelayAsLong(fixedDelayString);
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
                }
            }
            if ((fixedRate = scheduled.fixedRate()) >= 0L) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
            }
            if (StringUtils.hasText(fixedRateString = scheduled.fixedRateString())) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength(fixedRateString)) {
                    Assert.isTrue(!processedSchedule, errorMessage);
                    processedSchedule = true;
                    try {
                        fixedRate = ScheduledAnnotationBeanPostProcessor.parseDelayAsLong(fixedRateString);
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
                }
            }
            Assert.isTrue(processedSchedule, errorMessage);
            Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
            synchronized (map) {
                Set regTasks = this.scheduledTasks.computeIfAbsent(bean2, key -> new LinkedHashSet(4));
                regTasks.addAll(tasks);
            }
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    private static long parseDelayAsLong(String value) throws RuntimeException {
        if (value.length() > 1 && (ScheduledAnnotationBeanPostProcessor.isP(value.charAt(0)) || ScheduledAnnotationBeanPostProcessor.isP(value.charAt(1)))) {
            return Duration.parse(value).toMillis();
        }
        return Long.parseLong(value);
    }

    private static boolean isP(char ch) {
        return ch == 'P' || ch == 'p';
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<ScheduledTask> getScheduledTasks() {
        LinkedHashSet<ScheduledTask> result = new LinkedHashSet<ScheduledTask>();
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                result.addAll(tasks);
            }
        }
        result.addAll(this.registrar.getScheduledTasks());
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void postProcessBeforeDestruction(Object bean2, String beanName) {
        Set<ScheduledTask> tasks;
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            tasks = this.scheduledTasks.remove(bean2);
        }
        if (tasks != null) {
            for (ScheduledTask task : tasks) {
                task.cancel();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean requiresDestruction(Object bean2) {
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            return this.scheduledTasks.containsKey(bean2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                for (ScheduledTask task : tasks) {
                    task.cancel();
                }
            }
            this.scheduledTasks.clear();
        }
        this.registrar.destroy();
    }
}

