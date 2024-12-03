/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.felix.framework.cache.Content;

public class ContentDirectoryContent
implements Content {
    private final Content m_content;
    private final String m_rootPath;

    public ContentDirectoryContent(Content content, String path) {
        this.m_content = content;
        this.m_rootPath = path.length() > 0 && path.charAt(path.length() - 1) != '/' ? path + "/" : path;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasEntry(String name) throws IllegalStateException {
        name = this.getName(name);
        return this.m_content.hasEntry(this.m_rootPath + name);
    }

    @Override
    public boolean isDirectory(String name) {
        name = this.getName(name);
        return this.m_content.isDirectory(this.m_rootPath + name);
    }

    @Override
    public Enumeration<String> getEntries() {
        EntriesEnumeration result = new EntriesEnumeration(this.m_content.getEntries(), this.m_rootPath);
        return result.hasMoreElements() ? result : null;
    }

    @Override
    public byte[] getEntryAsBytes(String name) throws IllegalStateException {
        name = this.getName(name);
        return this.m_content.getEntryAsBytes(this.m_rootPath + name);
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IllegalStateException, IOException {
        name = this.getName(name);
        return this.m_content.getEntryAsStream(this.m_rootPath + name);
    }

    private String getName(String name) {
        if (name.length() > 0 && name.charAt(0) == '/') {
            name = name.substring(1);
        }
        return name;
    }

    @Override
    public URL getEntryAsURL(String name) {
        return this.m_content.getEntryAsURL(this.m_rootPath + name);
    }

    @Override
    public long getContentTime(String name) {
        name = this.getName(name);
        return this.m_content.getContentTime(this.m_rootPath + name);
    }

    @Override
    public Content getEntryAsContent(String name) {
        name = this.getName(name);
        return this.m_content.getEntryAsContent(this.m_rootPath + name);
    }

    @Override
    public String getEntryAsNativeLibrary(String name) {
        name = this.getName(name);
        return this.m_content.getEntryAsNativeLibrary(this.m_rootPath + name);
    }

    public String toString() {
        return "CONTENT DIR " + this.m_rootPath + " (" + this.m_content + ")";
    }

    private static class EntriesEnumeration
    implements Enumeration {
        private final Enumeration m_enumeration;
        private final String m_rootPath;
        private String m_nextEntry = null;

        public EntriesEnumeration(Enumeration enumeration, String rootPath) {
            this.m_enumeration = enumeration;
            this.m_rootPath = rootPath;
            this.m_nextEntry = this.findNextEntry();
        }

        @Override
        public synchronized boolean hasMoreElements() {
            return this.m_nextEntry != null;
        }

        public synchronized Object nextElement() {
            if (this.m_nextEntry == null) {
                throw new NoSuchElementException("No more elements.");
            }
            String currentEntry = this.m_nextEntry;
            this.m_nextEntry = this.findNextEntry();
            return currentEntry;
        }

        private String findNextEntry() {
            if (this.m_enumeration != null) {
                while (this.m_enumeration.hasMoreElements()) {
                    String next = (String)this.m_enumeration.nextElement();
                    if (!next.startsWith(this.m_rootPath) || next.equals(this.m_rootPath)) continue;
                    return next.substring(this.m_rootPath.length());
                }
            }
            return null;
        }
    }
}

