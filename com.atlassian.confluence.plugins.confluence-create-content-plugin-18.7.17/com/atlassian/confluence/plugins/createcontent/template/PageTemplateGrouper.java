/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import javax.annotation.Nullable;

public interface PageTemplateGrouper {
    public Collection<ContentBlueprint> getSpaceContentBlueprints(@Nullable Space var1);
}

