/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scripting.support;

import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;

public class StaticScriptSource
implements ScriptSource {
    private String script = "";
    private boolean modified;
    @Nullable
    private String className;

    public StaticScriptSource(String script) {
        this.setScript(script);
    }

    public StaticScriptSource(String script, @Nullable String className) {
        this.setScript(script);
        this.className = className;
    }

    public synchronized void setScript(String script) {
        Assert.hasText(script, "Script must not be empty");
        this.modified = !script.equals(this.script);
        this.script = script;
    }

    @Override
    public synchronized String getScriptAsString() {
        this.modified = false;
        return this.script;
    }

    @Override
    public synchronized boolean isModified() {
        return this.modified;
    }

    @Override
    @Nullable
    public String suggestedClassName() {
        return this.className;
    }

    public String toString() {
        return "static script" + (this.className != null ? " [" + this.className + "]" : "");
    }
}

