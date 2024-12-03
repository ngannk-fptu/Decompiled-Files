/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.log.SystemLogHandler
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.modeler.Util
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.res.StringManager;

public final class ApplicationFilterConfig
implements FilterConfig,
Serializable {
    private static final long serialVersionUID = 1L;
    static final StringManager sm = StringManager.getManager(ApplicationFilterConfig.class);
    private transient Log log = LogFactory.getLog(ApplicationFilterConfig.class);
    private static final List<String> emptyString = Collections.emptyList();
    private final transient Context context;
    private transient Filter filter = null;
    private final FilterDef filterDef;
    private ObjectName oname;

    ApplicationFilterConfig(Context context, FilterDef filterDef) throws ClassCastException, ReflectiveOperationException, ServletException, NamingException, IllegalArgumentException, SecurityException {
        this.context = context;
        this.filterDef = filterDef;
        if (filterDef.getFilter() == null) {
            this.getFilter();
        } else {
            this.filter = filterDef.getFilter();
            context.getInstanceManager().newInstance((Object)this.filter);
            this.initFilter();
        }
    }

    public String getFilterName() {
        return this.filterDef.getFilterName();
    }

    public String getFilterClass() {
        return this.filterDef.getFilterClass();
    }

    public String getInitParameter(String name) {
        Map map = this.filterDef.getParameterMap();
        if (map == null) {
            return null;
        }
        return (String)map.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        Map map = this.filterDef.getParameterMap();
        if (map == null) {
            return Collections.enumeration(emptyString);
        }
        return Collections.enumeration(map.keySet());
    }

    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ApplicationFilterConfig[");
        sb.append("name=");
        sb.append(this.filterDef.getFilterName());
        sb.append(", filterClass=");
        sb.append(this.filterDef.getFilterClass());
        sb.append(']');
        return sb.toString();
    }

    public Map<String, String> getFilterInitParameterMap() {
        return Collections.unmodifiableMap(this.filterDef.getParameterMap());
    }

    Filter getFilter() throws ClassCastException, ReflectiveOperationException, ServletException, NamingException, IllegalArgumentException, SecurityException {
        if (this.filter != null) {
            return this.filter;
        }
        String filterClass = this.filterDef.getFilterClass();
        this.filter = (Filter)this.context.getInstanceManager().newInstance(filterClass);
        this.initFilter();
        return this.filter;
    }

    private void initFilter() throws ServletException {
        if (this.context instanceof StandardContext && this.context.getSwallowOutput()) {
            try {
                SystemLogHandler.startCapture();
                this.filter.init((FilterConfig)this);
            }
            finally {
                String capturedlog = SystemLogHandler.stopCapture();
                if (capturedlog != null && capturedlog.length() > 0) {
                    this.getServletContext().log(capturedlog);
                }
            }
        } else {
            this.filter.init((FilterConfig)this);
        }
        this.registerJMX();
    }

    FilterDef getFilterDef() {
        return this.filterDef;
    }

    void release() {
        this.unregisterJMX();
        if (this.filter != null) {
            block10: {
                try {
                    if (Globals.IS_SECURITY_ENABLED) {
                        try {
                            SecurityUtil.doAsPrivilege("destroy", this.filter);
                            break block10;
                        }
                        finally {
                            SecurityUtil.remove(this.filter);
                        }
                    }
                    this.filter.destroy();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.context.getLogger().error((Object)sm.getString("applicationFilterConfig.release", new Object[]{this.filterDef.getFilterName(), this.filterDef.getFilterClass()}), t);
                }
            }
            if (!this.context.getIgnoreAnnotations()) {
                try {
                    this.context.getInstanceManager().destroyInstance((Object)this.filter);
                }
                catch (Exception e) {
                    Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.context.getLogger().error((Object)sm.getString("applicationFilterConfig.preDestroy", new Object[]{this.filterDef.getFilterName(), this.filterDef.getFilterClass()}), t);
                }
            }
        }
        this.filter = null;
    }

    private void registerJMX() {
        String hostName;
        String parentName = this.context.getName();
        if (!parentName.startsWith("/")) {
            parentName = "/" + parentName;
        }
        hostName = (hostName = this.context.getParent().getName()) == null ? "DEFAULT" : hostName;
        String domain = this.context.getParent().getParent().getName();
        String webMod = "//" + hostName + parentName;
        String onameStr = null;
        String filterName = this.filterDef.getFilterName();
        if (Util.objectNameValueNeedsQuote((String)filterName)) {
            filterName = ObjectName.quote(filterName);
        }
        if (this.context instanceof StandardContext) {
            StandardContext standardContext = (StandardContext)this.context;
            onameStr = domain + ":j2eeType=Filter,WebModule=" + webMod + ",name=" + filterName + ",J2EEApplication=" + standardContext.getJ2EEApplication() + ",J2EEServer=" + standardContext.getJ2EEServer();
        } else {
            onameStr = domain + ":j2eeType=Filter,name=" + filterName + ",WebModule=" + webMod;
        }
        try {
            this.oname = new ObjectName(onameStr);
            Registry.getRegistry(null, null).registerComponent((Object)this, this.oname, null);
        }
        catch (Exception ex) {
            this.log.warn((Object)sm.getString("applicationFilterConfig.jmxRegisterFail", new Object[]{this.getFilterClass(), this.getFilterName()}), (Throwable)ex);
        }
    }

    private void unregisterJMX() {
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("applicationFilterConfig.jmxUnregister", new Object[]{this.getFilterClass(), this.getFilterName()}));
                }
            }
            catch (Exception ex) {
                this.log.warn((Object)sm.getString("applicationFilterConfig.jmxUnregisterFail", new Object[]{this.getFilterClass(), this.getFilterName()}), (Throwable)ex);
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(ApplicationFilterConfig.class);
    }
}

