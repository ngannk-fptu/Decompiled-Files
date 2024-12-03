/*
 * Decompiled with CFR 0.152.
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

