/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 */
package com.atlassian.spring.extension.registration;

import com.atlassian.spring.extension.registration.Registration;
import com.atlassian.spring.extension.registration.RegistrationException;
import java.lang.reflect.Method;
import org.springframework.beans.factory.BeanFactory;

class KeyValueRegistration
implements Registration {
    private final String targetBeanName;
    private final String key;
    private final String beanNameToRegister;
    private final String registrationMethodName;

    public KeyValueRegistration(String targetBeanName, String key, String beanNameToRegister, String registrationMethodName) {
        this.targetBeanName = targetBeanName;
        this.key = key;
        this.beanNameToRegister = beanNameToRegister;
        this.registrationMethodName = registrationMethodName;
    }

    @Override
    public void register(BeanFactory beanFactory) throws RegistrationException {
        Object beanToRegister = this.findBeanToRegister(beanFactory);
        Object targetBean = this.findTargetBean(beanFactory);
        try {
            Method registrationMethod = this.findRegistrationMethod(targetBean.getClass(), beanToRegister.getClass(), this.registrationMethodName);
            registrationMethod.invoke(targetBean, this.key, beanToRegister);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RegistrationException("Unable to register bean " + this.beanNameToRegister + " with " + this.targetBeanName + ": " + e.getMessage(), e);
        }
    }

    private Object findBeanToRegister(BeanFactory beanFactory) throws RegistrationException {
        Object beanToRegister = beanFactory.getBean(this.beanNameToRegister);
        if (beanToRegister == null) {
            throw new RegistrationException("Unable to register " + this.beanNameToRegister + " with " + this.targetBeanName + ": bean with name " + this.beanNameToRegister + " not found.");
        }
        return beanToRegister;
    }

    private Object findTargetBean(BeanFactory beanFactory) throws RegistrationException {
        Object targetBean = beanFactory.getBean(this.targetBeanName);
        if (targetBean == null) {
            throw new RegistrationException("Unable to register " + this.beanNameToRegister + " with " + this.targetBeanName + ": bean with name " + this.targetBeanName + " not found.");
        }
        return targetBean;
    }

    private Method findRegistrationMethod(Class targetClass, Class classToRegister, String registrationMethodName) throws NoSuchMethodException {
        for (int i = 0; i < targetClass.getMethods().length; ++i) {
            Class<?>[] parameterTypes;
            Method method = targetClass.getMethods()[i];
            if (!method.getName().equals(registrationMethodName) || (parameterTypes = method.getParameterTypes()).length != 2 || !parameterTypes[0].isAssignableFrom(String.class) || !parameterTypes[1].isAssignableFrom(classToRegister)) continue;
            return method;
        }
        throw new NoSuchMethodException("No registration method " + registrationMethodName + " found on " + targetClass.getName() + " for type " + targetClass.getName());
    }
}

