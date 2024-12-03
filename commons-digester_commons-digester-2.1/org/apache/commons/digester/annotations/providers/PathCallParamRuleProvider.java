/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.PathCallParamRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.reflect.MethodArgument;
import org.apache.commons.digester.annotations.rules.PathCallParam;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PathCallParamRuleProvider
implements AnnotationRuleProvider<PathCallParam, MethodArgument, PathCallParamRule> {
    private int index;

    @Override
    public void init(PathCallParam annotation, MethodArgument element) {
        this.index = element.getIndex();
    }

    @Override
    public PathCallParamRule get() {
        return new PathCallParamRule(this.index);
    }
}

