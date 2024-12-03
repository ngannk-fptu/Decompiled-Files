/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONValue;

public class Identifier
implements Serializable,
Comparable<Identifier>,
JSONAware {
    public static final int DEFAULT_BYTE_LENGTH = 32;
    protected static final SecureRandom secureRandom = new SecureRandom();
    private final String value;

    public static List<String> toStringList(Collection<? extends Identifier> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        ArrayList<String> stringList = new ArrayList<String>(ids.size());
        for (Identifier identifier : ids) {
            stringList.add(identifier.getValue());
        }
        return stringList;
    }

    public Identifier(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("The value must not be null or empty string");
        }
        this.value = value;
    }

    public Identifier(int byteLength) {
        if (byteLength < 1) {
            throw new IllegalArgumentException("The byte length must be a positive integer");
        }
        byte[] n = new byte[byteLength];
        secureRandom.nextBytes(n);
        this.value = Base64URL.encode(n).toString();
    }

    public Identifier() {
        this(32);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toJSONString() {
        return "\"" + JSONValue.escape(this.value) + '\"';
    }

    public String toString() {
        return this.getValue();
    }

    @Override
    public int compareTo(Identifier other) {
        return this.getValue().compareTo(other.getValue());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Identifier that = (Identifier)o;
        return this.getValue() != null ? this.getValue().equals(that.getValue()) : that.getValue() == null;
    }

    public int hashCode() {
        return this.getValue() != null ? this.getValue().hashCode() : 0;
    }
}

