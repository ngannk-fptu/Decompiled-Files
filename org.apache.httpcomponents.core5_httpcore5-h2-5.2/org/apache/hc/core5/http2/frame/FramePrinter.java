/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 */
package org.apache.hc.core5.http2.frame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.config.H2Param;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.FrameType;
import org.apache.hc.core5.http2.frame.RawFrame;

@Internal
public final class FramePrinter {
    public void printFrameInfo(RawFrame frame, Appendable appendable) throws IOException {
        appendable.append("stream ").append(Integer.toString(frame.getStreamId())).append(" frame: ");
        FrameType type = FrameType.valueOf(frame.getType());
        appendable.append(Objects.toString((Object)type)).append(" (0x").append(Integer.toHexString(frame.getType())).append("); flags: ");
        int flags = frame.getFlags();
        if (flags > 0) {
            switch (type) {
                case SETTINGS: 
                case PING: {
                    if ((flags & FrameFlag.ACK.value) <= 0) break;
                    appendable.append(FrameFlag.ACK.name()).append(" ");
                    break;
                }
                case DATA: {
                    if ((flags & FrameFlag.END_STREAM.value) > 0) {
                        appendable.append(FrameFlag.END_STREAM.name()).append(" ");
                    }
                    if ((flags & FrameFlag.PADDED.value) <= 0) break;
                    appendable.append(FrameFlag.PADDED.name()).append(" ");
                    break;
                }
                case HEADERS: {
                    if ((flags & FrameFlag.END_STREAM.value) > 0) {
                        appendable.append(FrameFlag.END_STREAM.name()).append(" ");
                    }
                    if ((flags & FrameFlag.END_HEADERS.value) > 0) {
                        appendable.append(FrameFlag.END_HEADERS.name()).append(" ");
                    }
                    if ((flags & FrameFlag.PADDED.value) > 0) {
                        appendable.append(FrameFlag.PADDED.name()).append(" ");
                    }
                    if ((flags & FrameFlag.PRIORITY.value) <= 0) break;
                    appendable.append(FrameFlag.PRIORITY.name()).append(" ");
                    break;
                }
                case PUSH_PROMISE: {
                    if ((flags & FrameFlag.END_HEADERS.value) > 0) {
                        appendable.append(FrameFlag.END_HEADERS.name()).append(" ");
                    }
                    if ((flags & FrameFlag.PADDED.value) <= 0) break;
                    appendable.append(FrameFlag.PADDED.name()).append(" ");
                    break;
                }
                case CONTINUATION: {
                    if ((flags & FrameFlag.END_HEADERS.value) <= 0) break;
                    appendable.append(FrameFlag.END_HEADERS.name()).append(" ");
                }
            }
        }
        appendable.append("(0x").append(Integer.toHexString(flags)).append("); length: ").append(Integer.toString(frame.getLength()));
    }

    public void printPayload(RawFrame frame, Appendable appendable) throws IOException {
        FrameType type = FrameType.valueOf(frame.getType());
        ByteBuffer buf = frame.getPayloadContent();
        if (buf != null) {
            switch (type) {
                case SETTINGS: {
                    if (buf.remaining() % 6 == 0) {
                        while (buf.hasRemaining()) {
                            short code = buf.getShort();
                            H2Param param = H2Param.valueOf(code);
                            int value = buf.getInt();
                            if (param != null) {
                                appendable.append(param.name());
                            } else {
                                appendable.append("0x").append(Integer.toHexString(code));
                            }
                            appendable.append(": ").append(Integer.toString(value)).append("\r\n");
                        }
                        break;
                    }
                    appendable.append("Invalid\r\n");
                    break;
                }
                case RST_STREAM: {
                    if (buf.remaining() == 4) {
                        appendable.append("Code ");
                        int code = buf.getInt();
                        H2Error error = H2Error.getByCode(code);
                        if (error != null) {
                            appendable.append(error.name());
                        } else {
                            appendable.append("0x").append(Integer.toHexString(code));
                        }
                        appendable.append("\r\n");
                        break;
                    }
                    appendable.append("Invalid\r\n");
                    break;
                }
                case GOAWAY: {
                    if (buf.remaining() >= 8) {
                        int lastStream = buf.getInt();
                        appendable.append("Last stream ").append(Integer.toString(lastStream)).append("\r\n");
                        appendable.append("Code ");
                        int code2 = buf.getInt();
                        H2Error error2 = H2Error.getByCode(code2);
                        if (error2 != null) {
                            appendable.append(error2.name());
                        } else {
                            appendable.append("0x").append(Integer.toHexString(code2));
                        }
                        appendable.append("\r\n");
                        byte[] tmp = new byte[buf.remaining()];
                        buf.get(tmp);
                        appendable.append(new String(tmp, StandardCharsets.US_ASCII));
                        appendable.append("\r\n");
                        break;
                    }
                    appendable.append("Invalid\r\n");
                    break;
                }
                case WINDOW_UPDATE: {
                    if (buf.remaining() == 4) {
                        int increment = buf.getInt();
                        appendable.append("Increment ").append(Integer.toString(increment)).append("\r\n");
                        break;
                    }
                    appendable.append("Invalid\r\n");
                    break;
                }
                case PUSH_PROMISE: {
                    if (buf.remaining() > 4) {
                        int streamId = buf.getInt();
                        appendable.append("Promised stream ").append(Integer.toString(streamId)).append("\r\n");
                        this.printData(buf, appendable);
                        break;
                    }
                    appendable.append("Invalid\r\n");
                    break;
                }
                default: {
                    this.printData(frame.getPayload(), appendable);
                }
            }
        }
    }

    public void printData(ByteBuffer data, Appendable appendable) throws IOException {
        ByteBuffer buf = data.duplicate();
        byte[] line = new byte[16];
        while (buf.hasRemaining()) {
            int i;
            int chunk = Math.min(buf.remaining(), line.length);
            buf.get(line, 0, chunk);
            for (i = 0; i < chunk; ++i) {
                char ch = (char)line[i];
                if (ch > ' ' && ch <= '\u007f') {
                    appendable.append(ch);
                    continue;
                }
                if (Character.isWhitespace(ch)) {
                    appendable.append(' ');
                    continue;
                }
                appendable.append('.');
            }
            for (i = chunk; i < 17; ++i) {
                appendable.append(' ');
            }
            for (i = 0; i < chunk; ++i) {
                appendable.append(' ');
                int b = line[i] & 0xFF;
                String s = Integer.toHexString(b);
                if (s.length() == 1) {
                    appendable.append("0");
                }
                appendable.append(s);
            }
            appendable.append("\r\n");
        }
    }
}

