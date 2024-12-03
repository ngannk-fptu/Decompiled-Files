/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.profiling.DecoratorAction;
import com.atlassian.confluence.util.profiling.SpaceDecoratorAction;
import com.atlassian.spring.container.ContainerManager;
import org.checkerframework.checker.nullness.qual.Nullable;

class DecoratorActionFactory {
    DecoratorActionFactory() {
    }

    public static ConfluenceActionSupport createAction(@Nullable WebInterfaceContext webInterfaceContext, @Nullable Space space) {
        if (webInterfaceContext != null && ContainerManager.isContainerSetup()) {
            ConfluenceActionSupport action = space == null ? new DecoratorAction(webInterfaceContext) : new SpaceDecoratorAction(webInterfaceContext, space);
            ContainerManager.autowireComponent((Object)action);
            return action;
        }
        return GeneralUtil.newWiredConfluenceActionSupport(space);
    }
}

