/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

public interface IErrorHandlingPolicy {
    public boolean proceedOnErrors();

    public boolean stopOnFirstError();

    public boolean ignoreAllErrors();
}

