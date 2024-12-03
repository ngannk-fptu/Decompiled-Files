/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.util.velocity.ConfluenceVelocityTemplate;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate;

@Deprecated(forRemoval=true)
public interface TemplateHtmlEntityEncodingPolicy {
    public boolean shouldAutoEncode(HtmlSafeVelocityTemplate var1);

    @Deprecated
    default public boolean shouldAutoEncode(ConfluenceVelocityTemplate template) {
        return this.shouldAutoEncode((HtmlSafeVelocityTemplate)template);
    }
}

