/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.manager.host;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.HostConfig;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.res.StringManager;

public class HostManagerServlet
extends HttpServlet
implements ContainerServlet {
    private static final long serialVersionUID = 1L;
    protected transient Context context = null;
    protected int debug = 1;
    protected transient Host installedHost = null;
    protected transient Engine engine = null;
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.manager.host");
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
            this.installedHost = null;
            this.engine = null;
        } else {
            this.context = (Context)wrapper.getParent();
            this.installedHost = (Host)this.context.getParent();
            this.engine = (Engine)this.installedHost.getParent();
        }
    }

    public void destroy() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.manager.host", (Enumeration)request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        String name = request.getParameter("name");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("hostManagerServlet.noCommand"));
        } else if (command.equals("/add")) {
            this.add(request, writer, name, false, smClient);
        } else if (command.equals("/remove")) {
            this.remove(writer, name, smClient);
        } else if (command.equals("/list")) {
            this.list(writer, smClient);
        } else if (command.equals("/start")) {
            this.start(writer, name, smClient);
        } else if (command.equals("/stop")) {
            this.stop(writer, name, smClient);
        } else if (command.equals("/persist")) {
            this.persist(writer, smClient);
        } else {
            writer.println(smClient.getString("hostManagerServlet.unknownCommand", new Object[]{command}));
        }
        writer.flush();
        writer.close();
    }

    protected void add(HttpServletRequest request, PrintWriter writer, String name, boolean htmlMode, StringManager smClient) {
        String aliases = request.getParameter("aliases");
        String appBase = request.getParameter("appBase");
        boolean manager = this.booleanParameter(request, "manager", false, htmlMode);
        boolean autoDeploy = this.booleanParameter(request, "autoDeploy", true, htmlMode);
        boolean deployOnStartup = this.booleanParameter(request, "deployOnStartup", true, htmlMode);
        boolean deployXML = this.booleanParameter(request, "deployXML", true, htmlMode);
        boolean unpackWARs = this.booleanParameter(request, "unpackWARs", true, htmlMode);
        boolean copyXML = this.booleanParameter(request, "copyXML", false, htmlMode);
        this.add(writer, name, aliases, appBase, manager, autoDeploy, deployOnStartup, deployXML, unpackWARs, copyXML, smClient);
    }

    protected boolean booleanParameter(HttpServletRequest request, String parameter, boolean theDefault, boolean htmlMode) {
        String value = request.getParameter(parameter);
        boolean booleanValue = theDefault;
        if (value != null) {
            if (htmlMode) {
                if (value.equals("on")) {
                    booleanValue = true;
                }
            } else if (theDefault) {
                if (value.equals("false")) {
                    booleanValue = false;
                }
            } else if (value.equals("true")) {
                booleanValue = true;
            }
        } else if (htmlMode) {
            booleanValue = false;
        }
        return booleanValue;
    }

    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(sm.getString("hostManagerServlet.noWrapper"));
        }
        String value = null;
        try {
            value = this.getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
    }

    protected synchronized void add(PrintWriter writer, String name, String aliases, String appBase, boolean manager, boolean autoDeploy, boolean deployOnStartup, boolean deployXML, boolean unpackWARs, boolean copyXML, StringManager smClient) {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.add", new Object[]{name}));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[]{name}));
            return;
        }
        if (this.engine.findChild(name) != null) {
            writer.println(smClient.getString("hostManagerServlet.alreadyHost", new Object[]{name}));
            return;
        }
        File appBaseFile = null;
        File file = null;
        String applicationBase = appBase;
        if (applicationBase == null || applicationBase.length() == 0) {
            applicationBase = name;
        }
        if (!(file = new File(applicationBase)).isAbsolute()) {
            file = new File(this.engine.getCatalinaBase(), file.getPath());
        }
        try {
            appBaseFile = file.getCanonicalFile();
        }
        catch (IOException e) {
            appBaseFile = file;
        }
        if (!appBaseFile.mkdirs() && !appBaseFile.isDirectory()) {
            writer.println(smClient.getString("hostManagerServlet.appBaseCreateFail", new Object[]{appBaseFile.toString(), name}));
            return;
        }
        File configBaseFile = this.getConfigBase(name);
        if (manager) {
            if (configBaseFile == null) {
                writer.println(smClient.getString("hostManagerServlet.configBaseCreateFail", new Object[]{name}));
                return;
            }
            try (InputStream is = this.getServletContext().getResourceAsStream("/WEB-INF/manager.xml");){
                if (is == null) {
                    writer.println(smClient.getString("hostManagerServlet.managerXml"));
                    return;
                }
                Path dest = new File(configBaseFile, "manager.xml").toPath();
                Files.copy(is, dest, new CopyOption[0]);
            }
            catch (IOException e) {
                writer.println(smClient.getString("hostManagerServlet.managerXml"));
                return;
            }
        }
        StandardHost host = new StandardHost();
        host.setAppBase(applicationBase);
        host.setName(name);
        host.addLifecycleListener(new HostConfig());
        if (aliases != null && !aliases.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(aliases, ", ");
            while (tok.hasMoreTokens()) {
                host.addAlias(tok.nextToken());
            }
        }
        host.setAutoDeploy(autoDeploy);
        host.setDeployOnStartup(deployOnStartup);
        host.setDeployXML(deployXML);
        host.setUnpackWARs(unpackWARs);
        host.setCopyXML(copyXML);
        try {
            this.engine.addChild(host);
        }
        catch (Exception e) {
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[]{e.toString()}));
            return;
        }
        host = (StandardHost)this.engine.findChild(name);
        if (host != null) {
            writer.println(smClient.getString("hostManagerServlet.addSuccess", new Object[]{name}));
        } else {
            writer.println(smClient.getString("hostManagerServlet.addFailed", new Object[]{name}));
        }
    }

    protected synchronized void remove(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.remove", new Object[]{name}));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[]{name}));
            return;
        }
        if (this.engine.findChild(name) == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[]{name}));
            return;
        }
        if (this.engine.findChild(name) == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotRemoveOwnHost", new Object[]{name}));
            return;
        }
        try {
            Container child = this.engine.findChild(name);
            this.engine.removeChild(child);
            if (child instanceof ContainerBase) {
                child.destroy();
            }
        }
        catch (Exception e) {
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[]{e.toString()}));
            return;
        }
        StandardHost host = (StandardHost)this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.removeSuccess", new Object[]{name}));
        } else {
            writer.println(smClient.getString("hostManagerServlet.removeFailed", new Object[]{name}));
        }
    }

    protected void list(PrintWriter writer, StringManager smClient) {
        Container[] hosts;
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.list", new Object[]{this.engine.getName()}));
        }
        writer.println(smClient.getString("hostManagerServlet.listed", new Object[]{this.engine.getName()}));
        for (Container container : hosts = this.engine.findChildren()) {
            Host host = (Host)container;
            String name = host.getName();
            String[] aliases = host.findAliases();
            writer.println(String.format("[%s]:[%s]", name, StringUtils.join((String[])aliases)));
        }
    }

    protected void start(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.start", new Object[]{name}));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[]{name}));
            return;
        }
        Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[]{name}));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStartOwnHost", new Object[]{name}));
            return;
        }
        if (host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStarted", new Object[]{name}));
            return;
        }
        try {
            host.start();
            writer.println(smClient.getString("hostManagerServlet.started", new Object[]{name}));
        }
        catch (Exception e) {
            this.getServletContext().log(sm.getString("hostManagerServlet.startFailed", new Object[]{name}), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.startFailed", new Object[]{name}));
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[]{e.toString()}));
        }
    }

    protected void stop(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.stop", new Object[]{name}));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[]{name}));
            return;
        }
        Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[]{name}));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStopOwnHost", new Object[]{name}));
            return;
        }
        if (!host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStopped", new Object[]{name}));
            return;
        }
        try {
            host.stop();
            writer.println(smClient.getString("hostManagerServlet.stopped", new Object[]{name}));
        }
        catch (Exception e) {
            this.getServletContext().log(sm.getString("hostManagerServlet.stopFailed", new Object[]{name}), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.stopFailed", new Object[]{name}));
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[]{e.toString()}));
        }
    }

    protected void persist(PrintWriter writer, StringManager smClient) {
        if (this.debug >= 1) {
            this.log(sm.getString("hostManagerServlet.persist"));
        }
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName oname = new ObjectName(this.engine.getDomain() + ":type=StoreConfig");
            platformMBeanServer.invoke(oname, "storeConfig", null, null);
            writer.println(smClient.getString("hostManagerServlet.persisted"));
        }
        catch (Exception e) {
            this.getServletContext().log(sm.getString("hostManagerServlet.persistFailed"), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.persistFailed"));
            if (e instanceof InstanceNotFoundException) {
                writer.println(smClient.getString("hostManagerServlet.noStoreConfig"));
            }
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[]{e.toString()}));
        }
    }

    protected File getConfigBase(String hostName) {
        File configBase = new File(this.context.getCatalinaBase(), "conf");
        if (!configBase.exists()) {
            return null;
        }
        if (this.engine != null) {
            configBase = new File(configBase, this.engine.getName());
        }
        if (this.installedHost != null) {
            configBase = new File(configBase, hostName);
        }
        if (!configBase.mkdirs() && !configBase.isDirectory()) {
            return null;
        }
        return configBase;
    }
}

