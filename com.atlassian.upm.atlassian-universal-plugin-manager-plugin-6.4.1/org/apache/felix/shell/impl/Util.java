/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.apache.felix.shell.impl;

import org.osgi.framework.Bundle;

public class Util {
    private static StringBuffer m_sb = new StringBuffer();

    public static String getBundleName(Bundle bundle) {
        if (bundle != null) {
            String name = (String)bundle.getHeaders().get("Bundle-Name");
            return name == null ? "Bundle " + Long.toString(bundle.getBundleId()) : name + " (" + Long.toString(bundle.getBundleId()) + ")";
        }
        return "[STALE BUNDLE]";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getUnderlineString(String s) {
        StringBuffer stringBuffer = m_sb;
        synchronized (stringBuffer) {
            m_sb.delete(0, m_sb.length());
            for (int i = 0; i < s.length(); ++i) {
                m_sb.append('-');
            }
            return m_sb.toString();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getValueString(Object obj) {
        StringBuffer stringBuffer = m_sb;
        synchronized (stringBuffer) {
            if (obj instanceof String) {
                return (String)obj;
            }
            if (obj instanceof String[]) {
                String[] array = (String[])obj;
                m_sb.delete(0, m_sb.length());
                for (int i = 0; i < array.length; ++i) {
                    if (i != 0) {
                        m_sb.append(", ");
                    }
                    m_sb.append(array[i].toString());
                }
                return m_sb.toString();
            }
            if (obj instanceof Boolean) {
                return ((Boolean)obj).toString();
            }
            if (obj instanceof Long) {
                return ((Long)obj).toString();
            }
            if (obj instanceof Integer) {
                return ((Integer)obj).toString();
            }
            if (obj instanceof Short) {
                return ((Short)obj).toString();
            }
            if (obj instanceof Double) {
                return ((Double)obj).toString();
            }
            if (obj instanceof Float) {
                return ((Float)obj).toString();
            }
            return "<unknown value type>";
        }
    }
}

