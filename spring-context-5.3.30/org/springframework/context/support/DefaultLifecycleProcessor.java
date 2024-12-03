/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DefaultLifecycleProcessor
implements LifecycleProcessor,
BeanFactoryAware {
    private final Log logger = LogFactory.getLog(this.getClass());
    private volatile long timeoutPerShutdownPhase = 30000L;
    private volatile boolean running;
    @Nullable
    private volatile ConfigurableListableBeanFactory beanFactory;

    public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    private ConfigurableListableBeanFactory getBeanFactory() {
        ConfigurableListableBeanFactory beanFactory = this.beanFactory;
        Assert.state((beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory available");
        return beanFactory;
    }

    @Override
    public void start() {
        this.startBeans(false);
        this.running = true;
    }

    @Override
    public void stop() {
        this.stopBeans();
        this.running = false;
    }

    @Override
    public void onRefresh() {
        this.startBeans(true);
        this.running = true;
    }

    @Override
    public void onClose() {
        this.stopBeans();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    private void startBeans(boolean autoStartupOnly) {
        Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        TreeMap phases = new TreeMap();
        lifecycleBeans.forEach((beanName, bean2) -> {
            if (!autoStartupOnly || bean2 instanceof SmartLifecycle && ((SmartLifecycle)bean2).isAutoStartup()) {
                int phase = this.getPhase((Lifecycle)bean2);
                phases.computeIfAbsent(phase, p -> new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly)).add((String)beanName, (Lifecycle)bean2);
            }
        });
        if (!phases.isEmpty()) {
            phases.values().forEach(LifecycleGroup::start);
        }
    }

    private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
        Lifecycle bean2 = lifecycleBeans.remove(beanName);
        if (bean2 != null && bean2 != this) {
            String[] dependenciesForBean;
            for (String dependency : dependenciesForBean = this.getBeanFactory().getDependenciesForBean(beanName)) {
                this.doStart(lifecycleBeans, dependency, autoStartupOnly);
            }
            if (!(bean2.isRunning() || autoStartupOnly && bean2 instanceof SmartLifecycle && !((SmartLifecycle)bean2).isAutoStartup())) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Starting bean '" + beanName + "' of type [" + bean2.getClass().getName() + "]"));
                }
                try {
                    bean2.start();
                }
                catch (Throwable ex) {
                    throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Successfully started bean '" + beanName + "'"));
                }
            }
        }
    }

    private void stopBeans() {
        Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        HashMap phases = new HashMap();
        lifecycleBeans.forEach((beanName, bean2) -> {
            int shutdownPhase = this.getPhase((Lifecycle)bean2);
            LifecycleGroup group = (LifecycleGroup)phases.get(shutdownPhase);
            if (group == null) {
                group = new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
                phases.put(shutdownPhase, group);
            }
            group.add((String)beanName, (Lifecycle)bean2);
        });
        if (!phases.isEmpty()) {
            ArrayList keys = new ArrayList(phases.keySet());
            keys.sort(Collections.reverseOrder());
            for (Integer key : keys) {
                ((LifecycleGroup)phases.get(key)).stop();
            }
        }
    }

    private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, CountDownLatch latch, Set<String> countDownBeanNames) {
        block12: {
            Lifecycle bean2 = lifecycleBeans.remove(beanName);
            if (bean2 != null) {
                String[] dependentBeans;
                for (String dependentBean : dependentBeans = this.getBeanFactory().getDependentBeans(beanName)) {
                    this.doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
                }
                try {
                    if (bean2.isRunning()) {
                        if (bean2 instanceof SmartLifecycle) {
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace((Object)("Asking bean '" + beanName + "' of type [" + bean2.getClass().getName() + "] to stop"));
                            }
                            countDownBeanNames.add(beanName);
                            ((SmartLifecycle)bean2).stop(() -> {
                                latch.countDown();
                                countDownBeanNames.remove(beanName);
                                if (this.logger.isDebugEnabled()) {
                                    this.logger.debug((Object)("Bean '" + beanName + "' completed its stop procedure"));
                                }
                            });
                        } else {
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace((Object)("Stopping bean '" + beanName + "' of type [" + bean2.getClass().getName() + "]"));
                            }
                            bean2.stop();
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug((Object)("Successfully stopped bean '" + beanName + "'"));
                            }
                        }
                    } else if (bean2 instanceof SmartLifecycle) {
                        latch.countDown();
                    }
                }
                catch (Throwable ex) {
                    if (!this.logger.isWarnEnabled()) break block12;
                    this.logger.warn((Object)("Failed to stop bean '" + beanName + "'"), ex);
                }
            }
        }
    }

    protected Map<String, Lifecycle> getLifecycleBeans() {
        String[] beanNames;
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        LinkedHashMap<String, Lifecycle> beans2 = new LinkedHashMap<String, Lifecycle>();
        for (String beanName : beanNames = beanFactory.getBeanNamesForType(Lifecycle.class, false, false)) {
            Object bean2;
            String beanNameToCheck;
            String beanNameToRegister = BeanFactoryUtils.transformedBeanName((String)beanName);
            boolean isFactoryBean = beanFactory.isFactoryBean(beanNameToRegister);
            String string = beanNameToCheck = isFactoryBean ? "&" + beanName : beanName;
            if ((!beanFactory.containsSingleton(beanNameToRegister) || isFactoryBean && !this.matchesBeanType(Lifecycle.class, beanNameToCheck, (BeanFactory)beanFactory)) && !this.matchesBeanType(SmartLifecycle.class, beanNameToCheck, (BeanFactory)beanFactory) || (bean2 = beanFactory.getBean(beanNameToCheck)) == this || !(bean2 instanceof Lifecycle)) continue;
            beans2.put(beanNameToRegister, (Lifecycle)bean2);
        }
        return beans2;
    }

    private boolean matchesBeanType(Class<?> targetType, String beanName, BeanFactory beanFactory) {
        Class beanType = beanFactory.getType(beanName);
        return beanType != null && targetType.isAssignableFrom(beanType);
    }

    protected int getPhase(Lifecycle bean2) {
        return bean2 instanceof Phased ? ((Phased)((Object)bean2)).getPhase() : 0;
    }

    private class LifecycleGroupMember
    implements Comparable<LifecycleGroupMember> {
        private final String name;
        private final Lifecycle bean;

        LifecycleGroupMember(String name, Lifecycle bean2) {
            this.name = name;
            this.bean = bean2;
        }

        @Override
        public int compareTo(LifecycleGroupMember other) {
            int thisPhase = DefaultLifecycleProcessor.this.getPhase(this.bean);
            int otherPhase = DefaultLifecycleProcessor.this.getPhase(other.bean);
            return Integer.compare(thisPhase, otherPhase);
        }
    }

    private class LifecycleGroup {
        private final int phase;
        private final long timeout;
        private final Map<String, ? extends Lifecycle> lifecycleBeans;
        private final boolean autoStartupOnly;
        private final List<LifecycleGroupMember> members = new ArrayList<LifecycleGroupMember>();
        private int smartMemberCount;

        public LifecycleGroup(int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {
            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }

        public void add(String name, Lifecycle bean2) {
            this.members.add(new LifecycleGroupMember(name, bean2));
            if (bean2 instanceof SmartLifecycle) {
                ++this.smartMemberCount;
            }
        }

        public void start() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                DefaultLifecycleProcessor.this.logger.debug((Object)("Starting beans in phase " + this.phase));
            }
            Collections.sort(this.members);
            for (LifecycleGroupMember member : this.members) {
                DefaultLifecycleProcessor.this.doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
            }
        }

        public void stop() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                DefaultLifecycleProcessor.this.logger.debug((Object)("Stopping beans in phase " + this.phase));
            }
            this.members.sort(Collections.reverseOrder());
            CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
            Set countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet());
            HashSet<String> lifecycleBeanNames = new HashSet<String>(this.lifecycleBeans.keySet());
            for (LifecycleGroupMember member : this.members) {
                if (lifecycleBeanNames.contains(member.name)) {
                    DefaultLifecycleProcessor.this.doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                    continue;
                }
                if (!(member.bean instanceof SmartLifecycle)) continue;
                latch.countDown();
            }
            try {
                latch.await(this.timeout, TimeUnit.MILLISECONDS);
                if (latch.getCount() > 0L && !countDownBeanNames.isEmpty() && DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                    DefaultLifecycleProcessor.this.logger.info((Object)("Failed to shut down " + countDownBeanNames.size() + " bean" + (countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " + this.phase + " within timeout of " + this.timeout + "ms: " + countDownBeanNames));
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

