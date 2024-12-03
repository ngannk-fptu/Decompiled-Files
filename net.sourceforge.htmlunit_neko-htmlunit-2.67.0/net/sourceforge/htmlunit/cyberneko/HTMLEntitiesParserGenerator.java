/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.htmlunit.cyberneko;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class HTMLEntitiesParserGenerator {
    public static void main(String[] args) {
        Properties props = new Properties();
        HTMLEntitiesParserGenerator.load0(props, "res/html_entities.properties");
        Object[] entities = new String[props.size()];
        String[] mapped = new String[props.size()];
        int i = 0;
        for (Object key : props.keySet()) {
            entities[i++] = key.toString();
        }
        Arrays.sort(entities);
        for (i = 0; i < entities.length; ++i) {
            mapped[i] = props.getProperty((String)entities[i]).toString();
        }
        String start = "";
        LinkedList<State> states = new LinkedList<State>();
        HTMLEntitiesParserGenerator.switchChar((String[])entities, mapped, start, states);
        int splitter = 1000;
        int count = 1;
        System.out.println("    private boolean parse" + count + "(final int current) {");
        System.out.println("        consumedCount++;");
        System.out.println("        switch (state) {");
        for (State state : states) {
            if (state.id >= count * splitter) {
                System.out.println("        }");
                System.out.println("        return false;");
                System.out.println("    }");
                System.out.println();
                System.out.println("    private boolean parse" + ++count + "(final int current) {");
                System.out.println("        consumedCount++;");
                System.out.println("        switch (state) {");
            }
            System.out.println("            case " + state.id + ":");
            System.out.print(state.switchCode);
            System.out.println("                break;");
        }
        System.out.println("        }");
        System.out.println("        return false;");
        System.out.println("    }");
        System.out.println();
        System.out.println("    public boolean parse(final int current) {");
        for (int j = 1; j <= count; ++j) {
            System.out.println("        if (state < " + j * splitter + ") {");
            System.out.println("            return parse" + j + "(current);");
            System.out.println("        }");
        }
        System.out.println("        return false;");
        System.out.println("    }");
    }

    private static int switchChar(String[] entities, String[] mapped, String start, List<State> states) {
        int n = -1;
        State state = new State();
        states.add(state);
        state.switchCode = "                switch (current) {\n";
        for (int i = 0; i < entities.length; ++i) {
            int stateId;
            char c;
            String entity = entities[i];
            if (!entity.startsWith(start) || entity.length() <= start.length() || entity.charAt(start.length()) == c) continue;
            c = entity.charAt(start.length());
            if (entity.length() - start.length() > 1) {
                state.switchCode = state.switchCode + "                    case '" + (char)c + "' :\n";
                stateId = HTMLEntitiesParserGenerator.switchChar(entities, mapped, start + (char)c, states);
                state.switchCode = state.switchCode + "                        state = " + stateId + ";\n";
                state.switchCode = state.switchCode + "                        return true;\n";
                continue;
            }
            state.switchCode = state.switchCode + "                    case '" + (char)c + "' : // " + entity + "\n";
            state.switchCode = state.switchCode + "                        match = \"" + HTMLEntitiesParserGenerator.escape(mapped[i]) + "\";\n";
            state.switchCode = state.switchCode + "                        matchLength = consumedCount;\n";
            if (i + 1 < entities.length && entities[i + 1].startsWith(start + (char)c) && entities[i + 1].length() > start.length() + 1) {
                stateId = HTMLEntitiesParserGenerator.switchChar(entities, mapped, start + (char)c, states);
                state.switchCode = state.switchCode + "                        state = " + stateId + ";\n";
                state.switchCode = state.switchCode + "                        return true;\n";
                continue;
            }
            if (c == ';') {
                state.switchCode = state.switchCode + "                        state = STATE_ENDS_WITH_SEMICOLON;\n";
                state.switchCode = state.switchCode + "                        return false;\n";
                continue;
            }
            state.switchCode = state.switchCode + "                        return false;\n";
        }
        state.switchCode = state.switchCode + "                }\n";
        return state.id;
    }

    private static String escape(String input) {
        StringBuilder b = new StringBuilder();
        for (char c : input.toCharArray()) {
            if ('\n' == c) {
                b.append("\\n");
                continue;
            }
            if (c == '\"') {
                b.append("\\\"");
                continue;
            }
            if (c == '\\') {
                b.append("\\\\");
                continue;
            }
            if (c >= '\u007f' || c <= ' ') {
                b.append("\\u").append(String.format("%04X", c));
                continue;
            }
            b.append(c);
        }
        return b.toString();
    }

    private static void load0(Properties props, String filename) {
        try (InputStream stream = HTMLEntitiesParserGenerator.class.getResourceAsStream(filename);){
            props.load(stream);
        }
        catch (IOException e) {
            System.err.println("error: unable to load resource \"" + filename + "\"");
        }
    }

    private static final class State {
        private static int idGen = 0;
        int id = idGen++;
        String switchCode;
    }
}

