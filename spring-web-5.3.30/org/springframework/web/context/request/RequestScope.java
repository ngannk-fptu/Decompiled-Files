/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

    @Nullable
    public String getConversationId() {
        return null;
    }
}

