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

public class MailToMacro
extends LocalePreserved {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$MailToMacro == null ? (class$org$radeox$macro$MailToMacro = MailToMacro.class$("org.radeox.macro.MailToMacro")) : class$org$radeox$macro$MailToMacro));
    private String[] paramDescription = new String[]{"1: mail address"};
    static /* synthetic */ Class class$org$radeox$macro$MailToMacro;

    public String getLocaleKey() {
        return "macro.mailto";
    }

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() == 1) {
            String mail = params.get("0");
            writer.write("<a href=\"mailto:" + mail + "\">" + mail + "</a>");
        }
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

