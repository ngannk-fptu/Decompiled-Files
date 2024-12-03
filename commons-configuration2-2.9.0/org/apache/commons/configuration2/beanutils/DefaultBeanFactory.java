/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.beanutils.BeanCreationContext;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.BeanFactory;
import org.apache.commons.configuration2.beanutils.ConstructorArg;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class DefaultBeanFactory
implements BeanFactory {
    public static final DefaultBeanFactory INSTANCE = new DefaultBeanFactory();
    private static final String FMT_CTOR_ERROR = "%s! Bean class = %s, constructor arguments = %s";
    private final ConversionHandler conversionHandler;

    public DefaultBeanFactory() {
        this(null);
    }

    public DefaultBeanFactory(ConversionHandler convHandler) {
        this.conversionHandler = convHandler != null ? convHandler : DefaultConversionHandler.INSTANCE;
    }

    public ConversionHandler getConversionHandler() {
        return this.conversionHandler;
    }

    @Override
    public Object createBean(BeanCreationContext bcc) throws Exception {
        Object result = this.createBeanInstance(bcc);
        this.initBeanInstance(result, bcc);
        return result;
    }

    @Override
    public Class<?> getDefaultBeanClass() {
        return null;
    }

    protected Object createBeanInstance(BeanCreationContext bcc) throws Exception {
        Constructor<?> ctor = DefaultBeanFactory.findMatchingConstructor(bcc.getBeanClass(), bcc.getBeanDeclaration());
        Object[] args = this.fetchConstructorArgs(ctor, bcc);
        return ctor.newInstance(args);
    }

    protected void initBeanInstance(Object bean, BeanCreationContext bcc) throws Exception {
        bcc.initBean(bean, bcc.getBeanDeclaration());
    }

    protected static <T> Constructor<T> findMatchingConstructor(Class<T> beanClass, BeanDeclaration data) {
        List<Constructor<T>> matchingConstructors = DefaultBeanFactory.findMatchingConstructors(beanClass, data);
        DefaultBeanFactory.checkSingleMatchingConstructor(beanClass, data, matchingConstructors);
        return matchingConstructors.get(0);
    }

    private Object[] fetchConstructorArgs(Constructor<?> ctor, BeanCreationContext bcc) {
        Class<?>[] types = ctor.getParameterTypes();
        assert (types.length == DefaultBeanFactory.nullSafeConstructorArgs(bcc.getBeanDeclaration()).size()) : "Wrong number of constructor arguments!";
        Object[] args = new Object[types.length];
        int idx = 0;
        for (ConstructorArg arg : DefaultBeanFactory.nullSafeConstructorArgs(bcc.getBeanDeclaration())) {
            Object val = arg.isNestedBeanDeclaration() ? bcc.createBean(arg.getBeanDeclaration()) : arg.getValue();
            args[idx] = this.getConversionHandler().to(val, types[idx], null);
            ++idx;
        }
        return args;
    }

    private static Collection<ConstructorArg> nullSafeConstructorArgs(BeanDeclaration data) {
        Collection<ConstructorArg> args = data.getConstructorArgs();
        if (args == null) {
            args = Collections.emptySet();
        }
        return args;
    }

    private static <T> List<Constructor<T>> findMatchingConstructors(Class<T> beanClass, BeanDeclaration data) {
        LinkedList<Constructor<T>> result = new LinkedList<Constructor<T>>();
        Collection<ConstructorArg> args = DefaultBeanFactory.getConstructorArgs(data);
        for (Constructor<?> ctor : beanClass.getConstructors()) {
            if (!DefaultBeanFactory.matchesConstructor(ctor, args)) continue;
            Constructor<?> match = ctor;
            result.add(match);
        }
        return result;
    }

    private static boolean matchesConstructor(Constructor<?> ctor, Collection<ConstructorArg> args) {
        Class<?>[] types = ctor.getParameterTypes();
        if (types.length != args.size()) {
            return false;
        }
        int idx = 0;
        for (ConstructorArg arg : args) {
            if (arg.matches(types[idx++])) continue;
            return false;
        }
        return true;
    }

    private static Collection<ConstructorArg> getConstructorArgs(BeanDeclaration data) {
        Collection<ConstructorArg> args = data.getConstructorArgs();
        if (args == null) {
            args = Collections.emptySet();
        }
        return args;
    }

    private static <T> void checkSingleMatchingConstructor(Class<T> beanClass, BeanDeclaration data, List<Constructor<T>> matchingConstructors) {
        if (matchingConstructors.isEmpty()) {
            throw DefaultBeanFactory.constructorMatchingException(beanClass, data, "No matching constructor found");
        }
        if (matchingConstructors.size() > 1) {
            throw DefaultBeanFactory.constructorMatchingException(beanClass, data, "Multiple matching constructors found");
        }
    }

    private static ConfigurationRuntimeException constructorMatchingException(Class<?> beanClass, BeanDeclaration data, String msg) {
        return new ConfigurationRuntimeException(FMT_CTOR_ERROR, msg, beanClass.getName(), DefaultBeanFactory.getConstructorArgs(data).toString());
    }
}

