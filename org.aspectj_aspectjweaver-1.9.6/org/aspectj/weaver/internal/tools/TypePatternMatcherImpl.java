/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.internal.tools;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.tools.TypePatternMatcher;

public class TypePatternMatcherImpl
implements TypePatternMatcher {
    private final TypePattern pattern;
    private final World world;

    public TypePatternMatcherImpl(TypePattern pattern, World world) {
        this.pattern = pattern;
        this.world = world;
    }

    @Override
    public boolean matches(Class aClass) {
        ResolvedType rt = ReflectionBasedReferenceTypeDelegateFactory.resolveTypeInWorld(aClass, this.world);
        return this.pattern.matchesStatically(rt);
    }
}

