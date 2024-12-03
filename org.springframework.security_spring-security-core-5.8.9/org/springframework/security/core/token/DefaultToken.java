/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.token;

import java.util.Date;
import org.springframework.security.core.token.Token;
import org.springframework.util.Assert;

public class DefaultToken
implements Token {
    private final String key;
    private final long keyCreationTime;
    private final String extendedInformation;

    public DefaultToken(String key, long keyCreationTime, String extendedInformation) {
        Assert.hasText((String)key, (String)"Key required");
        Assert.notNull((Object)extendedInformation, (String)"Extended information cannot be null");
        this.key = key;
        this.keyCreationTime = keyCreationTime;
        this.extendedInformation = extendedInformation;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public long getKeyCreationTime() {
        return this.keyCreationTime;
    }

    @Override
    public String getExtendedInformation() {
        return this.extendedInformation;
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DefaultToken) {
            DefaultToken rhs = (DefaultToken)obj;
            return this.key.equals(rhs.key) && this.keyCreationTime == rhs.keyCreationTime && this.extendedInformation.equals(rhs.extendedInformation);
        }
        return false;
    }

    public int hashCode() {
        int code = 979;
        code *= this.key.hashCode();
        code *= new Long(this.keyCreationTime).hashCode();
        return code *= this.extendedInformation.hashCode();
    }

    public String toString() {
        return "DefaultToken[key=" + this.key + "; creation=" + new Date(this.keyCreationTime) + "; extended=" + this.extendedInformation + "]";
    }
}

