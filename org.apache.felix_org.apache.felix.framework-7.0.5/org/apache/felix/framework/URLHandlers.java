/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.lang.reflect.Method;
import java.net.ContentHandler;
import java.net.ContentHandlerFactory;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlersBundleStreamHandler;
import org.apache.felix.framework.URLHandlersContentHandlerProxy;
import org.apache.felix.framework.URLHandlersStreamHandlerProxy;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.SecurityManagerEx;
import org.apache.felix.framework.util.Util;
import org.osgi.service.url.URLStreamHandlerService;

class URLHandlers
implements URLStreamHandlerFactory,
ContentHandlerFactory {
    private static final Class[] CLASS_TYPE;
    private static final Class URLHANDLERS_CLASS;
    private static final SecureAction m_secureAction;
    private static volatile SecurityManagerEx m_sm;
    private static volatile URLHandlers m_handler;
    private static final ConcurrentHashMap<ClassLoader, List<Object>> m_classloaderToFrameworkLists;
    private static final CopyOnWriteArrayList<Felix> m_frameworks;
    private static volatile int m_counter;
    private static final ConcurrentHashMap<String, ContentHandler> m_contentHandlerCache;
    private static final ConcurrentHashMap<String, URLStreamHandler> m_streamHandlerCache;
    private static final ConcurrentHashMap<String, URL> m_protocolToURL;
    private static volatile URLStreamHandlerFactory m_streamHandlerFactory;
    private static volatile ContentHandlerFactory m_contentHandlerFactory;
    private static final String STREAM_HANDLER_PACKAGE_PROP = "java.protocol.handler.pkgs";
    private static final String DEFAULT_STREAM_HANDLER_PACKAGE = "sun.net.www.protocol|com.ibm.oti.net.www.protocol|gnu.java.net.protocol|wonka.net|com.acunia.wonka.net|org.apache.harmony.luni.internal.net.www.protocol|weblogic.utils|weblogic.net|javax.net.ssl|COM.newmonics.www.protocols";
    private static volatile Object m_rootURLHandlers;
    private static final String m_streamPkgs;
    private static final ConcurrentHashMap<String, URLStreamHandler> m_builtIn;
    private static final boolean m_loaded;

    private void init(String protocol, URLStreamHandlerFactory factory) {
        try {
            Method getURLStreamHandler = m_secureAction.getDeclaredMethod(URL.class, "getURLStreamHandler", new Class[]{String.class});
            URLStreamHandler handler = (URLStreamHandler)m_secureAction.invoke(getURLStreamHandler, null, new Object[]{protocol});
            URLHandlers.addToCache(m_builtIn, protocol, handler);
        }
        catch (Throwable ex) {
            try {
                URLStreamHandler handler = this.getBuiltInStreamHandler(protocol, factory);
                if (handler != null) {
                    URL url = new URL(protocol, null, -1, "", handler);
                    URLHandlers.addToCache(m_protocolToURL, protocol, url);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private URLHandlers() {
        m_sm = new SecurityManagerEx();
        Class<URL> clazz = URL.class;
        synchronized (URL.class) {
            block37: {
                URLStreamHandler handler2;
                Method getURLStreamHandler;
                URLStreamHandlerFactory currentFactory = null;
                try {
                    currentFactory = (URLStreamHandlerFactory)m_secureAction.swapStaticFieldIfNotClass(URL.class, URLStreamHandlerFactory.class, URLHANDLERS_CLASS, "streamHandlerLock");
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                this.init("file", currentFactory);
                this.init("ftp", currentFactory);
                this.init("http", currentFactory);
                this.init("https", currentFactory);
                if (URLHandlers.getFromCache(m_builtIn, "jrt") == null) {
                    try {
                        getURLStreamHandler = m_secureAction.getDeclaredMethod(URL.class, "getURLStreamHandler", new Class[]{String.class});
                        handler2 = (URLStreamHandler)m_secureAction.invoke(getURLStreamHandler, null, new Object[]{"jrt"});
                        URLHandlers.addToCache(m_builtIn, "jrt", handler2);
                    }
                    catch (Throwable ex) {
                        try {
                            this.getBuiltInStreamHandler("jrt", currentFactory);
                        }
                        catch (Throwable handler2) {
                            // empty catch block
                        }
                    }
                }
                if (URLHandlers.getFromCache(m_builtIn, "jar") == null) {
                    try {
                        getURLStreamHandler = m_secureAction.getDeclaredMethod(URL.class, "getURLStreamHandler", new Class[]{String.class});
                        handler2 = (URLStreamHandler)m_secureAction.invoke(getURLStreamHandler, null, new Object[]{"jar"});
                        URLHandlers.addToCache(m_builtIn, "jar", handler2);
                    }
                    catch (Throwable ex) {
                        try {
                            this.getBuiltInStreamHandler("jar", currentFactory);
                        }
                        catch (Throwable handler3) {
                            // empty catch block
                        }
                    }
                }
                if (currentFactory != null) {
                    try {
                        URL.setURLStreamHandlerFactory(currentFactory);
                    }
                    catch (Throwable ex) {
                        // empty catch block
                    }
                }
                try {
                    URL.setURLStreamHandlerFactory(this);
                    m_streamHandlerFactory = this;
                    m_rootURLHandlers = this;
                    try {
                        m_secureAction.flush(URL.class, URL.class);
                    }
                    catch (Throwable ex) {}
                }
                catch (Error err) {
                    try {
                        m_streamHandlerFactory = (URLStreamHandlerFactory)m_secureAction.swapStaticFieldIfNotClass(URL.class, URLStreamHandlerFactory.class, URLHANDLERS_CLASS, "streamHandlerLock");
                        if (m_streamHandlerFactory == null) {
                            throw err;
                        }
                        if (!m_streamHandlerFactory.getClass().getName().equals(URLHANDLERS_CLASS.getName())) {
                            URL.setURLStreamHandlerFactory(this);
                            m_rootURLHandlers = this;
                        }
                        if (URLHANDLERS_CLASS == m_streamHandlerFactory.getClass()) break block37;
                        try {
                            m_secureAction.invoke(m_secureAction.getDeclaredMethod(m_streamHandlerFactory.getClass(), "registerFrameworkListsForContextSearch", new Class[]{ClassLoader.class, List.class}), m_streamHandlerFactory, new Object[]{URLHANDLERS_CLASS.getClassLoader(), m_frameworks});
                            m_rootURLHandlers = m_streamHandlerFactory;
                        }
                        catch (Exception ex) {
                            throw new RuntimeException(ex.getMessage());
                        }
                    }
                    catch (Exception e) {
                        throw err;
                    }
                }
            }
            try {
                URLConnection.setContentHandlerFactory(this);
                m_contentHandlerFactory = this;
                try {
                    m_secureAction.flush(URLConnection.class, URLConnection.class);
                }
                catch (Throwable err) {}
            }
            catch (Error err) {
                try {
                    m_contentHandlerFactory = (ContentHandlerFactory)m_secureAction.swapStaticFieldIfNotClass(URLConnection.class, ContentHandlerFactory.class, URLHANDLERS_CLASS, null);
                    if (m_contentHandlerFactory == null) {
                        throw err;
                    }
                    if (!m_contentHandlerFactory.getClass().getName().equals(URLHANDLERS_CLASS.getName())) {
                        URLConnection.setContentHandlerFactory(this);
                    }
                }
                catch (Exception ex) {
                    throw err;
                }
            }
            if (m_streamHandlerFactory != this && URLHANDLERS_CLASS.getName().equals(m_streamHandlerFactory.getClass().getName())) {
                m_sm = null;
                m_protocolToURL.clear();
                m_builtIn.clear();
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void registerFrameworkListsForContextSearch(ClassLoader index, List frameworkLists) {
        Class<URL> clazz = URL.class;
        synchronized (URL.class) {
            ConcurrentHashMap<ClassLoader, List<Object>> concurrentHashMap = m_classloaderToFrameworkLists;
            synchronized (concurrentHashMap) {
                m_classloaderToFrameworkLists.put(index, frameworkLists);
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void unregisterFrameworkListsForContextSearch(ClassLoader index) {
        Class<URL> clazz = URL.class;
        synchronized (URL.class) {
            ConcurrentHashMap<ClassLoader, List<Object>> concurrentHashMap = m_classloaderToFrameworkLists;
            synchronized (concurrentHashMap) {
                m_classloaderToFrameworkLists.remove(index);
                if (m_classloaderToFrameworkLists.isEmpty()) {
                    CopyOnWriteArrayList<Felix> copyOnWriteArrayList = m_frameworks;
                    synchronized (copyOnWriteArrayList) {
                        if (m_frameworks.isEmpty()) {
                            try {
                                m_secureAction.swapStaticFieldIfNotClass(URL.class, URLStreamHandlerFactory.class, null, "streamHandlerLock");
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (m_streamHandlerFactory.getClass() != URLHANDLERS_CLASS) {
                                URL.setURLStreamHandlerFactory(m_streamHandlerFactory);
                            }
                            try {
                                m_secureAction.swapStaticFieldIfNotClass(URLConnection.class, ContentHandlerFactory.class, null, null);
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (m_contentHandlerFactory.getClass() != URLHANDLERS_CLASS) {
                                URLConnection.setContentHandlerFactory(m_contentHandlerFactory);
                            }
                        }
                    }
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private URLStreamHandler getBuiltInStreamHandler(String protocol, URLStreamHandlerFactory factory) {
        URLStreamHandler handler = URLHandlers.getFromCache(m_builtIn, protocol);
        if (handler != null) {
            return handler;
        }
        if (factory != null) {
            handler = factory.createURLStreamHandler(protocol);
        }
        if (handler == null) {
            handler = this.loadBuiltInStreamHandler(protocol, null);
        }
        if (handler == null) {
            handler = this.loadBuiltInStreamHandler(protocol, ClassLoader.getSystemClassLoader());
        }
        return URLHandlers.addToCache(m_builtIn, protocol, handler);
    }

    private URLStreamHandler loadBuiltInStreamHandler(String protocol, ClassLoader classLoader) {
        StringTokenizer pkgTok = new StringTokenizer(m_streamPkgs, "| ");
        while (pkgTok.hasMoreTokens()) {
            String pkg = pkgTok.nextToken().trim();
            String className = pkg + "." + protocol + ".Handler";
            try {
                Class handler = m_secureAction.forName(className, classLoader);
                if (handler == null) continue;
                return (URLStreamHandler)handler.newInstance();
            }
            catch (Throwable throwable) {
            }
        }
        String androidHandler = null;
        if ("file".equalsIgnoreCase(protocol)) {
            androidHandler = "libcore.net.url.FileHandler";
        } else if ("ftp".equalsIgnoreCase(protocol)) {
            androidHandler = "libcore.net.url.FtpHandler";
        } else if ("http".equalsIgnoreCase(protocol)) {
            androidHandler = "libcore.net.http.HttpHandler";
        } else if ("https".equalsIgnoreCase(protocol)) {
            androidHandler = "libcore.net.http.HttpsHandler";
        } else if ("jar".equalsIgnoreCase(protocol)) {
            androidHandler = "libcore.net.url.JarHandler";
        }
        if (androidHandler != null) {
            try {
                Class handler = m_secureAction.forName(androidHandler, classLoader);
                if (handler != null) {
                    return (URLStreamHandler)handler.newInstance();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        URLStreamHandler handler = URLHandlers.getFromCache(m_streamHandlerCache, protocol);
        if (handler != null) {
            return handler;
        }
        if (protocol.equals("bundle")) {
            return new URLHandlersBundleStreamHandler(URLHandlers.getFrameworkFromContext(), m_secureAction);
        }
        handler = this.getBuiltInStreamHandler(protocol, m_streamHandlerFactory != this ? m_streamHandlerFactory : null);
        if (handler == null && this.isJVM(protocol)) {
            return null;
        }
        return URLHandlers.addToCache(m_streamHandlerCache, protocol, URLHandlersStreamHandlerProxy.wrap(protocol, m_secureAction, handler, URLHandlers.getFromCache(m_protocolToURL, protocol)));
    }

    private boolean isJVM(String protocol) {
        return protocol.equals("file") || protocol.equals("ftp") || protocol.equals("http") || protocol.equals("https") || protocol.equals("jar") || protocol.equals("jmod") || protocol.equals("mailto") || protocol.equals("jrt");
    }

    @Override
    public ContentHandler createContentHandler(String mimeType) {
        ContentHandler handler = URLHandlers.getFromCache(m_contentHandlerCache, mimeType);
        if (handler != null) {
            return handler;
        }
        return URLHandlers.addToCache(m_contentHandlerCache, mimeType, new URLHandlersContentHandlerProxy(mimeType, m_secureAction, m_contentHandlerFactory != this ? m_contentHandlerFactory : null));
    }

    private static <K, V> V addToCache(ConcurrentHashMap<K, V> cache, K key, V value) {
        return key != null && value != null ? (V)Util.putIfAbsentAndReturn(cache, key, value) : null;
    }

    private static <K, V> V getFromCache(ConcurrentHashMap<K, V> cache, K key) {
        return key != null ? (V)cache.get(key) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void registerFrameworkInstance(Felix framework, boolean enable) {
        boolean register = false;
        Object object = m_frameworks;
        synchronized (object) {
            if (enable) {
                if (m_handler == null) {
                    register = true;
                } else {
                    m_frameworks.add(framework);
                    ++m_counter;
                }
            } else {
                ++m_counter;
            }
        }
        if (!register) return;
        object = URL.class;
        synchronized (URL.class) {
            ConcurrentHashMap<ClassLoader, List<Object>> concurrentHashMap = m_classloaderToFrameworkLists;
            synchronized (concurrentHashMap) {
                CopyOnWriteArrayList<Felix> copyOnWriteArrayList = m_frameworks;
                synchronized (copyOnWriteArrayList) {
                    if (m_handler == null) {
                        m_handler = new URLHandlers();
                    }
                    m_frameworks.add(framework);
                    ++m_counter;
                }
            }
            // ** MonitorExit[var3_3] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void unregisterFrameworkInstance(Object framework) {
        boolean unregister = false;
        Object object = m_frameworks;
        synchronized (object) {
            if (m_frameworks.contains(framework)) {
                if (m_frameworks.size() == 1 && m_handler != null) {
                    unregister = true;
                } else {
                    m_frameworks.remove(framework);
                    --m_counter;
                }
            } else {
                --m_counter;
            }
        }
        if (!unregister) return;
        object = URL.class;
        synchronized (URL.class) {
            ConcurrentHashMap<ClassLoader, List<Object>> concurrentHashMap = m_classloaderToFrameworkLists;
            synchronized (concurrentHashMap) {
                CopyOnWriteArrayList<Felix> copyOnWriteArrayList = m_frameworks;
                synchronized (copyOnWriteArrayList) {
                    m_frameworks.remove(framework);
                    --m_counter;
                    if (!m_frameworks.isEmpty() || m_handler == null) return;
                    m_handler = null;
                    try {
                        m_secureAction.invoke(m_secureAction.getDeclaredMethod(m_rootURLHandlers.getClass(), "unregisterFrameworkListsForContextSearch", new Class[]{ClassLoader.class}), m_rootURLHandlers, new Object[]{URLHANDLERS_CLASS.getClassLoader()});
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public static Object getFrameworkFromContext() {
        int attempts = 0;
        while (m_classloaderToFrameworkLists.isEmpty() && m_counter == 1 && m_frameworks.size() == 1) {
            Felix framework = m_frameworks.get(0);
            if (framework != null) {
                return framework;
            }
            if (attempts++ <= 3) continue;
            break;
        }
        Class[] stack = m_sm.getClassContext();
        Class targetClass = null;
        Object targetClassLoader = null;
        for (int i = 0; i < stack.length; ++i) {
            String name;
            ClassLoader classLoader = m_secureAction.getClassLoader(stack[i]);
            if (classLoader == null || !(name = classLoader.getClass().getName()).startsWith("org.apache.felix.framework.ModuleImpl$ModuleClassLoader") && !name.equals("org.apache.felix.framework.searchpolicy.ContentClassLoader") && !name.startsWith("org.apache.felix.framework.BundleWiringImpl$BundleClassLoader")) continue;
            targetClass = stack[i];
            targetClassLoader = classLoader;
            break;
        }
        if (targetClass != null) {
            ClassLoader index = m_secureAction.getClassLoader(targetClassLoader.getClass());
            List<Object> frameworks = m_classloaderToFrameworkLists.get(index);
            if (frameworks == null && index == URLHANDLERS_CLASS.getClassLoader()) {
                frameworks = m_frameworks;
            }
            if (frameworks != null) {
                for (Object framework : frameworks) {
                    try {
                        if (m_secureAction.invoke(m_secureAction.getDeclaredMethod(framework.getClass(), "getBundle", CLASS_TYPE), framework, new Object[]{targetClass}) == null) continue;
                        return framework;
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static Object getFrameworkFromContext(String uuid) {
        if (uuid != null) {
            for (Felix felix : m_frameworks) {
                if (!uuid.equals(felix._getProperty("org.osgi.framework.uuid"))) continue;
                return felix;
            }
            for (List list : m_classloaderToFrameworkLists.values()) {
                for (Object framework : list) {
                    try {
                        if (!uuid.equals(m_secureAction.invoke(m_secureAction.getDeclaredMethod(framework.getClass(), "getProperty", new Class[]{String.class}), framework, new Object[]{"org.osgi.framework.uuid"}))) continue;
                        return framework;
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return URLHandlers.getFrameworkFromContext();
    }

    static {
        boolean loaded;
        CLASS_TYPE = new Class[]{Class.class};
        URLHANDLERS_CLASS = URLHandlers.class;
        m_secureAction = new SecureAction();
        m_sm = null;
        m_handler = null;
        m_classloaderToFrameworkLists = new ConcurrentHashMap();
        m_frameworks = new CopyOnWriteArrayList();
        m_counter = 0;
        m_contentHandlerCache = new ConcurrentHashMap();
        m_streamHandlerCache = new ConcurrentHashMap();
        m_protocolToURL = new ConcurrentHashMap();
        m_builtIn = new ConcurrentHashMap();
        String pkgs = new SecureAction().getSystemProperty(STREAM_HANDLER_PACKAGE_PROP, "");
        m_streamPkgs = pkgs.equals("") ? DEFAULT_STREAM_HANDLER_PACKAGE : pkgs + "|" + DEFAULT_STREAM_HANDLER_PACKAGE;
        try {
            loaded = null != URLHandlersStreamHandlerProxy.class && null != URLHandlersContentHandlerProxy.class && null != URLStreamHandlerService.class && new URLHandlersStreamHandlerProxy(null, null) != null;
        }
        catch (Throwable e) {
            loaded = false;
        }
        m_loaded = loaded;
    }
}

