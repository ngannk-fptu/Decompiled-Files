/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrutsXmlConfigurationProvider
extends XmlConfigurationProvider {
    private static final Logger LOG = LogManager.getLogger(StrutsXmlConfigurationProvider.class);
    private static final Map<String, String> STRUTS_DTD_MAPPINGS = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put("-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "struts-2.0.dtd");
            this.put("-//Apache Software Foundation//DTD Struts Configuration 2.1//EN", "struts-2.1.dtd");
            this.put("-//Apache Software Foundation//DTD Struts Configuration 2.1.7//EN", "struts-2.1.7.dtd");
            this.put("-//Apache Software Foundation//DTD Struts Configuration 2.3//EN", "struts-2.3.dtd");
            this.put("-//Apache Software Foundation//DTD Struts Configuration 2.5//EN", "struts-2.5.dtd");
            this.put("-//Apache Software Foundation//DTD Struts Configuration 6.0//EN", "struts-6.0.dtd");
        }
    });
    private File baseDir = null;
    private final String filename;
    private final String reloadKey;
    private final ServletContext servletContext;

    public StrutsXmlConfigurationProvider() {
        this("struts.xml", null);
    }

    @Deprecated
    public StrutsXmlConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", null);
    }

    public StrutsXmlConfigurationProvider(String filename) {
        this(filename, null);
    }

    public StrutsXmlConfigurationProvider(String filename, ServletContext ctx) {
        super(filename);
        this.servletContext = ctx;
        this.filename = filename;
        this.reloadKey = "configurationReload-" + filename;
        this.setDtdMappings(STRUTS_DTD_MAPPINGS);
        File file = new File(filename);
        if (file.getParent() != null) {
            this.baseDir = file.getParentFile();
        }
    }

    @Deprecated
    public StrutsXmlConfigurationProvider(String filename, @Deprecated boolean errorIfMissing, ServletContext ctx) {
        this(filename, ctx);
    }

    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        if (this.servletContext != null && !containerBuilder.contains(ServletContext.class)) {
            containerBuilder.factory(ServletContext.class, new Factory<ServletContext>(){

                @Override
                public ServletContext create(Context context) throws Exception {
                    return StrutsXmlConfigurationProvider.this.servletContext;
                }

                @Override
                public Class<? extends ServletContext> type() {
                    return StrutsXmlConfigurationProvider.this.servletContext.getClass();
                }
            });
        }
        super.register(containerBuilder, props);
    }

    @Override
    public void loadPackages() {
        ActionContext ctx = ActionContext.getContext();
        ctx.put(this.reloadKey, Boolean.TRUE);
        super.loadPackages();
    }

    @Override
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        URL url = null;
        if (this.baseDir != null && (url = this.findInFileSystem(fileName)) == null) {
            return super.getConfigurationUrls(fileName);
        }
        if (url != null) {
            ArrayList<URL> list = new ArrayList<URL>();
            list.add(url);
            return list.iterator();
        }
        return super.getConfigurationUrls(fileName);
    }

    protected URL findInFileSystem(String fileName) throws IOException {
        URL url = null;
        File file = new File(fileName);
        LOG.debug("Trying to load file: {}", (Object)file);
        if (!file.exists()) {
            file = new File(this.baseDir, fileName);
        }
        if (file.exists()) {
            try {
                url = file.toURI().toURL();
            }
            catch (MalformedURLException e) {
                throw new IOException("Unable to convert " + file + " to a URL");
            }
        }
        return url;
    }

    @Override
    public boolean needsReload() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.get(this.reloadKey) == null && super.needsReload();
        }
        return super.needsReload();
    }

    @Override
    public String toString() {
        return "Struts XML configuration provider (" + this.filename + ")";
    }
}

