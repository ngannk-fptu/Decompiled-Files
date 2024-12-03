/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.header;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.libg.generics.Create;
import aQute.libg.qtokens.QuotedTokenizer;
import aQute.service.reporter.Reporter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class OSGiHeader {
    public static final Pattern TOKEN_P = Pattern.compile("[-a-zA-Z0-9_]+");

    public static Parameters parseHeader(String value) {
        return OSGiHeader.parseHeader(value, null);
    }

    public static Parameters parseHeader(String value, Reporter logger) {
        return OSGiHeader.parseHeader(value, logger, new Parameters());
    }

    public static Parameters parseHeader(String value, Reporter logger, Parameters result) {
        if (value == null || value.trim().length() == 0) {
            return result;
        }
        QuotedTokenizer qt = new QuotedTokenizer(value, ";=,");
        char del = '\u0000';
        do {
            boolean hadAttribute = false;
            Attrs clause = new Attrs();
            List<String> aliases = Create.list();
            String name = qt.nextToken(",;");
            del = qt.getSeparator();
            if (name == null || name.length() == 0) {
                if (logger != null && logger.isPedantic()) {
                    logger.warning("Empty clause, usually caused by repeating a comma without any name field or by having spaces after the backslash of a property file: %s", value);
                }
                if (name != null) continue;
                break;
            }
            name = name.trim();
            aliases.add(name);
            while (del == ';') {
                String adname = qt.nextToken();
                del = qt.getSeparator();
                if (del != '=') {
                    if (hadAttribute && logger != null) {
                        logger.error("Header contains name field after attribute or directive: %s from %s. Name fields must be consecutive, separated by a ';' like a;b;c;x=3;y=4", adname, value);
                    }
                    if (adname == null || adname.length() <= 0) continue;
                    aliases.add(adname.trim());
                    continue;
                }
                String advalue = qt.nextToken();
                if (clause.containsKey(adname)) {
                    if (result.allowDuplicateAttributes()) {
                        while (clause.containsKey(adname)) {
                            adname = adname + "~";
                        }
                    } else if (logger != null && logger.isPedantic()) {
                        logger.warning("Duplicate attribute/directive name %s in %s. This attribute/directive will be ignored", adname, value);
                    }
                }
                if (advalue == null) {
                    if (logger != null) {
                        logger.error("No value after '=' sign for attribute %s", adname);
                    }
                    advalue = "";
                }
                clause.put(adname.trim(), advalue);
                del = qt.getSeparator();
                hadAttribute = true;
            }
            for (String clauseName : aliases) {
                if (result.containsKey(clauseName)) {
                    if (logger != null && logger.isPedantic()) {
                        logger.warning("Duplicate name %s used in header: '%s'. Duplicate names are specially marked in Bnd with a ~ at the end (which is stripped at printing time).", clauseName, value);
                    }
                    while (result.containsKey(clauseName)) {
                        clauseName = clauseName + "~";
                    }
                }
                result.put(clauseName, clause);
            }
        } while (del == ',');
        return result;
    }

    public static Attrs parseProperties(String input) {
        return OSGiHeader.parseProperties(input, null);
    }

    public static Attrs parseProperties(String input, Reporter logger) {
        if (input == null || input.trim().length() == 0) {
            return new Attrs();
        }
        Attrs result = new Attrs();
        QuotedTokenizer qt = new QuotedTokenizer(input, "=,");
        int del = 44;
        while (del == 44) {
            String key = qt.nextToken(",=");
            if (key == null) {
                if (logger == null) {
                    throw new IllegalArgumentException("Trailing comma found, forgot to escape the newline? Input=" + input);
                }
                logger.error("Trailing comma found, forgot to escape the newline? Input=", input);
                break;
            }
            String value = "";
            del = qt.getSeparator();
            if (del == 61) {
                value = qt.nextToken(",=");
                if (value == null) {
                    value = "";
                }
                del = qt.getSeparator();
            }
            result.put(key.trim(), value);
        }
        if (del != 0) {
            if (logger == null) {
                throw new IllegalArgumentException("Invalid syntax for properties: " + input);
            }
            logger.error("Invalid syntax for properties: %s", input);
        }
        return result;
    }

    public static boolean quote(Appendable sb, String value) throws IOException {
        boolean clean;
        if (value.startsWith("\\\"")) {
            value = value.substring(2);
        }
        if (value.endsWith("\\\"")) {
            value = value.substring(0, value.length() - 2);
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        boolean bl = clean = value.length() >= 2 && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"' || TOKEN_P.matcher(value).matches();
        if (!clean) {
            sb.append("\"");
        }
        block3: for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
                case '\"': {
                    sb.append('\\').append('\"');
                    continue block3;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        if (!clean) {
            sb.append("\"");
        }
        return clean;
    }
}

