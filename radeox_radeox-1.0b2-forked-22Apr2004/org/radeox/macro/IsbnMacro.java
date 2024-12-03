/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.book.BookServices;
import org.radeox.macro.parameter.MacroParameter;

public class IsbnMacro
extends BaseLocaleMacro {
    private String[] paramDescription = new String[]{"1: isbn number"};
    private String NEEDS_ISBN_ERROR;

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.isbn";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() == 1) {
            BookServices.getInstance().appendUrl(writer, params.get("0"));
            return;
        }
        throw new IllegalArgumentException("needs an ISBN number as argument");
    }
}

