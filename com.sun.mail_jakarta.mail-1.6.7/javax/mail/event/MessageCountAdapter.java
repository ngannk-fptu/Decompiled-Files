/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.event;

import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;

public abstract class MessageCountAdapter
implements MessageCountListener {
    @Override
    public void messagesAdded(MessageCountEvent e) {
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {
    }
}

