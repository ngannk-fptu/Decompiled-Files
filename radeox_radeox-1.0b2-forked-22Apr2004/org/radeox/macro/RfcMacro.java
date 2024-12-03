/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.parameter.MacroParameter;

public class RfcMacro
extends BaseLocaleMacro {
    public String getLocaleKey() {
        return "macro.rfc";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        if (params.getLength() == 1) {
            String number = params.get("0");
            String view = "RFC" + number;
            this.appendRfc(writer, number, view);
            return;
        }
        if (params.getLength() != 2) {
            throw new IllegalArgumentException("needs an RFC numer as argument");
        }
        String number = params.get(0);
        String view = params.get(1);
        this.appendRfc(writer, number, view);
    }

    public void appendRfc(Writer writer, String number, String view) throws IOException, IllegalArgumentException {
        try {
            Integer dummy = Integer.getInteger(number);
        }
        catch (Exception e) {
            throw new IllegalArgumentException();
        }
        writer.write("<a href=\"http://zvon.org/tmRFC/RFC");
        writer.write(number);
        writer.write("/Output/index.html\">");
        writer.write(view);
        writer.write("</a>");
    }
}

