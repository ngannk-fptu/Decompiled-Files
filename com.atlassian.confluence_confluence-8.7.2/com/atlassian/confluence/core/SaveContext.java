/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import java.io.Serializable;

public interface SaveContext
extends OperationContext<PageUpdateTrigger>,
Serializable {
    public boolean doUpdateLastModifier();

    @Deprecated
    public void setUpdateLastModifier(boolean var1);

    @Deprecated
    public void setSuppressNotifications(boolean var1);

    public boolean isSuppressAutowatch();
}

