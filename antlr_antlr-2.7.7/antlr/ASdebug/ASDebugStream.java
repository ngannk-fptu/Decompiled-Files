/*
 * Decompiled with CFR 0.152.
 */
package antlr.ASdebug;

import antlr.ASdebug.IASDebugStream;
import antlr.ASdebug.TokenOffsetInfo;
import antlr.Token;
import antlr.TokenStream;

public final class ASDebugStream {
    public static String getEntireText(TokenStream tokenStream) {
        if (tokenStream instanceof IASDebugStream) {
            IASDebugStream iASDebugStream = (IASDebugStream)((Object)tokenStream);
            return iASDebugStream.getEntireText();
        }
        return null;
    }

    public static TokenOffsetInfo getOffsetInfo(TokenStream tokenStream, Token token) {
        if (tokenStream instanceof IASDebugStream) {
            IASDebugStream iASDebugStream = (IASDebugStream)((Object)tokenStream);
            return iASDebugStream.getOffsetInfo(token);
        }
        return null;
    }
}

