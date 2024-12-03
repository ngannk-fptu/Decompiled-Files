/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlers;
import org.apache.felix.framework.URLHandlersBundleURLConnection;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;

class URLHandlersBundleStreamHandler
extends URLStreamHandler {
    private final Object m_framework;
    private final SecureAction m_action;

    public URLHandlersBundleStreamHandler(Object framework, SecureAction action) {
        this.m_framework = framework;
        this.m_action = action;
    }

    public URLHandlersBundleStreamHandler(SecureAction action) {
        this.m_framework = null;
        this.m_action = action;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        Object framework;
        if (!"felix".equals(url.getAuthority())) {
            this.checkPermission(url);
        }
        if ((framework = this.m_framework) == null) {
            framework = URLHandlers.getFrameworkFromContext(Util.getFrameworkUUIDFromURL(url.getHost()));
        }
        if (framework != null) {
            if (framework instanceof Felix) {
                return new URLHandlersBundleURLConnection(url, (Felix)framework);
            }
            try {
                ClassLoader loader = this.m_action.getClassLoader(framework.getClass());
                Class<?> targetClass = loader.loadClass(URLHandlersBundleURLConnection.class.getName());
                Constructor constructor = this.m_action.getConstructor(targetClass, new Class[]{URL.class, loader.loadClass(Felix.class.getName())});
                this.m_action.setAccesssible(constructor);
                return (URLConnection)this.m_action.invoke(constructor, new Object[]{url, framework});
            }
            catch (Exception ex) {
                throw new IOException(ex.getMessage());
            }
        }
        throw new IOException("No framework context found");
    }

    @Override
    protected void parseURL(URL u, String spec, int start, int limit) {
        super.parseURL(u, spec, start, limit);
        if (this.checkPermission(u)) {
            super.setURL(u, u.getProtocol(), u.getHost(), u.getPort(), "felix", u.getUserInfo(), u.getPath(), u.getQuery(), u.getRef());
        }
    }

    @Override
    protected String toExternalForm(URL u) {
        StringBuilder result = new StringBuilder();
        result.append(u.getProtocol());
        result.append("://");
        result.append(u.getHost());
        result.append(':');
        result.append(u.getPort());
        if (u.getPath() != null) {
            result.append(u.getPath());
        }
        if (u.getQuery() != null) {
            result.append('?');
            result.append(u.getQuery());
        }
        if (u.getRef() != null) {
            result.append("#");
            result.append(u.getRef());
        }
        return result.toString();
    }

    @Override
    protected InetAddress getHostAddress(URL u) {
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean checkPermission(URL u) {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) return true;
        Object framework = this.m_framework;
        if (framework == null) {
            framework = URLHandlers.getFrameworkFromContext(Util.getFrameworkUUIDFromURL(u.getHost()));
        }
        try {
            long bundleId = Util.getBundleIdFromRevisionId(Util.getRevisionIdFromURL(u.getHost()));
            if (framework instanceof Felix) {
                Bundle bundle = ((Felix)framework).getBundle(bundleId);
                if (bundle == null) return false;
                sm.checkPermission(new AdminPermission(bundle, "resource"));
                return true;
            } else {
                if (framework == null) throw new IOException("No framework context found");
                Method method = this.m_action.getDeclaredMethod(framework.getClass(), "getBundle", new Class[]{Long.TYPE});
                this.m_action.setAccesssible(method);
                Object bundle = method.invoke(framework, bundleId);
                if (bundle == null) return false;
                ClassLoader loader = this.m_action.getClassLoader(framework.getClass());
                sm.checkPermission((Permission)this.m_action.getConstructor(loader.loadClass(AdminPermission.class.getName()), new Class[]{loader.loadClass(Bundle.class.getName()), String.class}).newInstance(bundle, "resource"));
                return true;
            }
        }
        catch (SecurityException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new SecurityException(ex);
        }
    }
}

