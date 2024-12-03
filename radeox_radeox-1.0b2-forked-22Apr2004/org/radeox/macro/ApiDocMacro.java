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
import org.radeox.macro.api.ApiDoc;
import org.radeox.macro.parameter.MacroParameter;

public class ApiDocMacro
extends BaseLocaleMacro {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$ApiDocMacro == null ? (class$org$radeox$macro$ApiDocMacro = ApiDocMacro.class$("org.radeox.macro.ApiDocMacro")) : class$org$radeox$macro$ApiDocMacro));
    private String[] paramDescription = new String[0];
    static /* synthetic */ Class class$org$radeox$macro$ApiDocMacro;

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.apidocs";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        ApiDoc apiDoc = ApiDoc.getInstance();
        apiDoc.appendTo(writer);
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

