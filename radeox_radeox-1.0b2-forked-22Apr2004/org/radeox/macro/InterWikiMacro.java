/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.filter.interwiki.InterWiki;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.parameter.MacroParameter;

public class InterWikiMacro
extends BaseLocaleMacro {
    private String[] paramDescription = new String[]{"none"};

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.interwiki";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        InterWiki interWiki = InterWiki.getInstance();
        interWiki.appendTo(writer);
    }
}

