/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

class ManagedFactoryDisposableInvoker {
    private static final Log log = LogFactory.getLog(ManagedFactoryDisposableInvoker.class);
    private final boolean isDisposable;
    private final Method customSpringMethod;
    private final Object[] customSpringMethodArgs;
    private final Method customOsgiDestructionMethod;

    public ManagedFactoryDisposableInvoker(Class<?> beanClass, String methodName) {
        this.isDisposable = DisposableBean.class.isAssignableFrom(beanClass);
        if (StringUtils.hasText((String)methodName)) {
            this.customSpringMethod = this.detectCustomSpringMethod(beanClass, methodName);
            if (this.customSpringMethod != null) {
                Object[] objectArray;
                Class<?>[] types = this.customSpringMethod.getParameterTypes();
                if (types.length == 1 && types[0].equals(Boolean.TYPE)) {
                    Object[] objectArray2 = new Object[1];
                    objectArray = objectArray2;
                    objectArray2[0] = Boolean.TRUE;
                } else {
                    objectArray = null;
                }
                this.customSpringMethodArgs = objectArray;
            } else {
                this.customSpringMethodArgs = null;
            }
            this.customOsgiDestructionMethod = this.detectCustomOsgiMethod(beanClass, methodName);
        } else {
            this.customSpringMethod = null;
            this.customSpringMethodArgs = null;
            this.customOsgiDestructionMethod = null;
        }
    }

    private Method detectCustomSpringMethod(Class<?> beanClass, String methodName) {
        Method m = BeanUtils.findMethod(beanClass, (String)methodName, null);
        if (m == null) {
            m = BeanUtils.findMethod(beanClass, (String)methodName, (Class[])new Class[]{Boolean.TYPE});
        }
        return m;
    }

    private Method detectCustomOsgiMethod(Class<?> beanClass, String methodName) {
        return BeanUtils.findMethod(beanClass, (String)methodName, (Class[])new Class[]{Integer.TYPE});
    }

    public void destroy(String beanName, Object beanInstance, DestructionCodes code) {
        if (this.isDisposable) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Invoking destroy() on bean with name '" + beanName + "'"));
            }
            try {
                ((DisposableBean)beanInstance).destroy();
            }
            catch (Throwable ex) {
                String msg = "Invocation of destroy method failed on bean with name '" + beanName + "'";
                if (log.isDebugEnabled()) {
                    log.warn((Object)msg, ex);
                }
                log.warn((Object)(msg + ": " + ex));
            }
        }
        this.invokeCustomMethod(beanName, beanInstance);
        this.invokeCustomMethod(beanName, beanInstance, code);
    }

    private void invokeCustomMethod(String targetName, Object target) {
        if (this.customSpringMethod != null) {
            this.invokeMethod(this.customSpringMethod, this.customSpringMethodArgs, targetName, target);
        }
    }

    private void invokeCustomMethod(String targetName, Object target, DestructionCodes code) {
        if (this.customOsgiDestructionMethod != null) {
            this.invokeMethod(this.customOsgiDestructionMethod, new Object[]{code.getValue()}, targetName, target);
        }
    }

    private void invokeMethod(Method method, Object[] args, String targetName, Object target) {
        try {
            method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            String msg = "Invocation of destroy method '" + method.getName() + "' failed on bean with name '" + targetName + "'";
            if (log.isDebugEnabled()) {
                log.warn((Object)msg, ex.getTargetException());
            } else {
                log.warn((Object)(msg + ": " + ex.getTargetException()));
            }
        }
        catch (Throwable ex) {
            log.error((Object)("Couldn't invoke destroy method '" + method.getName() + "' on bean with name '" + targetName + "'"), ex);
        }
    }

    static enum DestructionCodes {
        CM_ENTRY_DELETED(1),
        BUNDLE_STOPPING(2);

        private Integer value;

        private DestructionCodes(int value) {
            this.value = value;
        }

        public Integer getValue() {
            return this.value;
        }
    }
}

