/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.table;

import java.util.StringTokenizer;
import org.radeox.macro.table.Table;

public class TableBuilder {
    public static Table build(String content) {
        Table table = new Table();
        StringTokenizer tokenizer = new StringTokenizer(content, "|\n", true);
        String lastToken = null;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if ("\n".equals(token)) {
                if (null == lastToken || "|".equals(lastToken)) {
                    table.addCell(" ");
                }
                table.newRow();
            } else if (!"|".equals(token)) {
                table.addCell(token);
            } else if (null == lastToken || "|".equals(lastToken)) {
                table.addCell(" ");
            }
            lastToken = token;
        }
        return table;
    }
}

