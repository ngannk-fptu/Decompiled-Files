/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.OutputFormat;
import freemarker.core.ParserConfiguration;
import freemarker.template.Version;

public final class _ParserConfigurationWithInheritedFormat
implements ParserConfiguration {
    private final OutputFormat outputFormat;
    private final Integer autoEscapingPolicy;
    private final ParserConfiguration wrappedPCfg;

    public _ParserConfigurationWithInheritedFormat(ParserConfiguration wrappedPCfg, OutputFormat outputFormat, Integer autoEscapingPolicy) {
        this.outputFormat = outputFormat;
        this.autoEscapingPolicy = autoEscapingPolicy;
        this.wrappedPCfg = wrappedPCfg;
    }

    @Override
    public boolean getWhitespaceStripping() {
        return this.wrappedPCfg.getWhitespaceStripping();
    }

    @Override
    public int getTagSyntax() {
        return this.wrappedPCfg.getTagSyntax();
    }

    @Override
    public int getInterpolationSyntax() {
        return this.wrappedPCfg.getInterpolationSyntax();
    }

    @Override
    public boolean getStrictSyntaxMode() {
        return this.wrappedPCfg.getStrictSyntaxMode();
    }

    @Override
    public OutputFormat getOutputFormat() {
        return this.outputFormat != null ? this.outputFormat : this.wrappedPCfg.getOutputFormat();
    }

    @Override
    public boolean getRecognizeStandardFileExtensions() {
        return false;
    }

    @Override
    public int getNamingConvention() {
        return this.wrappedPCfg.getNamingConvention();
    }

    @Override
    public Version getIncompatibleImprovements() {
        return this.wrappedPCfg.getIncompatibleImprovements();
    }

    @Override
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy != null ? this.autoEscapingPolicy.intValue() : this.wrappedPCfg.getAutoEscapingPolicy();
    }

    @Override
    public ArithmeticEngine getArithmeticEngine() {
        return this.wrappedPCfg.getArithmeticEngine();
    }

    @Override
    public int getTabSize() {
        return this.wrappedPCfg.getTabSize();
    }
}

