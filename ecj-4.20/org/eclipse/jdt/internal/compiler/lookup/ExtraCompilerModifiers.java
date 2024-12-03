/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

public interface ExtraCompilerModifiers {
    public static final int AccJustFlag = 65535;
    public static final int AccDefaultMethod = 65536;
    public static final int AccCompactConstructor = 0x800000;
    public static final int AccRestrictedAccess = 262144;
    public static final int AccFromClassFile = 524288;
    public static final int AccDefaultAbstract = 524288;
    public static final int AccDeprecatedImplicitly = 0x200000;
    public static final int AccAlternateModifierProblem = 0x400000;
    public static final int AccModifierProblem = 0x800000;
    public static final int AccSemicolonBody = 0x1000000;
    public static final int AccRecord = 0x1000000;
    public static final int AccUnresolved = 0x2000000;
    public static final int AccBlankFinal = 0x4000000;
    public static final int AccIsDefaultConstructor = 0x4000000;
    public static final int AccNonSealed = 0x4000000;
    public static final int AccLocallyUsed = 0x8000000;
    public static final int AccVisibilityMASK = 7;
    public static final int AccSealed = 0x10000000;
    public static final int AccOverriding = 0x10000000;
    public static final int AccImplementing = 0x20000000;
    public static final int AccGenericSignature = 0x40000000;
    public static final int AccPatternVariable = 0x10000000;
}

