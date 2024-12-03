/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.io.Serializable;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class Actor
implements Serializable,
Comparable<Actor>,
JSONAware {
    private static final long serialVersionUID = 4171395610729169757L;
    private final Subject subject;
    private final Issuer issuer;
    private final Actor parent;

    public Actor(Subject subject) {
        this(subject, null, null);
    }

    public Actor(Subject subject, Issuer issuer, Actor parent) {
        if (subject == null) {
            throw new IllegalArgumentException("The subject must not be null");
        }
        this.subject = subject;
        this.issuer = issuer;
        this.parent = parent;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public Actor getParent() {
        return this.parent;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("sub", this.subject.getValue());
        if (this.issuer != null) {
            o.put("iss", this.issuer.getValue());
        }
        if (this.parent != null) {
            o.put("act", this.parent.toJSONObject());
        }
        return o;
    }

    @Override
    public int compareTo(Actor other) {
        return this.toJSONString().compareTo(other.toJSONString());
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actor)) {
            return false;
        }
        Actor actor = (Actor)o;
        return this.getSubject().equals(actor.getSubject()) && Objects.equals(this.getIssuer(), actor.getIssuer()) && Objects.equals(this.getParent(), actor.getParent());
    }

    public int hashCode() {
        return Objects.hash(this.getSubject(), this.getIssuer(), this.getParent());
    }

    public static Actor parse(JSONObject jsonObject) throws ParseException {
        Subject sub = new Subject(JSONObjectUtils.getString(jsonObject, "sub"));
        Issuer iss = null;
        if (jsonObject.containsKey("iss")) {
            iss = new Issuer(JSONObjectUtils.getString(jsonObject, "iss"));
        }
        Actor parent = Actor.parseTopLevel(jsonObject);
        return new Actor(sub, iss, parent);
    }

    public static Actor parseTopLevel(JSONObject jsonObject) throws ParseException {
        JSONObject actSpec = JSONObjectUtils.getJSONObject(jsonObject, "act", null);
        if (actSpec == null) {
            return null;
        }
        return Actor.parse(actSpec);
    }
}

