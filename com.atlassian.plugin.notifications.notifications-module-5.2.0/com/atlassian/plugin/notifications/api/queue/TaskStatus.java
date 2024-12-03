/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.queue;

public class TaskStatus {
    private final String id;
    private State state;
    private long lastStateChange;

    public TaskStatus(String id, State state) {
        this.id = id;
        this.state = state;
        this.lastStateChange = System.currentTimeMillis();
    }

    public String getId() {
        return this.id;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
        this.lastStateChange = System.currentTimeMillis();
    }

    public long getLastStateChange() {
        return this.lastStateChange;
    }

    public static enum State {
        NEW("notifications.plugin.status.new"),
        QUEUED("notifications.plugin.status.queued"),
        SENDING("notifications.plugin.status.sending"),
        ERROR("notifications.plugin.status.error"),
        AWAITING_RESEND("notifications.plugin.status.awaiting.resend"),
        DONE("notifications.plugin.status.done");

        private String i18nKey;

        private State(String i18nKey) {
            this.i18nKey = i18nKey;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }
    }
}

