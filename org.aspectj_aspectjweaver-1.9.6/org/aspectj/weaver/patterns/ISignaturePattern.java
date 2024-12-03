/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.List;
import java.util.Map;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.IScope;

public interface ISignaturePattern {
    public static final byte PATTERN = 1;
    public static final byte NOT = 2;
    public static final byte OR = 3;
    public static final byte AND = 4;

    public boolean matches(Member var1, World var2, boolean var3);

    public ISignaturePattern parameterizeWith(Map<String, UnresolvedType> var1, World var2);

    public ISignaturePattern resolveBindings(IScope var1, Bindings var2);

    public List<ExactTypePattern> getExactDeclaringTypes();

    public boolean isMatchOnAnyName();

    public boolean couldEverMatch(ResolvedType var1);

    public boolean isStarAnnotation();
}

