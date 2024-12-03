/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginArtifact$HasExtraModuleDescriptors
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.ReferenceMode
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.ReferenceMode;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JarPluginArtifact
implements PluginArtifact,
PluginArtifact.HasExtraModuleDescriptors {
    private static final Logger log = LoggerFactory.getLogger(JarPluginArtifact.class);
    private final File jarFile;
    final ReferenceMode referenceMode;

    public JarPluginArtifact(File jarFile) {
        this(jarFile, ReferenceMode.FORBID_REFERENCE);
    }

    public JarPluginArtifact(File jarFile, ReferenceMode referenceMode) {
        this.jarFile = Objects.requireNonNull(jarFile);
        this.referenceMode = referenceMode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean doesResourceExist(String name) {
        InputStream in = null;
        try {
            in = this.getResourceAsStream(name);
            boolean bl = in != null;
            return bl;
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }

    public InputStream getResourceAsStream(String fileName) {
        Objects.requireNonNull(fileName, "The file name must not be null");
        final JarFile jar = this.open();
        ZipEntry entry = jar.getEntry(fileName);
        if (entry == null) {
            this.closeJarQuietly(jar);
            return null;
        }
        try {
            return new BufferedInputStream(jar.getInputStream(entry)){

                @Override
                public void close() throws IOException {
                    super.close();
                    jar.close();
                }
            };
        }
        catch (IOException e) {
            throw new PluginParseException("Cannot retrieve " + fileName + " from plugin JAR [" + this.jarFile + "]", (Throwable)e);
        }
    }

    public String getName() {
        return this.jarFile.getName();
    }

    public String toString() {
        return this.getName();
    }

    public InputStream getInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(this.jarFile));
        }
        catch (FileNotFoundException e) {
            throw new PluginParseException("Could not open JAR file: " + this.jarFile, (Throwable)e);
        }
    }

    public File toFile() {
        return this.jarFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsJavaExecutableCode() {
        JarFile jar = this.open();
        try {
            Manifest manifest = this.getManifest(jar);
            boolean bl = this.hasBundleActivator(manifest) || this.hasSpringContext(manifest) || jar.stream().anyMatch(entry -> this.isJavaClass((ZipEntry)entry) || this.isJavaLibrary((ZipEntry)entry) || this.isSpringContext((ZipEntry)entry));
            return bl;
        }
        finally {
            this.closeJarQuietly(jar);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsSpringContext() {
        JarFile jar = this.open();
        try {
            Manifest manifest = this.getManifest(jar);
            boolean bl = this.hasSpringContext(manifest) || jar.stream().anyMatch(this::isSpringContext);
            return bl;
        }
        finally {
            this.closeJarQuietly(jar);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> extraModuleDescriptorFiles(String rootFolder) {
        JarFile jar = this.open();
        try {
            Matcher m = Pattern.compile(Pattern.quote(rootFolder) + "/[^/.]*\\.(?i)xml$").matcher("");
            Set<String> set = jar.stream().filter(e -> {
                m.reset(e.getName());
                return m.find();
            }).map(ZipEntry::getName).collect(Collectors.toSet());
            return set;
        }
        finally {
            this.closeJarQuietly(jar);
        }
    }

    public ReferenceMode getReferenceMode() {
        return this.referenceMode;
    }

    private boolean isJavaClass(ZipEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private boolean isJavaLibrary(ZipEntry entry) {
        return entry.getName().endsWith(".jar");
    }

    private boolean isSpringContext(ZipEntry entry) {
        String entryName = entry.getName();
        return entryName.startsWith("META-INF/spring/") && entryName.endsWith(".xml");
    }

    private boolean hasSpringContext(Manifest manifest) {
        return this.hasManifestEntry(manifest, "Spring-Context");
    }

    private boolean hasBundleActivator(Manifest manifest) {
        return this.hasManifestEntry(manifest, "Bundle-Activator");
    }

    private boolean hasManifestEntry(Manifest manifest, String manifestEntryName) {
        return manifest != null && manifest.getMainAttributes() != null && manifest.getMainAttributes().getValue(manifestEntryName) != null;
    }

    private JarFile open() {
        try {
            return new JarFile(this.jarFile);
        }
        catch (IOException e) {
            throw new PluginParseException("Cannot open JAR file: " + this.jarFile, (Throwable)e);
        }
    }

    private Manifest getManifest(JarFile jar) {
        try {
            return jar.getManifest();
        }
        catch (IOException e) {
            throw new PluginParseException("Cannot get manifest for JAR file: " + this.jarFile, (Throwable)e);
        }
    }

    private void closeJarQuietly(JarFile jar) {
        if (jar != null) {
            try {
                jar.close();
            }
            catch (IOException e) {
                log.debug("Exception closing jar file {}.", (Object)this.jarFile, (Object)e);
            }
        }
    }
}

