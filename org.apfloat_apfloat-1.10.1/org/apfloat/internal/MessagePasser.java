/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.HashMap;
import java.util.Map;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;

public class MessagePasser<K, V> {
    private Map<K, V> messages = new HashMap();

    public synchronized void sendMessage(K receiver, V message) {
        assert (message != null);
        assert (!this.messages.containsKey(receiver));
        this.messages.put(receiver, message);
        this.notifyAll();
    }

    public synchronized V getMessage(K receiver) {
        V message = this.messages.remove(receiver);
        return message;
    }

    public synchronized V receiveMessage(K receiver) throws ApfloatRuntimeException {
        V message;
        while ((message = this.messages.remove(receiver)) == null) {
            try {
                this.wait();
            }
            catch (InterruptedException ie) {
                throw new ApfloatInternalException("Wait for received message interrupted", ie);
            }
        }
        return message;
    }
}

