/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.AbstractRequestAttributesScope;

public class RequestScope
extends AbstractRequestAttributesScope {
    @Override
    protected int getScope() {
        return 0;
    }

    @Override
    @Nullable
    public String getConversationId() {
        return null;
    }
}

