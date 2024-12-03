/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.Project;

public class DemuxInputStream
extends InputStream {
    private static final int MASK_8BIT = 255;
    private Project project;

    public DemuxInputStream(Project project) {
        this.project = project;
    }

    @Override
    public int read() throws IOException {
        byte[] buffer = new byte[1];
        if (this.project.demuxInput(buffer, 0, 1) == -1) {
            return -1;
        }
        return buffer[0] & 0xFF;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return this.project.demuxInput(buffer, offset, length);
    }
}

