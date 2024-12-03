/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SitemeshBufferFragment {
    private final SitemeshBuffer buffer;
    private final int start;
    private final int length;
    private final TreeMap<Integer, Integer> deletions;

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length) {
        this(buffer, start, length, new TreeMap<Integer, Integer>());
    }

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length, TreeMap<Integer, Integer> deletions) {
        this.buffer = buffer;
        this.start = start;
        this.length = length;
        this.deletions = deletions;
    }

    public void writeTo(Writer writer) throws IOException {
        int pos = this.start;
        for (Map.Entry<Integer, Integer> delete : this.deletions.entrySet()) {
            int deletePos = delete.getKey();
            if (deletePos >= pos) {
                this.buffer.writeTo(writer, pos, deletePos - pos);
            }
            pos = Math.max(deletePos + delete.getValue(), this.start);
        }
        int remain = this.start + this.length - pos;
        if (remain >= 0) {
            this.buffer.writeTo(writer, pos, remain);
        }
    }

    public int getTotalLength() {
        int total = 0;
        int pos = this.start;
        for (Map.Entry<Integer, Integer> delete : this.deletions.entrySet()) {
            int deletePos = delete.getKey();
            if (deletePos > pos) {
                total += this.buffer.getTotalLength(pos, deletePos - pos);
            }
            pos = deletePos + delete.getValue();
        }
        int remain = this.start + this.length - pos;
        if (remain > 0) {
            total += this.buffer.getTotalLength(pos, remain);
        }
        return total;
    }

    public String getStringContent() {
        StringWriter writer = new StringWriter();
        try {
            this.writeTo(writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Exception writing to buffer", e);
        }
        return writer.toString();
    }

    public String toString() {
        return "SitemeshBufferFragment{buffer=" + this.buffer.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + ", start=" + this.start + ", length=" + this.length + ", deletions=" + this.deletions + '}';
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.length;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SitemeshBufferFragment fragment) {
        return new Builder(fragment);
    }

    public static class Builder {
        private DefaultSitemeshBuffer.Builder buffer;
        private int start;
        private int length;
        private final TreeMap<Integer, Integer> deletions;
        private Integer startDelete;

        private Builder() {
            this.deletions = new TreeMap();
        }

        private Builder(SitemeshBufferFragment fragment) {
            this.buffer = DefaultSitemeshBuffer.builder(fragment.buffer);
            this.start = fragment.start;
            this.length = fragment.length;
            this.deletions = new TreeMap(fragment.deletions);
        }

        public Builder setStart(int start) {
            this.start = start;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder delete(int pos, int length) {
            this.deletions.put(pos, length);
            return this;
        }

        public Builder markStart(int pos) {
            this.start = pos;
            this.length = 0;
            return this;
        }

        public Builder end(int pos) {
            this.length = pos - this.start;
            return this;
        }

        public Builder markStartDelete(int pos) {
            if (this.startDelete != null) {
                throw new IllegalStateException("Can't nested delete...");
            }
            this.startDelete = pos;
            return this;
        }

        public Builder endDelete(int pos) {
            if (this.startDelete == null) {
                throw new IllegalStateException("Ending delete with no start delete...");
            }
            this.delete(this.startDelete, pos - this.startDelete);
            this.startDelete = null;
            return this;
        }

        public Builder insert(int position, SitemeshBufferFragment fragment) {
            this.buffer.insert(position, fragment);
            return this;
        }

        public Builder insert(int position, String fragment) {
            this.buffer.insert(position, StringSitemeshBuffer.createBufferFragment(fragment));
            return this;
        }

        public Builder setBuffer(SitemeshBuffer sitemeshBuffer) {
            this.buffer = DefaultSitemeshBuffer.builder(sitemeshBuffer);
            this.start = 0;
            this.length = sitemeshBuffer.getBufferLength();
            return this;
        }

        public SitemeshBufferFragment build() {
            return new SitemeshBufferFragment(this.buffer.build(), this.start, this.length, this.deletions);
        }
    }
}

