/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.zip.FileUnzipper
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.util.zip.FileUnzipper;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.install.InvalidDescriptorException;
import com.atlassian.upm.core.install.PluginDescriptor;
import com.atlassian.upm.spi.PluginInstallException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarHelper {
    private static final Logger log = LoggerFactory.getLogger(JarHelper.class);
    private final File file;
    private static FilenameFilter jarFilenameFilter = (dir, name) -> name.endsWith(".jar");

    private JarHelper(File file) {
        this.file = Objects.requireNonNull(file);
    }

    public static boolean isJar(File file) {
        return JarHelper.fromFile(file).isDefined();
    }

    public static Option<JarHelper> fromFile(File file) {
        return JarHelper.withJar(file, Option.none(JarHelper.class), jar -> Option.some(new JarHelper(file)));
    }

    public File getFile() {
        return this.file;
    }

    public boolean isObr() {
        return JarHelper.withJar(this.file, false, jar -> jar.getJarEntry("obr.xml") != null);
    }

    public Option<String> getBundleSymbolicName() throws IOException {
        return JarHelper.withJar(this.file, Option.none(String.class), jar -> {
            Iterator<Manifest> iterator = Option.option(jar.getManifest()).iterator();
            if (iterator.hasNext()) {
                Manifest m = iterator.next();
                return Option.option(m.getMainAttributes().getValue("Bundle-SymbolicName"));
            }
            return Option.none();
        });
    }

    public Option<PluginDescriptor> getPluginDescriptor() throws IOException, InvalidDescriptorException {
        return JarHelper.withJar(this.file, Option.none(PluginDescriptor.class), jar -> {
            Iterator<JarEntry> iterator = Option.option(jar.getJarEntry("atlassian-plugin.xml")).iterator();
            if (iterator.hasNext()) {
                JarEntry de = iterator.next();
                InputStream is = jar.getInputStream(de);
                try {
                    Option<PluginDescriptor> option = Option.some(PluginDescriptor.fromInputStream(is));
                    return option;
                }
                finally {
                    IOUtils.closeQuietly((InputStream)is);
                }
            }
            return Option.none();
        });
    }

    public static Option<String> getPluginKeyFromJar(File pluginFile) {
        Iterator<JarHelper> iterator = JarHelper.fromFile(pluginFile).iterator();
        if (iterator.hasNext()) {
            JarHelper jar = iterator.next();
            return jar.isObr() ? JarHelper.getPluginKeyFromObr(pluginFile) : jar.getPluginKey();
        }
        return Option.none(String.class);
    }

    public static Pair<Option<String>, Option<String>> getPluginKeyAndVersionFromJar(File pluginFile) {
        Iterator<JarHelper> iterator = JarHelper.fromFile(pluginFile).iterator();
        if (iterator.hasNext()) {
            JarHelper jar = iterator.next();
            return jar.isObr() ? Pair.pair(JarHelper.getPluginKeyFromObr(pluginFile), JarHelper.getVersionFromObr(pluginFile)) : Pair.pair(jar.getPluginKey(), jar.getVersion());
        }
        return Pair.pair(Option.none(String.class), Option.none(String.class));
    }

    private Option<String> getPluginKey() {
        try {
            Iterator<PluginDescriptor> iterator = this.getPluginDescriptor().iterator();
            if (iterator.hasNext()) {
                PluginDescriptor descriptor = iterator.next();
                return descriptor.getPluginKey();
            }
            return this.getBundleSymbolicName();
        }
        catch (IOException e) {
            log.warn("Unable to get plugin key", (Throwable)e);
            return Option.none(String.class);
        }
    }

    private Option<String> getVersion() {
        try {
            Iterator<PluginDescriptor> iterator = this.getPluginDescriptor().iterator();
            if (iterator.hasNext()) {
                PluginDescriptor descriptor = iterator.next();
                return descriptor.getVersion();
            }
        }
        catch (IOException e) {
            log.warn("Unable to get plugin version", (Throwable)e);
        }
        return Option.none(String.class);
    }

    public Option<Boolean> isPrimaryApplicationPlugin() {
        try {
            Iterator<PluginDescriptor> iterator = this.getPluginDescriptor().iterator();
            if (iterator.hasNext()) {
                PluginDescriptor descriptor = iterator.next();
                return Option.some(descriptor.isApplication());
            }
        }
        catch (IOException e) {
            log.warn("Unable to get application state", (Throwable)e);
        }
        return Option.none();
    }

    private static Option<String> getPluginKeyFromObr(File obr) {
        return JarHelper.getFromObr(obr, JarHelper::getPluginKey);
    }

    private static Option<String> getVersionFromObr(File obr) {
        return JarHelper.getFromObr(obr, JarHelper::getVersion);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Option<String> getFromObr(File obr, Function<JarHelper, Option<String>> xform) {
        try {
            File obrDir = JarHelper.expandObrFile(obr);
            try {
                Option<String> option = xform.apply(Iterables.getOnlyElement(JarHelper.findJarsToInstall(obrDir)));
                return option;
            }
            catch (Exception e) {
                log.warn("Unable to get plugin key", (Throwable)e);
                return Option.none();
            }
            finally {
                try {
                    FileUtils.deleteDirectory((File)obrDir);
                }
                catch (Exception e2) {
                    log.warn("Failed to remove local OBR directory");
                }
            }
        }
        catch (Exception e3) {
            log.warn("Unable to get plugin key", (Throwable)e3);
        }
        return Option.none();
    }

    public static File expandObrFile(File plugin) throws PluginInstallException {
        try {
            File dir = File.createTempFile("obr-", plugin.getName());
            dir.delete();
            dir.mkdir();
            FileUnzipper unzipper = new FileUnzipper(plugin, dir);
            unzipper.unzip();
            return dir;
        }
        catch (IOException ioe) {
            log.error("Failed to expand OBR jar artifact", (Throwable)ioe);
            throw new PluginInstallException("Failed to expand OBR jar artifact", ioe, false);
        }
    }

    public static Iterable<JarHelper> findJarsToInstall(File obrDir) {
        return Arrays.stream(obrDir.listFiles(jarFilenameFilter)).map(JarHelper::fromFile).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> T withJar(File file, T defaultValue, WithJar<T> task) throws PluginInstallException {
        T t;
        JarFile jar = null;
        if (!file.exists() || !file.canRead()) {
            return defaultValue;
        }
        try {
            jar = new JarFile(file);
            t = task.call(jar);
        }
        catch (IOException e) {
            T t2 = defaultValue;
            return t2;
        }
        finally {
            if (jar != null) {
                try {
                    jar.close();
                }
                catch (IOException iOException) {}
            }
        }
        return t;
    }

    private static interface WithJar<T> {
        public T call(JarFile var1) throws IOException, PluginInstallException;
    }
}

