/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerTypePredicate;

public class ControllerAdviceBean
implements Ordered {
    private final Object beanOrName;
    private final boolean isSingleton;
    @Nullable
    private Object resolvedBean;
    @Nullable
    private final Class<?> beanType;
    private final HandlerTypePredicate beanTypePredicate;
    @Nullable
    private final BeanFactory beanFactory;
    @Nullable
    private Integer order;

    public ControllerAdviceBean(Object bean2) {
        Assert.notNull(bean2, "Bean must not be null");
        this.beanOrName = bean2;
        this.isSingleton = true;
        this.resolvedBean = bean2;
        this.beanType = ClassUtils.getUserClass(bean2.getClass());
        this.beanTypePredicate = ControllerAdviceBean.createBeanTypePredicate(this.beanType);
        this.beanFactory = null;
    }

    public ControllerAdviceBean(String beanName, BeanFactory beanFactory) {
        this(beanName, beanFactory, null);
    }

    public ControllerAdviceBean(String beanName, BeanFactory beanFactory, @Nullable ControllerAdvice controllerAdvice) {
        Assert.hasText(beanName, "Bean name must contain text");
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        Assert.isTrue(beanFactory.containsBean(beanName), () -> "BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'");
        this.beanOrName = beanName;
        this.isSingleton = beanFactory.isSingleton(beanName);
        this.beanType = ControllerAdviceBean.getBeanType(beanName, beanFactory);
        this.beanTypePredicate = controllerAdvice != null ? ControllerAdviceBean.createBeanTypePredicate(controllerAdvice) : ControllerAdviceBean.createBeanTypePredicate(this.beanType);
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        if (this.order == null) {
            String beanName = null;
            Object resolvedBean = null;
            if (this.beanFactory != null && this.beanOrName instanceof String) {
                beanName = (String)this.beanOrName;
                String targetBeanName = ScopedProxyUtils.getTargetBeanName(beanName);
                boolean isScopedProxy = this.beanFactory.containsBean(targetBeanName);
                if (!isScopedProxy && !ScopedProxyUtils.isScopedTarget(beanName)) {
                    resolvedBean = this.resolveBean();
                }
            } else {
                resolvedBean = this.resolveBean();
            }
            if (resolvedBean instanceof Ordered) {
                this.order = ((Ordered)resolvedBean).getOrder();
            } else {
                if (beanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
                    ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)this.beanFactory;
                    try {
                        Method factoryMethod;
                        BeanDefinition bd = cbf.getMergedBeanDefinition(beanName);
                        if (bd instanceof RootBeanDefinition && (factoryMethod = ((RootBeanDefinition)bd).getResolvedFactoryMethod()) != null) {
                            this.order = OrderUtils.getOrder(factoryMethod);
                        }
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                        // empty catch block
                    }
                }
                if (this.order == null) {
                    this.order = this.beanType != null ? Integer.valueOf(OrderUtils.getOrder(this.beanType, Integer.MAX_VALUE)) : Integer.valueOf(Integer.MAX_VALUE);
                }
            }
        }
        return this.order;
    }

    @Nullable
    public Class<?> getBeanType() {
        return this.beanType;
    }

    public Object resolveBean() {
        if (this.resolvedBean == null) {
            Object resolvedBean = this.obtainBeanFactory().getBean((String)this.beanOrName);
            if (!this.isSingleton) {
                return resolvedBean;
            }
            this.resolvedBean = resolvedBean;
        }
        return this.resolvedBean;
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        return this.beanTypePredicate.test(beanType);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControllerAdviceBean)) {
            return false;
        }
        ControllerAdviceBean otherAdvice = (ControllerAdviceBean)other;
        return this.beanOrName.equals(otherAdvice.beanOrName) && this.beanFactory == otherAdvice.beanFactory;
    }

    public int hashCode() {
        return this.beanOrName.hashCode();
    }

    public String toString() {
        return this.beanOrName.toString();
    }

    public static List<ControllerAdviceBean> findAnnotatedBeans(ApplicationContext context) {
        HierarchicalBeanFactory beanFactory = context;
        if (context instanceof ConfigurableApplicationContext) {
            beanFactory = ((ConfigurableApplicationContext)context).getBeanFactory();
        }
        ArrayList<ControllerAdviceBean> adviceBeans = new ArrayList<ControllerAdviceBean>();
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)((Object)beanFactory), Object.class)) {
            ControllerAdvice controllerAdvice;
            if (ScopedProxyUtils.isScopedTarget(name) || (controllerAdvice = beanFactory.findAnnotationOnBean(name, ControllerAdvice.class)) == null) continue;
            adviceBeans.add(new ControllerAdviceBean(name, beanFactory, controllerAdvice));
        }
        OrderComparator.sort(adviceBeans);
        return adviceBeans;
    }

    @Nullable
    private static Class<?> getBeanType(String beanName, BeanFactory beanFactory) {
        Class<?> beanType = beanFactory.getType(beanName);
        return beanType != null ? ClassUtils.getUserClass(beanType) : null;
    }

    private static HandlerTypePredicate createBeanTypePredicate(@Nullable Class<?> beanType) {
        ControllerAdvice controllerAdvice = beanType != null ? AnnotatedElementUtils.findMergedAnnotation(beanType, ControllerAdvice.class) : null;
        return ControllerAdviceBean.createBeanTypePredicate(controllerAdvice);
    }

    private static HandlerTypePredicate createBeanTypePredicate(@Nullable ControllerAdvice controllerAdvice) {
        if (controllerAdvice != null) {
            return HandlerTypePredicate.builder().basePackage(controllerAdvice.basePackages()).basePackageClass(controllerAdvice.basePackageClasses()).assignableType(controllerAdvice.assignableTypes()).annotation(controllerAdvice.annotations()).build();
        }
        return HandlerTypePredicate.forAnyHandlerType();
    }
}

