/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe;

final class RawVelocityReference {
    private final String referenceString;

    public RawVelocityReference(String referenceString) {
        if (referenceString == null) {
            throw new NullPointerException("referenceString must not be null");
        }
        this.referenceString = referenceString;
    }

    public boolean isScalar() {
        return this.referenceString.indexOf(46) == -1;
    }

    public RawVelocityReference getScalarComponent() {
        int pos = this.referenceString.indexOf(46);
        return pos == -1 ? this : new RawVelocityReference(this.referenceString.substring(0, pos));
    }

    public String getBaseReferenceName() {
        int len = this.referenceString.length();
        StringBuilder str = new StringBuilder(len);
        block4: for (int j = 0; j < len; ++j) {
            char c = this.referenceString.charAt(j);
            switch (c) {
                case '!': 
                case '$': 
                case '{': 
                case '}': {
                    continue block4;
                }
                case '.': {
                    return str.toString();
                }
                default: {
                    str.append(c);
                }
            }
        }
        return str.toString();
    }
}

