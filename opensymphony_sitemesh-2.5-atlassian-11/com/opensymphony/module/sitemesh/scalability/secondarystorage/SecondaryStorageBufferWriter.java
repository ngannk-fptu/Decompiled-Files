/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.util.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

public class SecondaryStorageBufferWriter
extends CharArrayWriter
implements SitemeshWriter {
    private final TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap();
    private SecondaryStorage secondaryStorage;
    private long charsSoFar = 0L;
    private long memoryLimit;
    private boolean insideBody = false;
    private boolean insideTag = false;
    private boolean hasWrittenToStorage = false;
    private StringBuilder currentTag = new StringBuilder();

    public SecondaryStorageBufferWriter(SecondaryStorage secondaryStorage) {
        this(4096, secondaryStorage);
    }

    public SecondaryStorageBufferWriter(int initialBufferSize, SecondaryStorage secondaryStorage) {
        super(initialBufferSize);
        this.memoryLimit = secondaryStorage.getMemoryLimitBeforeUse();
        this.secondaryStorage = secondaryStorage;
    }

    boolean isInsideBody() {
        return this.insideBody;
    }

    boolean isHasWrittenToStorage() {
        return this.hasWrittenToStorage;
    }

    private void parseChar(int c) {
        if (this.insideBody) {
            return;
        }
        if (!this.insideTag && c == 60) {
            this.currentTag.setLength(0);
            this.insideTag = true;
            return;
        }
        if (this.insideTag) {
            if (c == 62) {
                String tag = this.makeTag(this.currentTag);
                if (tag.equals("body")) {
                    this.insideBody = true;
                }
                this.currentTag.setLength(0);
                this.insideTag = false;
                return;
            }
            this.currentTag.append((char)c);
        }
    }

    private String makeTag(StringBuilder sb) {
        char c;
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < sb.length() && !Character.isWhitespace(c = sb.charAt(i)); ++i) {
            tag.append(c);
        }
        return tag.toString().toLowerCase();
    }

    private boolean isSpillEnabled() {
        return this.memoryLimit > 0L;
    }

    private boolean weShouldSpillToStorage() {
        return this.isSpillEnabled() && this.insideBody && this.charsSoFar > this.memoryLimit;
    }

    public Writer getUnderlyingWriter() {
        return this;
    }

    public void write(int c) {
        if (!this.isSpillEnabled()) {
            super.write(c);
        } else if (this.weShouldSpillToStorage()) {
            try {
                this.secondaryStorage.write(c);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.hasWrittenToStorage = true;
        } else {
            this.parseChar(c);
            super.write(c);
        }
        ++this.charsSoFar;
    }

    public void write(char[] chars, int off, int len) {
        if (!this.isSpillEnabled()) {
            super.write(chars, off, len);
        } else if (this.weShouldSpillToStorage()) {
            try {
                this.secondaryStorage.write(chars, off, len);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.hasWrittenToStorage = true;
        } else {
            for (int i = off; i < len; ++i) {
                this.parseChar(chars[i]);
            }
            super.write(chars, off, len);
        }
        this.charsSoFar += (long)(len - off);
    }

    public void write(char[] chars) throws IOException {
        this.write(chars, 0, chars.length);
    }

    public void write(String str, int off, int len) {
        if (!this.isSpillEnabled()) {
            super.write(str, off, len);
        } else if (this.weShouldSpillToStorage()) {
            try {
                this.secondaryStorage.write(str, off, len);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.hasWrittenToStorage = true;
        } else {
            for (int i = off; i < len; ++i) {
                this.parseChar(str.charAt(i));
            }
            super.write(str, off, len);
        }
        this.charsSoFar += (long)(len - off);
    }

    public void write(String str) throws IOException {
        this.write(str, 0, str.length());
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        this.fragments.put(this.count, bufferFragment);
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        if (!this.hasWrittenToStorage) {
            return new DefaultSitemeshBuffer(this.buf, this.count, this.fragments);
        }
        return new DefaultSitemeshBuffer(this.buf, this.count, this.fragments, this.secondaryStorage);
    }
}

