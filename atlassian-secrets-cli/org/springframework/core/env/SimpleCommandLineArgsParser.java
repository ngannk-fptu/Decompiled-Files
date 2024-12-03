/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import org.springframework.core.env.CommandLineArgs;

class SimpleCommandLineArgsParser {
    SimpleCommandLineArgsParser() {
    }

    public CommandLineArgs parse(String ... args) {
        CommandLineArgs commandLineArgs = new CommandLineArgs();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String optionName;
                String optionText = arg.substring(2, arg.length());
                String optionValue = null;
                if (optionText.contains("=")) {
                    optionName = optionText.substring(0, optionText.indexOf(61));
                    optionValue = optionText.substring(optionText.indexOf(61) + 1, optionText.length());
                } else {
                    optionName = optionText;
                }
                if (optionName.isEmpty() || optionValue != null && optionValue.isEmpty()) {
                    throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                }
                commandLineArgs.addOptionArg(optionName, optionValue);
                continue;
            }
            commandLineArgs.addNonOptionArg(arg);
        }
        return commandLineArgs;
    }
}

