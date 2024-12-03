/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.struts2.config.Settings;

class DelegatingSettings
implements Settings {
    List<Settings> delegates;

    public DelegatingSettings(List<Settings> delegates) {
        this.delegates = delegates;
    }

    @Override
    public String get(String name) throws IllegalArgumentException {
        for (Settings delegate : this.delegates) {
            String value = delegate.get(name);
            if (value == null) continue;
            return value;
        }
        return null;
    }

    @Override
    public Iterator list() {
        boolean workedAtAll = false;
        HashSet settingList = new HashSet();
        UnsupportedOperationException e = null;
        for (Settings delegate : this.delegates) {
            try {
                Iterator list = delegate.list();
                while (list.hasNext()) {
                    settingList.add(list.next());
                }
                workedAtAll = true;
            }
            catch (UnsupportedOperationException ex) {
                e = ex;
            }
        }
        if (!workedAtAll) {
            throw e == null ? new UnsupportedOperationException() : e;
        }
        return settingList.iterator();
    }

    @Override
    public Location getLocation(String name) {
        for (Settings delegate : this.delegates) {
            Location loc = delegate.getLocation(name);
            if (loc == null) continue;
            return loc;
        }
        return Location.UNKNOWN;
    }
}

