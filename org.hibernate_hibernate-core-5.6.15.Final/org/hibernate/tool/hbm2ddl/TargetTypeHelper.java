/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.util.EnumSet;
import org.hibernate.tool.hbm2ddl.Target;
import org.hibernate.tool.schema.TargetType;

public class TargetTypeHelper {
    public static EnumSet<TargetType> parseLegacyCommandLineOptions(boolean script, boolean export, String outputFile) {
        EnumSet<TargetType> options = EnumSet.noneOf(TargetType.class);
        Target target = Target.interpret(script, export);
        if (outputFile != null) {
            options.add(TargetType.SCRIPT);
        }
        if (target.doScript()) {
            options.add(TargetType.STDOUT);
        }
        if (target.doExport()) {
            options.add(TargetType.DATABASE);
        }
        return options;
    }

    public static EnumSet<TargetType> parseCommandLineOptions(String targetTypeText) {
        EnumSet<TargetType> options = EnumSet.noneOf(TargetType.class);
        if (!targetTypeText.equalsIgnoreCase("none")) {
            for (String option : targetTypeText.split(",")) {
                if (option.equalsIgnoreCase("database")) {
                    options.add(TargetType.DATABASE);
                    continue;
                }
                if (option.equalsIgnoreCase("stdout")) {
                    options.add(TargetType.STDOUT);
                    continue;
                }
                if (option.equalsIgnoreCase("script")) {
                    options.add(TargetType.SCRIPT);
                    continue;
                }
                throw new IllegalArgumentException("Unrecognized --target option : " + option);
            }
        }
        return options;
    }
}

