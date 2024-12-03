/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.manager.StatusTransformer;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class StatusManagerServlet
extends HttpServlet
implements NotificationListener {
    private static final long serialVersionUID = 1L;
    protected MBeanServer mBeanServer = null;
    @Deprecated
    protected final Vector<ObjectName> protocolHandlers = new Vector();
    protected final Vector<ObjectName> threadPools = new Vector();
    protected final Vector<ObjectName> requestProcessors = new Vector();
    protected final Vector<ObjectName> globalRequestProcessors = new Vector();
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.manager");

    public void init() throws ServletException {
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "*:type=ProtocolHandler,*";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                this.protocolHandlers.addElement(oi.getObjectName());
            }
            onStr = "*:type=ThreadPool,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                this.threadPools.addElement(oi.getObjectName());
            }
            onStr = "*:type=GlobalRequestProcessor,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                this.globalRequestProcessors.addElement(oi.getObjectName());
            }
            onStr = "*:type=RequestProcessor,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                this.requestProcessors.addElement(oi.getObjectName());
            }
            onStr = "JMImplementation:type=MBeanServerDelegate";
            objectName = new ObjectName(onStr);
            this.mBeanServer.addNotificationListener(objectName, this, null, null);
        }
        catch (Exception e) {
            this.log(sm.getString("managerServlet.error.jmx"), e);
        }
    }

    public void destroy() {
        String onStr = "JMImplementation:type=MBeanServerDelegate";
        try {
            ObjectName objectName = new ObjectName(onStr);
            this.mBeanServer.removeNotificationListener(objectName, this, null, null);
        }
        catch (Exception e) {
            this.log(sm.getString("managerServlet.error.jmx"), e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager", (Enumeration)request.getLocales());
        int mode = 0;
        if (request.getParameter("XML") != null && request.getParameter("XML").equals("true")) {
            mode = 1;
        }
        if (request.getParameter("JSON") != null && request.getParameter("JSON").equals("true")) {
            mode = 2;
        }
        StatusTransformer.setContentType(response, mode);
        PrintWriter writer = response.getWriter();
        boolean completeStatus = false;
        if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
            completeStatus = true;
        }
        Object[] args = new Object[]{this.getServletContext().getContextPath()};
        StatusTransformer.writeHeader(writer, args, mode);
        args = new Object[]{this.getServletContext().getContextPath(), completeStatus ? smClient.getString("statusServlet.complete") : smClient.getString("statusServlet.title")};
        StatusTransformer.writeBody(writer, args, mode);
        args = new Object[9];
        args[0] = smClient.getString("htmlManagerServlet.manager");
        args[1] = response.encodeURL(this.getServletContext().getContextPath() + "/html/list");
        args[2] = smClient.getString("htmlManagerServlet.list");
        args[3] = this.getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpHtmlManagerFile");
        args[4] = smClient.getString("htmlManagerServlet.helpHtmlManager");
        args[5] = this.getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpManagerFile");
        args[6] = smClient.getString("htmlManagerServlet.helpManager");
        if (completeStatus) {
            args[7] = response.encodeURL(this.getServletContext().getContextPath() + "/status");
            args[8] = smClient.getString("statusServlet.title");
        } else {
            args[7] = response.encodeURL(this.getServletContext().getContextPath() + "/status/all");
            args[8] = smClient.getString("statusServlet.complete");
        }
        StatusTransformer.writeManager(writer, args, mode);
        args = new Object[]{smClient.getString("htmlManagerServlet.serverTitle"), smClient.getString("htmlManagerServlet.serverVersion"), smClient.getString("htmlManagerServlet.serverJVMVersion"), smClient.getString("htmlManagerServlet.serverJVMVendor"), smClient.getString("htmlManagerServlet.serverOSName"), smClient.getString("htmlManagerServlet.serverOSVersion"), smClient.getString("htmlManagerServlet.serverOSArch"), smClient.getString("htmlManagerServlet.serverHostname"), smClient.getString("htmlManagerServlet.serverIPAddress")};
        StatusTransformer.writePageHeading(writer, args, mode);
        args = new Object[8];
        args[0] = ServerInfo.getServerInfo();
        args[1] = System.getProperty("java.runtime.version");
        args[2] = System.getProperty("java.vm.vendor");
        args[3] = System.getProperty("os.name");
        args[4] = System.getProperty("os.version");
        args[5] = System.getProperty("os.arch");
        try {
            InetAddress address = InetAddress.getLocalHost();
            args[6] = address.getHostName();
            args[7] = address.getHostAddress();
        }
        catch (UnknownHostException e) {
            args[6] = "-";
            args[7] = "-";
        }
        StatusTransformer.writeServerInfo(writer, args, mode);
        try {
            args = new Object[]{smClient.getString("htmlManagerServlet.osPhysicalMemory"), smClient.getString("htmlManagerServlet.osAvailableMemory"), smClient.getString("htmlManagerServlet.osTotalPageFile"), smClient.getString("htmlManagerServlet.osFreePageFile"), smClient.getString("htmlManagerServlet.osMemoryLoad"), smClient.getString("htmlManagerServlet.osKernelTime"), smClient.getString("htmlManagerServlet.osUserTime")};
            StatusTransformer.writeOSState(writer, mode, args);
            args = new Object[]{smClient.getString("htmlManagerServlet.jvmFreeMemory"), smClient.getString("htmlManagerServlet.jvmTotalMemory"), smClient.getString("htmlManagerServlet.jvmMaxMemory"), smClient.getString("htmlManagerServlet.jvmTableTitleMemoryPool"), smClient.getString("htmlManagerServlet.jvmTableTitleType"), smClient.getString("htmlManagerServlet.jvmTableTitleInitial"), smClient.getString("htmlManagerServlet.jvmTableTitleTotal"), smClient.getString("htmlManagerServlet.jvmTableTitleMaximum"), smClient.getString("htmlManagerServlet.jvmTableTitleUsed")};
            StatusTransformer.writeVMState(writer, mode, args);
            args = new Object[]{smClient.getString("htmlManagerServlet.connectorStateMaxThreads"), smClient.getString("htmlManagerServlet.connectorStateThreadCount"), smClient.getString("htmlManagerServlet.connectorStateThreadBusy"), smClient.getString("htmlManagerServlet.connectorStateAliveSocketCount"), smClient.getString("htmlManagerServlet.connectorStateMaxProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateRequestCount"), smClient.getString("htmlManagerServlet.connectorStateErrorCount"), smClient.getString("htmlManagerServlet.connectorStateBytesReceived"), smClient.getString("htmlManagerServlet.connectorStateBytesSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleStage"), smClient.getString("htmlManagerServlet.connectorStateTableTitleTime"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBRecv"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientForw"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientAct"), smClient.getString("htmlManagerServlet.connectorStateTableTitleVHost"), smClient.getString("htmlManagerServlet.connectorStateTableTitleRequest"), smClient.getString("htmlManagerServlet.connectorStateHint")};
            StatusTransformer.writeConnectorsState(writer, this.mBeanServer, this.threadPools, this.globalRequestProcessors, this.requestProcessors, mode, args);
            if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
                StatusTransformer.writeDetailedState(writer, this.mBeanServer, mode);
            }
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
        StatusTransformer.writeFooter(writer, mode);
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (notification instanceof MBeanServerNotification) {
            String type;
            ObjectName objectName = ((MBeanServerNotification)notification).getMBeanName();
            if (notification.getType().equals("JMX.mbean.registered")) {
                String type2 = objectName.getKeyProperty("type");
                if (type2 != null) {
                    if (type2.equals("ProtocolHandler")) {
                        this.protocolHandlers.addElement(objectName);
                    } else if (type2.equals("ThreadPool")) {
                        this.threadPools.addElement(objectName);
                    } else if (type2.equals("GlobalRequestProcessor")) {
                        this.globalRequestProcessors.addElement(objectName);
                    } else if (type2.equals("RequestProcessor")) {
                        this.requestProcessors.addElement(objectName);
                    }
                }
            } else if (notification.getType().equals("JMX.mbean.unregistered") && (type = objectName.getKeyProperty("type")) != null) {
                if (type.equals("ProtocolHandler")) {
                    this.protocolHandlers.removeElement(objectName);
                } else if (type.equals("ThreadPool")) {
                    this.threadPools.removeElement(objectName);
                } else if (type.equals("GlobalRequestProcessor")) {
                    this.globalRequestProcessors.removeElement(objectName);
                } else if (type.equals("RequestProcessor")) {
                    this.requestProcessors.removeElement(objectName);
                }
            }
        }
    }
}

