/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.Invitee;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class ExternalInvitee
implements Invitee {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalInvitee.class);
    private static final String TYPE = "external";
    private String email;
    private String avatarIconUrl;

    public ExternalInvitee() {
        this(null);
    }

    public ExternalInvitee(String email) {
        this.setEmail(email);
    }

    @Override
    @XmlElement
    public String getId() {
        return this.getEmail();
    }

    @Override
    public String getName() {
        return this.getEmail();
    }

    @Override
    @XmlElement
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @XmlElement
    public String getDisplayName() {
        return this.getEmail();
    }

    @Override
    @XmlElement
    public String getType() {
        return TYPE;
    }

    @Override
    @XmlElement
    public String getAvatarIconUrl() {
        return this.avatarIconUrl;
    }

    public void setAvatarIconUrl(String avatarIconUrl) {
        this.avatarIconUrl = avatarIconUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("id", (Object)this.getId());
            thisObject.put("email", (Object)this.getEmail());
            thisObject.put("displayName", (Object)this.getDisplayName());
            thisObject.put("type", (Object)this.getType());
            thisObject.put("avatarIconUrl", (Object)this.getAvatarIconUrl());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExternalInvitee that = (ExternalInvitee)o;
        return this.email != null ? this.email.equals(that.email) : that.email == null;
    }

    public int hashCode() {
        return this.email != null ? this.email.hashCode() : 0;
    }

    @Override
    public int compareTo(Invitee invitee) {
        if (StringUtils.equals((CharSequence)this.getType(), (CharSequence)invitee.getType())) {
            return StringUtils.defaultString((String)this.getEmail()).compareTo(StringUtils.defaultString((String)invitee.getEmail()));
        }
        return this.getType().compareTo(this.getType());
    }
}

