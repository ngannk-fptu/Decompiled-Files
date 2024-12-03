/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.radeox.macro.BaseMacro;
import org.radeox.util.Encoder;

public abstract class Preserved
extends BaseMacro {
    private Map special = new HashMap();
    private String specialString = "";

    protected void addSpecial(char c) {
        this.addSpecial("" + c, Encoder.toEntity(c));
    }

    protected void addSpecial(String c, String replacement) {
        this.specialString = this.specialString + c;
        this.special.put(c, replacement);
    }

    protected String replace(String source) {
        StringBuffer tmp = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(source, this.specialString, true);
        while (stringTokenizer.hasMoreTokens()) {
            String current = stringTokenizer.nextToken();
            if (this.special.containsKey(current)) {
                current = (String)this.special.get(current);
            }
            tmp.append(current);
        }
        return tmp.toString();
    }
}

