/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.sockjs.frame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.sockjs.frame.SockJsFrameType;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

public class SockJsFrame {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final SockJsFrame OPEN_FRAME = new SockJsFrame("o");
    private static final SockJsFrame HEARTBEAT_FRAME = new SockJsFrame("h");
    private static final SockJsFrame CLOSE_GO_AWAY_FRAME = SockJsFrame.closeFrame(3000, "Go away!");
    private static final SockJsFrame CLOSE_ANOTHER_CONNECTION_OPEN_FRAME = SockJsFrame.closeFrame(2010, "Another connection still open");
    private final SockJsFrameType type;
    private final String content;

    public SockJsFrame(String content) {
        Assert.hasText((String)content, (String)"Content must not be empty");
        if ("o".equals(content)) {
            this.type = SockJsFrameType.OPEN;
            this.content = content;
        } else if ("h".equals(content)) {
            this.type = SockJsFrameType.HEARTBEAT;
            this.content = content;
        } else if (content.charAt(0) == 'a') {
            this.type = SockJsFrameType.MESSAGE;
            this.content = content.length() > 1 ? content : "a[]";
        } else if (content.charAt(0) == 'm') {
            this.type = SockJsFrameType.MESSAGE;
            this.content = content.length() > 1 ? content : "null";
        } else if (content.charAt(0) == 'c') {
            this.type = SockJsFrameType.CLOSE;
            this.content = content.length() > 1 ? content : "c[]";
        } else {
            throw new IllegalArgumentException("Unexpected SockJS frame type in content \"" + content + "\"");
        }
    }

    public SockJsFrameType getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public byte[] getContentBytes() {
        return this.content.getBytes(CHARSET);
    }

    @Nullable
    public String getFrameData() {
        if (this.getType() == SockJsFrameType.OPEN || this.getType() == SockJsFrameType.HEARTBEAT) {
            return null;
        }
        return this.getContent().substring(1);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SockJsFrame)) {
            return false;
        }
        SockJsFrame otherFrame = (SockJsFrame)other;
        return this.type.equals((Object)otherFrame.type) && this.content.equals(otherFrame.content);
    }

    public int hashCode() {
        return this.content.hashCode();
    }

    public String toString() {
        String result = this.content;
        if (result.length() > 80) {
            result = result.substring(0, 80) + "...(truncated)";
        }
        result = StringUtils.replace((String)result, (String)"\n", (String)"\\n");
        result = StringUtils.replace((String)result, (String)"\r", (String)"\\r");
        return "SockJsFrame content='" + result + "'";
    }

    public static SockJsFrame openFrame() {
        return OPEN_FRAME;
    }

    public static SockJsFrame heartbeatFrame() {
        return HEARTBEAT_FRAME;
    }

    public static SockJsFrame messageFrame(SockJsMessageCodec codec, String ... messages) {
        String encoded = codec.encode(messages);
        return new SockJsFrame(encoded);
    }

    public static SockJsFrame closeFrameGoAway() {
        return CLOSE_GO_AWAY_FRAME;
    }

    public static SockJsFrame closeFrameAnotherConnectionOpen() {
        return CLOSE_ANOTHER_CONNECTION_OPEN_FRAME;
    }

    public static SockJsFrame closeFrame(int code, @Nullable String reason) {
        return new SockJsFrame("c[" + code + ",\"" + (reason != null ? reason : "") + "\"]");
    }
}

