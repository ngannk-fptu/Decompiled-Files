/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.ApiEnum;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalSpi
public abstract class BaseApiEnum
implements ApiEnum {
    protected final String value;

    protected BaseApiEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.serialise();
    }

    @Override
    public String serialise() {
        return this.value;
    }

    public String toString() {
        return this.serialise();
    }

    public boolean in(BaseApiEnum ... types) {
        for (BaseApiEnum matchType : types) {
            if (!this.equals(matchType)) continue;
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null || !this.getClass().equals(other.getClass())) {
            return false;
        }
        return Objects.equals(this.value, ((BaseApiEnum)other).value);
    }
}

