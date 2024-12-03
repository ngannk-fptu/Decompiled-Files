/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.util;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.IOException;
import java.io.Writer;

public class StringSitemeshBuffer
implements SitemeshBuffer {
    private final String buffer;

    public StringSitemeshBuffer(String buffer) {
        this.buffer = buffer;
    }

    public char[] getCharArray() {
        return this.buffer.toCharArray();
    }

    public int getBufferLength() {
        return this.buffer.length();
    }

    public int getTotalLength() {
        return this.buffer.length();
    }

    public int getTotalLength(int start, int length) {
        return length;
    }

    public void writeTo(Writer writer, int start, int length) throws IOException {
        writer.write(this.buffer, start, length);
    }

    public boolean hasFragments() {
        return false;
    }

    public static SitemeshBufferFragment createBufferFragment(String buffer) {
        return new SitemeshBufferFragment(new StringSitemeshBuffer(buffer), 0, buffer.length());
    }

    public boolean hasSecondaryStorage() {
        return false;
    }

    public SecondaryStorage getSecondaryStorage() {
        return null;
    }
}

