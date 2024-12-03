/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.adaptavist.confluence.naturalchildren;

import com.atlassian.confluence.core.ContentEntityObject;
import com.eekboom.utils.Strings;
import java.util.Comparator;

public class NaturalPageComparator
implements Comparator {
    public int compare(Object o1, Object o2) {
        String string1 = this.getNaturalTitle(o1);
        String string2 = this.getNaturalTitle(o2);
        return Strings.compareNatural(string1, string2);
    }

    private String getNaturalTitle(Object object) {
        if (object instanceof ContentEntityObject) {
            return ((ContentEntityObject)object).getTitle();
        }
        return "";
    }
}

