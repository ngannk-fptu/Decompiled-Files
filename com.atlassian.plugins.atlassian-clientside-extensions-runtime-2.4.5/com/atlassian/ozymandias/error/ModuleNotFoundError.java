/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ozymandias.error;

import com.atlassian.ozymandias.error.ModuleAccessError;

public class ModuleNotFoundError
extends ModuleAccessError {
    public ModuleNotFoundError() {
        super("Module not found");
    }
}

