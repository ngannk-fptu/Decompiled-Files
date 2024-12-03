/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.eventstream;

import java.util.Map;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;

@FunctionalInterface
public interface MessageBuilder {
    public Message build(Map<String, HeaderValue> var1, byte[] var2);

    public static MessageBuilder defaultBuilder() {
        return Message::new;
    }
}

