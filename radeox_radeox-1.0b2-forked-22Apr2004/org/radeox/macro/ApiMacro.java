/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.api.ApiDoc;
import org.radeox.macro.parameter.MacroParameter;

public class ApiMacro
extends BaseLocaleMacro {
    private String[] paramDescription = new String[]{"1: class name, e.g. java.lang.Object or java.lang.Object@Java131", "?2: mode, e.g. Java12, Ruby, defaults to Java"};

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.api";
    }

    /*
     * WARNING - void declaration
     */
    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        void var3_5;
        void var4_3;
        String mode;
        String klass;
        if (params.getLength() == 1) {
            klass = params.get("0");
            int index = klass.indexOf("@");
            if (index > 0) {
                mode = klass.substring(index + 1);
                klass = klass.substring(0, index);
            } else {
                mode = "java";
            }
        } else if (params.getLength() == 2) {
            mode = params.get("1").toLowerCase();
            klass = params.get("0");
        } else {
            throw new IllegalArgumentException("api macro needs one or two paramaters");
        }
        ApiDoc.getInstance().expand(writer, (String)var4_3, (String)var3_5);
    }
}

