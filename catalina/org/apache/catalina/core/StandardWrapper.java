/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.MultipartConfigElement
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.SingleThreadModel
 *  javax.servlet.UnavailableException
 *  javax.servlet.annotation.MultipartConfig
 *  javax.servlet.http.HttpServlet
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.PeriodicEventListener
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.log.SystemLogHandler
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.modeler.Util
 */
package org.apache.catalina.core;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapperFacade;
import org.apache.catalina.core.StandardWrapperValve;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.PeriodicEventListener;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;

public class StandardWrapper
extends ContainerBase
implements ServletConfig,
Wrapper,
NotificationEmitter {
    private final Log log = LogFactory.getLog(StandardWrapper.class);
    protected static final String[] DEFAULT_SERVLET_METHODS = new String[]{"GET", "HEAD", "POST"};
    protected long available = 0L;
    protected final NotificationBroadcasterSupport broadcaster;
    protected final AtomicInteger countAllocated = new AtomicInteger(0);
    protected final StandardWrapperFacade facade = new StandardWrapperFacade(this);
    protected volatile Servlet instance = null;
    protected volatile boolean instanceInitialized = false;
    protected int loadOnStartup = -1;
    protected final ArrayList<String> mappings = new ArrayList();
    protected HashMap<String, String> parameters = new HashMap();
    protected HashMap<String, String> references = new HashMap();
    protected String runAs = null;
    protected long sequenceNumber = 0L;
    protected String servletClass = null;
    @Deprecated
    protected volatile boolean singleThreadModel = false;
    protected volatile boolean unloading = false;
    @Deprecated
    protected int maxInstances = 20;
    @Deprecated
    protected int nInstances = 0;
    @Deprecated
    protected Stack<Servlet> instancePool = null;
    protected long unloadDelay = 2000L;
    protected boolean isJspServlet;
    protected ObjectName jspMonitorON;
    protected boolean swallowOutput = false;
    protected StandardWrapperValve swValve;
    protected long loadTime = 0L;
    protected int classLoadTime = 0;
    protected MultipartConfigElement multipartConfigElement = null;
    protected boolean asyncSupported = false;
    protected boolean enabled = true;
    private boolean overridable = false;
    protected static Class<?>[] classType = new Class[]{ServletConfig.class};
    private final ReentrantReadWriteLock parametersLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock mappingsLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock referencesLock = new ReentrantReadWriteLock();
    protected MBeanNotificationInfo[] notificationInfo;

    public StandardWrapper() {
        this.swValve = new StandardWrapperValve();
        this.pipeline.setBasic(this.swValve);
        this.broadcaster = new NotificationBroadcasterSupport();
    }

    @Override
    public boolean isOverridable() {
        return this.overridable;
    }

    @Override
    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    @Override
    public long getAvailable() {
        return this.available;
    }

    @Override
    public void setAvailable(long available) {
        long oldAvailable = this.available;
        this.available = available > System.currentTimeMillis() ? available : 0L;
        this.support.firePropertyChange("available", oldAvailable, this.available);
    }

    public int getCountAllocated() {
        return this.countAllocated.get();
    }

    @Override
    public int getLoadOnStartup() {
        if (this.isJspServlet && this.loadOnStartup == -1) {
            return Integer.MAX_VALUE;
        }
        return this.loadOnStartup;
    }

    @Override
    public void setLoadOnStartup(int value) {
        int oldLoadOnStartup = this.loadOnStartup;
        this.loadOnStartup = value;
        this.support.firePropertyChange("loadOnStartup", (Object)oldLoadOnStartup, (Object)this.loadOnStartup);
    }

    public void setLoadOnStartupString(String value) {
        try {
            this.setLoadOnStartup(Integer.parseInt(value));
        }
        catch (NumberFormatException e) {
            this.setLoadOnStartup(0);
        }
    }

    public String getLoadOnStartupString() {
        return Integer.toString(this.getLoadOnStartup());
    }

    @Deprecated
    public int getMaxInstances() {
        return this.maxInstances;
    }

    @Deprecated
    public void setMaxInstances(int maxInstances) {
        int oldMaxInstances = this.maxInstances;
        this.maxInstances = maxInstances;
        this.support.firePropertyChange("maxInstances", oldMaxInstances, this.maxInstances);
    }

    @Override
    public void setParent(Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("standardWrapper.notContext"));
        }
        if (container instanceof StandardContext) {
            this.swallowOutput = ((StandardContext)container).getSwallowOutput();
            this.unloadDelay = ((StandardContext)container).getUnloadDelay();
        }
        super.setParent(container);
    }

    @Override
    public String getRunAs() {
        return this.runAs;
    }

    @Override
    public void setRunAs(String runAs) {
        String oldRunAs = this.runAs;
        this.runAs = runAs;
        this.support.firePropertyChange("runAs", oldRunAs, this.runAs);
    }

    @Override
    public String getServletClass() {
        return this.servletClass;
    }

    @Override
    public void setServletClass(String servletClass) {
        String oldServletClass = this.servletClass;
        this.servletClass = servletClass;
        this.support.firePropertyChange("servletClass", oldServletClass, this.servletClass);
        if ("org.apache.jasper.servlet.JspServlet".equals(servletClass)) {
            this.isJspServlet = true;
        }
    }

    public void setServletName(String name) {
        this.setName(name);
    }

    @Deprecated
    public Boolean isSingleThreadModel() {
        if (this.singleThreadModel || this.instance != null) {
            return this.singleThreadModel;
        }
        return null;
    }

    @Override
    public boolean isUnavailable() {
        if (!this.isEnabled()) {
            return true;
        }
        if (this.available == 0L) {
            return false;
        }
        if (this.available <= System.currentTimeMillis()) {
            this.available = 0L;
            return false;
        }
        return true;
    }

    @Override
    public String[] getServletMethods() throws ServletException {
        this.instance = this.loadServlet();
        Class<?> servletClazz = this.instance.getClass();
        if (!HttpServlet.class.isAssignableFrom(servletClazz)) {
            return DEFAULT_SERVLET_METHODS;
        }
        HashSet<String> allow = new HashSet<String>();
        allow.add("OPTIONS");
        if (this.isJspServlet) {
            allow.add("GET");
            allow.add("HEAD");
            allow.add("POST");
        } else {
            allow.add("TRACE");
            Method[] methods = this.getAllDeclaredMethods(servletClazz);
            for (int i = 0; methods != null && i < methods.length; ++i) {
                Method m = methods[i];
                if (m.getName().equals("doGet")) {
                    allow.add("GET");
                    allow.add("HEAD");
                    continue;
                }
                if (m.getName().equals("doPost")) {
                    allow.add("POST");
                    continue;
                }
                if (m.getName().equals("doPut")) {
                    allow.add("PUT");
                    continue;
                }
                if (!m.getName().equals("doDelete")) continue;
                allow.add("DELETE");
            }
        }
        return allow.toArray(new String[0]);
    }

    @Override
    public Servlet getServlet() {
        return this.instance;
    }

    @Override
    public void setServlet(Servlet servlet) {
        this.instance = servlet;
    }

    @Override
    public void backgroundProcess() {
        super.backgroundProcess();
        if (!this.getState().isAvailable()) {
            return;
        }
        if (this.getServlet() instanceof PeriodicEventListener) {
            ((PeriodicEventListener)this.getServlet()).periodicEvent();
        }
    }

    public static Throwable getRootCause(ServletException e) {
        Throwable rootCause = e;
        Throwable rootCauseCheck = null;
        int loops = 0;
        do {
            ++loops;
            rootCauseCheck = rootCause.getCause();
            if (rootCauseCheck == null) continue;
            rootCause = rootCauseCheck;
        } while (rootCauseCheck != null && loops < 20);
        return rootCause;
    }

    @Override
    public void addChild(Container child) {
        throw new IllegalStateException(sm.getString("standardWrapper.notChild"));
    }

    @Override
    public void addInitParameter(String name, String value) {
        this.parametersLock.writeLock().lock();
        try {
            this.parameters.put(name, value);
        }
        finally {
            this.parametersLock.writeLock().unlock();
        }
        this.fireContainerEvent("addInitParameter", name);
    }

    @Override
    public void addMapping(String mapping) {
        this.mappingsLock.writeLock().lock();
        try {
            this.mappings.add(mapping);
        }
        finally {
            this.mappingsLock.writeLock().unlock();
        }
        if (this.parent.getState().equals((Object)LifecycleState.STARTED)) {
            this.fireContainerEvent("addMapping", mapping);
        }
    }

    @Override
    public void addSecurityReference(String name, String link) {
        this.referencesLock.writeLock().lock();
        try {
            this.references.put(name, link);
        }
        finally {
            this.referencesLock.writeLock().unlock();
        }
        this.fireContainerEvent("addSecurityReference", name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Servlet allocate() throws ServletException {
        Stack<Servlet> stack;
        if (this.unloading) {
            throw new ServletException(sm.getString("standardWrapper.unloading", new Object[]{this.getName()}));
        }
        boolean newInstance = false;
        if (!this.singleThreadModel) {
            if (this.instance == null || !this.instanceInitialized) {
                stack = this;
                synchronized (stack) {
                    if (this.instance == null) {
                        try {
                            if (this.log.isDebugEnabled()) {
                                this.log.debug((Object)"Allocating non-STM instance");
                            }
                            this.instance = this.loadServlet();
                            newInstance = true;
                            if (!this.singleThreadModel) {
                                this.countAllocated.incrementAndGet();
                            }
                        }
                        catch (ServletException e) {
                            throw e;
                        }
                        catch (Throwable e) {
                            ExceptionUtils.handleThrowable((Throwable)e);
                            throw new ServletException(sm.getString("standardWrapper.allocate"), e);
                        }
                    }
                    if (!this.instanceInitialized) {
                        this.initServlet(this.instance);
                    }
                }
            }
            if (this.singleThreadModel) {
                if (newInstance) {
                    stack = this.instancePool;
                    synchronized (stack) {
                        this.instancePool.push(this.instance);
                        ++this.nInstances;
                    }
                }
            } else {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)"  Returning non-STM instance");
                }
                if (!newInstance) {
                    this.countAllocated.incrementAndGet();
                }
                return this.instance;
            }
        }
        stack = this.instancePool;
        synchronized (stack) {
            while (this.countAllocated.get() >= this.nInstances) {
                if (this.nInstances < this.maxInstances) {
                    try {
                        this.instancePool.push(this.loadServlet());
                        ++this.nInstances;
                        continue;
                    }
                    catch (ServletException e) {
                        throw e;
                    }
                    catch (Throwable e) {
                        ExceptionUtils.handleThrowable((Throwable)e);
                        throw new ServletException(sm.getString("standardWrapper.allocate"), e);
                    }
                }
                try {
                    this.instancePool.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)"  Returning allocated STM instance");
            }
            this.countAllocated.incrementAndGet();
            return this.instancePool.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deallocate(Servlet servlet) throws ServletException {
        if (!this.singleThreadModel) {
            this.countAllocated.decrementAndGet();
            return;
        }
        Stack<Servlet> stack = this.instancePool;
        synchronized (stack) {
            this.countAllocated.decrementAndGet();
            this.instancePool.push(servlet);
            this.instancePool.notify();
        }
    }

    @Override
    public String findInitParameter(String name) {
        this.parametersLock.readLock().lock();
        try {
            String string = this.parameters.get(name);
            return string;
        }
        finally {
            this.parametersLock.readLock().unlock();
        }
    }

    @Override
    public String[] findInitParameters() {
        this.parametersLock.readLock().lock();
        try {
            String[] stringArray = this.parameters.keySet().toArray(new String[0]);
            return stringArray;
        }
        finally {
            this.parametersLock.readLock().unlock();
        }
    }

    @Override
    public String[] findMappings() {
        this.mappingsLock.readLock().lock();
        try {
            String[] stringArray = this.mappings.toArray(new String[0]);
            return stringArray;
        }
        finally {
            this.mappingsLock.readLock().unlock();
        }
    }

    @Override
    public String findSecurityReference(String name) {
        String reference = null;
        this.referencesLock.readLock().lock();
        try {
            reference = this.references.get(name);
        }
        finally {
            this.referencesLock.readLock().unlock();
        }
        if (this.getParent() instanceof Context) {
            Context context = (Context)this.getParent();
            reference = reference != null ? context.findRoleMapping(reference) : context.findRoleMapping(name);
        }
        return reference;
    }

    @Override
    public String[] findSecurityReferences() {
        this.referencesLock.readLock().lock();
        try {
            String[] stringArray = this.references.keySet().toArray(new String[0]);
            return stringArray;
        }
        finally {
            this.referencesLock.readLock().unlock();
        }
    }

    @Override
    public synchronized void load() throws ServletException {
        this.instance = this.loadServlet();
        if (!this.instanceInitialized) {
            this.initServlet(this.instance);
        }
        if (this.isJspServlet) {
            StringBuilder oname = new StringBuilder(this.getDomain());
            oname.append(":type=JspMonitor");
            oname.append(this.getWebModuleKeyProperties());
            oname.append(",name=");
            oname.append(this.getName());
            oname.append(this.getJ2EEKeyProperties());
            try {
                this.jspMonitorON = new ObjectName(oname.toString());
                Registry.getRegistry(null, null).registerComponent((Object)this.instance, this.jspMonitorON, null);
            }
            catch (Exception ex) {
                this.log.warn((Object)sm.getString("standardWrapper.jspMonitorError", new Object[]{this.instance}));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Servlet loadServlet() throws ServletException {
        Servlet servlet;
        if (!this.singleThreadModel && this.instance != null) {
            return this.instance;
        }
        PrintStream out = System.out;
        if (this.swallowOutput) {
            SystemLogHandler.startCapture();
        }
        try {
            MultipartConfig annotation;
            long t1 = System.currentTimeMillis();
            if (this.servletClass == null) {
                this.unavailable(null);
                throw new ServletException(sm.getString("standardWrapper.notClass", new Object[]{this.getName()}));
            }
            InstanceManager instanceManager = ((StandardContext)this.getParent()).getInstanceManager();
            try {
                servlet = (Servlet)instanceManager.newInstance(this.servletClass);
            }
            catch (ClassCastException e) {
                this.unavailable(null);
                throw new ServletException(sm.getString("standardWrapper.notServlet", new Object[]{this.servletClass}), (Throwable)e);
            }
            catch (Throwable e) {
                e = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
                ExceptionUtils.handleThrowable((Throwable)e);
                this.unavailable(null);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("standardWrapper.instantiate", new Object[]{this.servletClass}), e);
                }
                throw new ServletException(sm.getString("standardWrapper.instantiate", new Object[]{this.servletClass}), e);
            }
            if (this.multipartConfigElement == null && (annotation = servlet.getClass().getAnnotation(MultipartConfig.class)) != null) {
                this.multipartConfigElement = new MultipartConfigElement(annotation);
            }
            if (servlet instanceof ContainerServlet) {
                ((ContainerServlet)servlet).setWrapper(this);
            }
            this.classLoadTime = (int)(System.currentTimeMillis() - t1);
            if (servlet instanceof SingleThreadModel) {
                if (this.instancePool == null) {
                    this.instancePool = new Stack();
                }
                this.singleThreadModel = true;
            }
            this.initServlet(servlet);
            this.fireContainerEvent("load", this);
            this.loadTime = System.currentTimeMillis() - t1;
        }
        finally {
            String log;
            if (this.swallowOutput && (log = SystemLogHandler.stopCapture()) != null && log.length() > 0) {
                if (this.getServletContext() != null) {
                    this.getServletContext().log(log);
                } else {
                    out.println(log);
                }
            }
        }
        return servlet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void initServlet(Servlet servlet) throws ServletException {
        if (this.instanceInitialized && !this.singleThreadModel) {
            return;
        }
        try {
            if (Globals.IS_SECURITY_ENABLED) {
                boolean success = false;
                try {
                    Object[] args = new Object[]{this.facade};
                    SecurityUtil.doAsPrivilege("init", servlet, classType, args);
                    success = true;
                }
                finally {
                    if (!success) {
                        SecurityUtil.remove(servlet);
                    }
                }
            } else {
                servlet.init((ServletConfig)this.facade);
            }
            this.instanceInitialized = true;
        }
        catch (UnavailableException f) {
            this.unavailable(f);
            throw f;
        }
        catch (ServletException f) {
            throw f;
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
            this.getServletContext().log(sm.getString("standardWrapper.initException", new Object[]{this.getName()}), f);
            throw new ServletException(sm.getString("standardWrapper.initException", new Object[]{this.getName()}), f);
        }
    }

    @Override
    public void removeInitParameter(String name) {
        this.parametersLock.writeLock().lock();
        try {
            this.parameters.remove(name);
        }
        finally {
            this.parametersLock.writeLock().unlock();
        }
        this.fireContainerEvent("removeInitParameter", name);
    }

    @Override
    public void removeMapping(String mapping) {
        this.mappingsLock.writeLock().lock();
        try {
            this.mappings.remove(mapping);
        }
        finally {
            this.mappingsLock.writeLock().unlock();
        }
        if (this.parent.getState().equals((Object)LifecycleState.STARTED)) {
            this.fireContainerEvent("removeMapping", mapping);
        }
    }

    @Override
    public void removeSecurityReference(String name) {
        this.referencesLock.writeLock().lock();
        try {
            this.references.remove(name);
        }
        finally {
            this.referencesLock.writeLock().unlock();
        }
        this.fireContainerEvent("removeSecurityReference", name);
    }

    @Override
    public void unavailable(UnavailableException unavailable) {
        this.getServletContext().log(sm.getString("standardWrapper.unavailable", new Object[]{this.getName()}));
        if (unavailable == null) {
            this.setAvailable(Long.MAX_VALUE);
        } else if (unavailable.isPermanent()) {
            this.setAvailable(Long.MAX_VALUE);
        } else {
            int unavailableSeconds = unavailable.getUnavailableSeconds();
            if (unavailableSeconds <= 0) {
                unavailableSeconds = 60;
            }
            this.setAvailable(System.currentTimeMillis() + (long)unavailableSeconds * 1000L);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public synchronized void unload() throws ServletException {
        if (!this.singleThreadModel && this.instance == null) {
            return;
        }
        this.unloading = true;
        if (this.countAllocated.get() > 0) {
            long delay = this.unloadDelay / 20L;
            for (int nRetries = 0; nRetries < 21 && this.countAllocated.get() > 0; ++nRetries) {
                if (nRetries % 10 == 0) {
                    this.log.info((Object)sm.getString("standardWrapper.waiting", new Object[]{this.countAllocated.toString(), this.getName()}));
                }
                try {
                    Thread.sleep(delay);
                    continue;
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        if (this.instanceInitialized) {
            String log;
            PrintStream out;
            block38: {
                out = System.out;
                if (this.swallowOutput) {
                    SystemLogHandler.startCapture();
                }
                try {
                    if (Globals.IS_SECURITY_ENABLED) {
                        try {
                            SecurityUtil.doAsPrivilege("destroy", this.instance);
                        }
                        finally {
                            SecurityUtil.remove(this.instance);
                        }
                    } else {
                        this.instance.destroy();
                    }
                    if (((Context)this.getParent()).getIgnoreAnnotations()) break block38;
                }
                catch (Throwable t) {
                    try {
                        t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                        ExceptionUtils.handleThrowable((Throwable)t);
                        this.instancePool = null;
                        this.nInstances = 0;
                        this.fireContainerEvent("unload", this);
                        this.unloading = false;
                        throw new ServletException(sm.getString("standardWrapper.destroyException", new Object[]{this.getName()}), t);
                    }
                    catch (Throwable throwable) {
                        String log2;
                        if (!((Context)this.getParent()).getIgnoreAnnotations()) {
                            try {
                                ((Context)this.getParent()).getInstanceManager().destroyInstance((Object)this.instance);
                            }
                            catch (Throwable t2) {
                                ExceptionUtils.handleThrowable((Throwable)t2);
                                this.log.error((Object)sm.getString("standardWrapper.destroyInstance", new Object[]{this.getName()}), t2);
                            }
                        }
                        if (this.swallowOutput && (log2 = SystemLogHandler.stopCapture()) != null && log2.length() > 0) {
                            if (this.getServletContext() != null) {
                                this.getServletContext().log(log2);
                            } else {
                                out.println(log2);
                            }
                        }
                        this.instance = null;
                        this.instanceInitialized = false;
                        throw throwable;
                    }
                }
                try {
                    ((Context)this.getParent()).getInstanceManager().destroyInstance((Object)this.instance);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.log.error((Object)sm.getString("standardWrapper.destroyInstance", new Object[]{this.getName()}), t);
                }
            }
            if (this.swallowOutput && (log = SystemLogHandler.stopCapture()) != null && log.length() > 0) {
                if (this.getServletContext() != null) {
                    this.getServletContext().log(log);
                } else {
                    out.println(log);
                }
            }
            this.instance = null;
            this.instanceInitialized = false;
        }
        this.instance = null;
        if (this.isJspServlet && this.jspMonitorON != null) {
            Registry.getRegistry(null, null).unregisterComponent(this.jspMonitorON);
        }
        if (this.singleThreadModel && this.instancePool != null) {
            try {
                while (!this.instancePool.isEmpty()) {
                    Servlet s = this.instancePool.pop();
                    if (Globals.IS_SECURITY_ENABLED) {
                        try {
                            SecurityUtil.doAsPrivilege("destroy", s);
                        }
                        finally {
                            SecurityUtil.remove(s);
                        }
                    } else {
                        s.destroy();
                    }
                    if (((Context)this.getParent()).getIgnoreAnnotations()) continue;
                    ((StandardContext)this.getParent()).getInstanceManager().destroyInstance((Object)s);
                }
            }
            catch (Throwable t) {
                t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                ExceptionUtils.handleThrowable((Throwable)t);
                this.instancePool = null;
                this.nInstances = 0;
                this.unloading = false;
                this.fireContainerEvent("unload", this);
                throw new ServletException(sm.getString("standardWrapper.destroyException", new Object[]{this.getName()}), t);
            }
            this.instancePool = null;
            this.nInstances = 0;
        }
        this.singleThreadModel = false;
        this.unloading = false;
        this.fireContainerEvent("unload", this);
    }

    public String getInitParameter(String name) {
        return this.findInitParameter(name);
    }

    public Enumeration<String> getInitParameterNames() {
        this.parametersLock.readLock().lock();
        try {
            Enumeration<String> enumeration = Collections.enumeration(this.parameters.keySet());
            return enumeration;
        }
        finally {
            this.parametersLock.readLock().unlock();
        }
    }

    public ServletContext getServletContext() {
        if (this.parent == null) {
            return null;
        }
        if (!(this.parent instanceof Context)) {
            return null;
        }
        return ((Context)this.parent).getServletContext();
    }

    public String getServletName() {
        return this.getName();
    }

    public long getProcessingTime() {
        return this.swValve.getProcessingTime();
    }

    public long getMaxTime() {
        return this.swValve.getMaxTime();
    }

    public long getMinTime() {
        return this.swValve.getMinTime();
    }

    public int getRequestCount() {
        return this.swValve.getRequestCount();
    }

    public int getErrorCount() {
        return this.swValve.getErrorCount();
    }

    @Override
    public void incrementErrorCount() {
        this.swValve.incrementErrorCount();
    }

    public long getLoadTime() {
        return this.loadTime;
    }

    public int getClassLoadTime() {
        return this.classLoadTime;
    }

    @Override
    public MultipartConfigElement getMultipartConfigElement() {
        return this.multipartConfigElement;
    }

    @Override
    public void setMultipartConfigElement(MultipartConfigElement multipartConfigElement) {
        this.multipartConfigElement = multipartConfigElement;
    }

    @Override
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    @Override
    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected Method[] getAllDeclaredMethods(Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = this.getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (thisMethods.length == 0) {
            return parentMethods;
        }
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Notification notification;
        if (this.getObjectName() != null) {
            notification = new Notification("j2ee.state.starting", this.getObjectName(), this.sequenceNumber++);
            this.broadcaster.sendNotification(notification);
        }
        super.startInternal();
        this.setAvailable(0L);
        if (this.getObjectName() != null) {
            notification = new Notification("j2ee.state.running", this.getObjectName(), this.sequenceNumber++);
            this.broadcaster.sendNotification(notification);
        }
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        Notification notification;
        this.setAvailable(Long.MAX_VALUE);
        if (this.getObjectName() != null) {
            notification = new Notification("j2ee.state.stopping", this.getObjectName(), this.sequenceNumber++);
            this.broadcaster.sendNotification(notification);
        }
        try {
            this.unload();
        }
        catch (ServletException e) {
            this.getServletContext().log(sm.getString("standardWrapper.unloadException", new Object[]{this.getName()}), (Throwable)e);
        }
        super.stopInternal();
        if (this.getObjectName() != null) {
            notification = new Notification("j2ee.state.stopped", this.getObjectName(), this.sequenceNumber++);
            this.broadcaster.sendNotification(notification);
        }
        notification = new Notification("j2ee.object.deleted", this.getObjectName(), this.sequenceNumber++);
        this.broadcaster.sendNotification(notification);
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("j2eeType=Servlet");
        keyProperties.append(this.getWebModuleKeyProperties());
        keyProperties.append(",name=");
        String name = this.getName();
        if (Util.objectNameValueNeedsQuote((String)name)) {
            name = ObjectName.quote(name);
        }
        keyProperties.append(name);
        keyProperties.append(this.getJ2EEKeyProperties());
        return keyProperties.toString();
    }

    private String getWebModuleKeyProperties() {
        StringBuilder keyProperties = new StringBuilder(",WebModule=//");
        String hostName = this.getParent().getParent().getName();
        if (hostName == null) {
            keyProperties.append("DEFAULT");
        } else {
            keyProperties.append(hostName);
        }
        String contextName = this.getParent().getName();
        if (!contextName.startsWith("/")) {
            keyProperties.append('/');
        }
        keyProperties.append(contextName);
        return keyProperties.toString();
    }

    private String getJ2EEKeyProperties() {
        StringBuilder keyProperties = new StringBuilder(",J2EEApplication=");
        StandardContext ctx = null;
        if (this.parent instanceof StandardContext) {
            ctx = (StandardContext)this.getParent();
        }
        if (ctx == null) {
            keyProperties.append("none");
        } else {
            keyProperties.append(ctx.getJ2EEApplication());
        }
        keyProperties.append(",J2EEServer=");
        if (ctx == null) {
            keyProperties.append("none");
        } else {
            keyProperties.append(ctx.getJ2EEServer());
        }
        return keyProperties.toString();
    }

    @Override
    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener, filter, object);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notificationInfo == null) {
            this.notificationInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(new String[]{"j2ee.object.created"}, Notification.class.getName(), "servlet is created"), new MBeanNotificationInfo(new String[]{"j2ee.state.starting"}, Notification.class.getName(), "servlet is starting"), new MBeanNotificationInfo(new String[]{"j2ee.state.running"}, Notification.class.getName(), "servlet is running"), new MBeanNotificationInfo(new String[]{"j2ee.state.stopped"}, Notification.class.getName(), "servlet start to stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.stopped"}, Notification.class.getName(), "servlet is stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.deleted"}, Notification.class.getName(), "servlet is deleted")};
        }
        return this.notificationInfo;
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws IllegalArgumentException {
        this.broadcaster.addNotificationListener(listener, filter, object);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener);
    }
}

