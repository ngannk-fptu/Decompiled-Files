/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.core.service.ServiceCommand;

public interface RenderContentCommand
extends ServiceCommand {
    public String getRenderedContent();
}

