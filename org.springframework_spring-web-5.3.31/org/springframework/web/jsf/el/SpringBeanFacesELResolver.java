/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ELResolver
 *  javax.el.PropertyNotWritableException
 *  javax.faces.context.FacesContext
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.jsf.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

public class SpringBeanFacesELResolver
extends ELResolver {
    @Nullable
    public Object getValue(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = this.getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getBean(beanName);
            }
        }
        return null;
    }

    @Nullable
    public Class<?> getType(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = this.getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getType(beanName);
            }
        }
        return null;
    }

    public void setValue(ELContext elContext, @Nullable Object base, Object property, Object value) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = this.getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                if (value == wac.getBean(beanName)) {
                    elContext.setPropertyResolved(true);
                } else {
                    throw new PropertyNotWritableException("Variable '" + beanName + "' refers to a Spring bean which by definition is not writable");
                }
            }
        }
    }

    public boolean isReadOnly(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = this.getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, @Nullable Object base) {
        return null;
    }

    public Class<?> getCommonPropertyType(ELContext elContext, @Nullable Object base) {
        return Object.class;
    }

    protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}

