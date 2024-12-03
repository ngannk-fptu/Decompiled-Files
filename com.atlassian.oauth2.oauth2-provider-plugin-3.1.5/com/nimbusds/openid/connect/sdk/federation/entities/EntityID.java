/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import net.jcip.annotations.Immutable;

@Immutable
public final class EntityID
extends Identifier {
    private static final long serialVersionUID = -2884746939238001871L;

    public EntityID(URI value) {
        this(value.toString());
    }

    public EntityID(Issuer issuer) {
        this(issuer.getValue());
    }

    public EntityID(Subject subject) {
        this(subject.getValue());
    }

    public EntityID(ClientID clientID) {
        this(clientID.getValue());
    }

    public EntityID(String value) {
        super(value);
        URI uri;
        try {
            uri = new URI(value);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("The entity ID must be an URI: " + e.getMessage(), e);
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) && !"http".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("The entity ID must be an URI with https or http scheme");
        }
        if (StringUtils.isBlank(uri.getAuthority())) {
            throw new IllegalArgumentException("The entity ID must be an URI with authority (hostname)");
        }
    }

    public URI toURI() {
        return URI.create(this.getValue());
    }

    public Issuer toIssuer() {
        return new Issuer(this.getValue());
    }

    public Subject toSubject() {
        return new Subject(this.getValue());
    }

    public ClientID toClientID() {
        return new ClientID(this.getValue());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof EntityID && this.toString().equals(object.toString());
    }

    public static EntityID parse(String value) throws ParseException {
        try {
            return new EntityID(value);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }

    public static EntityID parse(Issuer issuer) throws ParseException {
        return EntityID.parse(issuer.getValue());
    }

    public static EntityID parse(Subject subject) throws ParseException {
        return EntityID.parse(subject.getValue());
    }

    public static EntityID parse(ClientID clientID) throws ParseException {
        return EntityID.parse(clientID.getValue());
    }
}

