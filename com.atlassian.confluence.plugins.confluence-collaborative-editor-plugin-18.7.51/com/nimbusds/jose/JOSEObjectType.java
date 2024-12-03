/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public final class JOSEObjectType
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final JOSEObjectType JOSE = new JOSEObjectType("JOSE");
    public static final JOSEObjectType JOSE_JSON = new JOSEObjectType("JOSE+JSON");
    public static final JOSEObjectType JWT = new JOSEObjectType("JWT");
    private final String type;

    public JOSEObjectType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("The object type must not be null");
        }
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public int hashCode() {
        return this.type.toLowerCase().hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof JOSEObjectType && this.type.equalsIgnoreCase(((JOSEObjectType)object).type);
    }

    public String toString() {
        return this.type;
    }

    public String toJSONString() {
        return JSONStringUtils.toJSONString(this.type);
    }
}

