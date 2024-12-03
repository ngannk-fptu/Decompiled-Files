/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.reloading.ReloadingController;

public class ReloadingEvent
extends Event {
    public static final EventType<ReloadingEvent> ANY = new EventType<Event>(Event.ANY, "RELOAD");
    private static final long serialVersionUID = 20140701L;
    private final Object data;

    public ReloadingEvent(ReloadingController source, Object addData) {
        super(source, ANY);
        this.data = addData;
    }

    public ReloadingController getController() {
        return (ReloadingController)this.getSource();
    }

    public Object getData() {
        return this.data;
    }
}

