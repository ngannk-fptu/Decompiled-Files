/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks.util;

import com.atlassian.webhooks.WebhookScope;

public class WebhookScopeUtil {
    private WebhookScopeUtil() {
        throw new UnsupportedOperationException("PropertyUtil is a utility class and should not be instantiated");
    }

    public static boolean equals(WebhookScope a, WebhookScope b) {
        if (a == b) {
            return true;
        }
        if (a != null && b != null) {
            return a.getId().equals(b.getId()) && b.getType().equals(b.getType());
        }
        return false;
    }
}

