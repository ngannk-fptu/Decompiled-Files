/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
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
import com.atlassian.confluence.user.ConfluenceUser;
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
public class ConfluenceUserInvitee
implements Invitee {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceUserInvitee.class);
    private static final String TYPE = "confluence";
    private ConfluenceUser user;
    private String avatarIconUrl;

    public ConfluenceUserInvitee() {
        this(null);
    }

    public ConfluenceUserInvitee(ConfluenceUser user) {
        this.setUser(user);
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public void setUser(ConfluenceUser user) {
        this.user = user;
    }

    @Override
    @XmlElement
    public String getId() {
        return this.getUser().getKey().toString();
    }

    @Override
    public String getName() {
        return this.getUser().getName();
    }

    @Override
    @XmlElement
    public String getEmail() {
        return this.getUser().getEmail();
    }

    @Override
    @XmlElement
    public String getDisplayName() {
        return this.getUser().getFullName();
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
            thisObject.put("name", (Object)this.getName());
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
        ConfluenceUserInvitee that = (ConfluenceUserInvitee)o;
        return this.user != null ? this.user.equals(that.user) : that.user == null;
    }

    public int hashCode() {
        return this.user != null ? this.user.hashCode() : 0;
    }

    @Override
    public int compareTo(Invitee invitee) {
        if (StringUtils.equals((CharSequence)this.getType(), (CharSequence)invitee.getType())) {
            ConfluenceUser leftUser = this.getUser();
            ConfluenceUser rightUser = ((ConfluenceUserInvitee)invitee).getUser();
            int result = StringUtils.defaultString((String)leftUser.getFullName()).compareTo(StringUtils.defaultString((String)rightUser.getFullName()));
            if (0 == result) {
                return leftUser.getName().compareTo(rightUser.getName());
            }
            return result;
        }
        return this.getType().compareTo(this.getType());
    }
}

