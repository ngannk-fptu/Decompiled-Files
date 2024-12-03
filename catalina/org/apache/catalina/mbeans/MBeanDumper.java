/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.StringJoiner;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class MBeanDumper {
    private static final Log log = LogFactory.getLog(MBeanDumper.class);
    protected static final StringManager sm = StringManager.getManager(MBeanDumper.class);
    private static final String CRLF = "\r\n";

    public static String dumpBeans(MBeanServer mbeanServer, Set<ObjectName> names) {
        StringBuilder buf = new StringBuilder();
        for (ObjectName oname : names) {
            buf.append("Name: ");
            buf.append(oname.toString());
            buf.append(CRLF);
            try {
                MBeanInfo minfo = mbeanServer.getMBeanInfo(oname);
                String code = minfo.getClassName();
                if ("org.apache.commons.modeler.BaseModelMBean".equals(code)) {
                    code = (String)mbeanServer.getAttribute(oname, "modelerType");
                }
                buf.append("modelerType: ");
                buf.append(code);
                buf.append(CRLF);
                MBeanAttributeInfo[] attrs = minfo.getAttributes();
                Object value = null;
                for (MBeanAttributeInfo attr : attrs) {
                    String attName;
                    if (!attr.isReadable() || "modelerType".equals(attName = attr.getName()) || attName.indexOf(61) >= 0 || attName.indexOf(58) >= 0 || attName.indexOf(32) >= 0) continue;
                    try {
                        value = mbeanServer.getAttribute(oname, attName);
                    }
                    catch (JMRuntimeException rme) {
                        Throwable cause = rme.getCause();
                        if (cause instanceof UnsupportedOperationException) {
                            if (!log.isDebugEnabled()) continue;
                            log.debug((Object)sm.getString("mBeanDumper.getAttributeError", new Object[]{attName, oname}), (Throwable)rme);
                            continue;
                        }
                        if (cause instanceof NullPointerException) {
                            if (!log.isDebugEnabled()) continue;
                            log.debug((Object)sm.getString("mBeanDumper.getAttributeError", new Object[]{attName, oname}), (Throwable)rme);
                            continue;
                        }
                        log.error((Object)sm.getString("mBeanDumper.getAttributeError", new Object[]{attName, oname}), (Throwable)rme);
                        continue;
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        log.error((Object)sm.getString("mBeanDumper.getAttributeError", new Object[]{attName, oname}), t);
                        continue;
                    }
                    if (value == null) continue;
                    try {
                        String valueString;
                        Class<?> c = value.getClass();
                        if (c.isArray()) {
                            int len = Array.getLength(value);
                            StringBuilder sb = new StringBuilder("Array[" + c.getComponentType().getName() + "] of length " + len);
                            if (len > 0) {
                                sb.append(CRLF);
                            }
                            for (int j = 0; j < len; ++j) {
                                Object item = Array.get(value, j);
                                sb.append(MBeanDumper.tableItemToString(item));
                                if (j >= len - 1) continue;
                                sb.append(CRLF);
                            }
                            valueString = sb.toString();
                        } else if (TabularData.class.isInstance(value)) {
                            TabularData tab = (TabularData)TabularData.class.cast(value);
                            StringJoiner joiner = new StringJoiner(CRLF);
                            joiner.add("TabularData[" + tab.getTabularType().getRowType().getTypeName() + "] of length " + tab.size());
                            for (Object item : tab.values()) {
                                joiner.add(MBeanDumper.tableItemToString(item));
                            }
                            valueString = joiner.toString();
                        } else {
                            valueString = MBeanDumper.valueToString(value);
                        }
                        buf.append(attName);
                        buf.append(": ");
                        buf.append(valueString);
                        buf.append(CRLF);
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                    }
                }
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
            }
            buf.append(CRLF);
        }
        return buf.toString();
    }

    public static String escape(String value) {
        int idx = value.indexOf(10);
        if (idx < 0) {
            return value;
        }
        int prev = 0;
        StringBuilder sb = new StringBuilder();
        while (idx >= 0) {
            MBeanDumper.appendHead(sb, value, prev, idx);
            sb.append("\\n\n ");
            prev = idx + 1;
            if (idx == value.length() - 1) break;
            idx = value.indexOf(10, idx + 1);
        }
        if (prev < value.length()) {
            MBeanDumper.appendHead(sb, value, prev, value.length());
        }
        return sb.toString();
    }

    private static void appendHead(StringBuilder sb, String value, int start, int end) {
        if (end < 1) {
            return;
        }
        int pos = start;
        while (end - pos > 78) {
            sb.append(value.substring(pos, pos + 78));
            sb.append("\n ");
            pos += 78;
        }
        sb.append(value.substring(pos, end));
    }

    private static String tableItemToString(Object item) {
        if (item == null) {
            return "\tNULL VALUE";
        }
        try {
            return "\t" + MBeanDumper.valueToString(item);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            return "\tNON-STRINGABLE VALUE";
        }
    }

    private static String valueToString(Object value) {
        String valueString;
        if (CompositeData.class.isInstance(value)) {
            StringBuilder sb = new StringBuilder("{");
            String sep = "";
            CompositeData composite = (CompositeData)CompositeData.class.cast(value);
            Set<String> keys = composite.getCompositeType().keySet();
            for (String key : keys) {
                sb.append(sep).append(key).append('=').append(composite.get(key));
                sep = ", ";
            }
            sb.append('}');
            valueString = sb.toString();
        } else {
            valueString = value.toString();
        }
        return MBeanDumper.escape(valueString);
    }
}

