/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.params;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.commons.httpclient.params.DefaultHttpParamsFactory;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.params.HttpParamsFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultHttpParams
implements HttpParams,
Serializable,
Cloneable {
    private static final Log LOG = LogFactory.getLog(DefaultHttpParams.class);
    private static HttpParamsFactory httpParamsFactory = new DefaultHttpParamsFactory();
    private HttpParams defaults = null;
    private HashMap parameters = null;

    public static HttpParams getDefaultParams() {
        return httpParamsFactory.getDefaultParams();
    }

    public static void setHttpParamsFactory(HttpParamsFactory httpParamsFactory) {
        if (httpParamsFactory == null) {
            throw new IllegalArgumentException("httpParamsFactory may not be null");
        }
        DefaultHttpParams.httpParamsFactory = httpParamsFactory;
    }

    public DefaultHttpParams(HttpParams defaults) {
        this.defaults = defaults;
    }

    public DefaultHttpParams() {
        this(DefaultHttpParams.getDefaultParams());
    }

    @Override
    public synchronized HttpParams getDefaults() {
        return this.defaults;
    }

    @Override
    public synchronized void setDefaults(HttpParams params) {
        this.defaults = params;
    }

    @Override
    public synchronized Object getParameter(String name) {
        Object param = null;
        if (this.parameters != null) {
            param = this.parameters.get(name);
        }
        if (param != null) {
            return param;
        }
        if (this.defaults != null) {
            return this.defaults.getParameter(name);
        }
        return null;
    }

    @Override
    public synchronized void setParameter(String name, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }
        this.parameters.put(name, value);
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Set parameter " + name + " = " + value));
        }
    }

    public synchronized void setParameters(String[] names, Object value) {
        for (int i = 0; i < names.length; ++i) {
            this.setParameter(names[i], value);
        }
    }

    @Override
    public long getLongParameter(String name, long defaultValue) {
        Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Long)param;
    }

    @Override
    public void setLongParameter(String name, long value) {
        this.setParameter(name, new Long(value));
    }

    @Override
    public int getIntParameter(String name, int defaultValue) {
        Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Integer)param;
    }

    @Override
    public void setIntParameter(String name, int value) {
        this.setParameter(name, new Integer(value));
    }

    @Override
    public double getDoubleParameter(String name, double defaultValue) {
        Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Double)param;
    }

    @Override
    public void setDoubleParameter(String name, double value) {
        this.setParameter(name, new Double(value));
    }

    @Override
    public boolean getBooleanParameter(String name, boolean defaultValue) {
        Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Boolean)param;
    }

    @Override
    public void setBooleanParameter(String name, boolean value) {
        this.setParameter(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public boolean isParameterSet(String name) {
        return this.getParameter(name) != null;
    }

    @Override
    public boolean isParameterSetLocally(String name) {
        return this.parameters != null && this.parameters.get(name) != null;
    }

    @Override
    public boolean isParameterTrue(String name) {
        return this.getBooleanParameter(name, false);
    }

    @Override
    public boolean isParameterFalse(String name) {
        return !this.getBooleanParameter(name, false);
    }

    public void clear() {
        this.parameters = null;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultHttpParams clone = (DefaultHttpParams)super.clone();
        if (this.parameters != null) {
            clone.parameters = (HashMap)this.parameters.clone();
        }
        clone.setDefaults(this.defaults);
        return clone;
    }
}

