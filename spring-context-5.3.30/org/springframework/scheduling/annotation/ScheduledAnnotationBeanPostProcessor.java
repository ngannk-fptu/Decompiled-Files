/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.NoUniqueBeanDefinitionException
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 *  org.springframework.beans.factory.config.NamedBeanHolder
 *  org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.core.MethodIntrospector
 *  org.springframework.core.Ordered
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.scheduling.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import org.springframework.core.annotation.AnnotationUtils;
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
    private final ScheduledTaskRegistrar registrar;
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
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
    private final Map<Object, Set<ScheduledTask>> scheduledTasks = new IdentityHashMap<Object, Set<ScheduledTask>>(16);

    public ScheduledAnnotationBeanPostProcessor() {
        this.registrar = new ScheduledTaskRegistrar();
    }

    public ScheduledAnnotationBeanPostProcessor(ScheduledTaskRegistrar registrar) {
        Assert.notNull((Object)registrar, (String)"ScheduledTaskRegistrar must not be null");
        this.registrar = registrar;
    }

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

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

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
            Map beans2 = ((ListableBeanFactory)this.beanFactory).getBeansOfType(SchedulingConfigurer.class);
            ArrayList configurers = new ArrayList(beans2.values());
            AnnotationAwareOrderComparator.sort(configurers);
            for (SchedulingConfigurer configurer : configurers) {
                configurer.configureTasks(this.registrar);
            }
        }
        if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
            Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory must be set to find scheduler by type");
            try {
                this.registrar.setTaskScheduler(this.resolveSchedulerBean(this.beanFactory, TaskScheduler.class, false));
            }
            catch (NoUniqueBeanDefinitionException ex) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Could not find unique TaskScheduler bean - attempting to resolve by name: " + ex.getMessage()));
                }
                try {
                    this.registrar.setTaskScheduler(this.resolveSchedulerBean(this.beanFactory, TaskScheduler.class, true));
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info((Object)("More than one TaskScheduler bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex.getBeanNamesFound()));
                    }
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Could not find default TaskScheduler bean - attempting to find ScheduledExecutorService: " + ex.getMessage()));
                }
                try {
                    this.registrar.setScheduler(this.resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, false));
                }
                catch (NoUniqueBeanDefinitionException ex2) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Could not find unique ScheduledExecutorService bean - attempting to resolve by name: " + ex2.getMessage()));
                    }
                    try {
                        this.registrar.setScheduler(this.resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, true));
                    }
                    catch (NoSuchBeanDefinitionException ex3) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info((Object)("More than one ScheduledExecutorService bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex2.getBeanNamesFound()));
                        }
                    }
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Could not find default ScheduledExecutorService bean - falling back to default: " + ex2.getMessage()));
                    }
                    this.logger.info((Object)"No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
                }
            }
        }
        this.registrar.afterPropertiesSet();
    }

    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> schedulerType, boolean byName) {
        if (byName) {
            Object scheduler = beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, schedulerType);
            if (this.beanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory)this.beanFactory).registerDependentBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, this.beanName);
            }
            return (T)scheduler;
        }
        if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder holder = ((AutowireCapableBeanFactory)beanFactory).resolveNamedBean(schedulerType);
            if (this.beanName != null && beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory)beanFactory).registerDependentBean(holder.getBeanName(), this.beanName);
            }
            return (T)holder.getBeanInstance();
        }
        return (T)beanFactory.getBean(schedulerType);
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    public Object postProcessBeforeInitialization(Object bean2, String beanName) {
        return bean2;
    }

    public Object postProcessAfterInitialization(Object bean2, String beanName) {
        if (bean2 instanceof AopInfrastructureBean || bean2 instanceof TaskScheduler || bean2 instanceof ScheduledExecutorService) {
            return bean2;
        }
        Class targetClass = AopProxyUtils.ultimateTargetClass((Object)bean2);
        if (!this.nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass((Class)targetClass, Arrays.asList(Scheduled.class, Schedules.class))) {
            Map annotatedMethods = MethodIntrospector.selectMethods((Class)targetClass, method -> {
                Set scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations((AnnotatedElement)method, Scheduled.class, Schedules.class);
                return !scheduledAnnotations.isEmpty() ? scheduledAnnotations : null;
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("No @Scheduled annotations found on bean class: " + targetClass));
                }
            } else {
                annotatedMethods.forEach((method, scheduledAnnotations) -> scheduledAnnotations.forEach(scheduled -> this.processScheduled((Scheduled)scheduled, (Method)method, bean2)));
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods));
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
            Runnable runnable = this.createRunnable(bean2, method);
            boolean processedSchedule = false;
            String errorMessage = "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";
            LinkedHashSet<ScheduledTask> tasks = new LinkedHashSet<ScheduledTask>(4);
            long initialDelay = ScheduledAnnotationBeanPostProcessor.convertToMillis(scheduled.initialDelay(), scheduled.timeUnit());
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText((String)initialDelayString)) {
                Assert.isTrue((initialDelay < 0L ? 1 : 0) != 0, (String)"Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                if (StringUtils.hasLength((String)initialDelayString)) {
                    try {
                        initialDelay = ScheduledAnnotationBeanPostProcessor.convertToMillis(initialDelayString, scheduled.timeUnit());
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into long");
                    }
                }
            }
            if (StringUtils.hasText((String)(cron = scheduled.cron()))) {
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength((String)cron)) {
                    Assert.isTrue((initialDelay == -1L ? 1 : 0) != 0, (String)"'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    if (!"-".equals(cron)) {
                        TimeZone timeZone = StringUtils.hasText((String)zone) ? StringUtils.parseTimeZoneString((String)zone) : TimeZone.getDefault();
                        tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone))));
                    }
                }
            }
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            if ((fixedDelay = ScheduledAnnotationBeanPostProcessor.convertToMillis(scheduled.fixedDelay(), scheduled.timeUnit())) >= 0L) {
                Assert.isTrue((!processedSchedule ? 1 : 0) != 0, (String)errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
            }
            if (StringUtils.hasText((String)(fixedDelayString = scheduled.fixedDelayString()))) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength((String)fixedDelayString)) {
                    Assert.isTrue((!processedSchedule ? 1 : 0) != 0, (String)errorMessage);
                    processedSchedule = true;
                    try {
                        fixedDelay = ScheduledAnnotationBeanPostProcessor.convertToMillis(fixedDelayString, scheduled.timeUnit());
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
                }
            }
            if ((fixedRate = ScheduledAnnotationBeanPostProcessor.convertToMillis(scheduled.fixedRate(), scheduled.timeUnit())) >= 0L) {
                Assert.isTrue((!processedSchedule ? 1 : 0) != 0, (String)errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
            }
            if (StringUtils.hasText((String)(fixedRateString = scheduled.fixedRateString()))) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength((String)fixedRateString)) {
                    Assert.isTrue((!processedSchedule ? 1 : 0) != 0, (String)errorMessage);
                    processedSchedule = true;
                    try {
                        fixedRate = ScheduledAnnotationBeanPostProcessor.convertToMillis(fixedRateString, scheduled.timeUnit());
                    }
                    catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
                }
            }
            Assert.isTrue((boolean)processedSchedule, (String)errorMessage);
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

    protected Runnable createRunnable(Object target, Method method) {
        Assert.isTrue((method.getParameterCount() == 0 ? 1 : 0) != 0, (String)"Only no-arg methods may be annotated with @Scheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod((Method)method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }

    private static long convertToMillis(long value, TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(value, timeUnit);
    }

    private static long convertToMillis(String value, TimeUnit timeUnit) {
        if (ScheduledAnnotationBeanPostProcessor.isDurationString(value)) {
            return Duration.parse(value).toMillis();
        }
        return ScheduledAnnotationBeanPostProcessor.convertToMillis(Long.parseLong(value), timeUnit);
    }

    private static boolean isDurationString(String value) {
        return value.length() > 1 && (ScheduledAnnotationBeanPostProcessor.isP(value.charAt(0)) || ScheduledAnnotationBeanPostProcessor.isP(value.charAt(1)));
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
    public void postProcessBeforeDestruction(Object bean2, String beanName) {
        Set<ScheduledTask> tasks;
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            tasks = this.scheduledTasks.remove(bean2);
        }
        if (tasks != null) {
            for (ScheduledTask task : tasks) {
                task.cancel(false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean requiresDestruction(Object bean2) {
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            return this.scheduledTasks.containsKey(bean2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        Map<Object, Set<ScheduledTask>> map = this.scheduledTasks;
        synchronized (map) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                for (ScheduledTask task : tasks) {
                    task.cancel(false);
                }
            }
            this.scheduledTasks.clear();
        }
        this.registrar.destroy();
    }
}

