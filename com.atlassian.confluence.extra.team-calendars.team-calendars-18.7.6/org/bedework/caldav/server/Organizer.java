/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.Serializable;

public class Organizer
implements Serializable {
    private String cn;
    private String dir;
    private String language;
    private String sentBy;
    private String organizerUri;

    public Organizer(String cn, String dir, String language, String sentBy, String organizerUri) {
        this.cn = cn;
        this.dir = dir;
        this.language = language;
        this.sentBy = sentBy;
        this.organizerUri = organizerUri;
    }

    public String getCn() {
        return this.cn;
    }

    public String getDir() {
        return this.dir;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getSentBy() {
        return this.sentBy;
    }

    public void setOrganizerUri(String val) {
        this.organizerUri = val;
    }

    public String getOrganizerUri() {
        return this.organizerUri;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BwOrganizer(");
        sb.append("cn=");
        sb.append(this.getCn());
        sb.append(", dir=");
        sb.append(this.getDir());
        sb.append(", language=");
        sb.append(this.getLanguage());
        sb.append(", sentBy=");
        sb.append(this.getSentBy());
        sb.append(", organizerUri=");
        sb.append(this.getOrganizerUri());
        sb.append("}");
        return sb.toString();
    }
}

