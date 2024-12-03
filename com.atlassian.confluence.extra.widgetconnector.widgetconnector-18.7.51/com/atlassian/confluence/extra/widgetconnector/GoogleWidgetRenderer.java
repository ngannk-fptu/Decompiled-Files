/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;

public abstract class GoogleWidgetRenderer
extends AbstractWidgetRenderer {
    @Override
    protected String getTrustedDomainsKey() {
        return "Google";
    }
}

