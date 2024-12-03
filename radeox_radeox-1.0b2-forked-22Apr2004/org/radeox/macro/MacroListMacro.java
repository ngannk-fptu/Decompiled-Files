/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.Macro;
import org.radeox.macro.MacroRepository;
import org.radeox.macro.parameter.MacroParameter;

public class MacroListMacro
extends BaseLocaleMacro {
    public String getLocaleKey() {
        return "macro.macrolist";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() != 0) {
            throw new IllegalArgumentException("MacroListMacro: number of arguments does not match");
        }
        this.appendTo(writer);
    }

    public Writer appendTo(Writer writer) throws IOException {
        List macroList = MacroRepository.getInstance().getPlugins();
        Collections.sort(macroList);
        Iterator iterator = macroList.iterator();
        writer.write("{table}\n");
        writer.write("Macro|Description|Parameters\n");
        while (iterator.hasNext()) {
            Macro macro = (Macro)iterator.next();
            writer.write(macro.getName());
            writer.write("|");
            writer.write(macro.getDescription());
            writer.write("|");
            String[] params = macro.getParamDescription();
            if (params.length == 0) {
                writer.write("none");
            } else {
                for (int i = 0; i < params.length; ++i) {
                    String description = params[i];
                    if (description.startsWith("?")) {
                        writer.write(description.substring(1));
                        writer.write(" (optional)");
                    } else {
                        writer.write(params[i]);
                    }
                    writer.write("\\\\");
                }
            }
            writer.write("\n");
        }
        writer.write("{table}");
        return writer;
    }
}

