/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.jms;

import java.util.HashMap;
import org.apache.axis.AxisProperties;
import org.apache.axis.components.jms.JMSVendorAdapter;

public class JMSVendorAdapterFactory {
    private static HashMap s_adapters = new HashMap();
    private static final String VENDOR_PKG = "org.apache.axis.components.jms";
    static /* synthetic */ Class class$org$apache$axis$components$jms$JMSVendorAdapter;

    public static final JMSVendorAdapter getJMSVendorAdapter() {
        return (JMSVendorAdapter)AxisProperties.newInstance(class$org$apache$axis$components$jms$JMSVendorAdapter == null ? (class$org$apache$axis$components$jms$JMSVendorAdapter = JMSVendorAdapterFactory.class$("org.apache.axis.components.jms.JMSVendorAdapter")) : class$org$apache$axis$components$jms$JMSVendorAdapter);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final JMSVendorAdapter getJMSVendorAdapter(String vendorId) {
        if (s_adapters.containsKey(vendorId)) {
            return (JMSVendorAdapter)s_adapters.get(vendorId);
        }
        JMSVendorAdapter adapter = null;
        try {
            Class<?> vendorClass = Class.forName(JMSVendorAdapterFactory.getVendorAdapterClassname(vendorId));
            adapter = (JMSVendorAdapter)vendorClass.newInstance();
        }
        catch (Exception e) {
            return null;
        }
        HashMap hashMap = s_adapters;
        synchronized (hashMap) {
            if (s_adapters.containsKey(vendorId)) {
                return (JMSVendorAdapter)s_adapters.get(vendorId);
            }
            if (adapter != null) {
                s_adapters.put(vendorId, adapter);
            }
        }
        return adapter;
    }

    private static String getVendorAdapterClassname(String vendorId) {
        StringBuffer sb = new StringBuffer(VENDOR_PKG).append(".");
        sb.append(vendorId);
        sb.append("VendorAdapter");
        return sb.toString();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        AxisProperties.setClassDefault(class$org$apache$axis$components$jms$JMSVendorAdapter == null ? (class$org$apache$axis$components$jms$JMSVendorAdapter = JMSVendorAdapterFactory.class$("org.apache.axis.components.jms.JMSVendorAdapter")) : class$org$apache$axis$components$jms$JMSVendorAdapter, "org.apache.axis.components.jms.JNDIVendorAdapter");
    }
}

