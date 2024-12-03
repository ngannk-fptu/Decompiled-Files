/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.JarContent;
import org.osgi.framework.PackagePermission;

public class BundleProtectionDomain
extends ProtectionDomain {
    private final WeakReference<BundleRevisionImpl> m_revision;
    private final int m_hashCode;
    private final String m_toString;
    private volatile PermissionCollection m_woven;

    BundleProtectionDomain(BundleRevisionImpl revision, Object certificates) throws MalformedURLException {
        super(new CodeSource(RevisionAsJarURL.create(revision), (Certificate[])certificates), null, null, null);
        this.m_revision = new WeakReference<BundleRevisionImpl>(revision);
        this.m_hashCode = revision.hashCode();
        this.m_toString = "[" + revision + "]";
    }

    BundleRevisionImpl getRevision() {
        return (BundleRevisionImpl)this.m_revision.get();
    }

    @Override
    public boolean implies(Permission permission) {
        Felix felix = this.getFramework();
        return felix != null && felix.impliesBundlePermission(this, permission, false);
    }

    boolean superImplies(Permission permission) {
        return super.implies(permission);
    }

    public boolean impliesDirect(Permission permission) {
        Felix felix = this.getFramework();
        return felix != null && felix.impliesBundlePermission(this, permission, true);
    }

    boolean impliesWoven(Permission permission) {
        return this.m_woven != null && this.m_woven.implies(permission);
    }

    synchronized void addWoven(String s) {
        if (this.m_woven == null) {
            this.m_woven = new Permissions();
        }
        this.m_woven.add(new PackagePermission(s, "import"));
    }

    BundleImpl getBundle() {
        BundleRevisionImpl revision = (BundleRevisionImpl)this.m_revision.get();
        return revision != null ? revision.getBundle() : null;
    }

    Felix getFramework() {
        BundleRevisionImpl revision = (BundleRevisionImpl)this.m_revision.get();
        return revision != null ? revision.getBundle().getFramework() : null;
    }

    public int hashCode() {
        return this.m_hashCode;
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != BundleProtectionDomain.class) {
            return false;
        }
        if (this.m_hashCode != other.hashCode()) {
            return false;
        }
        return this.m_revision.get() == ((BundleProtectionDomain)other).m_revision.get();
    }

    @Override
    public String toString() {
        return this.m_toString;
    }

    private static final class RevisionAsJarURL
    extends URLStreamHandler {
        private final WeakReference m_revision;
        private volatile URL url;

        private RevisionAsJarURL(BundleRevisionImpl revision) {
            this.m_revision = new WeakReference<BundleRevisionImpl>(revision);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            if (this.url != null) {
                return this.url.openConnection();
            }
            BundleRevisionImpl revision = (BundleRevisionImpl)this.m_revision.get();
            if (revision != null) {
                File target;
                block24: {
                    Content content = revision.getContent();
                    if (content instanceof JarContent) {
                        target = ((JarContent)content).getFile();
                    } else {
                        target = Felix.m_secureAction.createTempFile("jar", null, null);
                        Felix.m_secureAction.deleteFileOnExit(target);
                        OutputStream output = null;
                        BundleInputStream input = null;
                        IOException rethrow = null;
                        try {
                            output = Felix.m_secureAction.getOutputStream(target);
                            input = new BundleInputStream(content);
                            byte[] buffer = new byte[65536];
                            int i = input.read(buffer);
                            while (i != -1) {
                                output.write(buffer, 0, i);
                                i = input.read(buffer);
                            }
                        }
                        catch (IOException ex) {
                            rethrow = ex;
                            return rethrow;
                        }
                        finally {
                            block26: {
                                block25: {
                                    if (output != null) {
                                        try {
                                            output.close();
                                        }
                                        catch (IOException ex) {
                                            if (rethrow != null) break block25;
                                            rethrow = ex;
                                        }
                                    }
                                }
                                if (input != null) {
                                    try {
                                        input.close();
                                    }
                                    catch (IOException ex) {
                                        if (rethrow != null) break block26;
                                        rethrow = ex;
                                    }
                                }
                            }
                            if (rethrow == null) break block24;
                            throw rethrow;
                        }
                    }
                }
                this.url = new URL("jar:" + target.toURI().toURL() + "!/");
                return this.url.openConnection();
            }
            throw new IOException("Unable to access bundle revision.");
        }

        private static boolean getUseCachedURL(final BundleRevisionImpl revision) {
            String property = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<String>(){

                @Override
                public String run() {
                    return RevisionAsJarURL.getUseCachedURLProperty(revision);
                }
            }) : RevisionAsJarURL.getUseCachedURLProperty(revision);
            return Boolean.parseBoolean(property);
        }

        private static String getUseCachedURLProperty(BundleRevisionImpl revision) {
            return revision.getBundle().getFramework().getProperty("felix.bundlecodesource.usecachedurls");
        }

        private static URL create(BundleRevisionImpl revision) throws MalformedURLException {
            RevisionAsJarURL handler = new RevisionAsJarURL(revision);
            boolean useCachedUrlForCodeSource = RevisionAsJarURL.getUseCachedURL(revision);
            if (useCachedUrlForCodeSource) {
                String location = "jar:" + revision.getEntry("/") + "!/";
                return Felix.m_secureAction.createURL(Felix.m_secureAction.createURL(null, "jar:", handler), location, handler);
            }
            String location = revision.getBundle()._getLocation();
            if (location.startsWith("reference:")) {
                location = location.substring("reference:".length());
            }
            try {
                return Felix.m_secureAction.createURL(Felix.m_secureAction.createURL(null, "jar:", handler), location, handler);
            }
            catch (MalformedURLException ex) {
                location = "jar:" + revision.getEntry("/") + "!/";
                return Felix.m_secureAction.createURL(Felix.m_secureAction.createURL(null, "jar:", handler), location, handler);
            }
        }
    }

    private static final class OutputStreamBuffer
    extends OutputStream {
        ByteArrayOutputStream m_outBuffer = null;

        private OutputStreamBuffer() {
        }

        @Override
        public void write(int b) {
            this.m_outBuffer.write(b);
        }

        @Override
        public void write(byte[] buffer) throws IOException {
            this.m_outBuffer.write(buffer);
        }

        @Override
        public void write(byte[] buffer, int offset, int length) {
            this.m_outBuffer.write(buffer, offset, length);
        }
    }

    private static final class BundleInputStream
    extends InputStream {
        private final Content m_root;
        private final Enumeration m_content;
        private final OutputStreamBuffer m_outputBuffer = new OutputStreamBuffer();
        private ByteArrayInputStream m_buffer = null;
        private JarOutputStream m_output = null;
        private static final String DUMMY_ENTRY = "__DUMMY-ENTRY__/";

        public BundleInputStream(Content root) throws IOException {
            this.m_root = root;
            ArrayList<String> entries = new ArrayList<String>();
            int count = 0;
            String manifest = null;
            Enumeration<String> e = this.m_root.getEntries();
            while (e != null && e.hasMoreElements()) {
                String entry = e.nextElement();
                if (entry.endsWith("/")) continue;
                if (entry.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    if (manifest != null) continue;
                    manifest = entry;
                    continue;
                }
                if (entry.toUpperCase().startsWith("META-INF/") && entry.indexOf(47, "META-INF/".length()) < 0) {
                    entries.add(count++, entry);
                    continue;
                }
                entries.add(entry);
            }
            entries.add(count++, DUMMY_ENTRY);
            if (manifest == null) {
                manifest = "META-INF/MANIFEST.MF";
            }
            this.m_content = Collections.enumeration(entries);
            this.m_output = new JarOutputStream(this.m_outputBuffer);
            this.readNext(manifest);
            this.m_buffer = new ByteArrayInputStream(this.m_outputBuffer.m_outBuffer.toByteArray());
            this.m_outputBuffer.m_outBuffer = null;
        }

        @Override
        public int read() throws IOException {
            if (this.m_output == null && this.m_buffer == null) {
                return -1;
            }
            if (this.m_buffer != null) {
                int result = this.m_buffer.read();
                if (result == -1) {
                    this.m_buffer = null;
                    return this.read();
                }
                return result;
            }
            if (this.m_content.hasMoreElements()) {
                String current = (String)this.m_content.nextElement();
                this.readNext(current);
                if (!this.m_content.hasMoreElements()) {
                    this.m_output.close();
                    this.m_output = null;
                }
                this.m_buffer = new ByteArrayInputStream(this.m_outputBuffer.m_outBuffer.toByteArray());
                this.m_outputBuffer.m_outBuffer = null;
            } else {
                this.m_output.close();
                this.m_output = null;
            }
            return this.read();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void readNext(String path) throws IOException {
            this.m_outputBuffer.m_outBuffer = new ByteArrayOutputStream();
            if (path == DUMMY_ENTRY) {
                JarEntry entry = new JarEntry(path);
                this.m_output.putNextEntry(entry);
            } else {
                InputStream in = null;
                try {
                    in = this.m_root.getEntryAsStream(path);
                    if (in == null) {
                        throw new IOException("Missing entry");
                    }
                    JarEntry entry = new JarEntry(path);
                    this.m_output.putNextEntry(entry);
                    byte[] buffer = new byte[4096];
                    int c = in.read(buffer);
                    while (c != -1) {
                        this.m_output.write(buffer, 0, c);
                        c = in.read(buffer);
                    }
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Exception exception) {}
                    }
                }
            }
            this.m_output.closeEntry();
        }
    }
}

