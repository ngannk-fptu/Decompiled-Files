/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import javax.management.MBeanAttributeInfo;
import org.apache.tomcat.util.modeler.FeatureInfo;

public class AttributeInfo
extends FeatureInfo {
    private static final long serialVersionUID = -2511626862303972143L;
    protected String displayName = null;
    protected String getMethod = null;
    protected String setMethod = null;
    protected boolean readable = true;
    protected boolean writeable = true;
    protected boolean is = false;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGetMethod() {
        if (this.getMethod == null) {
            this.getMethod = this.getMethodName(this.getName(), true, this.isIs());
        }
        return this.getMethod;
    }

    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    public boolean isIs() {
        return this.is;
    }

    public void setIs(boolean is) {
        this.is = is;
    }

    public boolean isReadable() {
        return this.readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public String getSetMethod() {
        if (this.setMethod == null) {
            this.setMethod = this.getMethodName(this.getName(), false, false);
        }
        return this.setMethod;
    }

    public void setSetMethod(String setMethod) {
        this.setMethod = setMethod;
    }

    public boolean isWriteable() {
        return this.writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    MBeanAttributeInfo createAttributeInfo() {
        if (this.info == null) {
            this.info = new MBeanAttributeInfo(this.getName(), this.getType(), this.getDescription(), this.isReadable(), this.isWriteable(), false);
        }
        return (MBeanAttributeInfo)this.info;
    }

    private String getMethodName(String name, boolean getter, boolean is) {
        StringBuilder sb = new StringBuilder();
        if (getter) {
            if (is) {
                sb.append("is");
            } else {
                sb.append("get");
            }
        } else {
            sb.append("set");
        }
        sb.append(Character.toUpperCase(name.charAt(0)));
        sb.append(name.substring(1));
        return sb.toString();
    }
}

