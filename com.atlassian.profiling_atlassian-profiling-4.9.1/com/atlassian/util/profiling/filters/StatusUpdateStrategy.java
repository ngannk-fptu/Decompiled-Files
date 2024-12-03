/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package com.atlassian.util.profiling.filters;

import javax.servlet.ServletRequest;

public interface StatusUpdateStrategy {
    public void setStateViaRequest(ServletRequest var1);
}

