/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.weaver.tools.PointcutParser
 *  org.aspectj.weaver.tools.TypePatternMatcher
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
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
        Assert.notNull((Object)typePattern, (String)"Type pattern must not be null");
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().parseTypePattern(this.replaceBooleanOperators(typePattern));
    }

    public String getTypePattern() {
        return this.typePattern;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        Assert.state((this.aspectJTypePatternMatcher != null ? 1 : 0) != 0, (String)"No type pattern has been set");
        return this.aspectJTypePatternMatcher.matches(clazz);
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace((String)pcExpr, (String)" and ", (String)" && ");
        result = StringUtils.replace((String)result, (String)" or ", (String)" || ");
        return StringUtils.replace((String)result, (String)" not ", (String)" ! ");
    }

    public boolean equals(Object other) {
        return this == other || other instanceof TypePatternClassFilter && ObjectUtils.nullSafeEquals((Object)this.typePattern, (Object)((TypePatternClassFilter)other).typePattern);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.typePattern);
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.typePattern;
    }
}

