/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.zip.ZipEntry;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchiveRevision;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.JarContent;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.WeakZipFileFactory;

class JarRevision
extends BundleArchiveRevision {
    private static final transient String BUNDLE_JAR_FILE = "bundle.jar";
    private final WeakZipFileFactory m_zipFactory;
    private final File m_bundleFile;
    private final WeakZipFileFactory.WeakZipFile m_zipFile;

    public JarRevision(Logger logger, Map configMap, WeakZipFileFactory zipFactory, File revisionRootDir, String location, boolean byReference, InputStream is) throws Exception {
        super(logger, configMap, revisionRootDir, location);
        this.m_zipFactory = zipFactory;
        this.m_bundleFile = byReference ? new File(location.substring(location.indexOf("file:") + "file:".length())) : new File(this.getRevisionRootDir(), BUNDLE_JAR_FILE);
        this.initialize(byReference, is);
        WeakZipFileFactory.WeakZipFile zipFile = null;
        try {
            zipFile = this.m_zipFactory.create(this.m_bundleFile);
            if (zipFile == null) {
                throw new IOException("No JAR file found.");
            }
            this.m_zipFile = zipFile;
        }
        catch (Exception ex) {
            if (zipFile != null) {
                zipFile.close();
            }
            throw ex;
        }
    }

    @Override
    public Map<String, Object> getManifestHeader() throws Exception {
        ZipEntry manifestEntry = this.m_zipFile.getEntry("META-INF/MANIFEST.MF");
        Map<String, Object> manifest = manifestEntry != null ? BundleCache.getMainAttributes((Map<String, Object>)new StringMap(), this.m_zipFile.getInputStream(manifestEntry), manifestEntry.getSize()) : null;
        return manifest;
    }

    @Override
    public Content getContent() throws Exception {
        return new JarContent(this.getLogger(), this.getConfig(), this.m_zipFactory, this, this.getRevisionRootDir(), this.m_bundleFile, this.m_zipFile);
    }

    @Override
    protected void close() throws Exception {
        this.m_zipFile.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialize(boolean byReference, InputStream is) throws Exception {
        block11: {
            try {
                if (BundleCache.getSecureAction().fileExists(this.getRevisionRootDir())) break block11;
                if (!BundleCache.getSecureAction().mkdir(this.getRevisionRootDir())) {
                    this.getLogger().log(1, this.getClass().getName() + ": Unable to create revision directory.");
                    throw new IOException("Unable to create archive directory.");
                }
                if (byReference) break block11;
                URLConnection conn = null;
                try {
                    if (is == null) {
                        URL url = BundleCache.getSecureAction().createURL(null, this.getLocation(), null);
                        conn = BundleCache.getSecureAction().openURLConnection(url);
                        String auth = BundleCache.getSecureAction().getSystemProperty("http.proxyAuth", null);
                        if (auth != null && auth.length() > 0 && ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()))) {
                            String base64 = Util.base64Encode(auth);
                            conn.setRequestProperty("Proxy-Authorization", "Basic " + base64);
                        }
                        is = BundleCache.getSecureAction().getURLConnectionInputStream(conn);
                    }
                    BundleCache.copyStreamToFile(is, this.m_bundleFile);
                }
                finally {
                    if (conn != null && conn instanceof HttpURLConnection) {
                        ((HttpURLConnection)conn).disconnect();
                    }
                }
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
}

