/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.BlockWithImpliedExitPath;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;

interface ToolErrorHandler {
    public void warnAltAmbiguity(Grammar var1, AlternativeBlock var2, boolean var3, int var4, Lookahead[] var5, int var6, int var7);

    public void warnAltExitAmbiguity(Grammar var1, BlockWithImpliedExitPath var2, boolean var3, int var4, Lookahead[] var5, int var6);
}

