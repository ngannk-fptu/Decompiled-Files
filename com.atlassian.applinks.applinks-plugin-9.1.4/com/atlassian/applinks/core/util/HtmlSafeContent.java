/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.applinks.core.util;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public interface HtmlSafeContent {
    @HtmlSafe
    public CharSequence get();
}

