/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Version;

public class MultiReleaseContent
implements Content {
    private final Content m_content;
    private final int m_javaVersion;

    MultiReleaseContent(int javaVersion, Content content) {
        this.m_javaVersion = javaVersion;
        this.m_content = content;
    }

    public static Content wrap(String javaVersionString, Content content) {
        int javaVersion = 8;
        try {
            javaVersion = Version.parseVersion(javaVersionString).getMajor();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (javaVersion > 8) {
            try {
                Map<String, Object> versionManifest;
                byte[] versionManifestInput = content.getEntryAsBytes("META-INF/MANIFEST.MF");
                if (versionManifestInput != null && "true".equals((versionManifest = BundleCache.getMainAttributes((Map<String, Object>)new StringMap(), (InputStream)new ByteArrayInputStream(versionManifestInput), versionManifestInput.length)).get("Multi-Release"))) {
                    content = new MultiReleaseContent(javaVersion, content);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return content;
    }

    @Override
    public void close() {
        this.m_content.close();
    }

    @Override
    public boolean hasEntry(String name) {
        return this.m_content.hasEntry(this.findPath(name));
    }

    @Override
    public boolean isDirectory(String name) {
        return this.m_content.isDirectory(this.findPath(name));
    }

    @Override
    public Enumeration<String> getEntries() {
        Enumeration<String> entries = this.m_content.getEntries();
        if (entries != null) {
            LinkedHashSet<String> result = new LinkedHashSet<String>();
            while (entries.hasMoreElements()) {
                int version;
                int idx;
                String path = entries.nextElement();
                result.add(path);
                String internalPath = path;
                while (internalPath.startsWith("/")) {
                    internalPath = internalPath.substring(1);
                }
                if (!internalPath.startsWith("META-INF/versions/") || (idx = internalPath.indexOf(47, "META-INF/versions/".length())) == -1 || idx + 1 >= internalPath.length() || (version = Version.parseVersion(internalPath.substring("META-INF/versions/".length(), idx)).getMajor()) <= 8 || version > this.m_javaVersion || (internalPath = internalPath.substring(idx + 1)).startsWith("META-INF/")) continue;
                result.add(internalPath);
            }
            return Collections.enumeration(result);
        }
        return entries;
    }

    @Override
    public byte[] getEntryAsBytes(String name) {
        return this.m_content.getEntryAsBytes(this.findPath(name));
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IOException {
        return this.m_content.getEntryAsStream(this.findPath(name));
    }

    @Override
    public Content getEntryAsContent(String name) {
        return this.m_content.getEntryAsContent(this.findPath(name));
    }

    @Override
    public String getEntryAsNativeLibrary(String name) {
        return this.m_content.getEntryAsNativeLibrary(this.findPath(name));
    }

    @Override
    public URL getEntryAsURL(String name) {
        return this.m_content.getEntryAsURL(this.findPath(name));
    }

    @Override
    public long getContentTime(String name) {
        return this.m_content.getContentTime(this.findPath(name));
    }

    private String findPath(String path) {
        String internalPath = path;
        while (internalPath.startsWith("/")) {
            internalPath = internalPath.substring(1);
        }
        if (!internalPath.startsWith("META-INF/")) {
            int version = this.m_javaVersion;
            while (version >= 9) {
                String versionPath = "META-INF/versions/" + version-- + "/" + internalPath;
                if (!this.m_content.hasEntry(versionPath)) continue;
                return versionPath;
            }
        }
        return path;
    }
}

