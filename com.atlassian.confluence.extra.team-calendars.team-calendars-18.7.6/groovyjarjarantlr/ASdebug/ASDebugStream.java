/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.ASdebug;

import groovyjarjarantlr.ASdebug.IASDebugStream;
import groovyjarjarantlr.ASdebug.TokenOffsetInfo;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;

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

