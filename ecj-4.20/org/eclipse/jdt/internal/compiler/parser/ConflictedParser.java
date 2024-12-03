/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

public interface ConflictedParser {
    public boolean atConflictScenario(int var1);

    public boolean isParsingModuleDeclaration();

    public boolean isParsingJava14();
}

