/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.OutputFormat;
import freemarker.template.Version;

public interface ParserConfiguration {
    public int getTagSyntax();

    public int getInterpolationSyntax();

    public int getNamingConvention();

    public boolean getWhitespaceStripping();

    public ArithmeticEngine getArithmeticEngine();

    public boolean getStrictSyntaxMode();

    public int getAutoEscapingPolicy();

    public OutputFormat getOutputFormat();

    public boolean getRecognizeStandardFileExtensions();

    public Version getIncompatibleImprovements();

    public int getTabSize();
}

