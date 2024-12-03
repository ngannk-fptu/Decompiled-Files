/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.CsvFriendly;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

public class Snapshot
implements CsvFriendly {
    private String key;
    private byte[] value;
    private long contentId;
    private Date inserted;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public Date getInserted() {
        return this.inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    @Override
    public String toCsvString() {
        return this.getKey() + "," + DatatypeConverter.printHexBinary((byte[])this.getValue()) + "," + this.getContentId() + "," + this.getInserted() + "\n";
    }
}

