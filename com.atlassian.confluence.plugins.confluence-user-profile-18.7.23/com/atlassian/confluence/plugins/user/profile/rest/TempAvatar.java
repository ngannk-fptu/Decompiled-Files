/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.actions.TemporaryUploadedPicture
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.user.profile.rest;

import com.atlassian.confluence.user.actions.TemporaryUploadedPicture;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TempAvatar {
    @XmlElement
    private String src;
    @XmlElement
    private List<String> errors;

    public TempAvatar(TemporaryUploadedPicture pic) {
        this.src = pic.getThumbnailFileDownloadUrl();
    }

    public TempAvatar(String errorMessage) {
        this.errors.add(errorMessage);
    }
}

