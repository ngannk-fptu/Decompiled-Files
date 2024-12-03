/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import java.util.Optional;

@ExperimentalApi
public interface SandboxCallbackContext {
    public <T> Optional<T> get(Class<T> var1);
}

