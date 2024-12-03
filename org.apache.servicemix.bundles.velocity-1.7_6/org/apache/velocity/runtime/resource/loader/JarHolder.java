/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;

public class JarHolder {
    private String urlpath = null;
    private JarFile theJar = null;
    private JarURLConnection conn = null;
    private Log log = null;

    public JarHolder(RuntimeServices rs, String urlpath) {
        this.log = rs.getLog();
        this.urlpath = urlpath;
        this.init();
        if (this.log.isDebugEnabled()) {
            this.log.debug("JarHolder: initialized JAR: " + urlpath);
        }
    }

    public void init() {
        try {
            if (this.log.isDebugEnabled()) {
                this.log.debug("JarHolder: attempting to connect to " + this.urlpath);
            }
            URL url = new URL(this.urlpath);
            this.conn = (JarURLConnection)url.openConnection();
            this.conn.setAllowUserInteraction(false);
            this.conn.setDoInput(true);
            this.conn.setDoOutput(false);
            this.conn.connect();
            this.theJar = this.conn.getJarFile();
        }
        catch (IOException ioe) {
            String msg = "JarHolder: error establishing connection to JAR at \"" + this.urlpath + "\"";
            this.log.error(msg, ioe);
            throw new VelocityException(msg, ioe);
        }
    }

    public void close() {
        try {
            this.theJar.close();
        }
        catch (Exception e) {
            String msg = "JarHolder: error closing the JAR file";
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        this.theJar = null;
        this.conn = null;
        this.log.trace("JarHolder: JAR file closed");
    }

    public InputStream getResource(String theentry) throws ResourceNotFoundException {
        InputStream data = null;
        try {
            JarEntry entry = this.theJar.getJarEntry(theentry);
            if (entry != null) {
                data = this.theJar.getInputStream(entry);
            }
        }
        catch (Exception fnfe) {
            this.log.error("JarHolder: getResource() error", fnfe);
            throw new ResourceNotFoundException(fnfe);
        }
        return data;
    }

    public Hashtable getEntries() {
        Hashtable<String, String> allEntries = new Hashtable<String, String>(559);
        Enumeration<JarEntry> all = this.theJar.entries();
        while (all.hasMoreElements()) {
            JarEntry je = all.nextElement();
            if (je.isDirectory()) continue;
            allEntries.put(je.getName(), this.urlpath);
        }
        return allEntries;
    }

    public String getUrlPath() {
        return this.urlpath;
    }
}

