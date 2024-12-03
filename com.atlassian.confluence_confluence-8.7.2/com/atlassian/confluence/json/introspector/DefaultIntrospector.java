/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.springframework.aop.framework.Advised
 */
package com.atlassian.confluence.json.introspector;

import com.atlassian.confluence.json.introspector.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.aop.framework.Advised;

public class DefaultIntrospector
implements Introspector {
    private static final DefaultIntrospector INSTANCE = new DefaultIntrospector();

    private DefaultIntrospector() {
    }

    public static DefaultIntrospector getInstance() {
        return INSTANCE;
    }

    @Override
    public Map<String, Object> getProperties(Object bean) {
        Object unwrappedBean = this.unwrapBean(bean);
        return this.getPropertiesInternal(unwrappedBean);
    }

    private Map<String, Object> getPropertiesInternal(Object unwrappedBean) {
        try {
            return PropertyUtils.describe((Object)unwrappedBean);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object unwrapBean(Object bean) {
        if (bean instanceof Advised) {
            Advised advised = (Advised)bean;
            try {
                return advised.getTargetSource().getTarget();
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to extract ServiceCommand from AspectJ proxy", e);
            }
        }
        return bean;
    }
}

