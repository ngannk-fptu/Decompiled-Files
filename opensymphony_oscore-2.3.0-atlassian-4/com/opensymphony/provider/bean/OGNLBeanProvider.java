/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.Ognl
 *  ognl.OgnlException
 */
package com.opensymphony.provider.bean;

import com.opensymphony.provider.BeanProvider;
import com.opensymphony.provider.ProviderConfigurationException;
import ognl.Ognl;
import ognl.OgnlException;

public class OGNLBeanProvider
implements BeanProvider {
    @Override
    public boolean setProperty(Object object, String property, Object value) {
        if (object == null || property == null) {
            return false;
        }
        try {
            Ognl.setValue((String)property, (Object)object, (Object)value);
            return true;
        }
        catch (OgnlException e) {
            return false;
        }
    }

    @Override
    public Object getProperty(Object object, String property) {
        if (object == null || property == null) {
            return null;
        }
        try {
            return Ognl.getValue((String)property, (Object)object);
        }
        catch (OgnlException e) {
            return null;
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }
}

