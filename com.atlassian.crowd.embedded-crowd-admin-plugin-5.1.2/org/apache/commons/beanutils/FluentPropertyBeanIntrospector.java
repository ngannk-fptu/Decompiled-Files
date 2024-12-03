/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.IntrospectionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FluentPropertyBeanIntrospector
implements BeanIntrospector {
    public static final String DEFAULT_WRITE_METHOD_PREFIX = "set";
    private final Log log = LogFactory.getLog(this.getClass());
    private final String writeMethodPrefix;

    public FluentPropertyBeanIntrospector(String writePrefix) {
        if (writePrefix == null) {
            throw new IllegalArgumentException("Prefix for write methods must not be null!");
        }
        this.writeMethodPrefix = writePrefix;
    }

    public FluentPropertyBeanIntrospector() {
        this(DEFAULT_WRITE_METHOD_PREFIX);
    }

    public String getWriteMethodPrefix() {
        return this.writeMethodPrefix;
    }

    @Override
    public void introspect(IntrospectionContext icontext) throws IntrospectionException {
        for (Method m : icontext.getTargetClass().getMethods()) {
            if (!m.getName().startsWith(this.getWriteMethodPrefix())) continue;
            String propertyName = this.propertyName(m);
            PropertyDescriptor pd = icontext.getPropertyDescriptor(propertyName);
            try {
                if (pd == null) {
                    icontext.addPropertyDescriptor(this.createFluentPropertyDescritor(m, propertyName));
                    continue;
                }
                if (pd.getWriteMethod() != null) continue;
                pd.setWriteMethod(m);
            }
            catch (IntrospectionException e) {
                this.log.info((Object)("Error when creating PropertyDescriptor for " + m + "! Ignoring this property."));
                this.log.debug((Object)"Exception is:", (Throwable)e);
            }
        }
    }

    private String propertyName(Method m) {
        String methodName = m.getName().substring(this.getWriteMethodPrefix().length());
        return methodName.length() > 1 ? Introspector.decapitalize(methodName) : methodName.toLowerCase(Locale.ENGLISH);
    }

    private PropertyDescriptor createFluentPropertyDescritor(Method m, String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(this.propertyName(m), null, m);
    }
}

