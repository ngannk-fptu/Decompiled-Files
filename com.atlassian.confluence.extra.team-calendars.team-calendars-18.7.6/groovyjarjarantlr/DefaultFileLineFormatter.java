/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.FileLineFormatter;

public class DefaultFileLineFormatter
extends FileLineFormatter {
    public String getFormatString(String string, int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        if (string != null) {
            stringBuffer.append(string + ":");
        }
        if (n != -1) {
            if (string == null) {
                stringBuffer.append("line ");
            }
            stringBuffer.append(n);
            if (n2 != -1) {
                stringBuffer.append(":" + n2);
            }
            stringBuffer.append(":");
        }
        stringBuffer.append(" ");
        return stringBuffer.toString();
    }
}

