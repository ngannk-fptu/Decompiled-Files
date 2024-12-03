/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

public class Phases {
    public static final int INITIALIZATION = 1;
    public static final int PARSING = 2;
    public static final int CONVERSION = 3;
    public static final int SEMANTIC_ANALYSIS = 4;
    public static final int CANONICALIZATION = 5;
    public static final int INSTRUCTION_SELECTION = 6;
    public static final int CLASS_GENERATION = 7;
    public static final int OUTPUT = 8;
    public static final int FINALIZATION = 9;
    public static final int ALL = 9;
    public static final String[] descriptions = new String[]{"startup", "initialization", "parsing", "conversion", "semantic analysis", "canonicalization", "instruction selection", "class generation", "output", "cleanup"};

    public static String getDescription(int phase) {
        return descriptions[phase];
    }
}

