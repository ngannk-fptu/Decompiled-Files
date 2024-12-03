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
import org.radeox.macro.LocalePreserved;
import org.radeox.macro.parameter.MacroParameter;

public class QuoteMacro
extends LocalePreserved {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$QuoteMacro == null ? (class$org$radeox$macro$QuoteMacro = QuoteMacro.class$("org.radeox.macro.QuoteMacro")) : class$org$radeox$macro$QuoteMacro));
    private String[] paramDescription = new String[]{"?1: source", "?2: displayed description, default is Source"};
    static /* synthetic */ Class class$org$radeox$macro$QuoteMacro;

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.quote";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        writer.write("<blockquote class=\"quote\">");
        writer.write(params.getContent());
        String source = "Source";
        if (params.getLength() == 2) {
            source = params.get(1);
        }
        if (params.getLength() > 0) {
            writer.write("<a href=\"" + params.get(0) + "\">");
            writer.write(source);
            writer.write("</a>");
        }
        writer.write("</blockquote>");
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

