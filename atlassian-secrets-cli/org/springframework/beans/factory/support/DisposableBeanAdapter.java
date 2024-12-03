/*
 * Decompiled with CFR 0.152.
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
import org.springframework.beans.factory.config.BeanPostProcessor;
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
    private final boolean invokeDisposableBean;
    private final boolean nonPublicAccessAllowed;
    @Nullable
    private final AccessControlContext acc;
    @Nullable
    private String destroyMethodName;
    @Nullable
    private transient Method destroyMethod;
    @Nullable
    private final List<DestructionAwareBeanPostProcessor> beanPostProcessors;

    public DisposableBeanAdapter(Object bean2, String beanName, RootBeanDefinition beanDefinition, List<BeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {
        Assert.notNull(bean2, "Disposable bean must not be null");
        this.bean = bean2;
        this.beanName = beanName;
        this.invokeDisposableBean = bean2 instanceof DisposableBean && !beanDefinition.isExternallyManagedDestroyMethod(DESTROY_METHOD_NAME);
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.acc = acc;
        String destroyMethodName = DisposableBeanAdapter.inferDestroyMethodIfNecessary(bean2, beanDefinition);
        if (!(destroyMethodName == null || this.invokeDisposableBean && DESTROY_METHOD_NAME.equals(destroyMethodName) || beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName))) {
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
                destroyMethod = ClassUtils.getInterfaceMethodIfPossible((Method)destroyMethod);
            }
            this.destroyMethod = destroyMethod;
        }
        this.beanPostProcessors = this.filterPostProcessors(postProcessors, bean2);
    }

    public DisposableBeanAdapter(Object bean2, List<BeanPostProcessor> postProcessors, AccessControlContext acc) {
        Assert.notNull(bean2, "Disposable bean must not be null");
        this.bean = bean2;
        this.beanName = bean2.getClass().getName();
        this.invokeDisposableBean = this.bean instanceof DisposableBean;
        this.nonPublicAccessAllowed = true;
        this.acc = acc;
        this.beanPostProcessors = this.filterPostProcessors(postProcessors, bean2);
    }

    private DisposableBeanAdapter(Object bean2, String beanName, boolean invokeDisposableBean, boolean nonPublicAccessAllowed, @Nullable String destroyMethodName, @Nullable List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean2;
        this.beanName = beanName;
        this.invokeDisposableBean = invokeDisposableBean;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.acc = null;
        this.destroyMethodName = destroyMethodName;
        this.beanPostProcessors = postProcessors;
    }

    @Override
    public void run() {
        this.destroy();
    }

    @Override
    public void destroy() {
        Method methodToInvoke;
        if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
            for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
                processor.postProcessBeforeDestruction(this.bean, this.beanName);
            }
        }
        if (this.invokeDisposableBean) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
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
                String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                if (logger.isDebugEnabled()) {
                    logger.warn(msg, ex);
                }
                logger.warn(msg + ": " + ex);
            }
        }
        if (this.destroyMethod != null) {
            this.invokeCustomDestroyMethod(this.destroyMethod);
        } else if (this.destroyMethodName != null && (methodToInvoke = this.determineDestroyMethod(this.destroyMethodName)) != null) {
            this.invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible((Method)methodToInvoke));
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
        block10: {
            int paramCount = destroyMethod.getParameterCount();
            Object[] args = new Object[paramCount];
            if (paramCount == 1) {
                args[0] = Boolean.TRUE;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'");
            }
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(() -> {
                        ReflectionUtils.makeAccessible(destroyMethod);
                        return null;
                    });
                    try {
                        AccessController.doPrivileged(() -> destroyMethod.invoke(this.bean, args), this.acc);
                        break block10;
                    }
                    catch (PrivilegedActionException pax) {
                        throw (InvocationTargetException)pax.getException();
                    }
                }
                ReflectionUtils.makeAccessible(destroyMethod);
                destroyMethod.invoke(this.bean, args);
            }
            catch (InvocationTargetException ex) {
                String msg = "Destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "' threw an exception";
                if (logger.isDebugEnabled()) {
                    logger.warn(msg, ex.getTargetException());
                } else {
                    logger.warn(msg + ": " + ex.getTargetException());
                }
            }
            catch (Throwable ex) {
                logger.warn("Failed to invoke destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'", ex);
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
        return new DisposableBeanAdapter(this.bean, this.beanName, this.invokeDisposableBean, this.nonPublicAccessAllowed, this.destroyMethodName, serializablePostProcessors);
    }

    public static boolean hasDestroyMethod(Object bean2, RootBeanDefinition beanDefinition) {
        if (bean2 instanceof DisposableBean || bean2 instanceof AutoCloseable) {
            return true;
        }
        return DisposableBeanAdapter.inferDestroyMethodIfNecessary(bean2, beanDefinition) != null;
    }

    @Nullable
    private static String inferDestroyMethodIfNecessary(Object bean2, RootBeanDefinition beanDefinition) {
        String destroyMethodName = beanDefinition.resolvedDestroyMethodName;
        if (destroyMethodName == null) {
            destroyMethodName = beanDefinition.getDestroyMethodName();
            if ("(inferred)".equals(destroyMethodName) || destroyMethodName == null && bean2 instanceof AutoCloseable) {
                destroyMethodName = null;
                if (!(bean2 instanceof DisposableBean)) {
                    try {
                        destroyMethodName = bean2.getClass().getMethod(CLOSE_METHOD_NAME, new Class[0]).getName();
                    }
                    catch (NoSuchMethodException ex) {
                        try {
                            destroyMethodName = bean2.getClass().getMethod(SHUTDOWN_METHOD_NAME, new Class[0]).getName();
                        }
                        catch (NoSuchMethodException noSuchMethodException) {
                            // empty catch block
                        }
                    }
                }
            }
            beanDefinition.resolvedDestroyMethodName = destroyMethodName != null ? destroyMethodName : "";
        }
        return StringUtils.hasLength(destroyMethodName) ? destroyMethodName : null;
    }

    public static boolean hasApplicableProcessors(Object bean2, List<BeanPostProcessor> postProcessors) {
        if (!CollectionUtils.isEmpty(postProcessors)) {
            for (BeanPostProcessor processor : postProcessors) {
                DestructionAwareBeanPostProcessor dabpp;
                if (!(processor instanceof DestructionAwareBeanPostProcessor) || !(dabpp = (DestructionAwareBeanPostProcessor)processor).requiresDestruction(bean2)) continue;
                return true;
            }
        }
        return false;
    }

    @Nullable
    private List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<BeanPostProcessor> processors, Object bean2) {
        ArrayList<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (!CollectionUtils.isEmpty(processors)) {
            filteredPostProcessors = new ArrayList<DestructionAwareBeanPostProcessor>(processors.size());
            for (BeanPostProcessor processor : processors) {
                DestructionAwareBeanPostProcessor dabpp;
                if (!(processor instanceof DestructionAwareBeanPostProcessor) || !(dabpp = (DestructionAwareBeanPostProcessor)processor).requiresDestruction(bean2)) continue;
                filteredPostProcessors.add(dabpp);
            }
        }
        return filteredPostProcessors;
    }
}

