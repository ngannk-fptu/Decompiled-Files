/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.webdav.servlet.shared.WebdavException;

public class EntityTypeFilter
extends ObjectFilter<Integer> {
    public EntityTypeFilter(String name) {
        super(name, PropertyIndex.PropertyInfoIndex.ENTITY_TYPE);
    }

    public static EntityTypeFilter makeIcalEntityTypeFilter(String name, String val, boolean not) throws WebdavException {
        return EntityTypeFilter.makeEntityTypeFilter(name, val, not, IcalDefs.entityTypeIcalNames);
    }

    public static EntityTypeFilter makeEntityTypeFilter(String name, String val, boolean not) throws WebdavException {
        return EntityTypeFilter.makeEntityTypeFilter(name, val, not, IcalDefs.entityTypeNames);
    }

    public static EntityTypeFilter makeEntityTypeFilter(String name, String val, boolean not, String[] names) throws WebdavException {
        int type = -1;
        for (int i = 0; i < names.length; ++i) {
            if (!names[i].equalsIgnoreCase(val)) continue;
            type = i;
            break;
        }
        if (type < 0) {
            throw new WebdavException("Unknown entity type" + val);
        }
        EntityTypeFilter f = new EntityTypeFilter(name);
        f.setEntity(type);
        f.setNot(not);
        return f;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append((Object)this.getPropertyIndex());
        this.stringOper(sb);
        sb.append(IcalDefs.entityTypeNames[(Integer)this.getEntity()]);
        sb.append(")");
        return sb.toString();
    }
}

