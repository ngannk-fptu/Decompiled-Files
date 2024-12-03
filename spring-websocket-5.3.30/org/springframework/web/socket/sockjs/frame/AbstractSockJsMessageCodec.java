/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.frame;

import org.springframework.util.Assert;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

public abstract class AbstractSockJsMessageCodec
implements SockJsMessageCodec {
    @Override
    public String encode(String ... messages) {
        Assert.notNull((Object)messages, (String)"messages must not be null");
        StringBuilder sb = new StringBuilder();
        sb.append("a[");
        for (int i = 0; i < messages.length; ++i) {
            sb.append('\"');
            char[] quotedChars = this.applyJsonQuoting(messages[i]);
            sb.append(this.escapeSockJsSpecialChars(quotedChars));
            sb.append('\"');
            if (i >= messages.length - 1) continue;
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    protected abstract char[] applyJsonQuoting(String var1);

    private String escapeSockJsSpecialChars(char[] characters) {
        StringBuilder result = new StringBuilder();
        for (char c : characters) {
            if (this.isSockJsSpecialChar(c)) {
                result.append('\\').append('u');
                String hex = Integer.toHexString(c).toLowerCase();
                for (int i = 0; i < 4 - hex.length(); ++i) {
                    result.append('0');
                }
                result.append(hex);
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    private boolean isSockJsSpecialChar(char ch) {
        return ch <= '\u001f' || ch >= '\u200c' && ch <= '\u200f' || ch >= '\u2028' && ch <= '\u202f' || ch >= '\u2060' && ch <= '\u206f' || ch >= '\ufff0' || ch >= '\ud800' && ch <= '\udfff';
    }
}

