/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling.object;

import com.atlassian.annotations.Internal;

@Internal
public interface Profilable {
    public Object profile() throws Exception;
}

