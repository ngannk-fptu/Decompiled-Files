/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.BaseMacro;
import org.radeox.macro.parameter.MacroParameter;

public class HelloWorldMacro
extends BaseMacro {
    private String[] paramDescription = new String[]{"1: name to print"};

    public String getName() {
        return "hello";
    }

    public String getDescription() {
        return "Say hello example macro.";
    }

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() != 1) {
            throw new IllegalArgumentException("Number of arguments does not match");
        }
        writer.write("Hello <b>");
        writer.write(params.get("0"));
        writer.write("</b>");
    }
}

