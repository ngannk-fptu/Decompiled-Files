/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.synchrony.Events
 *  javax.xml.bind.DatatypeConverter
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.CsvFriendly;
import com.atlassian.synchrony.Events;
import javax.xml.bind.DatatypeConverter;

public class Event
extends Events
implements CsvFriendly {
    @Override
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getEventId().getRev()).append(",").append(this.getEventId().getHistory()).append(",").append(this.getPartition()).append(",").append(this.getSequence()).append(",").append(DatatypeConverter.printHexBinary((byte[])this.getEvent())).append(",").append(this.getContentId()).append(",").append(this.getInserted()).append("\n");
        return sb.toString();
    }
}

