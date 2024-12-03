/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate;

public interface ConfluenceVelocityTemplate
extends HtmlSafeVelocityTemplate {
    public String getName();

    public boolean isAutoEncodeDisabled();

    public boolean isDeclaredHtmlSafe();

    public boolean isPluginTemplate();
}

