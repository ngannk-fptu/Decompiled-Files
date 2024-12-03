/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.Maven;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.filelock.DirectoryLock;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MavenEntry
implements Closeable {
    final Maven maven;
    final File root;
    final File dir;
    final String path;
    final DirectoryLock lock;
    final Map<URI, CachedPom> poms = new HashMap<URI, CachedPom>();
    final File pomFile;
    final File artifactFile;
    final String pomPath;
    final File propertiesFile;
    UTF8Properties properties;
    private boolean propertiesChanged;
    FutureTask<File> artifact;
    String artifactPath;

    MavenEntry(Maven maven, String path) {
        this.root = maven.repository;
        this.maven = maven;
        this.path = path;
        this.pomPath = path + ".pom";
        this.artifactPath = path + ".jar";
        this.dir = IO.getFile(maven.repository, path).getParentFile();
        try {
            IO.mkdirs(this.dir);
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        this.pomFile = new File(maven.repository, this.pomPath);
        this.artifactFile = new File(maven.repository, this.artifactPath);
        this.propertiesFile = new File(this.dir, "bnd.properties");
        this.lock = new DirectoryLock(this.dir, 300000L);
    }

    public File getArtifactFile() {
        return this.artifactFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CachedPom getPom(URI[] urls) throws Exception {
        MavenEntry mavenEntry = this;
        synchronized (mavenEntry) {
            for (URI url : urls) {
                CachedPom pom = this.poms.get(url);
                if (pom == null) continue;
                return pom;
            }
        }
        try {
            CachedPom cachedPom;
            if (this.isValid()) {
                for (URI url : urls) {
                    String valid = this.getProperty(url.toASCIIString());
                    if (valid == null) continue;
                    CachedPom cachedPom2 = this.createPom(url);
                    return cachedPom2;
                }
                for (URI url : urls) {
                    if (!this.verify(url, this.pomPath)) continue;
                    cachedPom = this.createPom(url);
                    return cachedPom;
                }
            } else {
                IO.mkdirs(this.dir);
                for (final URI url : urls) {
                    if (!this.download(url, this.pomPath) || !this.verify(url, this.pomPath)) continue;
                    this.artifact = new FutureTask<File>(new Callable<File>(){

                        @Override
                        public File call() throws Exception {
                            if (MavenEntry.this.download(url, MavenEntry.this.artifactPath)) {
                                MavenEntry.this.verify(url, MavenEntry.this.artifactPath);
                            }
                            return MavenEntry.this.artifactFile;
                        }
                    });
                    this.maven.executor.execute(this.artifact);
                    cachedPom = this.createPom(url);
                    return cachedPom;
                }
            }
            mavenEntry = null;
            return mavenEntry;
        }
        finally {
            this.saveProperties();
        }
    }

    boolean download(URI repo, String path) throws MalformedURLException {
        try {
            URL url = this.toURL(repo, path);
            System.err.println("Downloading " + repo + " path " + path + " url " + url);
            File file = new File(this.root, path);
            IO.copy(url.openStream(), file);
            System.err.println("Downloaded " + url);
            return true;
        }
        catch (Exception e) {
            System.err.println("debug: " + e);
            return false;
        }
    }

    URL toURL(URI base, String path) throws MalformedURLException {
        StringBuilder r = new StringBuilder();
        r.append(base.toString());
        if (r.charAt(r.length() - 1) != '/') {
            r.append('/');
        }
        r.append(path);
        return new URL(r.toString());
    }

    private boolean isValid() {
        return this.pomFile.isFile() && this.pomFile.length() > 100L && this.artifactFile.isFile() && this.artifactFile.length() > 100L;
    }

    private void setProperty(String key, String value) {
        Properties properties = this.getProperties();
        properties.setProperty(key, value);
        this.propertiesChanged = true;
    }

    protected Properties getProperties() {
        if (this.properties == null) {
            this.properties = new UTF8Properties();
            File props = new File(this.dir, "bnd.properties");
            if (props.exists()) {
                try {
                    this.properties.load(props, null);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return this.properties;
    }

    private String getProperty(String key) {
        Properties properties = this.getProperties();
        return properties.getProperty(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveProperties() throws IOException {
        if (this.propertiesChanged) {
            try (OutputStreamWriter osw = new OutputStreamWriter(IO.outputStream(this.propertiesFile));){
                this.properties.store(osw, "");
            }
            finally {
                this.properties = null;
                this.propertiesChanged = false;
            }
        }
    }

    private CachedPom createPom(URI url) throws Exception {
        CachedPom pom = new CachedPom(this, url);
        pom.parse();
        this.poms.put(url, pom);
        this.setProperty(url.toASCIIString(), "true");
        return pom;
    }

    boolean verify(URI repo, String path) throws Exception {
        for (String algorithm : Maven.ALGORITHMS) {
            if (!this.verify(repo, path, algorithm)) continue;
            return true;
        }
        return false;
    }

    private boolean verify(URI repo, String path, String algorithm) throws Exception {
        String digestPath = path + "." + algorithm;
        File actualFile = new File(this.root, path);
        if (this.download(repo, digestPath)) {
            File digestFile = new File(this.root, digestPath);
            MessageDigest md = MessageDigest.getInstance(algorithm);
            IO.copy(actualFile, md);
            byte[] digest = md.digest();
            String source = IO.collect(digestFile).toUpperCase();
            String hex = Hex.toHexString(digest).toUpperCase();
            if (source.startsWith(hex)) {
                System.err.println("Verified ok " + actualFile + " digest " + algorithm);
                return true;
            }
        }
        System.err.println("Failed to verify " + actualFile + " for digest " + algorithm);
        return false;
    }

    public File getArtifact() throws Exception {
        if (this.artifact == null) {
            return this.artifactFile;
        }
        return this.artifact.get();
    }

    public File getPomFile() {
        return this.pomFile;
    }

    @Override
    public void close() throws IOException {
    }

    public void remove() {
        if (this.dir.getParentFile() != null) {
            IO.delete(this.dir);
        }
    }
}

