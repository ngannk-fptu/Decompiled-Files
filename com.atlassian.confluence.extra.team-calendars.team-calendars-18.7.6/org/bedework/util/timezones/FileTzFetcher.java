/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.bedework.util.timezones.TimezonesException;
import org.bedework.util.timezones.TzFetcher;

public class FileTzFetcher
implements TzFetcher {
    private final Map<String, VTimeZone> tzs = new HashMap<String, VTimeZone>();

    public FileTzFetcher(String path) throws TimezonesException {
        try {
            File dir = new File(path);
            if (!dir.isDirectory()) {
                throw new TimezonesException(path + " is not a directory");
            }
            this.processDir(dir);
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    @Override
    public VTimeZone getTz(String tzid) throws TimezonesException {
        return this.tzs.get(tzid);
    }

    @Override
    public List<String> getTzids() throws TimezonesException {
        return new ArrayList<String>(this.tzs.keySet());
    }

    private void processDir(File dir) throws TimezonesException {
        try {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    this.processDir(f);
                    continue;
                }
                if (!f.isFile()) continue;
                this.processFile(f);
            }
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    private void processFile(File f) throws TimezonesException {
        try {
            if (!f.getName().endsWith(".ics")) {
                return;
            }
            CalendarBuilder cb = new CalendarBuilder();
            Calendar c = cb.build(new FileReader(f));
            for (Object e : c.getComponents()) {
                if (!(e instanceof VTimeZone)) continue;
                VTimeZone vt = (VTimeZone)e;
                this.tzs.put(vt.getTimeZoneId().getValue(), vt);
            }
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    @Override
    public void close() throws Exception {
    }
}

