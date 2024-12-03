/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DiskStoreConfiguration {
    private static final Pattern PROPERTY_SUBSTITUTION_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    private static final Logger LOG = LoggerFactory.getLogger((String)DiskStoreConfiguration.class.getName());
    private String originalPath;
    private String path;

    public final String getPath() {
        return this.path;
    }

    public static String getDefaultPath() {
        return Env.JAVA_IO_TMPDIR.substitute(Env.JAVA_IO_TMPDIR.variable);
    }

    public final DiskStoreConfiguration path(String path) {
        this.setPath(path);
        return this;
    }

    public final void setPath(String path) {
        this.originalPath = path;
        this.path = DiskStoreConfiguration.translatePath(path);
    }

    public String getOriginalPath() {
        return this.originalPath;
    }

    private static String translatePath(String path) {
        String translatedPath = DiskStoreConfiguration.substituteProperties(path);
        for (Env e : Env.values()) {
            translatedPath = e.substitute(translatedPath);
        }
        translatedPath = translatedPath.replace(File.separator + File.separator, File.separator);
        LOG.debug("Disk Store Path: " + translatedPath);
        return translatedPath;
    }

    private static String substituteProperties(String string) {
        Matcher matcher = PROPERTY_SUBSTITUTION_PATTERN.matcher(string);
        StringBuffer eval = new StringBuffer();
        while (matcher.find()) {
            String substitution = System.getProperty(matcher.group(1));
            if (substitution == null) continue;
            matcher.appendReplacement(eval, Matcher.quoteReplacement(substitution));
        }
        matcher.appendTail(eval);
        return eval.toString();
    }

    private static enum Env {
        USER_HOME("user.home"),
        USER_DIR("user.dir"),
        JAVA_IO_TMPDIR("java.io.tmpdir"),
        EHCACHE_DISK_STORE_DIR("ehcache.disk.store.dir");

        private final String variable;

        private Env(String variable) {
            this.variable = variable;
        }

        String substitute(String string) {
            String substitution = System.getProperty(this.variable);
            if (substitution == null) {
                return string;
            }
            return string.replaceFirst(Pattern.quote(this.variable), Matcher.quoteReplacement(substitution));
        }
    }
}

