/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultSitemeshBuffer
implements SitemeshBuffer {
    private final char[] buffer;
    private final int length;
    private final TreeMap<Integer, SitemeshBufferFragment> bufferFragments;
    private final SecondaryStorage secondaryStorage;

    public DefaultSitemeshBuffer(char[] buffer) {
        this(buffer, buffer.length);
    }

    public DefaultSitemeshBuffer(char[] buffer, int length) {
        this(buffer, length, new TreeMap<Integer, SitemeshBufferFragment>());
    }

    public DefaultSitemeshBuffer(char[] buffer, int length, TreeMap<Integer, SitemeshBufferFragment> bufferFragments) {
        this(buffer, length, bufferFragments, null);
    }

    public DefaultSitemeshBuffer(char[] buffer, int length, TreeMap<Integer, SitemeshBufferFragment> bufferFragments, SecondaryStorage secondaryStorage) {
        this.buffer = buffer;
        this.length = length;
        this.bufferFragments = bufferFragments;
        this.secondaryStorage = secondaryStorage;
    }

    @Override
    public void writeTo(Writer writer, int start, int length) throws IOException {
        int pos = start;
        for (Map.Entry<Integer, SitemeshBufferFragment> entry : this.bufferFragments.entrySet()) {
            int fragmentPosition = entry.getKey();
            if (fragmentPosition < pos) continue;
            if (fragmentPosition > start + length) break;
            writer.write(this.buffer, pos, fragmentPosition - pos);
            entry.getValue().writeTo(writer);
            pos = fragmentPosition;
        }
        if (pos < start + length) {
            writer.write(this.buffer, pos, start + length - pos);
        }
    }

    @Override
    public int getTotalLength() {
        return this.getTotalLength(0, this.length);
    }

    @Override
    public int getTotalLength(int start, int length) {
        int total = length;
        for (Map.Entry<Integer, SitemeshBufferFragment> entry : this.bufferFragments.entrySet()) {
            int fragmentPosition = entry.getKey();
            if (fragmentPosition < start) continue;
            if (fragmentPosition > start + length) break;
            total += entry.getValue().getTotalLength();
        }
        return total;
    }

    @Override
    public int getBufferLength() {
        return this.length;
    }

    @Override
    public char[] getCharArray() {
        return this.buffer;
    }

    @Override
    public boolean hasFragments() {
        return !this.bufferFragments.isEmpty();
    }

    @Override
    public boolean hasSecondaryStorage() {
        return this.secondaryStorage != null;
    }

    @Override
    public SecondaryStorage getSecondaryStorage() {
        return this.secondaryStorage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SitemeshBuffer sitemeshBuffer) {
        if (sitemeshBuffer instanceof DefaultSitemeshBuffer) {
            return new Builder((DefaultSitemeshBuffer)sitemeshBuffer);
        }
        return new Builder(sitemeshBuffer);
    }

    public static class Builder {
        private char[] buffer;
        private int length;
        private final TreeMap<Integer, SitemeshBufferFragment> fragments;

        private Builder() {
            this.fragments = new TreeMap();
        }

        private Builder(DefaultSitemeshBuffer buffer) {
            this.buffer = buffer.buffer;
            this.length = buffer.length;
            this.fragments = new TreeMap(buffer.bufferFragments);
        }

        private Builder(SitemeshBuffer buffer) {
            this.buffer = buffer.getCharArray();
            this.length = buffer.getBufferLength();
            this.fragments = new TreeMap();
        }

        public Builder setBuffer(char[] buffer) {
            this.buffer = buffer;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder insert(int position, SitemeshBufferFragment fragment) {
            this.fragments.put(position, fragment);
            return this;
        }

        public SitemeshBuffer build() {
            return new DefaultSitemeshBuffer(this.buffer, this.length, this.fragments);
        }
    }
}

