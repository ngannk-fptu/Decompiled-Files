/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.json.JSONFilter
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.manager;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.manager.Constants;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.json.JSONFilter;
import org.apache.tomcat.util.security.Escape;

public class StatusTransformer {
    public static void setContentType(HttpServletResponse response, int mode) {
        if (mode == 0) {
            response.setContentType("text/html;charset=utf-8");
        } else if (mode == 1) {
            response.setContentType("text/xml;charset=utf-8");
        } else if (mode == 2) {
            response.setContentType("application/json;charset=utf-8");
        }
    }

    public static void writeHeader(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.HTML_HEADER_SECTION, args));
        } else if (mode == 1) {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.print(MessageFormat.format("<?xml-stylesheet type=\"text/xsl\" href=\"{0}/xform.xsl\" ?>\n", args));
            writer.write("<status>");
        } else if (mode == 2) {
            writer.append('{').append('\"').append("tomcat").append('\"').append(':').append('{').println();
        }
    }

    public static void writeBody(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        }
    }

    public static void writeManager(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        }
    }

    public static void writePageHeading(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, args));
        }
    }

    public static void writeServerInfo(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        }
    }

    public static void writeFooter(PrintWriter writer, int mode) {
        if (mode == 0) {
            writer.print(Constants.HTML_TAIL_SECTION);
        } else if (mode == 1) {
            writer.write("</status>");
        } else if (mode == 2) {
            writer.append('}').append('}');
        }
    }

    public static void writeOSState(PrintWriter writer, int mode, Object[] args) {
        long[] result = new long[16];
        boolean ok = false;
        try {
            String methodName = "info";
            Class[] paramTypes = new Class[]{result.getClass()};
            Object[] paramValues = new Object[]{result};
            Method method = Class.forName("org.apache.tomcat.jni.OS").getMethod(methodName, paramTypes);
            method.invoke(null, paramValues);
            ok = true;
        }
        catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        if (ok) {
            if (mode == 0) {
                writer.print("<h1>OS</h1>");
                writer.print("<p>");
                writer.print(args[0]);
                writer.print(' ');
                writer.print(StatusTransformer.formatSize(result[0], true));
                writer.print(' ');
                writer.print(args[1]);
                writer.print(' ');
                writer.print(StatusTransformer.formatSize(result[1], true));
                writer.print(' ');
                writer.print(args[2]);
                writer.print(' ');
                writer.print(StatusTransformer.formatSize(result[2], true));
                writer.print(' ');
                writer.print(args[3]);
                writer.print(' ');
                writer.print(StatusTransformer.formatSize(result[3], true));
                writer.print(' ');
                writer.print(args[4]);
                writer.print(' ');
                writer.print((Object)result[6]);
                writer.print("<br>");
                writer.print(args[5]);
                writer.print(' ');
                writer.print(StatusTransformer.formatTime(result[11] / 1000L, true));
                writer.print(' ');
                writer.print(args[6]);
                writer.print(' ');
                writer.print(StatusTransformer.formatTime(result[12] / 1000L, true));
                writer.print("</p>");
            } else if (mode == 1) {
                // empty if block
            }
        }
    }

    public static void writeVMState(PrintWriter writer, int mode, Object[] args) throws Exception {
        MemoryUsage usage;
        TreeMap<String, MemoryPoolMXBean> memoryPoolMBeans = new TreeMap<String, MemoryPoolMXBean>();
        for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            String sortKey = (Object)((Object)mbean.getType()) + ":" + mbean.getName();
            memoryPoolMBeans.put(sortKey, mbean);
        }
        if (mode == 0) {
            writer.print("<h1>JVM</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(' ');
            writer.print(StatusTransformer.formatSize(Runtime.getRuntime().freeMemory(), true));
            writer.print(' ');
            writer.print(args[1]);
            writer.print(' ');
            writer.print(StatusTransformer.formatSize(Runtime.getRuntime().totalMemory(), true));
            writer.print(' ');
            writer.print(args[2]);
            writer.print(' ');
            writer.print(StatusTransformer.formatSize(Runtime.getRuntime().maxMemory(), true));
            writer.print("</p>");
            writer.write("<table border=\"0\"><thead><tr><th>" + args[3] + "</th><th>" + args[4] + "</th><th>" + args[5] + "</th><th>" + args[6] + "</th><th>" + args[7] + "</th><th>" + args[8] + "</th></tr></thead><tbody>");
            for (MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                usage = memoryPoolMBean.getUsage();
                writer.write("<tr><td>");
                writer.print(memoryPoolMBean.getName());
                writer.write("</td><td>");
                writer.print((Object)memoryPoolMBean.getType());
                writer.write("</td><td>");
                writer.print(StatusTransformer.formatSize(usage.getInit(), true));
                writer.write("</td><td>");
                writer.print(StatusTransformer.formatSize(usage.getCommitted(), true));
                writer.write("</td><td>");
                writer.print(StatusTransformer.formatSize(usage.getMax(), true));
                writer.write("</td><td>");
                writer.print(StatusTransformer.formatSize(usage.getUsed(), true));
                if (usage.getMax() > 0L) {
                    writer.write(" (" + usage.getUsed() * 100L / usage.getMax() + "%)");
                }
                writer.write("</td></tr>");
            }
            writer.write("</tbody></table>");
        } else if (mode == 1) {
            writer.write("<jvm>");
            writer.write("<memory");
            writer.write(" free='" + Runtime.getRuntime().freeMemory() + "'");
            writer.write(" total='" + Runtime.getRuntime().totalMemory() + "'");
            writer.write(" max='" + Runtime.getRuntime().maxMemory() + "'/>");
            for (MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                usage = memoryPoolMBean.getUsage();
                writer.write("<memorypool");
                writer.write(" name='" + Escape.xml((String)"", (String)memoryPoolMBean.getName()) + "'");
                writer.write(" type='" + (Object)((Object)memoryPoolMBean.getType()) + "'");
                writer.write(" usageInit='" + usage.getInit() + "'");
                writer.write(" usageCommitted='" + usage.getCommitted() + "'");
                writer.write(" usageMax='" + usage.getMax() + "'");
                writer.write(" usageUsed='" + usage.getUsed() + "'/>");
            }
            writer.write("</jvm>");
        } else if (mode == 2) {
            writer.append('\"').append("jvm").append('\"').append(':').append('{').println();
            writer.append('\"').append("memory").append('\"').append(':').append('{');
            StatusTransformer.appendJSonValue(writer, "free", Long.toString(Runtime.getRuntime().freeMemory())).append(',');
            StatusTransformer.appendJSonValue(writer, "total", Long.toString(Runtime.getRuntime().totalMemory())).append(',');
            StatusTransformer.appendJSonValue(writer, "max", Long.toString(Runtime.getRuntime().maxMemory()));
            writer.append('}').append(',').println();
            writer.append('\"').append("memorypool").append('\"').append(':').append('[');
            boolean first = true;
            for (MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                MemoryUsage usage2 = memoryPoolMBean.getUsage();
                if (first) {
                    first = false;
                } else {
                    writer.append(',').println();
                }
                writer.append('{');
                StatusTransformer.appendJSonValue(writer, "name", JSONFilter.escape((String)memoryPoolMBean.getName())).append(',');
                StatusTransformer.appendJSonValue(writer, "type", memoryPoolMBean.getType().toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "usageInit", Long.toString(usage2.getInit())).append(',');
                StatusTransformer.appendJSonValue(writer, "usageCommitted", Long.toString(usage2.getCommitted())).append(',');
                StatusTransformer.appendJSonValue(writer, "usageMax", Long.toString(usage2.getMax())).append(',');
                StatusTransformer.appendJSonValue(writer, "usageUsed", Long.toString(usage2.getUsed()));
                writer.append('}');
            }
            writer.append(']').println();
            writer.append('}');
        }
    }

    private static PrintWriter appendJSonValue(PrintWriter writer, String name, String value) {
        return writer.append('\"').append(name).append('\"').append(':').append('\"').append(value).append('\"');
    }

    public static void writeConnectorsState(PrintWriter writer, MBeanServer mBeanServer, Vector<ObjectName> threadPools, Vector<ObjectName> globalRequestProcessors, Vector<ObjectName> requestProcessors, int mode, Object[] args) throws Exception {
        if (mode == 2) {
            writer.append(',').println();
            writer.append('\"').append("connector").append('\"').append(':').append('[').println();
        }
        boolean first = true;
        for (ObjectName objectName : threadPools) {
            if (first) {
                first = false;
            } else if (mode == 2) {
                writer.append(',').println();
            }
            String name = objectName.getKeyProperty("name");
            StatusTransformer.writeConnectorState(writer, objectName, name, mBeanServer, globalRequestProcessors, requestProcessors, mode, args);
        }
        if (mode == 2) {
            writer.append(']').println();
        }
    }

    public static void writeConnectorState(PrintWriter writer, ObjectName tpName, String name, MBeanServer mBeanServer, Vector<ObjectName> globalRequestProcessors, Vector<ObjectName> requestProcessors, int mode, Object[] args) throws Exception {
        if (mode == 0) {
            writer.print("<h1>");
            writer.print(name);
            writer.print("</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "maxThreads"));
            writer.print(' ');
            writer.print(args[1]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadCount"));
            writer.print(' ');
            writer.print(args[2]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
            writer.print(' ');
            writer.print(args[3]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "keepAliveCount"));
            writer.print("<br>");
            ObjectName grpName = null;
            for (ObjectName objectName : globalRequestProcessors) {
                if (!name.equals(objectName.getKeyProperty("name")) || objectName.getKeyProperty("Upgrade") != null) continue;
                grpName = objectName;
            }
            if (grpName == null) {
                return;
            }
            writer.print(args[4]);
            writer.print(' ');
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(grpName, "maxTime"), false));
            writer.print(' ');
            writer.print(args[5]);
            writer.print(' ');
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(grpName, "processingTime"), true));
            writer.print(' ');
            writer.print(args[6]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(grpName, "requestCount"));
            writer.print(' ');
            writer.print(args[7]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(grpName, "errorCount"));
            writer.print(' ');
            writer.print(args[8]);
            writer.print(' ');
            writer.print(StatusTransformer.formatSize(mBeanServer.getAttribute(grpName, "bytesReceived"), true));
            writer.print(' ');
            writer.print(args[9]);
            writer.print(' ');
            writer.print(StatusTransformer.formatSize(mBeanServer.getAttribute(grpName, "bytesSent"), true));
            writer.print("</p>");
            writer.print("<table border=\"0\"><tr><th>" + args[10] + "</th><th>" + args[11] + "</th><th>" + args[12] + "</th><th>" + args[13] + "</th><th>" + args[14] + "</th><th>" + args[15] + "</th><th>" + args[16] + "</th><th>" + args[17] + "</th></tr>");
            for (ObjectName objectName : requestProcessors) {
                if (!name.equals(objectName.getKeyProperty("worker"))) continue;
                writer.print("<tr>");
                StatusTransformer.writeProcessorState(writer, objectName, mBeanServer, mode);
                writer.print("</tr>");
            }
            writer.print("</table>");
            writer.print("<p>");
            writer.print(args[18]);
            writer.print("</p>");
        } else if (mode == 1) {
            writer.write("<connector name='" + name + "'>");
            writer.write("<threadInfo ");
            writer.write(" maxThreads=\"" + mBeanServer.getAttribute(tpName, "maxThreads") + "\"");
            writer.write(" currentThreadCount=\"" + mBeanServer.getAttribute(tpName, "currentThreadCount") + "\"");
            writer.write(" currentThreadsBusy=\"" + mBeanServer.getAttribute(tpName, "currentThreadsBusy") + "\"");
            writer.write(" />");
            ObjectName grpName = null;
            for (ObjectName objectName : globalRequestProcessors) {
                if (!name.equals(objectName.getKeyProperty("name")) || objectName.getKeyProperty("Upgrade") != null) continue;
                grpName = objectName;
            }
            if (grpName != null) {
                writer.write("<requestInfo ");
                writer.write(" maxTime=\"" + mBeanServer.getAttribute(grpName, "maxTime") + "\"");
                writer.write(" processingTime=\"" + mBeanServer.getAttribute(grpName, "processingTime") + "\"");
                writer.write(" requestCount=\"" + mBeanServer.getAttribute(grpName, "requestCount") + "\"");
                writer.write(" errorCount=\"" + mBeanServer.getAttribute(grpName, "errorCount") + "\"");
                writer.write(" bytesReceived=\"" + mBeanServer.getAttribute(grpName, "bytesReceived") + "\"");
                writer.write(" bytesSent=\"" + mBeanServer.getAttribute(grpName, "bytesSent") + "\"");
                writer.write(" />");
                writer.write("<workers>");
                for (ObjectName objectName : requestProcessors) {
                    if (!name.equals(objectName.getKeyProperty("worker"))) continue;
                    StatusTransformer.writeProcessorState(writer, objectName, mBeanServer, mode);
                }
                writer.write("</workers>");
            }
            writer.write("</connector>");
        } else if (mode == 2) {
            writer.append('{').println();
            StatusTransformer.appendJSonValue(writer, "name", JSONFilter.escape((String)name)).append(',').println();
            writer.append('\"').append("threadInfo").append('\"').append(':').append('{');
            StatusTransformer.appendJSonValue(writer, "maxThreads", mBeanServer.getAttribute(tpName, "maxThreads").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "currentThreadCount", mBeanServer.getAttribute(tpName, "currentThreadCount").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "currentThreadsBusy", mBeanServer.getAttribute(tpName, "currentThreadsBusy").toString());
            writer.append('}');
            ObjectName grpName = null;
            for (ObjectName objectName : globalRequestProcessors) {
                if (!name.equals(objectName.getKeyProperty("name")) || objectName.getKeyProperty("Upgrade") != null) continue;
                grpName = objectName;
            }
            if (grpName != null) {
                writer.append(',').println();
                writer.append('\"').append("requestInfo").append('\"').append(':').append('{');
                StatusTransformer.appendJSonValue(writer, "maxTime", mBeanServer.getAttribute(grpName, "maxTime").toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(grpName, "processingTime").toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "requestCount", mBeanServer.getAttribute(grpName, "requestCount").toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "errorCount", mBeanServer.getAttribute(grpName, "errorCount").toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "bytesReceived", mBeanServer.getAttribute(grpName, "bytesReceived").toString()).append(',');
                StatusTransformer.appendJSonValue(writer, "bytesSent", mBeanServer.getAttribute(grpName, "bytesSent").toString());
                writer.append('}').println();
            }
            writer.append('}');
        }
    }

    protected static void writeProcessorState(PrintWriter writer, ObjectName pName, MBeanServer mBeanServer, int mode) throws Exception {
        Integer stageValue = (Integer)mBeanServer.getAttribute(pName, "stage");
        int stage = stageValue;
        boolean fullStatus = true;
        boolean showRequest = true;
        String stageStr = null;
        switch (stage) {
            case 1: {
                stageStr = "P";
                fullStatus = false;
                break;
            }
            case 2: {
                stageStr = "P";
                fullStatus = false;
                break;
            }
            case 3: {
                stageStr = "S";
                break;
            }
            case 4: {
                stageStr = "F";
                break;
            }
            case 5: {
                stageStr = "F";
                break;
            }
            case 7: {
                stageStr = "R";
                fullStatus = false;
                break;
            }
            case 6: {
                stageStr = "K";
                showRequest = false;
                break;
            }
            case 0: {
                stageStr = "R";
                fullStatus = false;
                break;
            }
            default: {
                stageStr = "?";
                fullStatus = false;
            }
        }
        if (mode == 0) {
            writer.write("<td><strong>");
            writer.write(stageStr);
            writer.write("</strong></td>");
            if (fullStatus) {
                writer.write("<td>");
                writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(pName, "requestProcessingTime"), false));
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(StatusTransformer.formatSize(mBeanServer.getAttribute(pName, "requestBytesSent"), false));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(StatusTransformer.formatSize(mBeanServer.getAttribute(pName, "requestBytesReceived"), false));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "remoteAddrForwarded")));
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "remoteAddr")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                writer.write(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "virtualHost")));
                writer.write("</td>");
                writer.write("<td nowrap class=\"row-left\">");
                if (showRequest) {
                    writer.write(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "method")));
                    writer.write(32);
                    writer.write(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "currentUri")));
                    String queryString = (String)mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write("?");
                        writer.print(Escape.htmlElementContent((String)queryString));
                    }
                    writer.write(32);
                    writer.write(Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "protocol")));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
            } else {
                writer.write("<td>?</td><td>?</td><td>?</td><td>?</td><td>?</td><td>?</td>");
            }
        } else if (mode == 1) {
            writer.write("<worker ");
            writer.write(" stage=\"" + stageStr + "\"");
            if (fullStatus) {
                writer.write(" requestProcessingTime=\"" + mBeanServer.getAttribute(pName, "requestProcessingTime") + "\"");
                writer.write(" requestBytesSent=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesSent"));
                } else {
                    writer.write("0");
                }
                writer.write("\"");
                writer.write(" requestBytesReceived=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesReceived"));
                } else {
                    writer.write("0");
                }
                writer.write("\"");
                writer.write(" remoteAddr=\"" + Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "remoteAddr")) + "\"");
                writer.write(" virtualHost=\"" + Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "virtualHost")) + "\"");
                if (showRequest) {
                    writer.write(" method=\"" + Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "method")) + "\"");
                    writer.write(" currentUri=\"" + Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "currentUri")) + "\"");
                    String queryString = (String)mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write(" currentQueryString=\"" + Escape.htmlElementContent((String)queryString) + "\"");
                    } else {
                        writer.write(" currentQueryString=\"&#63;\"");
                    }
                    writer.write(" protocol=\"" + Escape.htmlElementContent((Object)mBeanServer.getAttribute(pName, "protocol")) + "\"");
                } else {
                    writer.write(" method=\"&#63;\"");
                    writer.write(" currentUri=\"&#63;\"");
                    writer.write(" currentQueryString=\"&#63;\"");
                    writer.write(" protocol=\"&#63;\"");
                }
            } else {
                writer.write(" requestProcessingTime=\"0\"");
                writer.write(" requestBytesSent=\"0\"");
                writer.write(" requestBytesReceived=\"0\"");
                writer.write(" remoteAddr=\"&#63;\"");
                writer.write(" virtualHost=\"&#63;\"");
                writer.write(" method=\"&#63;\"");
                writer.write(" currentUri=\"&#63;\"");
                writer.write(" currentQueryString=\"&#63;\"");
                writer.write(" protocol=\"&#63;\"");
            }
            writer.write(" />");
        }
    }

    public static void writeDetailedState(PrintWriter writer, MBeanServer mBeanServer, int mode) throws Exception {
        ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
        Set<ObjectName> hostsON = mBeanServer.queryNames(queryHosts, null);
        if (mode == 0) {
            writer.print("<h1>");
            writer.print("Application list");
            writer.print("</h1>");
            writer.print("<p>");
            int count = 0;
            Iterator<ObjectName> iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                int slash;
                ObjectName contextON = iterator.next();
                String webModuleName = contextON.getKeyProperty("name");
                if (webModuleName.startsWith("//")) {
                    webModuleName = webModuleName.substring(2);
                }
                if ((slash = webModuleName.indexOf(47)) == -1) {
                    ++count;
                    continue;
                }
                writer.print("<a href=\"#" + count++ + ".0\">");
                writer.print(Escape.htmlElementContent((String)webModuleName));
                writer.print("</a>");
                if (!iterator.hasNext()) continue;
                writer.print("<br>");
            }
            writer.print("</p>");
            count = 0;
            for (ObjectName contextON : hostsON) {
                writer.print("<a class=\"A.name\" name=\"" + count++ + ".0\">");
                StatusTransformer.writeContext(writer, contextON, mBeanServer, mode);
            }
        } else if (mode != 1 && mode == 2) {
            writer.append(',').println();
            writer.append('\"').append("context").append('\"').append(':').append('[');
            Iterator<ObjectName> iterator = hostsON.iterator();
            boolean first = true;
            while (iterator.hasNext()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',').println();
                }
                ObjectName contextON = iterator.next();
                StatusTransformer.writeContext(writer, contextON, mBeanServer, mode);
            }
            writer.append(']').println();
        }
    }

    protected static void writeContext(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        int slash;
        String webModuleName = objectName.getKeyProperty("name");
        String name = webModuleName;
        if (name == null) {
            return;
        }
        String hostName = null;
        String contextName = null;
        if (name.startsWith("//")) {
            name = name.substring(2);
        }
        if ((slash = name.indexOf(47)) == -1) {
            return;
        }
        hostName = name.substring(0, slash);
        contextName = name.substring(slash);
        ObjectName queryManager = new ObjectName(objectName.getDomain() + ":type=Manager,context=" + contextName + ",host=" + hostName + ",*");
        Set<ObjectName> managersON = mBeanServer.queryNames(queryManager, null);
        ObjectName managerON = null;
        Iterator<ObjectName> iterator = managersON.iterator();
        while (iterator.hasNext()) {
            ObjectName aManagersON;
            managerON = aManagersON = iterator.next();
        }
        ObjectName queryJspMonitor = new ObjectName(objectName.getDomain() + ":type=JspMonitor,WebModule=" + webModuleName + ",*");
        Set<ObjectName> jspMonitorONs = mBeanServer.queryNames(queryJspMonitor, null);
        if (contextName.equals("/")) {
            contextName = "";
        }
        if (mode == 0) {
            writer.print("<h1>");
            writer.print(Escape.htmlElementContent((String)name));
            writer.print("</h1>");
            writer.print("</a>");
            writer.print("<p>");
            Object startTime = mBeanServer.getAttribute(objectName, "startTime");
            writer.print(" Start time: " + new Date((Long)startTime));
            writer.print(" Startup time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "startupTime"), false));
            writer.print(" TLD scan time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "tldScanTime"), false));
            if (managerON != null) {
                StatusTransformer.writeManager(writer, managerON, mBeanServer, mode);
            }
            if (jspMonitorONs != null) {
                StatusTransformer.writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
            }
            writer.print("</p>");
            String onStr = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
            ObjectName servletObjectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(servletObjectName, null);
            for (ObjectInstance oi : set) {
                StatusTransformer.writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
            }
        } else if (mode != 1 && mode == 2) {
            writer.append('{');
            StatusTransformer.appendJSonValue(writer, "name", JSONFilter.escape((String)JSONFilter.escape((String)name))).append(',');
            StatusTransformer.appendJSonValue(writer, "startTime", new Date((Long)mBeanServer.getAttribute(objectName, "startTime")).toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "startupTime", mBeanServer.getAttribute(objectName, "startupTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "tldScanTime", mBeanServer.getAttribute(objectName, "tldScanTime").toString());
            if (managerON != null) {
                StatusTransformer.writeManager(writer, managerON, mBeanServer, mode);
            }
            if (jspMonitorONs != null) {
                StatusTransformer.writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
            }
            writer.append(',').println();
            writer.append('\"').append("wrapper").append('\"').append(':').append('[');
            String onStr = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
            ObjectName servletObjectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(servletObjectName, null);
            boolean first = true;
            for (ObjectInstance oi : set) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',').println();
                }
                StatusTransformer.writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
            }
            writer.append(']').println();
            writer.append('}');
        }
    }

    public static void writeManager(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" Active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "activeSessions"));
            writer.print(" Session count: ");
            writer.print(mBeanServer.getAttribute(objectName, "sessionCounter"));
            writer.print(" Max active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "maxActive"));
            writer.print(" Rejected session creations: ");
            writer.print(mBeanServer.getAttribute(objectName, "rejectedSessions"));
            writer.print(" Expired sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "expiredSessions"));
            writer.print(" Longest session alive time: ");
            writer.print(StatusTransformer.formatSeconds(mBeanServer.getAttribute(objectName, "sessionMaxAliveTime")));
            writer.print(" Average session alive time: ");
            writer.print(StatusTransformer.formatSeconds(mBeanServer.getAttribute(objectName, "sessionAverageAliveTime")));
            writer.print(" Processing time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "processingTime"), false));
        } else if (mode != 1 && mode == 2) {
            writer.append(',').println();
            writer.append('\"').append("manager").append('\"').append(':').append('{');
            StatusTransformer.appendJSonValue(writer, "activeSessions", mBeanServer.getAttribute(objectName, "activeSessions").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "sessionCounter", mBeanServer.getAttribute(objectName, "sessionCounter").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "maxActive", mBeanServer.getAttribute(objectName, "maxActive").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "rejectedSessions", mBeanServer.getAttribute(objectName, "rejectedSessions").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "expiredSessions", mBeanServer.getAttribute(objectName, "expiredSessions").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "sessionMaxAliveTime", mBeanServer.getAttribute(objectName, "sessionMaxAliveTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "sessionAverageAliveTime", mBeanServer.getAttribute(objectName, "sessionAverageAliveTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(objectName, "processingTime").toString());
            writer.append('}');
        }
    }

    public static void writeJspMonitor(PrintWriter writer, Set<ObjectName> jspMonitorONs, MBeanServer mBeanServer, int mode) throws Exception {
        int jspCount = 0;
        int jspReloadCount = 0;
        for (ObjectName jspMonitorON : jspMonitorONs) {
            Object obj = mBeanServer.getAttribute(jspMonitorON, "jspCount");
            jspCount += ((Integer)obj).intValue();
            obj = mBeanServer.getAttribute(jspMonitorON, "jspReloadCount");
            jspReloadCount += ((Integer)obj).intValue();
        }
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" JSPs loaded: ");
            writer.print(jspCount);
            writer.print(" JSPs reloaded: ");
            writer.print(jspReloadCount);
        } else if (mode != 1 && mode == 2) {
            writer.append(',').println();
            writer.append('\"').append("jsp").append('\"').append(':').append('{');
            StatusTransformer.appendJSonValue(writer, "jspCount", Integer.toString(jspCount)).append(',');
            StatusTransformer.appendJSonValue(writer, "jspReloadCount", Integer.toString(jspReloadCount));
            writer.append('}');
        }
    }

    public static void writeWrapper(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        String servletName = objectName.getKeyProperty("name");
        String[] mappings = (String[])mBeanServer.invoke(objectName, "findMappings", null, null);
        if (mode == 0) {
            writer.print("<h2>");
            writer.print(Escape.htmlElementContent((String)servletName));
            if (mappings != null && mappings.length > 0) {
                writer.print(" [ ");
                for (int i = 0; i < mappings.length; ++i) {
                    writer.print(Escape.htmlElementContent((String)mappings[i]));
                    if (i >= mappings.length - 1) continue;
                    writer.print(" , ");
                }
                writer.print(" ] ");
            }
            writer.print("</h2>");
            writer.print("<p>");
            writer.print(" Processing time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "processingTime"), true));
            writer.print(" Max time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "maxTime"), false));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(objectName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(objectName, "errorCount"));
            writer.print(" Load time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "loadTime"), false));
            writer.print(" Classloading time: ");
            writer.print(StatusTransformer.formatTime(mBeanServer.getAttribute(objectName, "classLoadTime"), false));
            writer.print("</p>");
        } else if (mode != 1 && mode == 2) {
            writer.append('{');
            StatusTransformer.appendJSonValue(writer, "servletName", JSONFilter.escape((String)servletName)).append(',');
            StatusTransformer.appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(objectName, "processingTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "maxTime", mBeanServer.getAttribute(objectName, "maxTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "requestCount", mBeanServer.getAttribute(objectName, "requestCount").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "errorCount", mBeanServer.getAttribute(objectName, "errorCount").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "loadTime", mBeanServer.getAttribute(objectName, "loadTime").toString()).append(',');
            StatusTransformer.appendJSonValue(writer, "classLoadTime", mBeanServer.getAttribute(objectName, "classLoadTime").toString());
            writer.append('}');
        }
    }

    public static String formatSize(Object obj, boolean mb) {
        long bytes = -1L;
        if (obj instanceof Long) {
            bytes = (Long)obj;
        } else if (obj instanceof Integer) {
            bytes = ((Integer)obj).intValue();
        }
        if (mb) {
            StringBuilder buff = new StringBuilder();
            if (bytes < 0L) {
                buff.append('-');
                bytes = -bytes;
            }
            long mbytes = bytes / 0x100000L;
            long rest = (bytes - mbytes * 0x100000L) * 100L / 0x100000L;
            buff.append(mbytes).append('.');
            if (rest < 10L) {
                buff.append('0');
            }
            buff.append(rest).append(" MiB");
            return buff.toString();
        }
        return bytes / 1024L + " KiB";
    }

    public static String formatTime(Object obj, boolean seconds) {
        long time = -1L;
        if (obj instanceof Long) {
            time = (Long)obj;
        } else if (obj instanceof Integer) {
            time = ((Integer)obj).intValue();
        }
        if (seconds) {
            return (float)time / 1000.0f + " s";
        }
        return time + " ms";
    }

    public static String formatSeconds(Object obj) {
        long time = -1L;
        if (obj instanceof Long) {
            time = (Long)obj;
        } else if (obj instanceof Integer) {
            time = ((Integer)obj).intValue();
        }
        return time + " s";
    }
}

