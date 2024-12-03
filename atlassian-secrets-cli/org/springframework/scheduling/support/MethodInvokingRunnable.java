/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }

    @Override
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        this.prepare();
    }

    @Override
    public void run() {
        try {
            this.invoke();
        }
        catch (InvocationTargetException ex) {
            this.logger.error(this.getInvocationFailureMessage(), ex.getTargetException());
        }
        catch (Throwable ex) {
            this.logger.error(this.getInvocationFailureMessage(), ex);
        }
    }

    protected String getInvocationFailureMessage() {
        return "Invocation of method '" + this.getTargetMethod() + "' on target class [" + this.getTargetClass() + "] failed";
    }
}

