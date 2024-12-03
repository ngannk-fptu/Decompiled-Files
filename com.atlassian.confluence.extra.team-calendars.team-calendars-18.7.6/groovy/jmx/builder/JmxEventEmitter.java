/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxEventEmitterMBean;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class JmxEventEmitter
extends NotificationBroadcasterSupport
implements JmxEventEmitterMBean {
    private String event;
    private String message;

    @Override
    public String getEvent() {
        return this.event;
    }

    @Override
    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public long send(Object data) {
        long seq = NumberSequencer.getNextSequence();
        Notification note = new Notification(this.getEvent(), this, seq, System.currentTimeMillis(), "Event notification " + this.getEvent());
        note.setUserData(data);
        super.sendNotification(note);
        return seq;
    }

    private static class NumberSequencer {
        private static AtomicLong num = new AtomicLong(0L);

        private NumberSequencer() {
        }

        public static long getNextSequence() {
            return num.incrementAndGet();
        }
    }
}

