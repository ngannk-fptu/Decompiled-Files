/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.api;

import com.ctc.wstx.util.ArgUtil;
import java.util.HashMap;
import org.codehaus.stax2.XMLStreamProperties;

abstract class CommonConfig
implements XMLStreamProperties {
    protected static final String IMPL_NAME = "woodstox";
    protected static final String IMPL_VERSION = "5.0";
    static final int CPROP_IMPL_NAME = 1;
    static final int CPROP_IMPL_VERSION = 2;
    static final int CPROP_SUPPORTS_XML11 = 3;
    static final int CPROP_SUPPORT_XMLID = 4;
    static final int CPROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE = 5;
    static final HashMap<String, Integer> sStdProperties = new HashMap(16);
    protected boolean mReturnNullForDefaultNamespace;

    protected CommonConfig(CommonConfig base) {
        this.mReturnNullForDefaultNamespace = base == null ? Boolean.getBoolean("com.ctc.wstx.returnNullForDefaultNamespace") : base.mReturnNullForDefaultNamespace;
    }

    public Object getProperty(String propName) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.getProperty(id);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            this.reportUnknownProperty(propName);
            return null;
        }
        return this.getStdProperty(id);
    }

    public boolean isPropertySupported(String propName) {
        return this.findPropertyId(propName) >= 0 || this.findStdPropertyId(propName) >= 0;
    }

    public boolean setProperty(String propName, Object value) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.setProperty(propName, id, value);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            this.reportUnknownProperty(propName);
            return false;
        }
        return this.setStdProperty(propName, id, value);
    }

    protected void reportUnknownProperty(String propName) {
        throw new IllegalArgumentException("Unrecognized property '" + propName + "'");
    }

    public final Object safeGetProperty(String propName) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.getProperty(id);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            return null;
        }
        return this.getStdProperty(id);
    }

    public static String getImplName() {
        return IMPL_NAME;
    }

    public static String getImplVersion() {
        return IMPL_VERSION;
    }

    protected abstract int findPropertyId(String var1);

    public boolean doesSupportXml11() {
        return true;
    }

    public boolean doesSupportXmlId() {
        return true;
    }

    public boolean returnNullForDefaultNamespace() {
        return this.mReturnNullForDefaultNamespace;
    }

    protected abstract Object getProperty(int var1);

    protected abstract boolean setProperty(String var1, int var2, Object var3);

    protected int findStdPropertyId(String propName) {
        Integer I = sStdProperties.get(propName);
        return I == null ? -1 : I;
    }

    protected boolean setStdProperty(String propName, int id, Object value) {
        switch (id) {
            case 5: {
                this.mReturnNullForDefaultNamespace = ArgUtil.convertToBoolean(propName, value);
                return true;
            }
        }
        return false;
    }

    protected Object getStdProperty(int id) {
        switch (id) {
            case 1: {
                return IMPL_NAME;
            }
            case 2: {
                return IMPL_VERSION;
            }
            case 3: {
                return this.doesSupportXml11() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4: {
                return this.doesSupportXmlId() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 5: {
                return this.returnNullForDefaultNamespace() ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
    }

    static {
        sStdProperties.put("org.codehaus.stax2.implName", 1);
        sStdProperties.put("org.codehaus.stax2.implVersion", 2);
        sStdProperties.put("org.codehaus.stax2.supportsXml11", 3);
        sStdProperties.put("org.codehaus.stax2.supportXmlId", 4);
        sStdProperties.put("com.ctc.wstx.returnNullForDefaultNamespace", 5);
        sStdProperties.put("http://java.sun.com/xml/stream/properties/implementation-name", 1);
    }
}

