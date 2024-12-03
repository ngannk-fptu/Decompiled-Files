/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.velocity.tools.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.PropertiesFactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.JeeConfig;
import org.apache.velocity.tools.view.JeeContextConfig;
import org.apache.velocity.tools.view.JeeFilterConfig;
import org.apache.velocity.tools.view.JeeServletConfig;
import org.apache.velocity.tools.view.VelocityView;

public class ServletUtils {
    public static final String VELOCITY_VIEW_KEY = VelocityView.class.getName();
    public static final String SHARED_CONFIG_PARAM = "org.apache.velocity.tools.shared.config";
    public static final String ALT_VELOCITY_VIEW_KEY = "org.apache.velocity.tools.view.class";
    public static final String CONFIGURATION_KEY = "org.apache.velocity.tools";
    public static final ServletUtils INSTANCE = new ServletUtils();

    protected ServletUtils() {
    }

    public ServletUtils getInstance() {
        return INSTANCE;
    }

    public static String getPath(HttpServletRequest request) {
        String path = (String)request.getAttribute("javax.servlet.include.servlet_path");
        String info = (String)request.getAttribute("javax.servlet.include.path_info");
        if (path == null) {
            path = request.getServletPath();
            info = request.getPathInfo();
        }
        if (info != null) {
            path = path + info;
        }
        return path;
    }

    public static VelocityView getVelocityView(ServletConfig config) {
        return ServletUtils.getVelocityView(new JeeServletConfig(config));
    }

    public static VelocityView getVelocityView(FilterConfig config) {
        return ServletUtils.getVelocityView(new JeeFilterConfig(config));
    }

    public static VelocityView getVelocityView(JeeConfig config) {
        String shared = config.findInitParameter(SHARED_CONFIG_PARAM);
        if (shared != null && shared.equals("false")) {
            return ServletUtils.createView(config);
        }
        ServletContext application = config.getServletContext();
        VelocityView view = ServletUtils.getVelocityView(application, false);
        if (view == null) {
            view = ServletUtils.createView(config);
            application.setAttribute(VELOCITY_VIEW_KEY, (Object)view);
        }
        return view;
    }

    private static VelocityView createView(JeeConfig config) {
        String cls = config.findInitParameter(ALT_VELOCITY_VIEW_KEY);
        if (cls == null) {
            return new VelocityView(config);
        }
        try {
            return ServletUtils.createView(ClassUtils.getClass(cls), config);
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Could not find class " + cls, cnfe);
        }
    }

    private static VelocityView createView(Class klass, JeeConfig config) {
        if (!VelocityView.class.isAssignableFrom(klass)) {
            throw new IllegalArgumentException(klass + " must extend " + VelocityView.class);
        }
        try {
            Constructor ctor = klass.getConstructor(JeeConfig.class);
            return (VelocityView)ctor.newInstance(config);
        }
        catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException(klass + " must have a constructor that takes " + JeeConfig.class, nsme);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not instantiate " + klass + " with " + config, e);
        }
    }

    public static VelocityView getVelocityView(ServletContext application) {
        return ServletUtils.getVelocityView(new JeeContextConfig(application));
    }

    public static VelocityView getVelocityView(ServletContext application, boolean createIfMissing) {
        VelocityView view = (VelocityView)application.getAttribute(VELOCITY_VIEW_KEY);
        if (view == null && createIfMissing) {
            return ServletUtils.getVelocityView(application);
        }
        return view;
    }

    public static Object findTool(String key, ServletContext application) {
        return ServletUtils.findTool(key, VelocityView.DEFAULT_TOOLBOX_KEY, application);
    }

    public static Object findTool(String key, String toolboxKey, ServletContext application) {
        Toolbox toolbox = (Toolbox)application.getAttribute(toolboxKey);
        if (toolbox != null) {
            return toolbox.get(key);
        }
        return null;
    }

    public static Object findTool(String key, HttpServletRequest request) {
        return ServletUtils.findTool(key, request, null);
    }

    public static Object findTool(String key, String toolboxKey, HttpServletRequest request) {
        return ServletUtils.findTool(key, toolboxKey, request, null);
    }

    public static Object findTool(String key, HttpServletRequest request, ServletContext application) {
        return ServletUtils.findTool(key, VelocityView.DEFAULT_TOOLBOX_KEY, request, application);
    }

    public static Object findTool(String key, String toolboxKey, HttpServletRequest request, ServletContext application) {
        Object tool;
        String path = ServletUtils.getPath(request);
        Toolbox toolbox = (Toolbox)request.getAttribute(toolboxKey);
        if (toolbox != null && (tool = toolbox.get(key, path)) != null) {
            return tool;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object tool2;
            toolbox = (Toolbox)session.getAttribute(toolboxKey);
            if (toolbox != null && (tool2 = toolbox.get(key, path)) != null) {
                return tool2;
            }
            if (application == null) {
                application = session.getServletContext();
            }
        }
        if (application != null && (toolbox = (Toolbox)application.getAttribute(toolboxKey)) != null) {
            return toolbox.get(key, path);
        }
        return null;
    }

    public static InputStream getInputStream(String path, ServletContext application) {
        File file;
        InputStream inputStream = ClassUtils.getResourceAsStream(path, ServletUtils.class);
        if (inputStream == null && (inputStream = application.getResourceAsStream(path)) == null && (file = new File(path)).exists()) {
            try {
                inputStream = new FileInputStream(file);
            }
            catch (FileNotFoundException fnfe) {
                throw new IllegalStateException(fnfe);
            }
        }
        return inputStream;
    }

    public static FactoryConfiguration getConfiguration(ServletContext application) {
        Object obj = application.getAttribute(CONFIGURATION_KEY);
        if (obj instanceof FactoryConfiguration) {
            String addnote;
            FactoryConfiguration injected = (FactoryConfiguration)obj;
            String source = injected.getSource();
            if (!source.endsWith(addnote = " from ServletContext.getAttribute(org.apache.velocity.tools)")) {
                injected.setSource(source + addnote);
            }
            return injected;
        }
        return null;
    }

    public static FactoryConfiguration getConfiguration(String path, ServletContext application) {
        return ServletUtils.getConfiguration(path, application, path.endsWith("toolbox.xml"));
    }

    public static FactoryConfiguration getConfiguration(String path, ServletContext application, boolean deprecationSupportMode) {
        InputStream inputStream = ServletUtils.getInputStream(path, application);
        if (inputStream == null) {
            return null;
        }
        FileFactoryConfiguration config = null;
        String source = "ServletUtils.getConfiguration(" + path + ",ServletContext[,depMode=" + deprecationSupportMode + "])";
        if (path.endsWith(".xml")) {
            config = new XmlFactoryConfiguration(deprecationSupportMode, source);
        } else if (path.endsWith(".properties")) {
            config = new PropertiesFactoryConfiguration(source);
        } else {
            String msg = "Unknown configuration file type: " + path + "\nOnly .xml and .properties configuration files are supported at this time.";
            throw new UnsupportedOperationException(msg);
        }
        try {
            ((FileFactoryConfiguration)config).read(inputStream);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Failed to load configuration at: " + path, ioe);
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ioe) {
                throw new RuntimeException("Failed to close input stream for " + path, ioe);
            }
        }
        return config;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object getMutex(HttpSession session, String key, Object caller) {
        Object lock = session.getAttribute(key);
        if (lock == null) {
            Object object = caller;
            synchronized (object) {
                lock = session.getAttribute(key);
                if (lock == null) {
                    lock = new SessionMutex();
                    session.setAttribute(key, lock);
                }
            }
        }
        return lock;
    }

    private static class SessionMutex
    implements Serializable {
        private SessionMutex() {
        }
    }
}

