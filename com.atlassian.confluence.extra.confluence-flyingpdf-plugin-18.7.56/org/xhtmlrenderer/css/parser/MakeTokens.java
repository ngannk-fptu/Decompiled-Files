/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MakeTokens {
    private static final String EOL = System.getProperty("line.separator");
    private static final String INPUT = "C:/eclipseWorkspaceQT/xhtmlrenderer/src/java/org/xhtmlrenderer/css/parser/tokens.txt";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void main(String[] args) throws IOException {
        String id;
        ArrayList<String> tokens = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            String s;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT)));
            while ((s = reader.readLine()) != null) {
                tokens.add(s);
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException s) {}
            }
        }
        StringBuffer buf = new StringBuffer();
        int offset = 1;
        for (String s : tokens) {
            id = s.substring(0, s.indexOf(44));
            buf.append("\tpublic static final int ");
            buf.append(id);
            buf.append(" = ");
            buf.append(offset);
            buf.append(";");
            buf.append(EOL);
            ++offset;
        }
        buf.append(EOL);
        for (String s : tokens) {
            id = s.substring(0, s.indexOf(44));
            String descr = s.substring(s.indexOf(44) + 1);
            buf.append("\tpublic static final Token TK_");
            buf.append(id);
            buf.append(" = new Token(");
            buf.append(id);
            buf.append(", \"");
            buf.append(id);
            buf.append("\", \"");
            buf.append(descr);
            buf.append("\");");
            buf.append(EOL);
            ++offset;
        }
        buf.append(EOL);
        System.out.println(buf.toString());
    }
}

