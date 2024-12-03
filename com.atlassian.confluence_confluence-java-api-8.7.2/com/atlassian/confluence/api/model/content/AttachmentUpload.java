/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import java.io.File;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class AttachmentUpload {
    private final File file;
    private final String name;
    private final String mediaType;
    private final String comment;
    private final boolean minorEdit;
    private final boolean hidden;

    public AttachmentUpload(File file, String name, String mediaType, String comment, boolean minorEdit) {
        this(file, name, mediaType, comment, minorEdit, false);
    }

    public AttachmentUpload(File file, String name, String mediaType, String comment, boolean minorEdit, boolean hidden) {
        this.file = file;
        this.name = name;
        this.mediaType = mediaType;
        this.comment = comment;
        this.minorEdit = minorEdit;
        this.hidden = hidden;
    }

    public AttachmentUpload withName(String newName) {
        return new AttachmentUpload(this.file, newName, this.mediaType, this.comment, this.minorEdit, this.hidden);
    }

    public String getComment() {
        return this.comment;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }

    public boolean isMinorEdit() {
        return this.minorEdit;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public String toString() {
        return "AttachmentUpload{name='" + this.name + '\'' + ", mediaType='" + this.mediaType + '\'' + ", comment='" + this.comment + '\'' + ", minorEdit=" + this.minorEdit + ", hidden=" + this.hidden + '}';
    }
}

