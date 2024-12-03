/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import java.net.MalformedURLException;
import java.net.URL;
import org.osgi.framework.Version;

public class PropertyImpl {
    private String m_name = null;
    private String m_type = null;
    private Object m_value = null;

    public PropertyImpl() {
    }

    public PropertyImpl(String name, String type, String value) {
        this.setN(name);
        this.setT(type);
        this.setV(value);
    }

    public void setN(String name) {
        this.m_name = name;
    }

    public String getN() {
        return this.m_name;
    }

    public void setT(String type) {
        this.m_type = type;
        if (this.m_value != null) {
            this.m_value = this.convertType(this.m_value.toString());
        }
    }

    public String getT() {
        return this.m_type;
    }

    public void setV(String value) {
        this.m_value = this.convertType(value);
    }

    public Object getV() {
        return this.m_value;
    }

    private Object convertType(String value) {
        if (this.m_type != null && this.m_type.equalsIgnoreCase("version")) {
            return new Version(value);
        }
        if (this.m_type != null && this.m_type.equalsIgnoreCase("url")) {
            try {
                return new URL(value);
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }
}

