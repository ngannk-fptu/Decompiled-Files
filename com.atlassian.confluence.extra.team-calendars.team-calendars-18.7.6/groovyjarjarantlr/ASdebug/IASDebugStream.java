/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.ASdebug;

import groovyjarjarantlr.ASdebug.TokenOffsetInfo;
import groovyjarjarantlr.Token;

public interface IASDebugStream {
    public String getEntireText();

    public TokenOffsetInfo getOffsetInfo(Token var1);
}

