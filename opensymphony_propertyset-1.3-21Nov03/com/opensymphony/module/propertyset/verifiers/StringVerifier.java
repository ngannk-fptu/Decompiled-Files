/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.verifiers;

import com.opensymphony.module.propertyset.verifiers.PropertyVerifier;
import com.opensymphony.module.propertyset.verifiers.VerifyException;
import java.util.HashSet;
import java.util.Set;

public class StringVerifier
implements PropertyVerifier {
    private Set allowableStrings;
    private String contains;
    private String prefix;
    private String suffix;
    private int max = 255;
    private int min = 0;

    public StringVerifier() {
    }

    public StringVerifier(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public StringVerifier(String[] allowable) {
        this.setAllowableValues(allowable);
    }

    public void setAllowableValues(String[] vals) {
        this.allowableStrings = new HashSet();
        for (int i = 0; i < vals.length; ++i) {
            this.allowableStrings.add(vals[i]);
        }
    }

    public void setContains(String s) {
        this.contains = s;
    }

    public String getContains() {
        return this.contains;
    }

    public void setMaxLength(int max) {
        this.max = max;
    }

    public int getMaxLength() {
        return this.max;
    }

    public void setMinLength(int min) {
        this.min = min;
    }

    public int getMinLength() {
        return this.min;
    }

    public void setPrefix(String s) {
        this.prefix = s;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setSuffix(String s) {
        this.suffix = s;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void verify(Object o) throws VerifyException {
        String s = (String)o;
        if (s.length() < this.min) {
            throw new VerifyException("String " + s + " too short, min length=" + this.min);
        }
        if (s.length() > this.max) {
            throw new VerifyException("String " + s + " too long, max length=" + this.max);
        }
        if (this.suffix != null && !s.endsWith(this.suffix)) {
            throw new VerifyException("String " + s + " has invalid suffix (suffix must be \"" + this.suffix + "\")");
        }
        if (this.prefix != null && !s.startsWith(this.prefix)) {
            throw new VerifyException("String " + s + " has invalid prefix (prefix must be \"" + this.prefix + "\")");
        }
        if (this.contains != null && s.indexOf(this.contains) == -1) {
            throw new VerifyException("String " + s + " does not contain required string \"" + this.contains + "\"");
        }
        if (this.allowableStrings != null && !this.allowableStrings.contains(s)) {
            throw new VerifyException("String " + s + " not in allowed set for this property");
        }
    }
}

