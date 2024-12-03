/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.core.service.SingleEntityLocator;
import com.atlassian.confluence.pages.Page;

public interface PageLocator
extends SingleEntityLocator {
    public Page getPage();
}

