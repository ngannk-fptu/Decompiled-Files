/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.weaver.tools.PointcutParser
 *  org.aspectj.weaver.tools.TypePatternMatcher
 */
package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class TypePatternClassFilter
implements ClassFilter {
    private String typePattern = "";
    @Nullable
    private TypePatternMatcher aspectJTypePatternMatcher;

    public TypePatternClassFilter() {
    }

    public TypePatternClassFilter(String typePattern) {
        this.setTypePattern(typePattern);
    }

    public void setTypePattern(String typePattern) {
        Assert.notNull((Object)typePattern, "Type pattern must not be null");
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().parseTypePattern(this.replaceBooleanOperators(typePattern));
    }

    public String getTypePattern() {
        return this.typePattern;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        Assert.state(this.aspectJTypePatternMatcher != null, "No type pattern has been set");
        return this.aspectJTypePatternMatcher.matches(clazz);
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        result = StringUtils.replace(result, " or ", " || ");
        return StringUtils.replace(result, " not ", " ! ");
    }

    public boolean equals(Object other) {
        return this == other || other instanceof TypePatternClassFilter && ObjectUtils.nullSafeEquals(this.typePattern, ((TypePatternClassFilter)other).typePattern);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.typePattern);
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.typePattern;
    }
}

