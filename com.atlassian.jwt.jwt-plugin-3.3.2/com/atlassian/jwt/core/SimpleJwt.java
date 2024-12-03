/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.Jwt;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SimpleJwt
implements Jwt {
    private final String iss;
    private final String sub;
    private final String payload;

    public SimpleJwt(String iss, String sub, String payload) {
        this.iss = iss;
        this.sub = sub;
        this.payload = payload;
    }

    @Override
    public String getIssuer() {
        return this.iss;
    }

    @Override
    public String getSubject() {
        return this.sub;
    }

    @Override
    public String getJsonPayload() {
        return this.payload;
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("iss", (Object)this.iss).append("sub", (Object)this.sub).append("payload", (Object)this.payload).toString();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        SimpleJwt rhs = (SimpleJwt)o;
        return new EqualsBuilder().append((Object)this.iss, (Object)rhs.iss).append((Object)this.sub, (Object)rhs.sub).append((Object)this.payload, (Object)rhs.payload).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(31, 17).append((Object)this.iss).append((Object)this.sub).append((Object)this.payload).hashCode();
    }
}

