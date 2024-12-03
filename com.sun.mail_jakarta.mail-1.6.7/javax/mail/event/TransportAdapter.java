/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.event;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

public abstract class TransportAdapter
implements TransportListener {
    @Override
    public void messageDelivered(TransportEvent e) {
    }

    @Override
    public void messageNotDelivered(TransportEvent e) {
    }

    @Override
    public void messagePartiallyDelivered(TransportEvent e) {
    }
}

