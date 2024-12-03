/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class AnsiTrimEmulationFunction
extends AbstractAnsiTrimEmulationFunction {
    public static final String LTRIM = "ltrim";
    public static final String RTRIM = "rtrim";
    public static final String REPLACE = "replace";
    public static final String SPACE_PLACEHOLDER = "${space}$";
    public static final String LEADING_SPACE_TRIM_TEMPLATE = "ltrim(?1)";
    public static final String TRAILING_SPACE_TRIM_TEMPLATE = "rtrim(?1)";
    public static final String BOTH_SPACE_TRIM_TEMPLATE = "ltrim(rtrim(?1))";
    public static final String BOTH_SPACE_TRIM_FROM_TEMPLATE = "ltrim(rtrim(?2))";
    public static final String LEADING_TRIM_TEMPLATE = "replace(replace(ltrim(replace(replace(?1,' ','${space}$'),?2,' ')),' ',?2),'${space}$',' ')";
    public static final String TRAILING_TRIM_TEMPLATE = "replace(replace(rtrim(replace(replace(?1,' ','${space}$'),?2,' ')),' ',?2),'${space}$',' ')";
    public static final String BOTH_TRIM_TEMPLATE = "replace(replace(ltrim(rtrim(replace(replace(?1,' ','${space}$'),?2,' '))),' ',?2),'${space}$',' ')";
    private final SQLFunction leadingSpaceTrim;
    private final SQLFunction trailingSpaceTrim;
    private final SQLFunction bothSpaceTrim;
    private final SQLFunction bothSpaceTrimFrom;
    private final SQLFunction leadingTrim;
    private final SQLFunction trailingTrim;
    private final SQLFunction bothTrim;

    public AnsiTrimEmulationFunction() {
        this(LTRIM, RTRIM, REPLACE);
    }

    public AnsiTrimEmulationFunction(String ltrimFunctionName, String rtrimFunctionName, String replaceFunctionName) {
        this.leadingSpaceTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, LEADING_SPACE_TRIM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName));
        this.trailingSpaceTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, TRAILING_SPACE_TRIM_TEMPLATE.replaceAll(RTRIM, rtrimFunctionName));
        this.bothSpaceTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, BOTH_SPACE_TRIM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName).replaceAll(RTRIM, rtrimFunctionName));
        this.bothSpaceTrimFrom = new SQLFunctionTemplate(StandardBasicTypes.STRING, BOTH_SPACE_TRIM_FROM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName).replaceAll(RTRIM, rtrimFunctionName));
        this.leadingTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, LEADING_TRIM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName).replaceAll(RTRIM, rtrimFunctionName).replaceAll(REPLACE, replaceFunctionName));
        this.trailingTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, TRAILING_TRIM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName).replaceAll(RTRIM, rtrimFunctionName).replaceAll(REPLACE, replaceFunctionName));
        this.bothTrim = new SQLFunctionTemplate(StandardBasicTypes.STRING, BOTH_TRIM_TEMPLATE.replaceAll(LTRIM, ltrimFunctionName).replaceAll(RTRIM, rtrimFunctionName).replaceAll(REPLACE, replaceFunctionName));
    }

    @Override
    protected SQLFunction resolveBothSpaceTrimFunction() {
        return this.bothSpaceTrim;
    }

    @Override
    protected SQLFunction resolveBothSpaceTrimFromFunction() {
        return this.bothSpaceTrimFrom;
    }

    @Override
    protected SQLFunction resolveLeadingSpaceTrimFunction() {
        return this.leadingSpaceTrim;
    }

    @Override
    protected SQLFunction resolveTrailingSpaceTrimFunction() {
        return this.trailingSpaceTrim;
    }

    @Override
    protected SQLFunction resolveBothTrimFunction() {
        return this.bothTrim;
    }

    @Override
    protected SQLFunction resolveLeadingTrimFunction() {
        return this.leadingTrim;
    }

    @Override
    protected SQLFunction resolveTrailingTrimFunction() {
        return this.trailingTrim;
    }
}

