/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;

class DecoratorAction
extends ConfluenceActionSupport {
    private final WebInterfaceContext contextPrototype;

    public DecoratorAction(WebInterfaceContext context) {
        this.contextPrototype = context;
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        return DefaultWebInterfaceContext.copyOf(this.contextPrototype);
    }
}

