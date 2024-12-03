/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

class DisposableBeanAdapter
implements DisposableBean,
Runnable,
Serializable {
    private static final String DESTROY_METHOD_NAME = "destroy";
    private static final String CLOSE_METHOD_NAME = "close";
    private static final String SHUTDOWN_METHOD_NAME = "shutdown";
    private static final Log logger = LogFactory.getLog(DisposableBeanAdapter.class);
    private final Object bean;
    private final String beanName;
    private final boolean nonPublicAccessAllowed;
    private final boolean invokeDisposableBean;
    private boolean invokeAutoCloseable;
    @Nullable
    private String destroyMethodName;
    @Nullable
    private transient Method destroyMethod;
    @Nullable
    private final List<DestructionAwareBeanPostProcessor> beanPostProcessors;
    @Nullable
    private final AccessControlContext acc;

    public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition, List<DestructionAwareBeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {
        Assert.notNull((Object)bean, (String)"Disposable bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.invokeDisposableBean = bean instanceof DisposableBean && !beanDefinition.hasAnyExternallyManagedDestroyMethod(DESTROY_METHOD_NAME);
        String destroyMethodName = DisposableBeanAdapter.inferDestroyMethodIfNecessary(bean, beanDefinition);
        if (!(destroyMethodName == null || this.invokeDisposableBean && DESTROY_METHOD_NAME.equals(destroyMethodName) || beanDefinition.hasAnyExternallyManagedDestroyMethod(destroyMethodName))) {
            boolean bl = this.invokeAutoCloseable = bean instanceof AutoCloseable && CLOSE_METHOD_NAME.equals(destroyMethodName);
            if (!this.invokeAutoCloseable) {
                this.destroyMethodName = destroyMethodName;
                Method destroyMethod = this.determineDestroyMethod(destroyMethodName);
                if (destroyMethod == null) {
                    if (beanDefinition.isEnforceDestroyMethod()) {
                        throw new BeanDefinitionValidationException("Could not find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
                    }
                } else {
                    if (destroyMethod.getParameterCount() > 0) {
                        Class<?>[] paramTypes = destroyMethod.getParameterTypes();
                        if (paramTypes.length > 1) {
                            throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
                        }
                        if (paramTypes.length == 1 && Boolean.TYPE != paramTypes[0]) {
                            throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
                        }
                    }
                    destroyMethod = ClassUtils.getInterfaceMethodIfPossible((Method)destroyMethod, bean.getClass());
                }
                this.destroyMethod = destroyMethod;
            }
        }
        this.beanPostProcessors = DisposableBeanAdapter.filterPostProcessors(postProcessors, bean);
        this.acc = acc;
    }

    public DisposableBeanAdapter(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors, AccessControlContext acc) {
        Assert.notNull((Object)bean, (String)"Disposable bean must not be null");
        this.bean = bean;
        this.beanName = bean.getClass().getName();
        this.nonPublicAccessAllowed = true;
        this.invokeDisposableBean = this.bean instanceof DisposableBean;
        this.beanPostProcessors = DisposableBeanAdapter.filterPostProcessors(postProcessors, bean);
        this.acc = acc;
    }

    private DisposableBeanAdapter(Object bean, String beanName, boolean nonPublicAccessAllowed, boolean invokeDisposableBean, boolean invokeAutoCloseable, @Nullable String destroyMethodName, @Nullable List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean;
        this.beanName = beanName;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.invokeDisposableBean = invokeDisposableBean;
        this.invokeAutoCloseable = invokeAutoCloseable;
        this.destroyMethodName = destroyMethodName;
        this.beanPostProcessors = postProcessors;
        this.acc = null;
    }

    @Override
    public void run() {
        this.destroy();
    }

    @Override
    public void destroy() {
        block20: {
            Method destroyMethod;
            String msg;
            block19: {
                if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
                    for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
                        processor.postProcessBeforeDestruction(this.bean, this.beanName);
                    }
                }
                if (this.invokeDisposableBean) {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Invoking destroy() on bean with name '" + this.beanName + "'"));
                    }
                    try {
                        if (System.getSecurityManager() != null) {
                            AccessController.doPrivileged(() -> {
                                ((DisposableBean)this.bean).destroy();
                                return null;
                            }, this.acc);
                        } else {
                            ((DisposableBean)this.bean).destroy();
                        }
                    }
                    catch (Throwable ex) {
                        if (!logger.isWarnEnabled()) break block19;
                        msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                        if (logger.isDebugEnabled()) {
                            logger.warn((Object)msg, ex);
                        }
                        logger.warn((Object)(msg + ": " + ex));
                    }
                }
            }
            if (this.invokeAutoCloseable) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Invoking close() on bean with name '" + this.beanName + "'"));
                }
                try {
                    if (System.getSecurityManager() != null) {
                        AccessController.doPrivileged(() -> {
                            ((AutoCloseable)this.bean).close();
                            return null;
                        }, this.acc);
                        break block20;
                    }
                    ((AutoCloseable)this.bean).close();
                }
                catch (Throwable ex) {
                    if (!logger.isWarnEnabled()) break block20;
                    msg = "Invocation of close method failed on bean with name '" + this.beanName + "'";
                    if (logger.isDebugEnabled()) {
                        logger.warn((Object)msg, ex);
                        break block20;
                    }
                    logger.warn((Object)(msg + ": " + ex));
                }
            } else if (this.destroyMethod != null) {
                this.invokeCustomDestroyMethod(this.destroyMethod);
            } else if (this.destroyMethodName != null && (destroyMethod = this.determineDestroyMethod(this.destroyMethodName)) != null) {
                this.invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible((Method)destroyMethod, this.bean.getClass()));
            }
        }
    }

    @Nullable
    private Method determineDestroyMethod(String name) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> this.findDestroyMethod(name));
            }
            return this.findDestroyMethod(name);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanDefinitionValidationException("Could not find unique destroy method on bean with name '" + this.beanName + ": " + ex.getMessage());
        }
    }

    @Nullable
    private Method findDestroyMethod(String name) {
        return this.nonPublicAccessAllowed ? BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), name) : BeanUtils.findMethodWithMinimalParameters(this.bean.getClass().getMethods(), name);
    }

    private void invokeCustomDestroyMethod(Method destroyMethod) {
        block11: {
            int paramCount = destroyMethod.getParameterCount();
            Object[] args = new Object[paramCount];
            if (paramCount == 1) {
                args[0] = Boolean.TRUE;
            }
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Invoking custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'"));
            }
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(() -> {
                        ReflectionUtils.makeAccessible((Method)destroyMethod);
                        return null;
                    });
                    try {
                        AccessController.doPrivileged(() -> destroyMethod.invoke(this.bean, args), this.acc);
                        break block11;
                    }
                    catch (PrivilegedActionException pax) {
                        throw (InvocationTargetException)pax.getException();
                    }
                }
                ReflectionUtils.makeAccessible((Method)destroyMethod);
                destroyMethod.invoke(this.bean, args);
            }
            catch (InvocationTargetException ex) {
                if (logger.isWarnEnabled()) {
                    String msg = "Custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "' threw an exception";
                    if (logger.isDebugEnabled()) {
                        logger.warn((Object)msg, ex.getTargetException());
                    } else {
                        logger.warn((Object)(msg + ": " + ex.getTargetException()));
                    }
                }
            }
            catch (Throwable ex) {
                if (!logger.isWarnEnabled()) break block11;
                logger.warn((Object)("Failed to invoke custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'"), ex);
            }
        }
    }

    protected Object writeReplace() {
        ArrayList<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList<DestructionAwareBeanPostProcessor>();
            for (DestructionAwareBeanPostProcessor postProcessor : this.beanPostProcessors) {
                if (!(postProcessor instanceof Serializable)) continue;
                serializablePostProcessors.add(postProcessor);
            }
        }
        return new DisposableBeanAdapter(this.bean, this.beanName, this.nonPublicAccessAllowed, this.invokeDisposableBean, this.invokeAutoCloseable, this.destroyMethodName, serializablePostProcessors);
    }

    public static boolean hasDestroyMethod(Object bean, RootBeanDefinition beanDefinition) {
        return bean instanceof DisposableBean || DisposableBeanAdapter.inferDestroyMethodIfNecessary(bean, beanDefinition) != null;
    }

    @Nullable
    private static String inferDestroyMethodIfNecessary(Object bean, RootBeanDefinition beanDefinition) {
        String destroyMethodName = beanDefinition.resolvedDestroyMethodName;
        if (destroyMethodName == null) {
            destroyMethodName = beanDefinition.getDestroyMethodName();
            boolean autoCloseable = bean instanceof AutoCloseable;
            if ("(inferred)".equals(destroyMethodName) || destroyMethodName == null && autoCloseable) {
                destroyMethodName = null;
                if (!(bean instanceof DisposableBean)) {
                    if (autoCloseable) {
                        destroyMethodName = CLOSE_METHOD_NAME;
                    } else {
                        try {
                            destroyMethodName = bean.getClass().getMethod(CLOSE_METHOD_NAME, new Class[0]).getName();
                        }
                        catch (NoSuchMethodException ex) {
                            try {
                                destroyMethodName = bean.getClass().getMethod(SHUTDOWN_METHOD_NAME, new Class[0]).getName();
                            }
                            catch (NoSuchMethodException noSuchMethodException) {
                                // empty catch block
                            }
                        }
                    }
                }
            }
            beanDefinition.resolvedDestroyMethodName = destroyMethodName != null ? destroyMethodName : "";
        }
        return StringUtils.hasLength((String)destroyMethodName) ? destroyMethodName : null;
    }

    public static boolean hasApplicableProcessors(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors) {
        if (!CollectionUtils.isEmpty(postProcessors)) {
            for (DestructionAwareBeanPostProcessor processor : postProcessors) {
                if (!processor.requiresDestruction(bean)) continue;
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<DestructionAwareBeanPostProcessor> processors, Object bean) {
        ArrayList<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (!CollectionUtils.isEmpty(processors)) {
            filteredPostProcessors = new ArrayList<DestructionAwareBeanPostProcessor>(processors.size());
            for (DestructionAwareBeanPostProcessor processor : processors) {
                if (!processor.requiresDestruction(bean)) continue;
                filteredPostProcessors.add(processor);
            }
        }
        return filteredPostProcessors;
    }
}

