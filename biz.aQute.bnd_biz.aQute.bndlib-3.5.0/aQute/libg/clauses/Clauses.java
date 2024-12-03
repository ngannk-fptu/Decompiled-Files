/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.clauses;

import aQute.libg.clauses.Clause;
import aQute.libg.log.Logger;
import aQute.libg.qtokens.QuotedTokenizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Clauses
extends LinkedHashMap<String, Map<String, String>> {
    private static final long serialVersionUID = 1L;

    public static Clauses parse(String value, Logger logger) {
        char del;
        if (value == null || value.trim().length() == 0) {
            return new Clauses();
        }
        Clauses result = new Clauses();
        QuotedTokenizer qt = new QuotedTokenizer(value, ";=,");
        do {
            boolean hadAttribute = false;
            Clause clause = new Clause();
            ArrayList<String> aliases = new ArrayList<String>();
            aliases.add(qt.nextToken());
            del = qt.getSeparator();
            while (del == ';') {
                String adname = qt.nextToken();
                del = qt.getSeparator();
                if (del != '=') {
                    if (hadAttribute) {
                        throw new IllegalArgumentException("Header contains name field after attribute or directive: " + adname + " from " + value);
                    }
                    aliases.add(adname);
                    continue;
                }
                String advalue = qt.nextToken();
                clause.put(adname, advalue);
                del = qt.getSeparator();
                hadAttribute = true;
            }
            for (String packageName : aliases) {
                if (result.containsKey(packageName)) {
                    if (logger == null) continue;
                    logger.warning("Duplicate package name in header: " + packageName + ". Multiple package names in one clause not supported in Bnd.", new Object[0]);
                    continue;
                }
                result.put(packageName, clause);
            }
        } while (del == ',');
        return result;
    }
}

