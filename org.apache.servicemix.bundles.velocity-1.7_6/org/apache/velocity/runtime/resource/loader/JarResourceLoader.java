/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.JarHolder;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class JarResourceLoader
extends ResourceLoader {
    private Map entryDirectory = new HashMap(559);
    private Map jarfiles = new HashMap(89);

    public void init(ExtendedProperties configuration) {
        this.log.trace("JarResourceLoader : initialization starting.");
        Vector paths = configuration.getVector("path");
        org.apache.velocity.util.StringUtils.trimStrings(paths);
        if (paths == null || paths.size() == 0) {
            paths = configuration.getVector("resource.path");
            org.apache.velocity.util.StringUtils.trimStrings(paths);
            if (paths != null && paths.size() > 0) {
                this.log.debug("JarResourceLoader : you are using a deprecated configuration property for the JarResourceLoader -> '<name>.resource.loader.resource.path'. Please change to the conventional '<name>.resource.loader.path'.");
            }
        }
        if (paths != null) {
            this.log.debug("JarResourceLoader # of paths : " + paths.size());
            for (int i = 0; i < paths.size(); ++i) {
                this.loadJar((String)paths.get(i));
            }
        }
        this.log.trace("JarResourceLoader : initialization complete.");
    }

    private void loadJar(String path) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("JarResourceLoader : trying to load \"" + path + "\"");
        }
        if (path == null) {
            String msg = "JarResourceLoader : can not load JAR - JAR path is null";
            this.log.error(msg);
            throw new RuntimeException(msg);
        }
        if (!path.startsWith("jar:")) {
            String msg = "JarResourceLoader : JAR path must start with jar: -> see java.net.JarURLConnection for information";
            this.log.error(msg);
            throw new RuntimeException(msg);
        }
        if (path.indexOf("!/") < 0) {
            path = path + "!/";
        }
        this.closeJar(path);
        JarHolder temp = new JarHolder(this.rsvc, path);
        this.addEntries(temp.getEntries());
        this.jarfiles.put(temp.getUrlPath(), temp);
    }

    private void closeJar(String path) {
        if (this.jarfiles.containsKey(path)) {
            JarHolder theJar = (JarHolder)this.jarfiles.get(path);
            theJar.close();
        }
    }

    private void addEntries(Hashtable entries) {
        this.entryDirectory.putAll(entries);
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        String jarurl;
        InputStream results = null;
        if (StringUtils.isEmpty((String)source)) {
            throw new ResourceNotFoundException("Need to have a resource!");
        }
        String normalizedPath = org.apache.velocity.util.StringUtils.normalizePath(source);
        if (normalizedPath == null || normalizedPath.length() == 0) {
            String msg = "JAR resource error : argument " + normalizedPath + " contains .. and may be trying to access " + "content outside of template root.  Rejected.";
            this.log.error("JarResourceLoader : " + msg);
            throw new ResourceNotFoundException(msg);
        }
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (this.entryDirectory.containsKey(normalizedPath) && this.jarfiles.containsKey(jarurl = (String)this.entryDirectory.get(normalizedPath))) {
            JarHolder holder = (JarHolder)this.jarfiles.get(jarurl);
            results = holder.getResource(normalizedPath);
            return results;
        }
        throw new ResourceNotFoundException("JarResourceLoader Error: cannot find resource " + source);
    }

    public boolean isSourceModified(Resource resource) {
        return true;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}

