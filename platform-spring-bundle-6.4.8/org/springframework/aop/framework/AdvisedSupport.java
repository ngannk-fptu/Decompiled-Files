/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisorChainFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

public class AdvisedSupport
extends ProxyConfig
implements Advised {
    private static final long serialVersionUID = 2651364800145442165L;
    public static final TargetSource EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;
    TargetSource targetSource = EMPTY_TARGET_SOURCE;
    private boolean preFiltered = false;
    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();
    private transient Map<MethodCacheKey, List<Object>> methodCache;
    private List<Class<?>> interfaces = new ArrayList();
    private List<Advisor> advisors = new ArrayList<Advisor>();

    public AdvisedSupport() {
        this.methodCache = new ConcurrentHashMap<MethodCacheKey, List<Object>>(32);
    }

    public AdvisedSupport(Class<?> ... interfaces) {
        this();
        this.setInterfaces(interfaces);
    }

    public void setTarget(Object target) {
        this.setTargetSource(new SingletonTargetSource(target));
    }

    @Override
    public void setTargetSource(@Nullable TargetSource targetSource) {
        this.targetSource = targetSource != null ? targetSource : EMPTY_TARGET_SOURCE;
    }

    @Override
    public TargetSource getTargetSource() {
        return this.targetSource;
    }

    public void setTargetClass(@Nullable Class<?> targetClass) {
        this.targetSource = EmptyTargetSource.forClass(targetClass);
    }

    @Override
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetSource.getTargetClass();
    }

    @Override
    public void setPreFiltered(boolean preFiltered) {
        this.preFiltered = preFiltered;
    }

    @Override
    public boolean isPreFiltered() {
        return this.preFiltered;
    }

    public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
        Assert.notNull((Object)advisorChainFactory, "AdvisorChainFactory must not be null");
        this.advisorChainFactory = advisorChainFactory;
    }

    public AdvisorChainFactory getAdvisorChainFactory() {
        return this.advisorChainFactory;
    }

    public void setInterfaces(Class<?> ... interfaces) {
        Assert.notNull(interfaces, "Interfaces must not be null");
        this.interfaces.clear();
        for (Class<?> ifc : interfaces) {
            this.addInterface(ifc);
        }
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
            this.adviceChanged();
        }
    }

    public boolean removeInterface(Class<?> intf) {
        return this.interfaces.remove(intf);
    }

    @Override
    public Class<?>[] getProxiedInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override
    public boolean isInterfaceProxied(Class<?> intf) {
        for (Class<?> proxyIntf : this.interfaces) {
            if (!intf.isAssignableFrom(proxyIntf)) continue;
            return true;
        }
        return false;
    }

    @Override
    public final Advisor[] getAdvisors() {
        return this.advisors.toArray(new Advisor[0]);
    }

    @Override
    public int getAdvisorCount() {
        return this.advisors.size();
    }

    @Override
    public void addAdvisor(Advisor advisor) {
        int pos = this.advisors.size();
        this.addAdvisor(pos, advisor);
    }

    @Override
    public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
        if (advisor instanceof IntroductionAdvisor) {
            this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
        }
        this.addAdvisorInternal(pos, advisor);
    }

    @Override
    public boolean removeAdvisor(Advisor advisor) {
        int index = this.indexOf(advisor);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        return true;
    }

    @Override
    public void removeAdvisor(int index) throws AopConfigException {
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
        }
        if (index < 0 || index > this.advisors.size() - 1) {
            throw new AopConfigException("Advisor index " + index + " is out of bounds: This configuration only has " + this.advisors.size() + " advisors.");
        }
        Advisor advisor = this.advisors.remove(index);
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
            for (Class<?> ifc : ia.getInterfaces()) {
                this.removeInterface(ifc);
            }
        }
        this.adviceChanged();
    }

    @Override
    public int indexOf(Advisor advisor) {
        Assert.notNull((Object)advisor, "Advisor must not be null");
        return this.advisors.indexOf(advisor);
    }

    @Override
    public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
        Assert.notNull((Object)a, "Advisor a must not be null");
        Assert.notNull((Object)b, "Advisor b must not be null");
        int index = this.indexOf(a);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        this.addAdvisor(index, b);
        return true;
    }

    public void addAdvisors(Advisor ... advisors) {
        this.addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(Collection<Advisor> advisors) {
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (!CollectionUtils.isEmpty(advisors)) {
            for (Advisor advisor : advisors) {
                if (advisor instanceof IntroductionAdvisor) {
                    this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
                }
                Assert.notNull((Object)advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
            this.adviceChanged();
        }
    }

    private void validateIntroductionAdvisor(IntroductionAdvisor advisor) {
        Class<?>[] ifcs;
        advisor.validateInterfaces();
        for (Class<?> ifc : ifcs = advisor.getInterfaces()) {
            this.addInterface(ifc);
        }
    }

    private void addAdvisorInternal(int pos, Advisor advisor) throws AopConfigException {
        Assert.notNull((Object)advisor, "Advisor must not be null");
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (pos > this.advisors.size()) {
            throw new IllegalArgumentException("Illegal position " + pos + " in advisor list with size " + this.advisors.size());
        }
        this.advisors.add(pos, advisor);
        this.adviceChanged();
    }

    protected final List<Advisor> getAdvisorsInternal() {
        return this.advisors;
    }

    @Override
    public void addAdvice(Advice advice) throws AopConfigException {
        int pos = this.advisors.size();
        this.addAdvice(pos, advice);
    }

    @Override
    public void addAdvice(int pos, Advice advice) throws AopConfigException {
        Assert.notNull((Object)advice, "Advice must not be null");
        if (advice instanceof IntroductionInfo) {
            this.addAdvisor(pos, new DefaultIntroductionAdvisor(advice, (IntroductionInfo)((Object)advice)));
        } else {
            if (advice instanceof DynamicIntroductionAdvice) {
                throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
            }
            this.addAdvisor(pos, new DefaultPointcutAdvisor(advice));
        }
    }

    @Override
    public boolean removeAdvice(Advice advice) throws AopConfigException {
        int index = this.indexOf(advice);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        return true;
    }

    @Override
    public int indexOf(Advice advice) {
        Assert.notNull((Object)advice, "Advice must not be null");
        for (int i2 = 0; i2 < this.advisors.size(); ++i2) {
            Advisor advisor = this.advisors.get(i2);
            if (advisor.getAdvice() != advice) continue;
            return i2;
        }
        return -1;
    }

    public boolean adviceIncluded(@Nullable Advice advice) {
        if (advice != null) {
            for (Advisor advisor : this.advisors) {
                if (advisor.getAdvice() != advice) continue;
                return true;
            }
        }
        return false;
    }

    public int countAdvicesOfType(@Nullable Class<?> adviceClass) {
        int count = 0;
        if (adviceClass != null) {
            for (Advisor advisor : this.advisors) {
                if (!adviceClass.isInstance(advisor.getAdvice())) continue;
                ++count;
            }
        }
        return count;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
        MethodCacheKey cacheKey = new MethodCacheKey(method);
        List<Object> cached = this.methodCache.get(cacheKey);
        if (cached == null) {
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }

    protected void adviceChanged() {
        this.methodCache.clear();
    }

    protected void copyConfigurationFrom(AdvisedSupport other) {
        this.copyConfigurationFrom(other, other.targetSource, new ArrayList<Advisor>(other.advisors));
    }

    protected void copyConfigurationFrom(AdvisedSupport other, TargetSource targetSource, List<Advisor> advisors) {
        this.copyFrom(other);
        this.targetSource = targetSource;
        this.advisorChainFactory = other.advisorChainFactory;
        this.interfaces = new ArrayList(other.interfaces);
        for (Advisor advisor : advisors) {
            if (advisor instanceof IntroductionAdvisor) {
                this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
            }
            Assert.notNull((Object)advisor, "Advisor must not be null");
            this.advisors.add(advisor);
        }
        this.adviceChanged();
    }

    AdvisedSupport getConfigurationOnlyCopy() {
        AdvisedSupport copy = new AdvisedSupport();
        copy.copyFrom(this);
        copy.targetSource = EmptyTargetSource.forClass(this.getTargetClass(), this.getTargetSource().isStatic());
        copy.advisorChainFactory = this.advisorChainFactory;
        copy.methodCache = this.methodCache;
        copy.interfaces = new ArrayList(this.interfaces);
        copy.advisors = new ArrayList<Advisor>(this.advisors);
        return copy;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.methodCache = new ConcurrentHashMap<MethodCacheKey, List<Object>>(32);
    }

    @Override
    public String toProxyConfigString() {
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
        sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
        sb.append(this.advisors.size()).append(" advisors ");
        sb.append(this.advisors).append("; ");
        sb.append("targetSource [").append(this.targetSource).append("]; ");
        sb.append(super.toString());
        return sb.toString();
    }

    private static final class MethodCacheKey
    implements Comparable<MethodCacheKey> {
        private final Method method;
        private final int hashCode;

        public MethodCacheKey(Method method) {
            this.method = method;
            this.hashCode = method.hashCode();
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof MethodCacheKey && this.method == ((MethodCacheKey)other).method;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return this.method.toString();
        }

        @Override
        public int compareTo(MethodCacheKey other) {
            int result = this.method.getName().compareTo(other.method.getName());
            if (result == 0) {
                result = this.method.toString().compareTo(other.method.toString());
            }
            return result;
        }
    }
}

