/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

public interface ScriptSource {
    public String getScriptAsString() throws IOException;

    public boolean isModified();

    @Nullable
    public String suggestedClassName();
}

