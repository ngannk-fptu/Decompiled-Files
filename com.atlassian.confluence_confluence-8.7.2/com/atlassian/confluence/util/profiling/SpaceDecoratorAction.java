/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import java.util.Objects;

class SpaceDecoratorAction
extends AbstractSpaceAction {
    private final WebInterfaceContext contextPrototype;

    SpaceDecoratorAction(WebInterfaceContext contextPrototype, Space space) {
        this.contextPrototype = Objects.requireNonNull(contextPrototype);
        this.space = Objects.requireNonNull(space);
        this.key = space.getKey();
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        return DefaultWebInterfaceContext.copyOf(this.contextPrototype);
    }
}

