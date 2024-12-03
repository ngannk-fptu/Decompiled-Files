/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public interface ANTLRErrorStrategy {
    public void reset(Parser var1);

    public Token recoverInline(Parser var1) throws RecognitionException;

    public void recover(Parser var1, RecognitionException var2) throws RecognitionException;

    public void sync(Parser var1) throws RecognitionException;

    public boolean inErrorRecoveryMode(Parser var1);

    public void reportMatch(Parser var1);

    public void reportError(Parser var1, RecognitionException var2);
}

