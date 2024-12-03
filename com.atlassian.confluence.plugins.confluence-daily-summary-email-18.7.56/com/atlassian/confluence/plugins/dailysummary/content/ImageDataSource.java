/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.dailysummary.content;

import javax.activation.DataSource;

public interface ImageDataSource
extends DataSource {
    public int getWidth();

    public int getHeight();
}

