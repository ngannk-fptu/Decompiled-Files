/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.Token;

public class NodeUtils {
    public static String specialText(Token t) {
        if (t.specialToken == null || t.specialToken.image.startsWith("##")) {
            return "";
        }
        return NodeUtils.getSpecialText(t).toString();
    }

    public static StrBuilder getSpecialText(Token t) {
        StrBuilder sb = new StrBuilder();
        Token tmp_t = t.specialToken;
        while (tmp_t.specialToken != null) {
            tmp_t = tmp_t.specialToken;
        }
        while (tmp_t != null) {
            String st = tmp_t.image;
            int is = st.length();
            for (int i = 0; i < is; ++i) {
                int j;
                char c = st.charAt(i);
                if (c == '#' || c == '$') {
                    sb.append(c);
                }
                if (c != '\\') continue;
                boolean ok = true;
                boolean term = false;
                ok = true;
                for (j = i; ok && j < is; ++j) {
                    char cc = st.charAt(j);
                    if (cc == '\\') continue;
                    if (cc == '$') {
                        term = true;
                        ok = false;
                        continue;
                    }
                    ok = false;
                }
                if (!term) continue;
                String foo = st.substring(i, j);
                sb.append(foo);
                i = j;
            }
            tmp_t = tmp_t.next;
        }
        return sb;
    }

    public static String tokenLiteral(Token t) {
        if (t.kind == 24) {
            return "";
        }
        if (t.specialToken == null || t.specialToken.image.startsWith("##")) {
            return t.image;
        }
        StrBuilder special = NodeUtils.getSpecialText(t);
        if (special.length() > 0) {
            return special.append(t.image).toString();
        }
        return t.image;
    }

    public static String interpolate(String argStr, Context vars) throws MethodInvocationException {
        if (argStr.indexOf(36) == -1) {
            return argStr;
        }
        StrBuilder argBuf = new StrBuilder();
        int cIdx = 0;
        int is = argStr.length();
        while (cIdx < is) {
            char ch = argStr.charAt(cIdx);
            if (ch == '$') {
                StrBuilder nameBuf = new StrBuilder();
                ++cIdx;
                while (cIdx < is) {
                    ch = argStr.charAt(cIdx);
                    if (ch == '_' || ch == '-' || Character.isLetterOrDigit(ch)) {
                        nameBuf.append(ch);
                    } else if (ch != '{' && ch != '}') break;
                    ++cIdx;
                }
                if (nameBuf.length() <= 0) continue;
                Object value = vars.get(nameBuf.toString());
                if (value == null) {
                    argBuf.append("$").append(nameBuf.toString());
                    continue;
                }
                argBuf.append(value.toString());
                continue;
            }
            argBuf.append(ch);
            ++cIdx;
        }
        return argBuf.toString();
    }
}

