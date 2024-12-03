/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.stax;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom2.DocType;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DTDParser {
    private static final String metapattern = " os <!DOCTYPE ms ( name )( ms ((SYSTEM ms  id )|(PUBLIC ms  id ( ms  id )?)))?( os \\[( internal )\\])? os > os ";
    private static final Pattern pattern = DTDParser.buildPattern(DTDParser.populatePatterns(), " os <!DOCTYPE ms ( name )( ms ((SYSTEM ms  id )|(PUBLIC ms  id ( ms  id )?)))?( os \\[( internal )\\])? os > os ");

    private static final HashMap<String, String> populatePatterns() {
        HashMap<String, String> p = new HashMap<String, String>();
        p.put("name", "[^ \\n\\r\\t\\[>]+");
        p.put("ms", "[ \\n\\r\\t]+");
        p.put("os", "[ \\n\\r\\t]*");
        p.put("id", "(('([^']*)')|(\"([^\"]*)\"))");
        p.put("internal", ".*");
        return p;
    }

    private static final Pattern buildPattern(HashMap<String, String> map, String input) {
        Pattern search = Pattern.compile(" (\\w+) ");
        Matcher mat = search.matcher(input);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (mat.find()) {
            String rep = map.get(mat.group(1));
            sb.append(input.substring(pos, mat.start()));
            sb.append(rep);
            pos = mat.end();
        }
        sb.append(input.substring(pos));
        return Pattern.compile(sb.toString(), 32);
    }

    private static final String getGroup(Matcher mat, int ... groups) {
        for (int g : groups) {
            String s = mat.group(g);
            if (s == null) continue;
            return s;
        }
        return null;
    }

    private static final boolean isWhite(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private static String formatInternal(String internal) {
        StringBuilder sb = new StringBuilder(internal.length());
        char quote = ' ';
        boolean white = true;
        for (char ch : internal.toCharArray()) {
            if (quote == ' ') {
                if (DTDParser.isWhite(ch)) {
                    if (white) continue;
                    sb.append(' ');
                    white = true;
                    continue;
                }
                if (ch == '\'' || ch == '\"') {
                    quote = ch;
                } else if (ch == '<') {
                    sb.append("  ");
                }
                if (ch == '>') {
                    if (white) {
                        sb.setCharAt(sb.length() - 1, ch);
                    } else {
                        sb.append(ch);
                    }
                    sb.append('\n');
                    white = true;
                    continue;
                }
                sb.append(ch);
                white = false;
                continue;
            }
            if (ch == quote) {
                quote = ' ';
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static DocType parse(String input, JDOMFactory factory) throws JDOMException {
        Matcher mat = pattern.matcher(input);
        if (!mat.matches()) {
            throw new JDOMException("Doctype input does not appear to be valid: " + input);
        }
        String docemt = mat.group(1);
        String sysid = DTDParser.getGroup(mat, 7, 9, 19, 21);
        String pubid = DTDParser.getGroup(mat, 13, 15);
        String internal = DTDParser.getGroup(mat, 23);
        DocType dt = null;
        dt = pubid != null ? factory.docType(docemt, pubid, sysid) : (sysid != null ? factory.docType(docemt, sysid) : factory.docType(docemt));
        if (internal != null) {
            dt.setInternalSubset(DTDParser.formatInternal(internal));
        }
        return dt;
    }

    private DTDParser() {
    }
}

