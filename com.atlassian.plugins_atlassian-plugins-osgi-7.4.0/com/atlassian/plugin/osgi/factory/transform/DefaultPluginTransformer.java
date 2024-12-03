/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.JarPluginArtifact
 *  com.atlassian.plugin.PluginArtifact
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformer;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.model.SystemExports;
import com.atlassian.plugin.osgi.factory.transform.stage.AddBundleOverridesStage;
import com.atlassian.plugin.osgi.factory.transform.stage.ComponentImportSpringStage;
import com.atlassian.plugin.osgi.factory.transform.stage.ComponentSpringStage;
import com.atlassian.plugin.osgi.factory.transform.stage.GenerateManifestStage;
import com.atlassian.plugin.osgi.factory.transform.stage.HostComponentSpringStage;
import com.atlassian.plugin.osgi.factory.transform.stage.ModuleTypeSpringStage;
import com.atlassian.plugin.osgi.factory.transform.stage.ScanDescriptorForHostClassesStage;
import com.atlassian.plugin.osgi.factory.transform.stage.ScanInnerJarsStage;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPluginTransformer
implements PluginTransformer {
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginTransformer.class);
    public static final String TRANSFORM_COMPRESSION_LEVEL = "atlassian.plugins.plugin.transformer.compression";
    private final String pluginDescriptorPath;
    private final List<TransformStage> stages;
    private final File bundleCacheDir;
    private final SystemExports systemExports;
    private final Set<Application> applications;
    private final OsgiContainerManager osgiContainerManager;

    public static ArrayList<TransformStage> getDefaultTransformStages() {
        return new ArrayList<TransformStage>(Arrays.asList(new AddBundleOverridesStage(), new ScanInnerJarsStage(), new ComponentImportSpringStage(), new ComponentSpringStage(), new ScanDescriptorForHostClassesStage(), new ModuleTypeSpringStage(), new HostComponentSpringStage(), new GenerateManifestStage()));
    }

    public DefaultPluginTransformer(OsgiPersistentCache cache, SystemExports systemExports, Set<Application> applications, String pluginDescriptorPath, OsgiContainerManager osgiContainerManager) {
        this(cache, systemExports, applications, pluginDescriptorPath, osgiContainerManager, DefaultPluginTransformer.getDefaultTransformStages());
    }

    public DefaultPluginTransformer(OsgiPersistentCache cache, SystemExports systemExports, Set<Application> applications, String pluginDescriptorPath, OsgiContainerManager osgiContainerManager, List<TransformStage> stages) {
        this.pluginDescriptorPath = (String)Preconditions.checkNotNull((Object)pluginDescriptorPath, (Object)"The plugin descriptor path is required");
        this.osgiContainerManager = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgiContainerManager);
        this.stages = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(stages, (Object)"A list of stages is required")));
        this.bundleCacheDir = ((OsgiPersistentCache)Preconditions.checkNotNull((Object)cache)).getTransformedPluginCache();
        this.systemExports = systemExports;
        this.applications = applications;
    }

    public File transform(File pluginJar, List<HostComponentRegistration> regs) {
        return this.transform((PluginArtifact)new JarPluginArtifact(pluginJar), regs);
    }

    @Override
    public File transform(PluginArtifact pluginArtifact, List<HostComponentRegistration> regs) {
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin artifact is required");
        Preconditions.checkNotNull(regs, (Object)"The host component registrations are required");
        File artifactFile = pluginArtifact.toFile();
        File cachedPlugin = this.getFromCache(artifactFile);
        if (cachedPlugin != null) {
            return cachedPlugin;
        }
        TransformContext context = new TransformContext(regs, this.systemExports, pluginArtifact, this.applications, this.pluginDescriptorPath, this.osgiContainerManager);
        for (TransformStage stage : this.stages) {
            stage.execute(context);
        }
        try {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Overriding files in ").append(pluginArtifact.toString()).append(":\n");
                for (Map.Entry<String, byte[]> entry : context.getFileOverrides().entrySet()) {
                    sb.append("==").append(entry.getKey()).append("==\n");
                    sb.append(new String(entry.getValue()));
                }
                log.debug(sb.toString());
            }
            return this.addFilesToExistingZip(artifactFile, context.getFileOverrides());
        }
        catch (IOException e) {
            throw new PluginTransformationException("Unable to add files to plugin jar", e);
        }
    }

    private File getFromCache(File artifact) {
        String name = DefaultPluginTransformer.generateCacheName(artifact);
        File[] files = this.bundleCacheDir.listFiles();
        if (files == null) {
            return null;
        }
        for (File child : files) {
            if (!child.getName().equals(name)) continue;
            return child;
        }
        return null;
    }

    static String generateCacheName(File file) {
        int dotPos = file.getName().lastIndexOf(46);
        if (dotPos > 0 && file.getName().length() - 1 > dotPos) {
            return file.getName().substring(0, dotPos) + "_" + file.lastModified() + file.getName().substring(dotPos);
        }
        return file.getName() + "_" + file.lastModified();
    }

    File addFilesToExistingZip(File zipFile, Map<String, byte[]> files) throws IOException {
        File tempFile = new File(this.bundleCacheDir, DefaultPluginTransformer.generateCacheName(zipFile));
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));){
            int requestedCompressionLevel = Integer.getInteger(TRANSFORM_COMPRESSION_LEVEL, 0);
            int clampedCompressionLevel = Math.max(0, Math.min(requestedCompressionLevel, 9));
            out.setLevel(clampedCompressionLevel);
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                if (!files.containsKey(name)) {
                    ZipEntry newEntry = new ZipEntry(name);
                    newEntry.setTime(entry.getTime());
                    out.putNextEntry(newEntry);
                    IOUtils.copyLarge((InputStream)zin, (OutputStream)out);
                }
                entry = zin.getNextEntry();
            }
            TreeMap<String, byte[]> sortedFiles = new TreeMap<String, byte[]>(files);
            for (Map.Entry fentry : sortedFiles.entrySet()) {
                ByteArrayInputStream in = new ByteArrayInputStream((byte[])fentry.getValue());
                Throwable throwable = null;
                try {
                    ZipEntry newEntry = new ZipEntry((String)fentry.getKey());
                    newEntry.setTime(zipFile.lastModified());
                    out.putNextEntry(newEntry);
                    IOUtils.copyLarge((InputStream)in, (OutputStream)out);
                    out.closeEntry();
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (in == null) continue;
                    if (throwable != null) {
                        try {
                            ((InputStream)in).close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    ((InputStream)in).close();
                }
            }
        }
        return tempFile;
    }
}

