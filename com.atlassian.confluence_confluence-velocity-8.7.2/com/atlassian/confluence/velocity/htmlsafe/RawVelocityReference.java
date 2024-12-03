/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.velocity.htmlsafe;

import java.util.regex.Pattern;
import org.springframework.util.Assert;

@Deprecated
final class RawVelocityReference {
    private static final Pattern REFERENCE_SUGAR = Pattern.compile("[\\!\\$\\{\\}]");
    private final String referenceString;

    public RawVelocityReference(String referenceString) {
        Assert.notNull((Object)referenceString, (String)"referenceString must not be null");
        this.referenceString = referenceString;
    }

    public boolean isScalar() {
        return !this.referenceString.contains(".");
    }

    public RawVelocityReference getScalarComponent() {
        if (this.isScalar()) {
            return this;
        }
        return new RawVelocityReference(this.referenceString.substring(0, this.referenceString.indexOf(".")));
    }

    public String getBaseReferenceName() {
        return REFERENCE_SUGAR.matcher(this.getScalarComponent().referenceString).replaceAll("");
    }
}

