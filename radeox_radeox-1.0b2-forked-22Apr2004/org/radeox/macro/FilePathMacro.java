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

public class FilePathMacro
extends LocalePreserved {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$FilePathMacro == null ? (class$org$radeox$macro$FilePathMacro = FilePathMacro.class$("org.radeox.macro.FilePathMacro")) : class$org$radeox$macro$FilePathMacro));
    private String[] paramDescription = new String[]{"1: file path"};
    static /* synthetic */ Class class$org$radeox$macro$FilePathMacro;

    public String getLocaleKey() {
        return "macro.filepath";
    }

    public FilePathMacro() {
        this.addSpecial('\\');
    }

    public String getDescription() {
        return "Displays a file system path. The file path should use slashes. Defaults to windows.";
    }

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() == 1) {
            String path = params.get("0").replace('/', '\\');
            writer.write(this.replace(path));
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

