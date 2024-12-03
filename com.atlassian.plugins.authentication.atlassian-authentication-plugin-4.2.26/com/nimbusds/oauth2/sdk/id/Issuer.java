/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import net.jcip.annotations.Immutable;

@Immutable
public final class Issuer
extends Identifier {
    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        try {
            return Issuer.isValid(new URI(value));
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValid(Issuer value) {
        if (value == null) {
            return false;
        }
        try {
            return Issuer.isValid(new URI(value.getValue()));
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValid(URI value) {
        if (value == null) {
            return false;
        }
        if (value.getScheme() == null || !value.getScheme().equalsIgnoreCase("https")) {
            return false;
        }
        if (value.getRawQuery() != null) {
            return false;
        }
        return value.getRawFragment() == null;
    }

    public Issuer(String value) {
        super(value);
    }

    public Issuer(URI value) {
        super(value.toString());
    }

    public Issuer(Identifier value) {
        super(value.getValue());
    }

    public boolean isValid() {
        return Issuer.isValid(this);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Issuer && this.toString().equals(object.toString());
    }

    public static Issuer parse(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return new Issuer(s);
    }
}

