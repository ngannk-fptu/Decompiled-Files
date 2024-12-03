/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.frame;

import org.springframework.util.Assert;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;

public class DefaultSockJsFrameFormat
implements SockJsFrameFormat {
    private final String format;

    public DefaultSockJsFrameFormat(String format) {
        Assert.notNull((Object)format, (String)"format must not be null");
        this.format = format;
    }

    @Override
    public String format(SockJsFrame frame) {
        return String.format(this.format, this.preProcessContent(frame.getContent()));
    }

    protected String preProcessContent(String content) {
        return content;
    }
}

