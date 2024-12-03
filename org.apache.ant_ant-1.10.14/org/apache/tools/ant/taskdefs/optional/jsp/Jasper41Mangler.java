/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;

public class Jasper41Mangler
implements JspMangler {
    @Override
    public String mapJspToJavaName(File jspFile) {
        String jspUri = jspFile.getAbsolutePath();
        int start = jspUri.lastIndexOf(File.separatorChar) + 1;
        StringBuilder modifiedClassName = new StringBuilder(jspUri.length() - start);
        if (!Character.isJavaIdentifierStart(jspUri.charAt(start)) || jspUri.charAt(start) == '_') {
            modifiedClassName.append('_');
        }
        for (char ch : jspUri.substring(start).toCharArray()) {
            if (Character.isJavaIdentifierPart(ch)) {
                modifiedClassName.append(ch);
                continue;
            }
            if (ch == '.') {
                modifiedClassName.append('_');
                continue;
            }
            modifiedClassName.append(Jasper41Mangler.mangleChar(ch));
        }
        return modifiedClassName.toString();
    }

    private static String mangleChar(char ch) {
        int i;
        String s = Integer.toHexString(ch);
        int nzeros = 5 - s.length();
        char[] result = new char[6];
        result[0] = 95;
        for (i = 1; i <= nzeros; ++i) {
            result[i] = 48;
        }
        i = nzeros + 1;
        int j = 0;
        while (i < 6) {
            result[i] = s.charAt(j);
            ++i;
            ++j;
        }
        return new String(result);
    }

    @Override
    public String mapPath(String path) {
        return null;
    }
}

