/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.google.common.base.Supplier;

public interface MacroIdSupplier
extends Supplier<MacroId> {
    public MacroId get();
}

