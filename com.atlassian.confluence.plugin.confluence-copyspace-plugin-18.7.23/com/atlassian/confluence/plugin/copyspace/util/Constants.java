/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.DefaultSaveContext$Builder
 *  com.atlassian.confluence.core.OperationTrigger
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 */
package com.atlassian.confluence.plugin.copyspace.util;

import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.OperationTrigger;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.PageUpdateTrigger;

public final class Constants {
    public static final SaveContext SUPPRESS_EVENT_KEEP_LAST_MODIFIER = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().suppressNotifications(true)).suppressAutowatch(true).updateLastModifier(false).suppressEvents(true)).updateTrigger((OperationTrigger)PageUpdateTrigger.LINK_REFACTORING)).build();
    public static final float CORRECTION_VALUE = 1.1f;

    private Constants() {
    }
}

