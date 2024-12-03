/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import java.util.Date;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContentReconciliationManager {
    public void handleContentUpdateBeforeSave(@NonNull ContentEntityObject var1, @Nullable SaveContext var2);

    public void handleEditorOnlyContentUpdateBeforeSave(@NonNull ContentEntityObject var1, @Nullable SaveContext var2);

    public void handleContentUpdateAfterSave(@NonNull ContentEntityObject var1, @Nullable SaveContext var2, @NonNull Optional<Date> var3);

    public void handleEditorOnlyContentUpdateAfterSave(@NonNull ContentEntityObject var1, @Nullable SaveContext var2, @NonNull Optional<Date> var3);

    public void reconcileIfNeeded(@NonNull ContentEntityObject var1, @Nullable SaveContext var2);

    public boolean isReconciled(@NonNull ContentEntityObject var1);

    public void reconcileDraft(@NonNull SpaceContentEntityObject var1, @NonNull SpaceContentEntityObject var2);

    public void markDraftSynchronised(@NonNull SpaceContentEntityObject var1);
}

