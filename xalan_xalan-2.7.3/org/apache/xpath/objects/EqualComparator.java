/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import org.apache.xml.utils.XMLString;
import org.apache.xpath.objects.Comparator;

class EqualComparator
extends Comparator {
    EqualComparator() {
    }

    @Override
    boolean compareStrings(XMLString s1, XMLString s2) {
        return s1.equals(s2);
    }

    @Override
    boolean compareNumbers(double n1, double n2) {
        return n1 == n2;
    }
}

