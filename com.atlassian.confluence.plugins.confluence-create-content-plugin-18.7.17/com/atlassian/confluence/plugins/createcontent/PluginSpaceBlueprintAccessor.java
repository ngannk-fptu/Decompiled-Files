/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PluginSpaceBlueprintAccessor {
    @Nonnull
    public SpaceBlueprint getByModuleCompleteKey(@Nonnull ModuleCompleteKey var1);

    @Nullable
    public DialogWizard getDialogByModuleCompleteKey(@Nonnull ModuleCompleteKey var1);

    @Nonnull
    public List<SpaceBlueprint> getAll();
}

