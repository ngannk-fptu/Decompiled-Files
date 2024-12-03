/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.introspection.BoxedValue
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;

@Deprecated
public final class HtmlFragment
implements BoxedValue {
    private final Object fragment;

    public HtmlFragment(Object fragment) {
        this.fragment = fragment;
    }

    @HtmlSafe
    public String toString() {
        return this.fragment.toString();
    }

    public Object unbox() {
        return this.fragment;
    }
}

