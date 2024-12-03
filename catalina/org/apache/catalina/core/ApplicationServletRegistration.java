/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.MultipartConfigElement
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.ServletSecurityElement
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.ParameterMap;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.res.StringManager;

public class ApplicationServletRegistration
implements ServletRegistration.Dynamic {
    private static final StringManager sm = StringManager.getManager(ApplicationServletRegistration.class);
    private final Wrapper wrapper;
    private final Context context;
    private ServletSecurityElement constraint;

    public ApplicationServletRegistration(Wrapper wrapper, Context context) {
        this.wrapper = wrapper;
        this.context = context;
    }

    public String getClassName() {
        return this.wrapper.getServletClass();
    }

    public String getInitParameter(String name) {
        return this.wrapper.findInitParameter(name);
    }

    public Map<String, String> getInitParameters() {
        String[] parameterNames;
        ParameterMap<String, String> result = new ParameterMap<String, String>();
        for (String parameterName : parameterNames = this.wrapper.findInitParameters()) {
            result.put(parameterName, this.wrapper.findInitParameter(parameterName));
        }
        result.setLocked(true);
        return result;
    }

    public String getName() {
        return this.wrapper.getName();
    }

    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParam", new Object[]{name, value}));
        }
        if (this.getInitParameter(name) != null) {
            return false;
        }
        this.wrapper.addInitParameter(name, value);
        return true;
    }

    public Set<String> setInitParameters(Map<String, String> initParameters) {
        HashSet<String> conflicts = new HashSet<String>();
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParams", new Object[]{entry.getKey(), entry.getValue()}));
            }
            if (this.getInitParameter(entry.getKey()) == null) continue;
            conflicts.add(entry.getKey());
        }
        if (conflicts.isEmpty()) {
            for (Map.Entry<String, String> entry : initParameters.entrySet()) {
                this.setInitParameter(entry.getKey(), entry.getValue());
            }
        }
        return conflicts;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.wrapper.setAsyncSupported(asyncSupported);
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.wrapper.setLoadOnStartup(loadOnStartup);
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.wrapper.setMultipartConfigElement(multipartConfig);
    }

    public void setRunAsRole(String roleName) {
        this.wrapper.setRunAs(roleName);
    }

    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException(sm.getString("applicationServletRegistration.setServletSecurity.iae", new Object[]{this.getName(), this.context.getName()}));
        }
        if (!this.context.getState().equals((Object)LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationServletRegistration.setServletSecurity.ise", new Object[]{this.getName(), this.context.getName()}));
        }
        this.constraint = constraint;
        return this.context.addServletSecurity(this, constraint);
    }

    public Set<String> addMapping(String ... urlPatterns) {
        if (urlPatterns == null) {
            return Collections.emptySet();
        }
        HashSet<String> conflicts = new HashSet<String>();
        for (String urlPattern : urlPatterns) {
            String wrapperName = this.context.findServletMapping(urlPattern);
            if (wrapperName == null) continue;
            Wrapper wrapper = (Wrapper)this.context.findChild(wrapperName);
            if (wrapper.isOverridable()) {
                this.context.removeServletMapping(urlPattern);
                continue;
            }
            conflicts.add(urlPattern);
        }
        if (!conflicts.isEmpty()) {
            return conflicts;
        }
        for (String urlPattern : urlPatterns) {
            this.context.addServletMappingDecoded(UDecoder.URLDecode((String)urlPattern, (Charset)StandardCharsets.UTF_8), this.wrapper.getName());
        }
        if (this.constraint != null) {
            this.context.addServletSecurity(this, this.constraint);
        }
        return Collections.emptySet();
    }

    public Collection<String> getMappings() {
        String[] urlPatterns;
        HashSet<String> result = new HashSet<String>();
        String servletName = this.wrapper.getName();
        for (String urlPattern : urlPatterns = this.context.findServletMappings()) {
            String name = this.context.findServletMapping(urlPattern);
            if (!name.equals(servletName)) continue;
            result.add(urlPattern);
        }
        return result;
    }

    public String getRunAsRole() {
        return this.wrapper.getRunAs();
    }
}

