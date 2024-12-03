/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.editor;

import com.atlassian.confluence.plugin.editor.Editor;

public interface EditorManager {
    public Editor getCurrentEditor();

    public String getCurrentEditorVersion();
}

