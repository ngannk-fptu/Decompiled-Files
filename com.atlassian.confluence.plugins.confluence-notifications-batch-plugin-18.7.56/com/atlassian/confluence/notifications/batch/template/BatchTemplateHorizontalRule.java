/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;

@Deprecated
public class BatchTemplateHorizontalRule
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "hr";

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

