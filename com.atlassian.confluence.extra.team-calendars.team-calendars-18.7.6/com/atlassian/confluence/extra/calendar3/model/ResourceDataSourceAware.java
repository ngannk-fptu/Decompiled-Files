/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package com.atlassian.confluence.extra.calendar3.model;

import javax.activation.DataHandler;

public interface ResourceDataSourceAware {
    public DataHandler getResourceDataHandler();

    public void setResourceDataHandler(DataHandler var1);
}

