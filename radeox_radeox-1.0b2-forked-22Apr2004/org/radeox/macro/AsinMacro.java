/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.book.AsinServices;
import org.radeox.macro.parameter.MacroParameter;

public class AsinMacro
extends BaseLocaleMacro {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$AsinMacro == null ? (class$org$radeox$macro$AsinMacro = AsinMacro.class$("org.radeox.macro.AsinMacro")) : class$org$radeox$macro$AsinMacro));
    private String[] paramDescription = new String[]{"1: asin number"};
    static /* synthetic */ Class class$org$radeox$macro$AsinMacro;

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.asin";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() == 1) {
            AsinServices.getInstance().appendUrl(writer, params.get("0"));
            return;
        }
        log.warn((Object)"needs an ASIN number as argument");
        throw new IllegalArgumentException("needs an ASIN number as argument");
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

