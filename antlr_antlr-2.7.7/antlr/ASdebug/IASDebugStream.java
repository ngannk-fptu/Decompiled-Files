/*
 * Decompiled with CFR 0.152.
 */
package antlr.ASdebug;

import antlr.ASdebug.TokenOffsetInfo;
import antlr.Token;

public interface IASDebugStream {
    public String getEntireText();

    public TokenOffsetInfo getOffsetInfo(Token var1);
}

