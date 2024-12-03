/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroIdSupplier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import java.util.UUID;

public class DefaultMacroIdSupplier
implements MacroIdSupplier {
    @Override
    public MacroId get() {
        return MacroId.fromString(UUID.randomUUID().toString());
    }
}

