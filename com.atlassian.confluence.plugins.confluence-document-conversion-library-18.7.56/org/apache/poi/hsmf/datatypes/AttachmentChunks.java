/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.DirectoryChunk;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.StringChunk;

public class AttachmentChunks
implements ChunkGroup {
    private static final Logger LOG = LogManager.getLogger(AttachmentChunks.class);
    public static final String PREFIX = "__attach_version1.0_#";
    private ByteChunk attachData;
    private StringChunk attachExtension;
    private StringChunk attachFileName;
    private StringChunk attachLongFileName;
    private StringChunk attachMimeTag;
    private DirectoryChunk attachmentDirectory;
    private StringChunk attachContentId;
    private ByteChunk attachRenderingWMF;
    private final String poifsName;
    private final List<Chunk> allChunks = new ArrayList<Chunk>();

    public AttachmentChunks(String poifsName) {
        this.poifsName = poifsName;
    }

    public boolean isEmbeddedMessage() {
        return this.attachmentDirectory != null;
    }

    public MAPIMessage getEmbeddedMessage() throws IOException {
        if (this.attachmentDirectory != null) {
            return this.attachmentDirectory.getAsEmbeddedMessage();
        }
        return null;
    }

    public byte[] getEmbeddedAttachmentObject() {
        if (this.attachData != null) {
            return this.attachData.getValue();
        }
        return null;
    }

    public Chunk[] getAll() {
        return this.allChunks.toArray(new Chunk[0]);
    }

    @Override
    public Chunk[] getChunks() {
        return this.getAll();
    }

    public String getPOIFSName() {
        return this.poifsName;
    }

    public ByteChunk getAttachData() {
        return this.attachData;
    }

    public StringChunk getAttachExtension() {
        return this.attachExtension;
    }

    public StringChunk getAttachFileName() {
        return this.attachFileName;
    }

    public StringChunk getAttachLongFileName() {
        return this.attachLongFileName;
    }

    public StringChunk getAttachMimeTag() {
        return this.attachMimeTag;
    }

    public DirectoryChunk getAttachmentDirectory() {
        return this.attachmentDirectory;
    }

    public ByteChunk getAttachRenderingWMF() {
        return this.attachRenderingWMF;
    }

    public StringChunk getAttachContentId() {
        return this.attachContentId;
    }

    @Override
    public void record(Chunk chunk) {
        int chunkId = chunk.getChunkId();
        if (chunkId == MAPIProperty.ATTACH_DATA.id) {
            if (chunk instanceof ByteChunk) {
                this.attachData = (ByteChunk)chunk;
            } else if (chunk instanceof DirectoryChunk) {
                this.attachmentDirectory = (DirectoryChunk)chunk;
            } else {
                LOG.atError().log("Unexpected data chunk of type {}", (Object)chunk.getEntryName());
            }
        } else if (chunkId == MAPIProperty.ATTACH_EXTENSION.id) {
            this.attachExtension = (StringChunk)chunk;
        } else if (chunkId == MAPIProperty.ATTACH_FILENAME.id) {
            this.attachFileName = (StringChunk)chunk;
        } else if (chunkId == MAPIProperty.ATTACH_LONG_FILENAME.id) {
            this.attachLongFileName = (StringChunk)chunk;
        } else if (chunkId == MAPIProperty.ATTACH_MIME_TAG.id) {
            this.attachMimeTag = (StringChunk)chunk;
        } else if (chunkId == MAPIProperty.ATTACH_RENDERING.id) {
            this.attachRenderingWMF = (ByteChunk)chunk;
        } else if (chunkId == MAPIProperty.ATTACH_CONTENT_ID.id) {
            this.attachContentId = (StringChunk)chunk;
        } else {
            LOG.atWarn().log("Currently unsupported attachment chunk property will be ignored. {}", (Object)chunk.getEntryName());
        }
        this.allChunks.add(chunk);
    }

    @Override
    public void chunksComplete() {
    }

    public static class AttachmentChunksSorter
    implements Comparator<AttachmentChunks>,
    Serializable {
        @Override
        public int compare(AttachmentChunks a, AttachmentChunks b) {
            return a.poifsName.compareTo(b.poifsName);
        }
    }
}

