/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.oauth.consumer;

import com.atlassian.oauth.consumer.ConsumerToken;
import java.util.Map;
import net.jcip.annotations.Immutable;

public interface ConsumerTokenStore {
    public ConsumerToken get(Key var1);

    public Map<Key, ConsumerToken> getConsumerTokens(String var1);

    public ConsumerToken put(Key var1, ConsumerToken var2);

    public void remove(Key var1);

    public void removeTokensForConsumer(String var1);

    @Immutable
    public static final class Key {
        private final String key;

        public Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public String toString() {
            return this.key;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !o.getClass().equals(Key.class)) {
                return false;
            }
            return this.key.equals(((Key)o).key);
        }

        public int hashCode() {
            return this.key.hashCode();
        }
    }
}

