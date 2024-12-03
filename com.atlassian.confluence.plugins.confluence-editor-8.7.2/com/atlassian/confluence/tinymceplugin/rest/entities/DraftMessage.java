/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.atlassian.confluence.tinymceplugin.rest.entities.DraftData;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class DraftMessage {
    @XmlElement
    private DraftData draftData;
    @XmlElement
    private boolean newPage;
    @XmlElement
    private boolean conflictFound;
    @XmlElement
    private boolean mergeRequired;

    public DraftMessage() {
    }

    public DraftMessage(DraftData draftData, boolean newPage, boolean conflictFound, boolean mergeRequired) {
        this.draftData = draftData;
        this.newPage = newPage;
        this.conflictFound = conflictFound;
        this.mergeRequired = mergeRequired;
    }

    public DraftData getDraftData() {
        return this.draftData;
    }

    public boolean isNewPage() {
        return this.newPage;
    }

    public boolean isConflictFound() {
        return this.conflictFound;
    }

    public boolean isMergeRequired() {
        return this.mergeRequired;
    }
}

