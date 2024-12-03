/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.archive.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.hibernate.boot.archive.internal.ExplodedArchiveDescriptor;
import org.hibernate.boot.archive.internal.JarFileBasedArchiveDescriptor;
import org.hibernate.boot.archive.internal.JarInputStreamBasedArchiveDescriptor;
import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.archive.spi.JarFileEntryUrlAdjuster;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public class StandardArchiveDescriptorFactory
implements ArchiveDescriptorFactory,
JarFileEntryUrlAdjuster {
    private static final Logger log = Logger.getLogger(StandardArchiveDescriptorFactory.class);
    public static final StandardArchiveDescriptorFactory INSTANCE = new StandardArchiveDescriptorFactory();

    @Override
    public ArchiveDescriptor buildArchiveDescriptor(URL url) {
        return this.buildArchiveDescriptor(url, "");
    }

    @Override
    public ArchiveDescriptor buildArchiveDescriptor(URL url, String entry) {
        String protocol = url.getProtocol();
        if ("jar".equals(protocol)) {
            return new JarProtocolArchiveDescriptor(this, url, entry);
        }
        if (StringHelper.isEmpty(protocol) || "file".equals(protocol) || "vfszip".equals(protocol) || "vfsfile".equals(protocol)) {
            File file = new File(this.extractLocalFilePath(url));
            if (file.isDirectory()) {
                return new ExplodedArchiveDescriptor(this, url, entry);
            }
            return new JarFileBasedArchiveDescriptor(this, url, entry);
        }
        return new JarInputStreamBasedArchiveDescriptor(this, url, entry);
    }

    protected String extractLocalFilePath(URL url) {
        String filePart = url.getFile();
        if (filePart != null && filePart.indexOf(32) != -1) {
            return filePart;
        }
        try {
            return url.toURI().getSchemeSpecificPart();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Unable to visit JAR " + url + ". Cause: " + e.getMessage(), e);
        }
    }

    @Override
    public URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException {
        return ArchiveHelper.getJarURLFromURLEntry(url, entry);
    }

    @Override
    public URL getURLFromPath(String jarPath) {
        return ArchiveHelper.getURLFromPath(jarPath);
    }

    @Override
    public URL adjustJarFileEntryUrl(URL url, URL rootUrl) {
        block6: {
            boolean check;
            String protocol = url.getProtocol();
            boolean bl = check = StringHelper.isEmpty(protocol) || "file".equals(protocol) || "vfszip".equals(protocol) || "vfsfile".equals(protocol);
            if (!check) {
                return url;
            }
            String filePart = this.extractLocalFilePath(url);
            if (filePart.startsWith("/") || new File(url.getFile()).isAbsolute()) {
                return url;
            }
            File rootUrlFile = new File(this.extractLocalFilePath(rootUrl));
            try {
                if (rootUrlFile.isDirectory()) {
                    File combined = new File(rootUrlFile, filePart);
                    if (combined.exists()) {
                        return combined.toURI().toURL();
                    }
                    break block6;
                }
                return new URL("jar:" + protocol + "://" + rootUrlFile.getAbsolutePath() + "!/" + filePart);
            }
            catch (MalformedURLException e) {
                log.debugf((Throwable)e, "Unable to adjust relative <jar-file/> URL [%s] relative to root URL [%s]", (Object)filePart, (Object)rootUrlFile.getAbsolutePath());
            }
        }
        return url;
    }
}

