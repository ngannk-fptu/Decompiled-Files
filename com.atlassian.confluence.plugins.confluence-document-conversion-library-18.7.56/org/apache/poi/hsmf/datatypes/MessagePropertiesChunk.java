/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.PropertiesChunk;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.util.LittleEndian;

public class MessagePropertiesChunk
extends PropertiesChunk {
    private boolean isEmbedded;
    private long nextRecipientId;
    private long nextAttachmentId;
    private long recipientCount;
    private long attachmentCount;

    public MessagePropertiesChunk(ChunkGroup parentGroup) {
        super(parentGroup);
    }

    public MessagePropertiesChunk(ChunkGroup parentGroup, boolean isEmbedded) {
        super(parentGroup);
        this.isEmbedded = isEmbedded;
    }

    public long getNextRecipientId() {
        return this.nextRecipientId;
    }

    public long getNextAttachmentId() {
        return this.nextAttachmentId;
    }

    public long getRecipientCount() {
        return this.recipientCount;
    }

    public long getAttachmentCount() {
        return this.attachmentCount;
    }

    public void setNextRecipientId(long nextRecipientId) {
        this.nextRecipientId = nextRecipientId;
    }

    public void setNextAttachmentId(long nextAttachmentId) {
        this.nextAttachmentId = nextAttachmentId;
    }

    public void setRecipientCount(long recipientCount) {
        this.recipientCount = recipientCount;
    }

    public void setAttachmentCount(long attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    @Override
    protected void readProperties(InputStream stream) throws IOException {
        LittleEndian.readLong(stream);
        this.nextRecipientId = LittleEndian.readUInt(stream);
        this.nextAttachmentId = LittleEndian.readUInt(stream);
        this.recipientCount = LittleEndian.readUInt(stream);
        this.attachmentCount = LittleEndian.readUInt(stream);
        if (!this.isEmbedded) {
            LittleEndian.readLong(stream);
        }
        super.readProperties(stream);
    }

    @Override
    public void readValue(InputStream value) throws IOException {
        this.readProperties(value);
    }

    @Override
    protected List<PropertyValue> writeProperties(OutputStream stream) throws IOException {
        LittleEndian.putLong(0L, stream);
        LittleEndian.putUInt(this.nextRecipientId, stream);
        LittleEndian.putUInt(this.nextAttachmentId, stream);
        LittleEndian.putUInt(this.recipientCount, stream);
        LittleEndian.putUInt(this.attachmentCount, stream);
        if (!this.isEmbedded) {
            LittleEndian.putLong(0L, stream);
        }
        return super.writeProperties(stream);
    }

    @Override
    public void writeValue(OutputStream stream) throws IOException {
        this.writeProperties(stream);
    }
}

