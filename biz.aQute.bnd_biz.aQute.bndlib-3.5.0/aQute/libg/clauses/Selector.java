/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.clauses;

import aQute.libg.clauses.Clause;
import aQute.libg.clauses.Clauses;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector {
    Pattern pattern;
    String instruction;
    boolean negated;
    Clause clause;

    public Selector(String instruction, boolean negated) {
        this.instruction = instruction;
        this.negated = negated;
    }

    public boolean matches(String value) {
        if (this.pattern == null) {
            this.pattern = Pattern.compile(this.instruction);
        }
        Matcher m = this.pattern.matcher(value);
        return m.matches();
    }

    public boolean isNegated() {
        return this.negated;
    }

    public String getPattern() {
        return this.instruction;
    }

    public static Selector getPattern(String string) {
        boolean negated = false;
        if (string.startsWith("!")) {
            negated = true;
            string = string.substring(1);
        }
        StringBuilder sb = new StringBuilder();
        block5: for (int c = 0; c < string.length(); ++c) {
            switch (string.charAt(c)) {
                case '.': {
                    sb.append("\\.");
                    continue block5;
                }
                case '*': {
                    sb.append(".*");
                    continue block5;
                }
                case '?': {
                    sb.append(".?");
                    continue block5;
                }
                default: {
                    sb.append(string.charAt(c));
                }
            }
        }
        string = sb.toString();
        if (string.endsWith("\\..*")) {
            sb.append("|");
            sb.append(string.substring(0, string.length() - 4));
        }
        return new Selector(sb.toString(), negated);
    }

    public String toString() {
        return this.getPattern();
    }

    public Clause getClause() {
        return this.clause;
    }

    public void setClause(Clause clause) {
        this.clause = clause;
    }

    public static List<Selector> getInstructions(Clauses clauses) {
        ArrayList<Selector> result = new ArrayList<Selector>();
        for (Map.Entry entry : clauses.entrySet()) {
            Selector instruction = Selector.getPattern((String)entry.getKey());
            result.add(instruction);
        }
        return result;
    }

    public static <T> List<T> select(Collection<T> domain, List<Selector> instructions) {
        ArrayList<T> result = new ArrayList<T>();
        block0: for (T value : domain) {
            for (Selector instruction : instructions) {
                if (!instruction.matches(value.toString())) continue;
                if (instruction.isNegated()) continue block0;
                result.add(value);
                continue block0;
            }
        }
        return result;
    }
}

