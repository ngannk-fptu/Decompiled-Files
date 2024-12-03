/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.support.ArgumentConvertingMethodInvoker
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.scheduling.support;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class MethodInvokingRunnable
extends ArgumentConvertingMethodInvoker
implements Runnable,
BeanClassLoaderAware,
InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName((String)className, (ClassLoader)this.beanClassLoader);
    }

    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        this.prepare();
    }

    @Override
    public void run() {
        try {
            this.invoke();
        }
        catch (InvocationTargetException ex) {
            this.logger.error((Object)this.getInvocationFailureMessage(), ex.getTargetException());
        }
        catch (Throwable ex) {
            this.logger.error((Object)this.getInvocationFailureMessage(), ex);
        }
    }

    protected String getInvocationFailureMessage() {
        return "Invocation of method '" + this.getTargetMethod() + "' on target class [" + this.getTargetClass() + "] failed";
    }
}

