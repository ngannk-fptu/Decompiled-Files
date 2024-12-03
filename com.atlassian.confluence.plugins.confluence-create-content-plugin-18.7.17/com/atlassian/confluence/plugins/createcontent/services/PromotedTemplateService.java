/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import javax.annotation.Nonnull;

public interface PromotedTemplateService {
    public void promoteTemplate(long var1, @Nonnull String var3) throws BlueprintIllegalArgumentException;

    public void demoteTemplate(long var1, @Nonnull String var3) throws BlueprintIllegalArgumentException;

    public Collection<Long> getPromotedTemplates(@Nonnull Space var1);
}

