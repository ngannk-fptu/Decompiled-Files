/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.util.List;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.bedework.util.timezones.TimezonesException;

public interface TzFetcher
extends AutoCloseable {
    public VTimeZone getTz(String var1) throws TimezonesException;

    public List<String> getTzids() throws TimezonesException;
}

