/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeBlock;
import antlr.BlockWithImpliedExitPath;
import antlr.Grammar;
import antlr.Lookahead;

interface ToolErrorHandler {
    public void warnAltAmbiguity(Grammar var1, AlternativeBlock var2, boolean var3, int var4, Lookahead[] var5, int var6, int var7);

    public void warnAltExitAmbiguity(Grammar var1, BlockWithImpliedExitPath var2, boolean var3, int var4, Lookahead[] var5, int var6);
}

