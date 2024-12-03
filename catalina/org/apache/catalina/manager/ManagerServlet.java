/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.coyote.ProtocolHandler
 *  org.apache.coyote.http11.AbstractHttp11Protocol
 *  org.apache.tomcat.util.Diagnostics
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.net.SSLContext
 *  org.apache.tomcat.util.net.SSLHostConfig
 *  org.apache.tomcat.util.net.SSLHostConfigCertificate
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Session;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.Diagnostics;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class ManagerServlet
extends HttpServlet
implements ContainerServlet {
    private static final long serialVersionUID = 1L;
    protected File configBase = null;
    protected transient Context context = null;
    protected int debug = 1;
    protected File versioned = null;
    protected transient Host host = null;
    protected transient MBeanServer mBeanServer = null;
    protected ObjectName oname = null;
    protected transient javax.naming.Context global = null;
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.manager");
    protected transient Wrapper wrapper = null;

    @Override
    public Wrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
        if (wrapper == null) {
            this.context = null;
            this.host = null;
            this.oname = null;
        } else {
            this.context = (Context)wrapper.getParent();
            this.host = (Host)this.context.getParent();
            Engine engine = (Engine)this.host.getParent();
            String name = engine.getName() + ":type=Deployer,host=" + this.host.getName();
            try {
                this.oname = new ObjectName(name);
            }
            catch (Exception e) {
                this.log(sm.getString("managerServlet.objectNameFail", new Object[]{name}), e);
            }
        }
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
    }

    public void destroy() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        String path = request.getParameter("path");
        String war = request.getParameter("war");
        String config = request.getParameter("config");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        } else if (config != null) {
            cn = ContextName.extractFromPath(config);
        } else if (war != null) {
            cn = ContextName.extractFromPath(war);
        }
        String type = request.getParameter("type");
        String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        String tlsHostName = request.getParameter("tlsHostName");
        boolean statusLine = false;
        if ("true".equals(request.getParameter("statusLine"))) {
            statusLine = true;
        }
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        } else if (command.equals("/deploy")) {
            if (war != null || config != null) {
                this.deploy(writer, config, cn, war, update, smClient);
            } else if (tag != null) {
                this.deploy(writer, cn, tag, smClient);
            } else {
                writer.println(smClient.getString("managerServlet.invalidCommand", new Object[]{command}));
            }
        } else if (command.equals("/list")) {
            this.list(writer, smClient);
        } else if (command.equals("/reload")) {
            this.reload(writer, cn, smClient);
        } else if (command.equals("/resources")) {
            this.resources(writer, type, smClient);
        } else if (command.equals("/save")) {
            this.save(writer, path, smClient);
        } else if (command.equals("/serverinfo")) {
            this.serverinfo(writer, smClient);
        } else if (command.equals("/sessions")) {
            this.expireSessions(writer, cn, request, smClient);
        } else if (command.equals("/expire")) {
            this.expireSessions(writer, cn, request, smClient);
        } else if (command.equals("/start")) {
            this.start(writer, cn, smClient);
        } else if (command.equals("/stop")) {
            this.stop(writer, cn, smClient);
        } else if (command.equals("/undeploy")) {
            this.undeploy(writer, cn, smClient);
        } else if (command.equals("/findleaks")) {
            this.findleaks(statusLine, writer, smClient);
        } else if (command.equals("/vminfo")) {
            this.vmInfo(writer, smClient, request.getLocales());
        } else if (command.equals("/threaddump")) {
            this.threadDump(writer, smClient, request.getLocales());
        } else if (command.equals("/sslConnectorCiphers")) {
            this.sslConnectorCiphers(writer, smClient);
        } else if (command.equals("/sslConnectorCerts")) {
            this.sslConnectorCerts(writer, smClient);
        } else if (command.equals("/sslConnectorTrustedCerts")) {
            this.sslConnectorTrustedCerts(writer, smClient);
        } else if (command.equals("/sslReload")) {
            this.sslReload(writer, tlsHostName, smClient);
        } else {
            writer.println(smClient.getString("managerServlet.unknownCommand", new Object[]{command}));
        }
        writer.flush();
        writer.close();
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        String config = request.getParameter("config");
        String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        } else if (command.equals("/deploy")) {
            this.deploy(writer, config, cn, tag, update, request, smClient);
        } else {
            writer.println(smClient.getString("managerServlet.unknownCommand", new Object[]{command}));
        }
        writer.flush();
        writer.close();
    }

    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(sm.getString("managerServlet.noWrapper"));
        }
        String value = null;
        try {
            value = this.getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        Server server = ((Engine)this.host.getParent()).getService().getServer();
        if (server != null) {
            this.global = server.getGlobalNamingContext();
        }
        this.versioned = (File)this.getServletContext().getAttribute("javax.servlet.context.tempdir");
        this.configBase = new File(this.context.getCatalinaBase(), "conf");
        Context host = null;
        Context engine = null;
        for (Container container = this.context; container != null; container = container.getParent()) {
            if (container instanceof Host) {
                host = container;
            }
            if (!(container instanceof Engine)) continue;
            engine = container;
        }
        if (engine != null) {
            this.configBase = new File(this.configBase, engine.getName());
        }
        if (host != null) {
            this.configBase = new File(this.configBase, host.getName());
        }
        if (this.debug >= 1) {
            this.log("init: Associated with Deployer '" + this.oname + "'");
            if (this.global != null) {
                this.log("init: Global resources are available");
            }
        }
    }

    protected void findleaks(boolean statusLine, PrintWriter writer, StringManager smClient) {
        if (!(this.host instanceof StandardHost)) {
            writer.println(smClient.getString("managerServlet.findleaksFail"));
            return;
        }
        String[] results = ((StandardHost)this.host).findReloadedContextMemoryLeaks();
        if (results.length > 0) {
            if (statusLine) {
                writer.println(smClient.getString("managerServlet.findleaksList"));
            }
            for (String result : results) {
                if (result.isEmpty()) {
                    result = "/";
                }
                writer.println(result);
            }
        } else if (statusLine) {
            writer.println(smClient.getString("managerServlet.findleaksNone"));
        }
    }

    protected void sslReload(PrintWriter writer, String tlsHostName, StringManager smClient) {
        Connector[] connectors = this.getConnectors();
        boolean found = false;
        for (Connector connector : connectors) {
            SSLHostConfig[] sslHostConfigs;
            ProtocolHandler protocol;
            if (!Boolean.TRUE.equals(connector.getProperty("SSLEnabled")) || !((protocol = connector.getProtocolHandler()) instanceof AbstractHttp11Protocol)) continue;
            AbstractHttp11Protocol http11Protoocol = (AbstractHttp11Protocol)protocol;
            if (tlsHostName == null || tlsHostName.length() == 0) {
                found = true;
                http11Protoocol.reloadSslHostConfigs();
                continue;
            }
            for (SSLHostConfig sslHostConfig : sslHostConfigs = http11Protoocol.findSslHostConfigs()) {
                if (!sslHostConfig.getHostName().equalsIgnoreCase(tlsHostName)) continue;
                found = true;
                http11Protoocol.reloadSslHostConfig(tlsHostName);
            }
        }
        if (found) {
            if (tlsHostName == null || tlsHostName.length() == 0) {
                writer.println(smClient.getString("managerServlet.sslReloadAll"));
            } else {
                writer.println(smClient.getString("managerServlet.sslReload", new Object[]{tlsHostName}));
            }
        } else {
            writer.println(smClient.getString("managerServlet.sslReloadFail"));
        }
    }

    protected void vmInfo(PrintWriter writer, StringManager smClient, Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.vminfo"));
        writer.print(Diagnostics.getVMInfo(requestedLocales));
    }

    protected void threadDump(PrintWriter writer, StringManager smClient, Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.threaddump"));
        writer.print(Diagnostics.getThreadDump(requestedLocales));
    }

    protected void sslConnectorCiphers(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCiphers"));
        Map<String, List<String>> connectorCiphers = this.getConnectorCiphers(smClient);
        for (Map.Entry<String, List<String>> entry : connectorCiphers.entrySet()) {
            writer.println(entry.getKey());
            for (String cipher : entry.getValue()) {
                writer.print("  ");
                writer.println(cipher);
            }
        }
    }

    private void sslConnectorCerts(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCerts"));
        Map<String, List<String>> connectorCerts = this.getConnectorCerts(smClient);
        for (Map.Entry<String, List<String>> entry : connectorCerts.entrySet()) {
            writer.println(entry.getKey());
            for (String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }

    private void sslConnectorTrustedCerts(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorTrustedCerts"));
        Map<String, List<String>> connectorTrustedCerts = this.getConnectorTrustedCerts(smClient);
        for (Map.Entry<String, List<String>> entry : connectorTrustedCerts.entrySet()) {
            writer.println(entry.getKey());
            for (String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }

    protected synchronized void save(PrintWriter writer, String path, StringManager smClient) {
        ObjectName storeConfigOname;
        try {
            storeConfigOname = new ObjectName("Catalina:type=StoreConfig");
        }
        catch (MalformedObjectNameException e) {
            this.log(sm.getString("managerServlet.exception"), e);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{e.toString()}));
            return;
        }
        if (!this.mBeanServer.isRegistered(storeConfigOname)) {
            writer.println(smClient.getString("managerServlet.storeConfig.noMBean", new Object[]{storeConfigOname}));
            return;
        }
        if (path == null || path.length() == 0 || !path.startsWith("/")) {
            try {
                this.mBeanServer.invoke(storeConfigOname, "storeConfig", null, null);
                writer.println(smClient.getString("managerServlet.saved"));
            }
            catch (Exception e) {
                this.log(sm.getString("managerServlet.error.storeConfig"), e);
                writer.println(smClient.getString("managerServlet.exception", new Object[]{e.toString()}));
            }
        } else {
            Context context;
            String contextPath = path;
            if (path.equals("/")) {
                contextPath = "";
            }
            if ((context = (Context)this.host.findChild(contextPath)) == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{path}));
                return;
            }
            try {
                Boolean result = (Boolean)this.mBeanServer.invoke(storeConfigOname, "store", new Object[]{context}, new String[]{"org.apache.catalina.Context"});
                if (result.booleanValue()) {
                    writer.println(smClient.getString("managerServlet.savedContext", new Object[]{path}));
                } else {
                    writer.println(smClient.getString("managerServlet.savedContextFail", new Object[]{path}));
                }
            }
            catch (Exception e) {
                this.log(sm.getString("managerServlet.error.storeContextConfig", new Object[]{path}), e);
                writer.println(smClient.getString("managerServlet.exception", new Object[]{e.toString()}));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deploy(PrintWriter writer, String config, ContextName cn, String tag, boolean update, HttpServletRequest request, StringManager smClient) {
        String displayPath;
        String name;
        block31: {
            File uploadedWar;
            if (config != null && config.length() == 0) {
                config = null;
            }
            if (this.debug >= 1) {
                if (config == null) {
                    this.log("deploy: Deploying web application '" + cn + "'");
                } else {
                    this.log("deploy: Deploying web application '" + cn + "' with context configuration at '" + config + "'");
                }
            }
            if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
                return;
            }
            name = cn.getName();
            String baseName = cn.getBaseName();
            displayPath = cn.getDisplayName();
            Context context = (Context)this.host.findChild(name);
            if (context != null && !update) {
                writer.println(smClient.getString("managerServlet.alreadyContext", new Object[]{displayPath}));
                return;
            }
            if (config != null && config.startsWith("file:")) {
                config = config.substring("file:".length());
            }
            File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
            if (tag == null) {
                if (update) {
                    uploadedWar = new File(deployedWar.getAbsolutePath() + ".tmp");
                    if (uploadedWar.exists() && !uploadedWar.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{uploadedWar}));
                    }
                } else {
                    uploadedWar = deployedWar;
                }
            } else {
                File uploadPath = new File(this.versioned, tag);
                if (!uploadPath.mkdirs() && !uploadPath.isDirectory()) {
                    writer.println(smClient.getString("managerServlet.mkdirFail", new Object[]{uploadPath}));
                    return;
                }
                uploadedWar = new File(uploadPath, baseName + ".war");
            }
            if (this.debug >= 2) {
                this.log("Uploading WAR file to " + uploadedWar);
            }
            try {
                if (this.tryAddServiced(name)) {
                    try {
                        if (config != null) {
                            if (!this.configBase.mkdirs() && !this.configBase.isDirectory()) {
                                writer.println(smClient.getString("managerServlet.mkdirFail", new Object[]{this.configBase}));
                                return;
                            }
                            if (!ExpandWar.copy(new File(config), new File(this.configBase, baseName + ".xml"))) {
                                throw new Exception(sm.getString("managerServlet.copyError", new Object[]{config}));
                            }
                        }
                        this.uploadWar(writer, request, uploadedWar, smClient);
                        if (update && tag == null) {
                            if (deployedWar.exists() && !deployedWar.delete()) {
                                writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{deployedWar}));
                                return;
                            }
                            if (!uploadedWar.renameTo(deployedWar)) {
                                writer.println(smClient.getString("managerServlet.renameFail", new Object[]{uploadedWar, deployedWar}));
                                return;
                            }
                        }
                        if (tag != null) {
                            ExpandWar.copy(uploadedWar, deployedWar);
                        }
                    }
                    finally {
                        this.removeServiced(name);
                    }
                    this.check(name);
                    break block31;
                }
                writer.println(smClient.getString("managerServlet.inService", new Object[]{displayPath}));
            }
            catch (Exception e) {
                this.log(sm.getString("managerServlet.error.deploy", new Object[]{displayPath}), e);
                writer.println(smClient.getString("managerServlet.exception", new Object[]{e.toString()}));
                return;
            }
        }
        this.writeDeployResult(writer, smClient, name, displayPath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deploy(PrintWriter writer, ContextName cn, String tag, StringManager smClient) {
        String displayPath;
        String name;
        block10: {
            if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
                return;
            }
            String baseName = cn.getBaseName();
            name = cn.getName();
            displayPath = cn.getDisplayName();
            File localWar = new File(new File(this.versioned, tag), baseName + ".war");
            File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
            try {
                if (this.tryAddServiced(name)) {
                    try {
                        if (!deployedWar.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{deployedWar}));
                            return;
                        }
                        ExpandWar.copy(localWar, deployedWar);
                    }
                    finally {
                        this.removeServiced(name);
                    }
                    this.check(name);
                    break block10;
                }
                writer.println(smClient.getString("managerServlet.inService", new Object[]{displayPath}));
            }
            catch (Exception e) {
                this.log(sm.getString("managerServlet.error.deploy", new Object[]{displayPath}), e);
                writer.println(smClient.getString("managerServlet.exception", new Object[]{e.toString()}));
                return;
            }
        }
        this.writeDeployResult(writer, smClient, name, displayPath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deploy(PrintWriter writer, String config, ContextName cn, String war, boolean update, StringManager smClient) {
        if (config != null && config.length() == 0) {
            config = null;
        }
        if (war != null && war.length() == 0) {
            war = null;
        }
        if (this.debug >= 1) {
            if (config != null) {
                if (war != null) {
                    this.log("install: Installing context configuration at '" + config + "' from '" + war + "'");
                } else {
                    this.log("install: Installing context configuration at '" + config + "'");
                }
            } else if (cn != null) {
                this.log("install: Installing web application '" + cn + "' from '" + war + "'");
            } else {
                this.log("install: Installing web application from '" + war + "'");
            }
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        String name = cn.getName();
        String baseName = cn.getBaseName();
        String displayPath = cn.getDisplayName();
        Context context = (Context)this.host.findChild(name);
        if (context != null && !update) {
            writer.println(smClient.getString("managerServlet.alreadyContext", new Object[]{displayPath}));
            return;
        }
        if (config != null && config.startsWith("file:")) {
            config = config.substring("file:".length());
        }
        if (war != null && war.startsWith("file:")) {
            war = war.substring("file:".length());
        }
        try {
            if (this.tryAddServiced(name)) {
                try {
                    if (config != null) {
                        if (!this.configBase.mkdirs() && !this.configBase.isDirectory()) {
                            writer.println(smClient.getString("managerServlet.mkdirFail", new Object[]{this.configBase}));
                            return;
                        }
                        File localConfigFile = new File(this.configBase, baseName + ".xml");
                        File configFile = new File(config);
                        if (!configFile.getCanonicalPath().equals(localConfigFile.getCanonicalPath())) {
                            if (localConfigFile.isFile() && !localConfigFile.delete()) {
                                writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{localConfigFile}));
                                return;
                            }
                            ExpandWar.copy(configFile, localConfigFile);
                        }
                    }
                    if (war != null) {
                        File localWarFile = war.endsWith(".war") ? new File(this.host.getAppBaseFile(), baseName + ".war") : new File(this.host.getAppBaseFile(), baseName);
                        File warFile = new File(war);
                        if (!warFile.getCanonicalPath().equals(localWarFile.getCanonicalPath())) {
                            if (localWarFile.exists() && !ExpandWar.delete(localWarFile)) {
                                writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{localWarFile}));
                                return;
                            }
                            ExpandWar.copy(warFile, localWarFile);
                        }
                    }
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            } else {
                writer.println(smClient.getString("managerServlet.inService", new Object[]{displayPath}));
            }
            this.writeDeployResult(writer, smClient, name, displayPath);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.deploy", new Object[]{displayPath}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    private void writeDeployResult(PrintWriter writer, StringManager smClient, String name, String displayPath) {
        Context deployed = (Context)this.host.findChild(name);
        if (deployed != null && deployed.getConfigured() && deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployed", new Object[]{displayPath}));
        } else if (deployed != null && !deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployedButNotStarted", new Object[]{displayPath}));
        } else {
            writer.println(smClient.getString("managerServlet.deployFailed", new Object[]{displayPath}));
        }
    }

    protected void list(PrintWriter writer, StringManager smClient) {
        Container[] contexts;
        if (this.debug >= 1) {
            this.log("list: Listing contexts for virtual host '" + this.host.getName() + "'");
        }
        writer.println(smClient.getString("managerServlet.listed", new Object[]{this.host.getName()}));
        for (Container container : contexts = this.host.findChildren()) {
            Context context = (Context)container;
            if (context == null) continue;
            String displayPath = context.getPath();
            if (displayPath.equals("")) {
                displayPath = "/";
            }
            List<String> parts = null;
            parts = context.getState().isAvailable() ? Arrays.asList(displayPath, "running", "" + context.getManager().findSessions().length, context.getDocBase()) : Arrays.asList(displayPath, "stopped", "0", context.getDocBase());
            writer.println(StringUtils.join(parts, (char)':'));
        }
    }

    protected void reload(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("restart: Reloading web application '" + cn + "'");
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        try {
            Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)cn.getDisplayName())}));
                return;
            }
            if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
                return;
            }
            context.reload();
            writer.println(smClient.getString("managerServlet.reloaded", new Object[]{cn.getDisplayName()}));
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.reload", new Object[]{cn.getDisplayName()}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected void resources(PrintWriter writer, String type, StringManager smClient) {
        if (this.debug >= 1) {
            if (type != null) {
                this.log("resources:  Listing resources of type " + type);
            } else {
                this.log("resources:  Listing resources of all types");
            }
        }
        if (this.global == null) {
            writer.println(smClient.getString("managerServlet.noGlobal"));
            return;
        }
        if (type != null) {
            writer.println(smClient.getString("managerServlet.resourcesType", new Object[]{type}));
        } else {
            writer.println(smClient.getString("managerServlet.resourcesAll"));
        }
        this.printResources(writer, "", this.global, type, smClient);
    }

    @Deprecated
    protected void printResources(PrintWriter writer, String prefix, javax.naming.Context namingContext, String type, Class<?> clazz, StringManager smClient) {
        this.printResources(writer, prefix, namingContext, type, smClient);
    }

    protected void printResources(PrintWriter writer, String prefix, javax.naming.Context namingContext, String type, StringManager smClient) {
        try {
            NamingEnumeration<Binding> items = namingContext.listBindings("");
            while (items.hasMore()) {
                Binding item = items.next();
                Object obj = item.getObject();
                if (obj instanceof javax.naming.Context) {
                    this.printResources(writer, prefix + item.getName() + "/", (javax.naming.Context)obj, type, smClient);
                    continue;
                }
                if (type != null && (obj == null || !IntrospectionUtils.isInstance(obj.getClass(), (String)type))) continue;
                writer.print(prefix + item.getName());
                writer.print(':');
                writer.print(item.getClassName());
                writer.println();
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.resources", new Object[]{type}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected void serverinfo(PrintWriter writer, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("serverinfo");
        }
        try {
            writer.println(smClient.getString("managerServlet.serverInfo", new Object[]{ServerInfo.getServerInfo(), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor")}));
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.serverInfo"), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected void sessions(PrintWriter writer, ContextName cn, int idle, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("sessions: Session information for web application '" + cn + "'");
            if (idle >= 0) {
                this.log("sessions: Session expiration for " + idle + " minutes '" + cn + "'");
            }
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            Manager manager = context.getManager();
            if (manager == null) {
                writer.println(smClient.getString("managerServlet.noManager", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            int maxCount = 60;
            int histoInterval = 1;
            int maxInactiveInterval = context.getSessionTimeout();
            if (maxInactiveInterval > 0) {
                histoInterval = maxInactiveInterval / maxCount;
                if (histoInterval * maxCount < maxInactiveInterval) {
                    ++histoInterval;
                }
                if (0 == histoInterval) {
                    histoInterval = 1;
                }
                if (histoInterval * (maxCount = maxInactiveInterval / histoInterval) < maxInactiveInterval) {
                    ++maxCount;
                }
            }
            writer.println(smClient.getString("managerServlet.sessions", new Object[]{displayPath}));
            writer.println(smClient.getString("managerServlet.sessiondefaultmax", new Object[]{"" + maxInactiveInterval}));
            Session[] sessions = manager.findSessions();
            int[] timeout = new int[maxCount + 1];
            int notimeout = 0;
            int expired = 0;
            for (Session session : sessions) {
                int time = (int)(session.getIdleTimeInternal() / 1000L);
                if (idle >= 0 && time >= idle * 60) {
                    session.expire();
                    ++expired;
                }
                if ((time = time / 60 / histoInterval) < 0) {
                    ++notimeout;
                    continue;
                }
                if (time >= maxCount) {
                    int n = maxCount;
                    timeout[n] = timeout[n] + 1;
                    continue;
                }
                int n = time;
                timeout[n] = timeout[n] + 1;
            }
            if (timeout[0] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[]{"<" + histoInterval, "" + timeout[0]}));
            }
            for (int i = 1; i < maxCount; ++i) {
                if (timeout[i] <= 0) continue;
                writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[]{"" + i * histoInterval + " - <" + (i + 1) * histoInterval, "" + timeout[i]}));
            }
            if (timeout[maxCount] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[]{">=" + maxCount * histoInterval, "" + timeout[maxCount]}));
            }
            if (notimeout > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.unlimited", new Object[]{"" + notimeout}));
            }
            if (idle >= 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.expired", new Object[]{">" + idle, "" + expired}));
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.sessions", new Object[]{displayPath}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected void expireSessions(PrintWriter writer, ContextName cn, HttpServletRequest req, StringManager smClient) {
        int idle = -1;
        String idleParam = req.getParameter("idle");
        if (idleParam != null) {
            try {
                idle = Integer.parseInt(idleParam);
            }
            catch (NumberFormatException e) {
                this.log(sm.getString("managerServlet.error.idleParam", new Object[]{idleParam}));
            }
        }
        this.sessions(writer, cn, idle, smClient);
    }

    protected void start(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("start: Starting web application '" + cn + "'");
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            context.start();
            if (context.getState().isAvailable()) {
                writer.println(smClient.getString("managerServlet.started", new Object[]{displayPath}));
            } else {
                writer.println(smClient.getString("managerServlet.startFailed", new Object[]{displayPath}));
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.start", new Object[]{displayPath}), t);
            writer.println(smClient.getString("managerServlet.startFailed", new Object[]{displayPath}));
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected void stop(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("stop: Stopping web application '" + cn + "'");
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
                return;
            }
            context.stop();
            writer.println(smClient.getString("managerServlet.stopped", new Object[]{displayPath}));
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.stop", new Object[]{displayPath}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void undeploy(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            this.log("undeploy: Undeploying web application at '" + cn + "'");
        }
        if (!ManagerServlet.validateContextName(cn, writer, smClient)) {
            return;
        }
        String name = cn.getName();
        String baseName = cn.getBaseName();
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context)this.host.findChild(name);
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            if (!this.isDeployed(name)) {
                writer.println(smClient.getString("managerServlet.notDeployed", new Object[]{Escape.htmlElementContent((String)displayPath)}));
                return;
            }
            if (this.tryAddServiced(name)) {
                try {
                    context.stop();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                }
                try {
                    File war = new File(this.host.getAppBaseFile(), baseName + ".war");
                    File dir = new File(this.host.getAppBaseFile(), baseName);
                    File xml = new File(this.configBase, baseName + ".xml");
                    if (war.exists() && !war.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{war}));
                        return;
                    }
                    if (dir.exists() && !ExpandWar.delete(dir, false)) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{dir}));
                        return;
                    }
                    if (xml.exists() && !xml.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{xml}));
                        return;
                    }
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            } else {
                writer.println(smClient.getString("managerServlet.inService", new Object[]{displayPath}));
            }
            writer.println(smClient.getString("managerServlet.undeployed", new Object[]{displayPath}));
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log(sm.getString("managerServlet.error.undeploy", new Object[]{displayPath}), t);
            writer.println(smClient.getString("managerServlet.exception", new Object[]{t.toString()}));
        }
    }

    protected boolean isDeployed(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isDeployed", params, signature);
        return result;
    }

    protected void check(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "check", params, signature);
    }

    @Deprecated
    protected boolean isServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isServiced", params, signature);
        return result;
    }

    @Deprecated
    protected void addServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "addServiced", params, signature);
    }

    protected boolean tryAddServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "tryAddServiced", params, signature);
        return result;
    }

    protected void removeServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "removeServiced", params, signature);
    }

    protected void uploadWar(PrintWriter writer, HttpServletRequest request, File war, StringManager smClient) throws IOException {
        if (war.exists() && !war.delete()) {
            String msg = smClient.getString("managerServlet.deleteFail", new Object[]{war});
            throw new IOException(msg);
        }
        try (ServletInputStream istream = request.getInputStream();
             FileOutputStream ostream = new FileOutputStream(war);){
            IOTools.flow((InputStream)istream, ostream);
        }
        catch (IOException e) {
            if (war.exists() && !war.delete()) {
                writer.println(smClient.getString("managerServlet.deleteFail", new Object[]{war}));
            }
            throw e;
        }
    }

    protected static boolean validateContextName(ContextName cn, PrintWriter writer, StringManager smClient) {
        if (cn != null && (cn.getPath().startsWith("/") || cn.getPath().equals(""))) {
            return true;
        }
        String path = null;
        if (cn != null) {
            path = Escape.htmlElementContent((String)cn.getPath());
        }
        writer.println(smClient.getString("managerServlet.invalidPath", new Object[]{path}));
        return false;
    }

    protected Map<String, List<String>> getConnectorCiphers(StringManager smClient) {
        Connector[] connectors;
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        for (Connector connector : connectors = this.getConnectors()) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs;
                for (SSLHostConfig sslHostConfig : sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs()) {
                    String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    result.put(name, new ArrayList<String>(new LinkedHashSet<String>(Arrays.asList(sslHostConfig.getEnabledCiphers()))));
                }
                continue;
            }
            ArrayList<String> cipherList = new ArrayList<String>(1);
            cipherList.add(smClient.getString("managerServlet.notSslConnector"));
            result.put(connector.toString(), cipherList);
        }
        return result;
    }

    protected Map<String, List<String>> getConnectorCerts(StringManager smClient) {
        Connector[] connectors;
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        for (Connector connector : connectors = this.getConnectors()) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs;
                for (SSLHostConfig sslHostConfig : sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs()) {
                    if (sslHostConfig.getOpenSslContext() == 0L) {
                        Set sslHostConfigCerts = sslHostConfig.getCertificates();
                        for (SSLHostConfigCertificate sslHostConfigCert : sslHostConfigCerts) {
                            X509Certificate[] certs;
                            String name = connector.toString() + "-" + sslHostConfig.getHostName() + "-" + sslHostConfigCert.getType();
                            ArrayList<String> certList = new ArrayList<String>();
                            SSLContext sslContext = sslHostConfigCert.getSslContext();
                            String alias = sslHostConfigCert.getCertificateKeyAlias();
                            if (alias == null) {
                                alias = "tomcat";
                            }
                            if ((certs = sslContext.getCertificateChain(alias)) == null) {
                                certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                            } else {
                                for (X509Certificate cert : certs) {
                                    certList.add(cert.toString());
                                }
                            }
                            result.put(name, certList);
                        }
                        continue;
                    }
                    ArrayList<String> certList = new ArrayList<String>();
                    certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                    String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    result.put(name, certList);
                }
                continue;
            }
            ArrayList<String> certList = new ArrayList<String>(1);
            certList.add(smClient.getString("managerServlet.notSslConnector"));
            result.put(connector.toString(), certList);
        }
        return result;
    }

    protected Map<String, List<String>> getConnectorTrustedCerts(StringManager smClient) {
        Connector[] connectors;
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        for (Connector connector : connectors = this.getConnectors()) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs;
                for (SSLHostConfig sslHostConfig : sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs()) {
                    String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    ArrayList<String> certList = new ArrayList<String>();
                    if (sslHostConfig.getOpenSslContext() == 0L) {
                        SSLContext sslContext = ((SSLHostConfigCertificate)sslHostConfig.getCertificates().iterator().next()).getSslContext();
                        X509Certificate[] certs = sslContext.getAcceptedIssuers();
                        if (certs == null) {
                            certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                        } else if (certs.length == 0) {
                            certList.add(smClient.getString("managerServlet.trustedCertsNotConfigured"));
                        } else {
                            for (X509Certificate cert : certs) {
                                certList.add(cert.toString());
                            }
                        }
                    } else {
                        certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                    }
                    result.put(name, certList);
                }
                continue;
            }
            ArrayList<String> certList = new ArrayList<String>(1);
            certList.add(smClient.getString("managerServlet.notSslConnector"));
            result.put(connector.toString(), certList);
        }
        return result;
    }

    private Connector[] getConnectors() {
        Engine e = (Engine)this.host.getParent();
        Service s = e.getService();
        return s.findConnectors();
    }
}

