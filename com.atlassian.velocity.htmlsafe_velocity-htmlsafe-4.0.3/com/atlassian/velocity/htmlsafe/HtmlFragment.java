/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;

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

