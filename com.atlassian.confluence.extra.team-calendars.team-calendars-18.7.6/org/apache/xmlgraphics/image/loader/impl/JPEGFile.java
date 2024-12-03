/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.impl.JPEGConstants;

public class JPEGFile
implements JPEGConstants {
    protected static final Log log = LogFactory.getLog(JPEGFile.class);
    private DataInput in;

    public JPEGFile(ImageInputStream in) {
        this.in = in;
    }

    public JPEGFile(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public DataInput getDataInput() {
        return this.in;
    }

    public int readMarkerSegment() throws IOException {
        int segID;
        int marker;
        while ((marker = this.in.readByte() & 0xFF) != 255) {
        }
        while ((segID = this.in.readByte() & 0xFF) == 255) {
        }
        return segID;
    }

    public int readSegmentLength() throws IOException {
        int reclen = this.in.readUnsignedShort();
        return reclen;
    }

    public void skipCurrentMarkerSegment() throws IOException {
        int reclen = this.readSegmentLength();
        this.in.skipBytes(reclen - 2);
    }
}

