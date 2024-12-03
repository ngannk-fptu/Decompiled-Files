/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import java.util.Vector;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.xni.QName;

public class XSEmptyCM
implements XSCMValidator {
    private static final short STATE_START = 0;
    private static final Vector EMPTY = new Vector(0);

    @Override
    public int[] startContentModel() {
        return new int[]{0};
    }

    @Override
    public Object oneTransition(QName qName, int[] nArray, SubstitutionGroupHandler substitutionGroupHandler) {
        if (nArray[0] < 0) {
            nArray[0] = -2;
            return null;
        }
        nArray[0] = -1;
        return null;
    }

    @Override
    public boolean endContentModel(int[] nArray) {
        boolean bl = false;
        int n = nArray[0];
        return n >= 0;
    }

    @Override
    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler substitutionGroupHandler) throws XMLSchemaException {
        return false;
    }

    @Override
    public Vector whatCanGoHere(int[] nArray) {
        return EMPTY;
    }

    @Override
    public int[] occurenceInfo(int[] nArray) {
        return null;
    }

    @Override
    public String getTermName(int n) {
        return null;
    }

    @Override
    public boolean isCompactedForUPA() {
        return false;
    }
}

