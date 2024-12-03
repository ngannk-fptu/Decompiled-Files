/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.util.StringUtils;

public class JspNameMangler
implements JspMangler {
    public static final String[] keywords = new String[]{"assert", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

    @Override
    public String mapJspToJavaName(File jspFile) {
        return this.mapJspToBaseName(jspFile) + ".java";
    }

    private String mapJspToBaseName(File jspFile) {
        String className = this.stripExtension(jspFile);
        for (String keyword : keywords) {
            if (!className.equals(keyword)) continue;
            className = className + "%";
            break;
        }
        StringBuilder modifiedClassName = new StringBuilder(className.length());
        char firstChar = className.charAt(0);
        if (Character.isJavaIdentifierStart(firstChar)) {
            modifiedClassName.append(firstChar);
        } else {
            modifiedClassName.append(JspNameMangler.mangleChar(firstChar));
        }
        for (char subChar : className.substring(1).toCharArray()) {
            if (Character.isJavaIdentifierPart(subChar)) {
                modifiedClassName.append(subChar);
                continue;
            }
            modifiedClassName.append(JspNameMangler.mangleChar(subChar));
        }
        return modifiedClassName.toString();
    }

    private String stripExtension(File jspFile) {
        return StringUtils.removeSuffix(jspFile.getName(), ".jsp");
    }

    private static String mangleChar(char ch) {
        if (ch == File.separatorChar) {
            ch = (char)47;
        }
        String s = Integer.toHexString(ch);
        int nzeros = 5 - s.length();
        char[] result = new char[6];
        result[0] = 95;
        for (int i = 1; i <= nzeros; ++i) {
            result[i] = 48;
        }
        int resultIndex = 0;
        for (int i = nzeros + 1; i < 6; ++i) {
            result[i] = s.charAt(resultIndex++);
        }
        return new String(result);
    }

    @Override
    public String mapPath(String path) {
        return null;
    }
}

