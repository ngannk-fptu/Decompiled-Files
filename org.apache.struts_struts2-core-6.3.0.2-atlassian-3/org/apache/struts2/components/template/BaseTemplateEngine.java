/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components.template;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;

public abstract class BaseTemplateEngine
implements TemplateEngine {
    private static final Logger LOG = LogManager.getLogger(BaseTemplateEngine.class);
    public static final String DEFAULT_THEME_PROPERTIES_FILE_NAME = "theme.properties";
    private final Map<String, Properties> themeProps = new ConcurrentHashMap<String, Properties>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map getThemeProps(Template template) {
        Properties props = this.themeProps.get(template.getTheme());
        if (props == null) {
            Map<String, Properties> map = this.themeProps;
            synchronized (map) {
                props = this.readNewProperties(template);
                this.themeProps.put(template.getTheme(), props);
            }
        }
        return props;
    }

    private Properties readNewProperties(Template template) {
        String propName = this.buildPropertyFilename(template);
        return this.loadProperties(propName);
    }

    private Properties loadProperties(String propName) {
        InputStream is = this.readProperty(propName);
        Properties props = new Properties();
        if (is != null) {
            this.tryToLoadPropertiesFromStream(props, propName, is);
        }
        return props;
    }

    private InputStream readProperty(String propName) {
        InputStream is = this.tryReadingPropertyFileFromFileSystem(propName);
        if (is == null) {
            is = this.readPropertyFromClasspath(propName);
        }
        if (is == null) {
            is = this.readPropertyUsingServletContext(propName);
        }
        return is;
    }

    private InputStream readPropertyUsingServletContext(String propName) {
        String path;
        ServletContext servletContext = ServletActionContext.getServletContext();
        String string = path = propName.startsWith("/") ? propName : "/" + propName;
        if (servletContext != null) {
            return servletContext.getResourceAsStream(path);
        }
        LOG.warn("ServletContext is null, cannot obtain {}", (Object)path);
        return null;
    }

    private InputStream readPropertyFromClasspath(String propName) {
        return ClassLoaderUtil.getResourceAsStream(propName, this.getClass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryToLoadPropertiesFromStream(Properties props, String propName, InputStream is) {
        try {
            props.load(is);
        }
        catch (IOException e) {
            LOG.error("Could not load property with name: {}", (Object)propName, (Object)e);
        }
        finally {
            this.tryCloseStream(is);
        }
    }

    private void tryCloseStream(InputStream is) {
        try {
            is.close();
        }
        catch (IOException io) {
            LOG.warn("Unable to close input stream", (Throwable)io);
        }
    }

    private String buildPropertyFilename(Template template) {
        return template.getDir() + "/" + template.getTheme() + "/" + this.getThemePropertiesFileName();
    }

    private InputStream tryReadingPropertyFileFromFileSystem(String propName) {
        File propFile = new File(propName);
        try {
            return this.createFileInputStream(propFile);
        }
        catch (FileNotFoundException e) {
            LOG.warn("Unable to find file in filesystem [{}]", (Object)propFile.getAbsolutePath());
            return null;
        }
    }

    private InputStream createFileInputStream(File propFile) throws FileNotFoundException {
        FileInputStream is = null;
        if (propFile.exists()) {
            is = new FileInputStream(propFile);
        }
        return is;
    }

    protected String getFinalTemplateName(Template template) {
        String t = template.toString();
        if (t.indexOf(46) <= 0) {
            return t + "." + this.getSuffix();
        }
        return t;
    }

    protected String getThemePropertiesFileName() {
        return DEFAULT_THEME_PROPERTIES_FILE_NAME;
    }

    protected abstract String getSuffix();
}

