/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  org.apache.sling.api.SlingException
 *  org.apache.sling.api.SlingServletException
 *  org.apache.sling.api.scripting.SlingBindings
 *  org.apache.sling.commons.compiler.source.JavaEscapeHelper
 *  org.apache.sling.scripting.spi.bundle.BundledRenderUnit
 *  org.osgi.framework.Bundle
 */
package org.apache.sling.scripting.jsp;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.SlingServletException;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.commons.compiler.source.JavaEscapeHelper;
import org.apache.sling.scripting.jsp.JspServletConfig;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationProcessor;
import org.apache.sling.scripting.jsp.jasper.runtime.HttpJspBase;
import org.apache.sling.scripting.jsp.jasper.servlet.JspServletWrapper;
import org.apache.sling.scripting.spi.bundle.BundledRenderUnit;
import org.osgi.framework.Bundle;

public class PrecompiledJSPRunner {
    private final Options options;
    private final ConcurrentHashMap<HttpJspBase, JspHolder> holders = new ConcurrentHashMap();

    public PrecompiledJSPRunner(Options options) {
        this.options = options;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean callPrecompiledJSP(JspRuntimeContext runtimeContext, JspRuntimeContext.JspFactoryHandler jspFactoryHandler, JspServletConfig jspServletConfig, SlingBindings bindings) {
        boolean found = false;
        BundledRenderUnit bundledRenderUnit = (BundledRenderUnit)bindings.get((Object)BundledRenderUnit.VARIABLE);
        if (bundledRenderUnit != null && bundledRenderUnit.getUnit() instanceof HttpJspBase) {
            found = true;
            HttpJspBase jsp = (HttpJspBase)((Object)bundledRenderUnit.getUnit());
            JspHolder holder = this.holders.computeIfAbsent(jsp, key -> new JspHolder());
            if (holder.wrapper == null) {
                JspHolder jspHolder = holder;
                synchronized (jspHolder) {
                    if (holder.wrapper == null) {
                        try {
                            PrecompiledServletConfig servletConfig = new PrecompiledServletConfig(jspServletConfig, bundledRenderUnit);
                            AnnotationProcessor annotationProcessor = (AnnotationProcessor)jspServletConfig.getServletContext().getAttribute(AnnotationProcessor.class.getName());
                            if (annotationProcessor != null) {
                                annotationProcessor.processAnnotations((Object)jsp);
                                annotationProcessor.postConstruct((Object)jsp);
                            }
                            JspServletWrapper wrapper = new JspServletWrapper(servletConfig, this.options, bundledRenderUnit.getPath(), false, runtimeContext, (Servlet)jsp);
                            jsp.init(servletConfig);
                            holder.wrapper = wrapper;
                        }
                        catch (ServletException se) {
                            throw new SlingServletException(se);
                        }
                        catch (IllegalAccessException | InvocationTargetException | NamingException e) {
                            throw new SlingException("Unable to process annotations for servlet " + ((Object)((Object)jsp)).getClass().getName() + ".", (Throwable)e);
                        }
                        catch (NoClassDefFoundError noClassDefFoundError) {
                            // empty catch block
                        }
                    }
                }
            }
            holder.wrapper.service(bindings);
        }
        return found;
    }

    public void cleanup() {
        HashSet<JspHolder> holders = new HashSet<JspHolder>(this.holders.values());
        this.holders.clear();
        try {
            for (JspHolder h : holders) {
                if (h.wrapper == null) continue;
                h.wrapper.destroy(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static final class JspHolder {
        public volatile JspServletWrapper wrapper;
    }

    private static class PrecompiledServletConfig
    extends JspServletConfig {
        private final BundledRenderUnit bundledRenderUnit;
        private String servletName;

        PrecompiledServletConfig(JspServletConfig jspServletConfig, BundledRenderUnit bundledRenderUnit) {
            super(jspServletConfig.getServletContext(), new HashMap<String, String>(jspServletConfig.getProperties()));
            this.bundledRenderUnit = bundledRenderUnit;
        }

        @Override
        public String getServletName() {
            if (this.servletName == null && this.bundledRenderUnit.getUnit() != null) {
                Bundle bundle = this.bundledRenderUnit.getBundle();
                Object jsp = this.bundledRenderUnit.getUnit();
                String originalName = JavaEscapeHelper.unescapeAll((String)jsp.getClass().getPackage().getName()) + "/" + JavaEscapeHelper.unescapeAll((String)jsp.getClass().getSimpleName());
                this.servletName = bundle.getSymbolicName() + ": " + originalName;
            }
            return this.servletName;
        }
    }
}

