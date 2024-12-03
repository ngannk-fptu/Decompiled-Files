/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.JaiI18N;
import com.sun.media.jai.codec.SectorStreamSegmentMapper;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.StreamSegment;
import com.sun.media.jai.codec.StreamSegmentMapper;
import com.sun.media.jai.codec.StreamSegmentMapperImpl;
import java.io.IOException;

public class SegmentedSeekableStream
extends SeekableStream {
    private SeekableStream stream;
    private StreamSegmentMapper mapper;
    private long pointer = 0L;
    private boolean canSeekBackwards;
    private StreamSegment streamSegment = new StreamSegment();

    public SegmentedSeekableStream(SeekableStream stream, StreamSegmentMapper mapper, boolean canSeekBackwards) {
        this.stream = stream;
        this.mapper = mapper;
        this.canSeekBackwards = canSeekBackwards;
        if (canSeekBackwards && !stream.canSeekBackwards()) {
            throw new IllegalArgumentException(JaiI18N.getString("SegmentedSeekableStream0"));
        }
    }

    public SegmentedSeekableStream(SeekableStream stream, long[] segmentPositions, int[] segmentLengths, boolean canSeekBackwards) {
        this(stream, new StreamSegmentMapperImpl(segmentPositions, segmentLengths), canSeekBackwards);
    }

    public SegmentedSeekableStream(SeekableStream stream, long[] segmentPositions, int segmentLength, int totalLength, boolean canSeekBackwards) {
        this(stream, new SectorStreamSegmentMapper(segmentPositions, segmentLength, totalLength), canSeekBackwards);
    }

    public long getFilePointer() {
        return this.pointer;
    }

    public boolean canSeekBackwards() {
        return this.canSeekBackwards;
    }

    public void seek(long pos) throws IOException {
        if (pos < 0L) {
            throw new IOException();
        }
        this.pointer = pos;
    }

    public int read() throws IOException {
        this.mapper.getStreamSegment(this.pointer, 1, this.streamSegment);
        this.stream.seek(this.streamSegment.getStartPos());
        int val = this.stream.read();
        ++this.pointer;
        return val;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        this.mapper.getStreamSegment(this.pointer, len, this.streamSegment);
        this.stream.seek(this.streamSegment.getStartPos());
        int nbytes = this.stream.read(b, off, this.streamSegment.getSegmentLength());
        this.pointer += (long)nbytes;
        return nbytes;
    }
}

