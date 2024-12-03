/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.PersonalInformation
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.user.PersonalInformation;
import java.util.Date;

public class RemoteUserInformation {
    private String username;
    private String content;
    private String creatorName;
    private String lastModifierName;
    private int version;
    private long id;
    private Date creationDate;
    private Date lastModificationDate;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.user.PersonalInformation info \nsetContent java.lang.String content \nsetCreationDate java.util.Date creationDate \nsetCreatorName java.lang.String creatorName \nsetId long id \nsetLastModificationDate java.util.Date lastModificationDate \nsetLastModifierName java.lang.String lastModifierName \nsetUsername java.lang.String username \nsetVersion int version \n";

    public RemoteUserInformation() {
    }

    public RemoteUserInformation(PersonalInformation info) {
        this.username = info.getUsername();
        this.content = info.getBodyAsString();
        this.creatorName = info.getCreatorName();
        this.creationDate = info.getCreationDate();
        this.lastModifierName = info.getLastModifierName();
        this.lastModificationDate = info.getLastModificationDate();
        this.version = info.getVersion();
        this.id = info.getId();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getLastModifierName() {
        return this.lastModifierName;
    }

    public void setLastModifierName(String lastModifierName) {
        this.lastModifierName = lastModifierName;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}

