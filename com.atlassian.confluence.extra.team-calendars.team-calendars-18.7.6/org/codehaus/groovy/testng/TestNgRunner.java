/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.testng;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import java.lang.reflect.Method;
import org.codehaus.groovy.plugin.GroovyRunner;
import org.codehaus.groovy.runtime.InvokerHelper;

public class TestNgRunner
implements GroovyRunner {
    @Override
    public boolean canRun(Class scriptClass, GroovyClassLoader loader) {
        boolean isTest;
        block6: {
            isTest = false;
            try {
                try {
                    Method[] methods;
                    Class<?> testAnnotationClass = loader.loadClass("org.testng.annotations.Test");
                    Object annotation = scriptClass.getAnnotation(testAnnotationClass);
                    if (annotation != null) {
                        isTest = true;
                        break block6;
                    }
                    for (Method method : methods = scriptClass.getMethods()) {
                        annotation = method.getAnnotation(testAnnotationClass);
                        if (annotation == null) continue;
                        isTest = true;
                        break;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return isTest;
    }

    @Override
    public Object run(Class scriptClass, GroovyClassLoader loader) {
        try {
            Class<?> testNGClass = loader.loadClass("org.testng.TestNG");
            Object testng = InvokerHelper.invokeConstructorOf(testNGClass, (Object)new Object[0]);
            InvokerHelper.invokeMethod(testng, "setTestClasses", new Object[]{scriptClass});
            Class<?> listenerClass = loader.loadClass("org.testng.TestListenerAdapter");
            Object listener = InvokerHelper.invokeConstructorOf(listenerClass, (Object)new Object[0]);
            InvokerHelper.invokeMethod(testng, "addListener", new Object[]{listener});
            return InvokerHelper.invokeMethod(testng, "run", new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new GroovyRuntimeException("Error running TestNG test.", e);
        }
    }
}

