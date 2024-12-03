/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;

public interface UserAgent<T extends CalendarComponent> {
    public Calendar publish(T ... var1);

    public Calendar request(T ... var1);

    public Calendar delegate(Calendar var1);

    public Calendar reply(Calendar var1);

    public Calendar add(T var1);

    public Calendar cancel(T ... var1);

    public Calendar refresh(T var1);

    public Calendar counter(Calendar var1);

    public Calendar declineCounter(Calendar var1);
}

