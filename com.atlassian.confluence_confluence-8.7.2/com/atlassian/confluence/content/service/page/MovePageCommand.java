/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.Page;
import com.atlassian.core.util.ProgressMeter;

public interface MovePageCommand
extends ServiceCommand {
    public static final String POSITION_ABOVE = "above";
    public static final String POSITION_BELOW = "below";
    public static final String POSITION_APPEND = "append";
    public static final String POSITION_TOP_LEVEL = "topLevel";

    public Page getPage();

    public ProgressMeter getProgressMeter();
}

