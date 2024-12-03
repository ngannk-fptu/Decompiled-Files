/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.MethodNode;

public class GeneratorContext {
    private int innerClassIdx = 1;
    private int closureClassIdx = 1;
    private CompileUnit compileUnit;
    private static final int MIN_ENCODING = 32;
    private static final int MAX_ENCODING = 93;
    private static final boolean[] CHARACTERS_TO_ENCODE = new boolean[62];

    public GeneratorContext(CompileUnit compileUnit) {
        this.compileUnit = compileUnit;
    }

    public GeneratorContext(CompileUnit compileUnit, int innerClassOffset) {
        this.compileUnit = compileUnit;
        this.innerClassIdx = innerClassOffset;
    }

    public int getNextInnerClassIdx() {
        return this.innerClassIdx++;
    }

    public CompileUnit getCompileUnit() {
        return this.compileUnit;
    }

    public String getNextClosureInnerName(ClassNode owner, ClassNode enclosingClass, MethodNode enclosingMethod) {
        String methodName = "";
        if (enclosingMethod != null) {
            methodName = enclosingMethod.getName();
            methodName = enclosingClass.isDerivedFrom(ClassHelper.CLOSURE_TYPE) ? "" : "_" + GeneratorContext.encodeAsValidClassName(methodName);
        }
        return methodName + "_closure" + this.closureClassIdx++;
    }

    public static String encodeAsValidClassName(String name) {
        int l = name.length();
        StringBuilder b = null;
        int lastEscape = -1;
        for (int i = 0; i < l; ++i) {
            int encodeIndex = name.charAt(i) - 32;
            if (encodeIndex < 0 || encodeIndex >= CHARACTERS_TO_ENCODE.length || !CHARACTERS_TO_ENCODE[encodeIndex]) continue;
            if (b == null) {
                b = new StringBuilder(name.length() + 3);
                b.append(name, 0, i);
            } else {
                b.append(name, lastEscape + 1, i);
            }
            b.append('_');
            lastEscape = i;
        }
        if (b == null) {
            return name.toString();
        }
        if (lastEscape == -1) {
            throw new GroovyBugError("unexpected escape char control flow in " + name);
        }
        b.append(name, lastEscape + 1, l);
        return b.toString();
    }

    static {
        GeneratorContext.CHARACTERS_TO_ENCODE[0] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[1] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[15] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[14] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[27] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[4] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[28] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[30] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[59] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[61] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[26] = true;
        GeneratorContext.CHARACTERS_TO_ENCODE[60] = true;
    }
}

