/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.Page;

public interface CreatePageCommand
extends ServiceCommand {
    public Page getCreatedPage();
}

