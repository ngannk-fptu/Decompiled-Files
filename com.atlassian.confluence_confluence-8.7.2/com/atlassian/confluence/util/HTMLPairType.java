/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import java.io.Serializable;

public class HTMLPairType
implements Serializable {
    private HtmlFragment key;
    private HtmlFragment value;

    public HTMLPairType() {
    }

    public HTMLPairType(HtmlFragment key, HtmlFragment value) {
        this.key = key;
        this.value = value;
    }

    public HTMLPairType(String key, String value) {
        this(new HtmlFragment((Object)key), new HtmlFragment((Object)value));
    }

    public HtmlFragment getKey() {
        return this.key;
    }

    public void setKey(HtmlFragment key) {
        this.key = key;
    }

    public HtmlFragment getValue() {
        return this.value;
    }

    public void setValue(HtmlFragment value) {
        this.value = value;
    }

    @HtmlSafe
    public String toString() {
        return this.key + "/" + this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HTMLPairType)) {
            return false;
        }
        HTMLPairType pairType = (HTMLPairType)o;
        if (!this.key.equals(pairType.key)) {
            return false;
        }
        return this.value.equals(pairType.value);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 29 * result + this.value.hashCode();
        return result;
    }
}

