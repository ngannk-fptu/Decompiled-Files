/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.util.velocity.TemplateHtmlEntityEncodingPolicy;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate;

@Deprecated(forRemoval=true)
public class DefaultHtmlEntityEncodingPolicy
implements TemplateHtmlEntityEncodingPolicy {
    @Override
    public boolean shouldAutoEncode(HtmlSafeVelocityTemplate template) {
        return !template.isAutoEncodeDisabled();
    }
}

