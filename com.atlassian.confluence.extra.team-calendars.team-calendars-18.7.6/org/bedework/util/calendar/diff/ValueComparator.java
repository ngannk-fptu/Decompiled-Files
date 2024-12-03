/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.util.misc.Util;

public class ValueComparator
implements Comparable<ValueComparator> {
    private List<ValueTypeEntry> vtes = new ArrayList<ValueTypeEntry>();

    void addValue(QName typeElement, String value) {
        this.vtes.add(new ValueTypeEntry(typeElement, value));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ValueTypeEntry vte : this.vtes) {
            sb.append(vte.toString());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(ValueComparator o) {
        Integer thatSz;
        Integer thisSz = this.vtes.size();
        int res = thisSz.compareTo(thatSz = Integer.valueOf(o.vtes.size()));
        if (res != 0) {
            return res;
        }
        Iterator<ValueTypeEntry> thatIt = o.vtes.iterator();
        for (ValueTypeEntry vte : this.vtes) {
            ValueTypeEntry thatVte;
            res = vte.compareTo(thatVte = thatIt.next());
            if (res == 0) continue;
            return res;
        }
        return 0;
    }

    public int hashCode() {
        int res = this.vtes.size();
        for (ValueTypeEntry vte : this.vtes) {
            res *= vte.hashCode();
        }
        return res;
    }

    public boolean equals(Object o) {
        return this.compareTo((ValueComparator)o) == 0;
    }

    private static class ValueTypeEntry
    implements Comparable<ValueTypeEntry> {
        QName typeElement;
        String value;

        public ValueTypeEntry(QName typeElement, String value) {
            this.typeElement = typeElement;
            this.value = value;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" (");
            sb.append(this.typeElement);
            sb.append(", ");
            sb.append(this.value);
            sb.append(")");
            return sb.toString();
        }

        @Override
        public int compareTo(ValueTypeEntry o) {
            int res = this.typeElement.getNamespaceURI().compareTo(o.typeElement.getNamespaceURI());
            if (res != 0) {
                return res;
            }
            res = this.typeElement.getLocalPart().compareTo(o.typeElement.getLocalPart());
            if (res != 0) {
                return res;
            }
            return Util.compareStrings(this.value, o.value);
        }

        public int hashCode() {
            return this.typeElement.hashCode() * this.value.hashCode();
        }

        public boolean equals(Object o) {
            return this.compareTo((ValueTypeEntry)o) == 0;
        }
    }
}

