/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Option;

public final class OptionBuilder {
    private static String longopt;
    private static String description;
    private static String argName;
    private static boolean required;
    private static int numberOfArgs;
    private static Object type;
    private static boolean optionalArg;
    private static char valuesep;
    private static OptionBuilder instance;

    private OptionBuilder() {
    }

    private static void reset() {
        description = null;
        argName = "arg";
        longopt = null;
        type = null;
        required = false;
        numberOfArgs = -1;
        optionalArg = false;
        valuesep = '\u0000';
    }

    public static OptionBuilder withLongOpt(String newLongopt) {
        longopt = newLongopt;
        return instance;
    }

    public static OptionBuilder hasArg() {
        numberOfArgs = 1;
        return instance;
    }

    public static OptionBuilder hasArg(boolean hasArg) {
        numberOfArgs = hasArg ? 1 : -1;
        return instance;
    }

    public static OptionBuilder withArgName(String name) {
        argName = name;
        return instance;
    }

    public static OptionBuilder isRequired() {
        required = true;
        return instance;
    }

    public static OptionBuilder withValueSeparator(char sep) {
        valuesep = sep;
        return instance;
    }

    public static OptionBuilder withValueSeparator() {
        valuesep = (char)61;
        return instance;
    }

    public static OptionBuilder isRequired(boolean newRequired) {
        required = newRequired;
        return instance;
    }

    public static OptionBuilder hasArgs() {
        numberOfArgs = -2;
        return instance;
    }

    public static OptionBuilder hasArgs(int num) {
        numberOfArgs = num;
        return instance;
    }

    public static OptionBuilder hasOptionalArg() {
        numberOfArgs = 1;
        optionalArg = true;
        return instance;
    }

    public static OptionBuilder hasOptionalArgs() {
        numberOfArgs = -2;
        optionalArg = true;
        return instance;
    }

    public static OptionBuilder hasOptionalArgs(int numArgs) {
        numberOfArgs = numArgs;
        optionalArg = true;
        return instance;
    }

    public static OptionBuilder withType(Object newType) {
        type = newType;
        return instance;
    }

    public static OptionBuilder withDescription(String newDescription) {
        description = newDescription;
        return instance;
    }

    public static Option create(char opt) throws IllegalArgumentException {
        return OptionBuilder.create(String.valueOf(opt));
    }

    public static Option create() throws IllegalArgumentException {
        if (longopt == null) {
            OptionBuilder.reset();
            throw new IllegalArgumentException("must specify longopt");
        }
        return OptionBuilder.create(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Option create(String opt) throws IllegalArgumentException {
        Option option = null;
        try {
            option = new Option(opt, description);
            option.setLongOpt(longopt);
            option.setRequired(required);
            option.setOptionalArg(optionalArg);
            option.setArgs(numberOfArgs);
            option.setType(type);
            option.setValueSeparator(valuesep);
            option.setArgName(argName);
        }
        finally {
            OptionBuilder.reset();
        }
        return option;
    }

    static {
        numberOfArgs = -1;
        instance = new OptionBuilder();
    }
}

