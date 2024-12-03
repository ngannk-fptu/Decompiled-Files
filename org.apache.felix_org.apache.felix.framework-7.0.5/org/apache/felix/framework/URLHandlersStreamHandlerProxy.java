/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.apache.felix.framework.ExtensionManager;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlers;
import org.apache.felix.framework.util.SecureAction;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerSetter;

public class URLHandlersStreamHandlerProxy
extends URLStreamHandler
implements URLStreamHandlerSetter,
InvocationHandler {
    private static final Class[] URL_PROXY_CLASS;
    private static final Class[] STRING_TYPES;
    private static final Method EQUALS;
    private static final Method GET_DEFAULT_PORT;
    private static final Method GET_HOST_ADDRESS;
    private static final Method HASH_CODE;
    private static final Method HOSTS_EQUAL;
    private static final Method OPEN_CONNECTION;
    private static final Method OPEN_CONNECTION_PROXY;
    private static final Method SAME_FILE;
    private static final Method TO_EXTERNAL_FORM;
    private final Object m_service;
    private final SecureAction m_action;
    private final URLStreamHandler m_builtIn;
    private final URL m_builtInURL;
    private final String m_protocol;
    private static final ThreadLocal m_loopCheck;

    private static Method reflect(SecureAction action, String name, Class ... classes) {
        try {
            Method method = URLStreamHandler.class.getDeclaredMethod(name, classes);
            action.setAccesssible(method);
            return method;
        }
        catch (Throwable t) {
            return null;
        }
    }

    public URLHandlersStreamHandlerProxy(String protocol, SecureAction action, URLStreamHandler builtIn, URL builtInURL) {
        this.m_protocol = protocol;
        this.m_service = null;
        this.m_action = action;
        this.m_builtIn = builtIn;
        this.m_builtInURL = builtInURL;
    }

    URLHandlersStreamHandlerProxy(Object service, SecureAction action) {
        this.m_protocol = null;
        this.m_service = service;
        this.m_action = action;
        this.m_builtIn = null;
        this.m_builtInURL = null;
    }

    static URLStreamHandler wrap(String protocol, SecureAction action, URLStreamHandler builtIn, URL builtInURL) {
        return (EQUALS == null || GET_DEFAULT_PORT == null || GET_HOST_ADDRESS == null || HASH_CODE == null || HOSTS_EQUAL == null || OPEN_CONNECTION == null || OPEN_CONNECTION_PROXY == null || SAME_FILE == null || TO_EXTERNAL_FORM == null) && builtIn != null ? builtIn : new URLHandlersStreamHandlerProxy(protocol, action, builtIn, builtInURL);
    }

    @Override
    protected boolean equals(URL url1, URL url2) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url1.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).equals(url1, url2);
        }
        try {
            return (Boolean)EQUALS.invoke(svc, url1, url2);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected int getDefaultPort() {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Stream handler unavailable.");
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).getDefaultPort();
        }
        try {
            return (Integer)GET_DEFAULT_PORT.invoke(svc, null);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected InetAddress getHostAddress(URL url) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).getHostAddress(url);
        }
        try {
            return (InetAddress)GET_HOST_ADDRESS.invoke(svc, url);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected int hashCode(URL url) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).hashCode(url);
        }
        try {
            return (Integer)HASH_CODE.invoke(svc, url);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected boolean hostsEqual(URL url1, URL url2) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url1.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).hostsEqual(url1, url2);
        }
        try {
            return (Boolean)HOSTS_EQUAL.invoke(svc, url1, url2);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new MalformedURLException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).openConnection(url);
        }
        try {
            if ("http".equals(url.getProtocol()) && "felix.extensions".equals(url.getHost()) && 9 == url.getPort()) {
                try {
                    Object handler = this.m_action.getDeclaredField(ExtensionManager.class, "m_extensionManager", null);
                    if (handler != null) {
                        return (URLConnection)this.m_action.invoke(this.m_action.getMethod(handler.getClass(), "openConnection", new Class[]{URL.class}), handler, new Object[]{url});
                    }
                    throw new IOException("Extensions not supported or ambiguous context.");
                }
                catch (IOException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    throw new IOException(ex.getMessage());
                }
            }
            return (URLConnection)OPEN_CONNECTION.invoke(svc, url);
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new MalformedURLException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            Method method;
            try {
                method = svc.getClass().getMethod("openConnection", URL_PROXY_CLASS);
            }
            catch (NoSuchMethodException e) {
                UnsupportedOperationException rte = new UnsupportedOperationException(e.getMessage());
                rte.initCause(e);
                throw rte;
            }
            try {
                this.m_action.setAccesssible(method);
                return (URLConnection)method.invoke(svc, url, proxy);
            }
            catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(e.getMessage(), e);
            }
        }
        try {
            return (URLConnection)OPEN_CONNECTION_PROXY.invoke(svc, url, proxy);
        }
        catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void parseURL(URL url, String spec, int start, int limit) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            ((URLStreamHandlerService)svc).parseURL(this, url, spec, start, limit);
        } else {
            try {
                URL test = null;
                if (this.m_builtInURL != null) {
                    if (m_loopCheck.get() != null) {
                        test = new URL(new URL(this.m_builtInURL, url.toExternalForm()), spec, (URLStreamHandler)svc);
                    } else {
                        m_loopCheck.set(Thread.currentThread());
                        try {
                            test = new URL(new URL(this.m_builtInURL, url.toExternalForm()), spec);
                        }
                        finally {
                            m_loopCheck.set(null);
                        }
                    }
                } else {
                    test = this.m_action.createURL(url, spec, (URLStreamHandler)svc);
                }
                super.setURL(url, test.getProtocol(), test.getHost(), test.getPort(), test.getAuthority(), test.getUserInfo(), test.getPath(), test.getQuery(), test.getRef());
            }
            catch (Exception ex) {
                throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    protected boolean sameFile(URL url1, URL url2) {
        Object svc = this.getStreamHandlerService();
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url1.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).sameFile(url1, url2);
        }
        try {
            return (Boolean)SAME_FILE.invoke(svc, url1, url2);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void setURL(URL url, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        super.setURL(url, protocol, host, port, authority, userInfo, path, query, ref);
    }

    @Override
    public void setURL(URL url, String protocol, String host, int port, String file, String ref) {
        super.setURL(url, protocol, host, port, file, ref);
    }

    @Override
    protected String toExternalForm(URL url) {
        return this.toExternalForm(url, this.getStreamHandlerService());
    }

    private String toExternalForm(URL url, Object svc) {
        if (svc == null) {
            throw new IllegalStateException("Unknown protocol: " + url.getProtocol());
        }
        if (svc instanceof URLStreamHandlerService) {
            return ((URLStreamHandlerService)svc).toExternalForm(url);
        }
        try {
            try {
                String result = (String)TO_EXTERNAL_FORM.invoke(svc, url);
                if (result != null && result.equals(url.getProtocol() + "://null")) {
                    result = url.getProtocol() + ":";
                }
                return result;
            }
            catch (InvocationTargetException ex) {
                Throwable t = ex.getTargetException();
                if (t instanceof Exception) {
                    throw (Exception)t;
                }
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new IllegalStateException("Unknown throwable: " + t, t);
            }
        }
        catch (NullPointerException ex) {
            StringBuilder answer = new StringBuilder();
            answer.append(url.getProtocol());
            answer.append(':');
            String authority = url.getAuthority();
            if (authority != null && authority.length() > 0) {
                answer.append("//");
                answer.append(url.getAuthority());
            }
            String file = url.getFile();
            String ref = url.getRef();
            if (file != null) {
                answer.append(file);
            }
            if (ref != null) {
                answer.append('#');
                answer.append(ref);
            }
            return answer.toString();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Stream handler unavailable due to: " + ex.getMessage(), ex);
        }
    }

    private Object getStreamHandlerService() {
        try {
            Object framework = URLHandlers.getFrameworkFromContext();
            if (framework == null) {
                return this.m_builtIn;
            }
            Object service = framework instanceof Felix ? ((Felix)framework).getStreamHandlerService(this.m_protocol) : this.m_action.invoke(this.m_action.getDeclaredMethod(framework.getClass(), "getStreamHandlerService", STRING_TYPES), framework, new Object[]{this.m_protocol});
            if (service == null) {
                return this.m_builtIn;
            }
            if (service instanceof URLStreamHandlerService) {
                return (URLStreamHandlerService)service;
            }
            return this.m_action.createProxy(this.m_action.getClassLoader(URLStreamHandlerService.class), new Class[]{URLStreamHandlerService.class}, new URLHandlersStreamHandlerProxy(service, this.m_action));
        }
        catch (ThreadDeath td) {
            throw td;
        }
        catch (Throwable t) {
            return this.m_builtIn;
        }
    }

    @Override
    public Object invoke(Object obj, Method method, Object[] params) throws Throwable {
        Class[] types = method.getParameterTypes();
        if (this.m_service == null) {
            return this.m_action.invoke(this.m_action.getMethod(this.getClass(), method.getName(), types), this, params);
        }
        if ("parseURL".equals(method.getName())) {
            ClassLoader loader = this.m_action.getClassLoader(this.m_service.getClass());
            types[0] = loader.loadClass(URLStreamHandlerSetter.class.getName());
            params[0] = this.m_action.createProxy(loader, new Class[]{types[0]}, (URLHandlersStreamHandlerProxy)params[0]);
        }
        return this.m_action.invokeDirect(this.m_action.getDeclaredMethod(this.m_service.getClass(), method.getName(), types), this.m_service, params);
    }

    static {
        STRING_TYPES = new Class[]{String.class};
        SecureAction action = new SecureAction();
        EQUALS = URLHandlersStreamHandlerProxy.reflect(action, "equals", URL.class, URL.class);
        GET_DEFAULT_PORT = URLHandlersStreamHandlerProxy.reflect(action, "getDefaultPort", null);
        GET_HOST_ADDRESS = URLHandlersStreamHandlerProxy.reflect(action, "getHostAddress", URL.class);
        HASH_CODE = URLHandlersStreamHandlerProxy.reflect(action, "hashCode", URL.class);
        HOSTS_EQUAL = URLHandlersStreamHandlerProxy.reflect(action, "hostsEqual", URL.class, URL.class);
        OPEN_CONNECTION = URLHandlersStreamHandlerProxy.reflect(action, "openConnection", URL.class);
        SAME_FILE = URLHandlersStreamHandlerProxy.reflect(action, "sameFile", URL.class, URL.class);
        TO_EXTERNAL_FORM = URLHandlersStreamHandlerProxy.reflect(action, "toExternalForm", URL.class);
        URL_PROXY_CLASS = new Class[]{URL.class, Proxy.class};
        OPEN_CONNECTION_PROXY = URLHandlersStreamHandlerProxy.reflect(action, "openConnection", URL_PROXY_CLASS);
        m_loopCheck = new ThreadLocal();
    }
}

