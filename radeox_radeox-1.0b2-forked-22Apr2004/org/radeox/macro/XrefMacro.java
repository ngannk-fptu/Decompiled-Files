/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.macro.xref.XrefMapper;

public class XrefMacro
extends BaseLocaleMacro {
    private String[] paramDescription = new String[]{"1: class name, e.g. java.lang.Object or java.lang.Object@Nanning", "?2: line number"};

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.xref";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        String project;
        String klass;
        int lineNumber = 0;
        if (params.getLength() >= 1) {
            klass = params.get("0");
            int index = klass.indexOf("@");
            if (index > 0) {
                project = klass.substring(index + 1);
                klass = klass.substring(0, index);
            } else {
                project = "SnipSnap";
            }
            if (params.getLength() == 2) {
                lineNumber = Integer.parseInt(params.get("1"));
            }
        } else {
            throw new IllegalArgumentException("xref macro needs one or two paramaters");
        }
        XrefMapper.getInstance().expand(writer, klass, project, lineNumber);
    }
}

