/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 */
package org.hibernate.hql.internal.ast;

import antlr.RecognitionException;

public interface ErrorReporter {
    public void reportError(RecognitionException var1);

    public void reportError(String var1);

    public void reportWarning(String var1);
}

