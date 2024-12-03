/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

public class ControllerAdviceBean
implements Ordered {
    private final Object bean;
    @Nullable
    private final BeanFactory beanFactory;
    private final int order;
    private final Set<String> basePackages;
    private final List<Class<?>> assignableTypes;
    private final List<Class<? extends Annotation>> annotations;

    public ControllerAdviceBean(Object bean2) {
        this(bean2, null);
    }

    public ControllerAdviceBean(String beanName, @Nullable BeanFactory beanFactory) {
        this((Object)beanName, beanFactory);
    }

    private ControllerAdviceBean(Object bean2, @Nullable BeanFactory beanFactory) {
        ControllerAdvice annotation;
        Class<?> beanType;
        this.bean = bean2;
        this.beanFactory = beanFactory;
        if (bean2 instanceof String) {
            String beanName = (String)bean2;
            Assert.hasText(beanName, "Bean name must not be null");
            Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
            if (!beanFactory.containsBean(beanName)) {
                throw new IllegalArgumentException("BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'");
            }
            beanType = this.beanFactory.getType(beanName);
            this.order = ControllerAdviceBean.initOrderFromBeanType(beanType);
        } else {
            Assert.notNull(bean2, "Bean must not be null");
            beanType = bean2.getClass();
            this.order = ControllerAdviceBean.initOrderFromBean(bean2);
        }
        ControllerAdvice controllerAdvice = annotation = beanType != null ? AnnotatedElementUtils.findMergedAnnotation(beanType, ControllerAdvice.class) : null;
        if (annotation != null) {
            this.basePackages = ControllerAdviceBean.initBasePackages(annotation);
            this.assignableTypes = Arrays.asList(annotation.assignableTypes());
            this.annotations = Arrays.asList(annotation.annotations());
        } else {
            this.basePackages = Collections.emptySet();
            this.assignableTypes = Collections.emptyList();
            this.annotations = Collections.emptyList();
        }
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Nullable
    public Class<?> getBeanType() {
        Class<?> beanType = this.bean instanceof String ? this.obtainBeanFactory().getType((String)this.bean) : this.bean.getClass();
        return beanType != null ? ClassUtils.getUserClass(beanType) : null;
    }

    public Object resolveBean() {
        return this.bean instanceof String ? this.obtainBeanFactory().getBean((String)this.bean) : this.bean;
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        if (!this.hasSelectors()) {
            return true;
        }
        if (beanType != null) {
            for (String string : this.basePackages) {
                if (!beanType.getName().startsWith(string)) continue;
                return true;
            }
            for (Class clazz : this.assignableTypes) {
                if (!ClassUtils.isAssignable(clazz, beanType)) continue;
                return true;
            }
            for (Class clazz : this.annotations) {
                if (AnnotationUtils.findAnnotation(beanType, clazz) == null) continue;
                return true;
            }
        }
        return false;
    }

    private boolean hasSelectors() {
        return !this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControllerAdviceBean)) {
            return false;
        }
        ControllerAdviceBean otherAdvice = (ControllerAdviceBean)other;
        return this.bean.equals(otherAdvice.bean) && this.beanFactory == otherAdvice.beanFactory;
    }

    public int hashCode() {
        return this.bean.hashCode();
    }

    public String toString() {
        return this.bean.toString();
    }

    public static List<ControllerAdviceBean> findAnnotatedBeans(ApplicationContext applicationContext) {
        ArrayList<ControllerAdviceBean> beans2 = new ArrayList<ControllerAdviceBean>();
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)applicationContext, Object.class)) {
            if (applicationContext.findAnnotationOnBean(name, ControllerAdvice.class) == null) continue;
            beans2.add(new ControllerAdviceBean(name, (BeanFactory)applicationContext));
        }
        return beans2;
    }

    private static int initOrderFromBean(Object bean2) {
        return bean2 instanceof Ordered ? ((Ordered)bean2).getOrder() : ControllerAdviceBean.initOrderFromBeanType(bean2.getClass());
    }

    private static int initOrderFromBeanType(@Nullable Class<?> beanType) {
        Integer order = null;
        if (beanType != null) {
            order = OrderUtils.getOrder(beanType);
        }
        return order != null ? order : Integer.MAX_VALUE;
    }

    private static Set<String> initBasePackages(ControllerAdvice annotation) {
        LinkedHashSet<String> basePackages = new LinkedHashSet<String>();
        for (String basePackage : annotation.basePackages()) {
            if (!StringUtils.hasText(basePackage)) continue;
            basePackages.add(ControllerAdviceBean.adaptBasePackage(basePackage));
        }
        for (Class<?> markerClass : annotation.basePackageClasses()) {
            basePackages.add(ControllerAdviceBean.adaptBasePackage(ClassUtils.getPackageName(markerClass)));
        }
        return basePackages;
    }

    private static String adaptBasePackage(String basePackage) {
        return basePackage.endsWith(".") ? basePackage : basePackage + ".";
    }
}

