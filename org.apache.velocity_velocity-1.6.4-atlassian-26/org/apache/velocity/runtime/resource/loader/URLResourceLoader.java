/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class URLResourceLoader
extends ResourceLoader {
    private String[] roots = null;
    protected HashMap templateRoots = null;
    private int timeout = -1;
    private Method[] timeoutMethods;

    @Override
    public void init(ExtendedProperties configuration) {
        this.log.trace("URLResourceLoader : initialization starting.");
        this.roots = configuration.getStringArray("root");
        if (this.log.isDebugEnabled()) {
            for (int i = 0; i < this.roots.length; ++i) {
                this.log.debug("URLResourceLoader : adding root '" + this.roots[i] + "'");
            }
        }
        this.timeout = configuration.getInt("timeout", -1);
        if (this.timeout > 0) {
            try {
                Class[] types = new Class[]{Integer.TYPE};
                Method conn = URLConnection.class.getMethod("setConnectTimeout", types);
                Method read = URLConnection.class.getMethod("setReadTimeout", types);
                this.timeoutMethods = new Method[]{conn, read};
                this.log.debug("URLResourceLoader : timeout set to " + this.timeout);
            }
            catch (NoSuchMethodException nsme) {
                this.log.debug("URLResourceLoader : Java 1.5+ is required to customize timeout!", nsme);
                this.timeout = -1;
            }
        }
        this.templateRoots = new HashMap();
        this.log.trace("URLResourceLoader : initialization complete.");
    }

    @Override
    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (StringUtils.isEmpty((CharSequence)name)) {
            throw new ResourceNotFoundException("URLResourceLoader : No template name provided");
        }
        InputStream inputStream = null;
        Throwable exception = null;
        for (int i = 0; i < this.roots.length; ++i) {
            try {
                URL u = new URL(this.roots[i] + name);
                URLConnection conn = u.openConnection();
                this.tryToSetTimeout(conn);
                inputStream = conn.getInputStream();
                if (inputStream == null) continue;
                if (this.log.isDebugEnabled()) {
                    this.log.debug("URLResourceLoader: Found '" + name + "' at '" + this.roots[i] + "'");
                }
                this.templateRoots.put(name, this.roots[i]);
                break;
            }
            catch (IOException ioe) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("URLResourceLoader: Exception when looking for '" + name + "' at '" + this.roots[i] + "'", ioe);
                }
                if (exception != null) continue;
                exception = ioe;
            }
        }
        if (inputStream == null) {
            String msg = exception == null ? "URLResourceLoader : Resource '" + name + "' not found." : exception.getMessage();
            throw new ResourceNotFoundException(msg);
        }
        return inputStream;
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        long fileLastModified = this.getLastModified(resource);
        return fileLastModified == 0L || fileLastModified != resource.getLastModified();
    }

    @Override
    public long getLastModified(Resource resource) {
        String name = resource.getName();
        String root = (String)this.templateRoots.get(name);
        try {
            URL u = new URL(root + name);
            URLConnection conn = u.openConnection();
            this.tryToSetTimeout(conn);
            return conn.getLastModified();
        }
        catch (IOException ioe) {
            String msg = "URLResourceLoader: '" + name + "' is no longer reachable at '" + root + "'";
            this.log.error(msg, ioe);
            throw new ResourceNotFoundException(msg, ioe);
        }
    }

    public int getTimeout() {
        return this.timeout;
    }

    private void tryToSetTimeout(URLConnection conn) {
        if (this.timeout > 0) {
            Object[] arg = new Object[]{new Integer(this.timeout)};
            try {
                this.timeoutMethods[0].invoke((Object)conn, arg);
                this.timeoutMethods[1].invoke((Object)conn, arg);
            }
            catch (Exception e) {
                String msg = "Unexpected exception while setting connection timeout for " + conn;
                this.log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
    }
}

