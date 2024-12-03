/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.URLResource;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarResource
extends URLResource {
    private static final Logger LOG = LoggerFactory.getLogger(JarResource.class);
    protected JarURLConnection _jarConnection;

    protected JarResource(URL url) {
        super(url, null);
    }

    protected JarResource(URL url, boolean useCaches) {
        super(url, null, useCaches);
    }

    @Override
    public void close() {
        try (AutoLock l = this._lock.lock();){
            this._jarConnection = null;
            super.close();
        }
    }

    @Override
    protected boolean checkConnection() {
        try (AutoLock l = this._lock.lock();){
            super.checkConnection();
            try {
                if (this._jarConnection != this._connection) {
                    this.newConnection();
                }
            }
            catch (IOException e) {
                LOG.trace("IGNORED", (Throwable)e);
                this._jarConnection = null;
            }
            boolean bl = this._jarConnection != null;
            return bl;
        }
    }

    protected void newConnection() throws IOException {
        this._jarConnection = (JarURLConnection)this._connection;
    }

    @Override
    public boolean exists() {
        if (this._urlString.endsWith("!/")) {
            return this.checkConnection();
        }
        return super.exists();
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.checkConnection();
        if (!this._urlString.endsWith("!/")) {
            return new FilterInputStream(this.getInputStream(false)){

                @Override
                public void close() {
                    this.in = IO.getClosedStream();
                }
            };
        }
        URL url = new URL(this._urlString.substring(4, this._urlString.length() - 2));
        InputStream is = url.openStream();
        return is;
    }

    @Override
    public void copyTo(File directory) throws IOException {
        block34: {
            boolean subEntryIsDir;
            String urlString;
            int endOfJarUrl;
            int startOfJarUrl;
            if (!this.exists()) {
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Extract {} to {}", (Object)this, (Object)directory);
            }
            int n = startOfJarUrl = (endOfJarUrl = (urlString = this.getURI().toASCIIString().trim()).indexOf("!/")) >= 0 ? 4 : 0;
            if (endOfJarUrl < 0) {
                throw new IOException("Not a valid jar url: " + urlString);
            }
            URL jarFileURL = new URL(urlString.substring(startOfJarUrl, endOfJarUrl));
            String subEntryName = endOfJarUrl + 2 < urlString.length() ? urlString.substring(endOfJarUrl + 2) : null;
            boolean bl = subEntryIsDir = subEntryName != null && subEntryName.endsWith("/");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Extracting entry = {} from jar {}", (Object)subEntryName, (Object)jarFileURL);
            }
            URLConnection c = jarFileURL.openConnection();
            c.setUseCaches(false);
            try (InputStream is = c.getInputStream();
                 JarInputStream jin = new JarInputStream(is);){
                Manifest manifest;
                JarEntry entry;
                while ((entry = jin.getNextJarEntry()) != null) {
                    boolean shouldExtract;
                    String entryName = entry.getName();
                    if (subEntryName != null && entryName.startsWith(subEntryName)) {
                        if (!subEntryIsDir && subEntryName.length() + 1 == entryName.length() && entryName.endsWith("/")) {
                            subEntryIsDir = true;
                        }
                        shouldExtract = subEntryIsDir ? !(entryName = entryName.substring(subEntryName.length())).equals("") : true;
                    } else {
                        shouldExtract = subEntryName == null || entryName.startsWith(subEntryName);
                    }
                    if (!shouldExtract) {
                        if (!LOG.isDebugEnabled()) continue;
                        LOG.debug("Skipping entry: {}", (Object)entryName);
                        continue;
                    }
                    String dotCheck = StringUtil.replace(entryName, '\\', '/');
                    if ((dotCheck = URIUtil.canonicalPath(dotCheck)) == null) {
                        if (!LOG.isDebugEnabled()) continue;
                        LOG.debug("Invalid entry: {}", (Object)entryName);
                        continue;
                    }
                    File file = new File(directory, entryName);
                    if (entry.isDirectory()) {
                        if (file.exists()) continue;
                        file.mkdirs();
                        continue;
                    }
                    File dir = new File(file.getParent());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    try (FileOutputStream fout = new FileOutputStream(file);){
                        IO.copy(jin, fout);
                    }
                    if (entry.getTime() < 0L) continue;
                    file.setLastModified(entry.getTime());
                }
                if (subEntryName != null && (subEntryName == null || !subEntryName.equalsIgnoreCase("META-INF/MANIFEST.MF")) || (manifest = jin.getManifest()) == null) break block34;
                File metaInf = new File(directory, "META-INF");
                metaInf.mkdir();
                File f = new File(metaInf, "MANIFEST.MF");
                try (FileOutputStream fout = new FileOutputStream(f);){
                    manifest.write(fout);
                }
            }
        }
    }

    public static Resource newJarResource(Resource resource) throws IOException {
        if (resource instanceof JarResource) {
            return resource;
        }
        return Resource.newResource("jar:" + resource + "!/");
    }
}

