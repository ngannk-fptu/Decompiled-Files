/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.application.filtering;

import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.model.application.Application;

public interface AccessFilterFactory {
    public AccessFilter create(Application var1, boolean var2);
}

