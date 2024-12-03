/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import java.util.Vector;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.xni.QName;

public class XSAllCM
implements XSCMValidator {
    private static final short STATE_START = 0;
    private static final short STATE_VALID = 1;
    private static final short STATE_CHILD = 1;
    private final XSElementDecl[] fAllElements;
    private final boolean[] fIsOptionalElement;
    private final boolean fHasOptionalContent;
    private int fNumElements = 0;

    public XSAllCM(boolean bl, int n) {
        this.fHasOptionalContent = bl;
        this.fAllElements = new XSElementDecl[n];
        this.fIsOptionalElement = new boolean[n];
    }

    public void addElement(XSElementDecl xSElementDecl, boolean bl) {
        this.fAllElements[this.fNumElements] = xSElementDecl;
        this.fIsOptionalElement[this.fNumElements] = bl;
        ++this.fNumElements;
    }

    @Override
    public int[] startContentModel() {
        int[] nArray = new int[this.fNumElements + 1];
        for (int i = 0; i <= this.fNumElements; ++i) {
            nArray[i] = 0;
        }
        return nArray;
    }

    Object findMatchingDecl(QName qName, SubstitutionGroupHandler substitutionGroupHandler) {
        XSElementDecl xSElementDecl = null;
        for (int i = 0; i < this.fNumElements && (xSElementDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fAllElements[i])) == null; ++i) {
        }
        return xSElementDecl;
    }

    @Override
    public Object oneTransition(QName qName, int[] nArray, SubstitutionGroupHandler substitutionGroupHandler) {
        if (nArray[0] < 0) {
            nArray[0] = -2;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        nArray[0] = 1;
        XSElementDecl xSElementDecl = null;
        for (int i = 0; i < this.fNumElements; ++i) {
            if (nArray[i + 1] != 0 || (xSElementDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fAllElements[i])) == null) continue;
            nArray[i + 1] = 1;
            return xSElementDecl;
        }
        nArray[0] = -1;
        return this.findMatchingDecl(qName, substitutionGroupHandler);
    }

    @Override
    public boolean endContentModel(int[] nArray) {
        int n = nArray[0];
        if (n == -1 || n == -2) {
            return false;
        }
        if (this.fHasOptionalContent && n == 0) {
            return true;
        }
        for (int i = 0; i < this.fNumElements; ++i) {
            if (this.fIsOptionalElement[i] || nArray[i + 1] != 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler substitutionGroupHandler) throws XMLSchemaException {
        for (int i = 0; i < this.fNumElements; ++i) {
            for (int j = i + 1; j < this.fNumElements; ++j) {
                if (!XSConstraints.overlapUPA(this.fAllElements[i], this.fAllElements[j], substitutionGroupHandler)) continue;
                throw new XMLSchemaException("cos-nonambig", new Object[]{this.fAllElements[i].toString(), this.fAllElements[j].toString()});
            }
        }
        return false;
    }

    @Override
    public Vector whatCanGoHere(int[] nArray) {
        Vector<XSElementDecl> vector = new Vector<XSElementDecl>();
        for (int i = 0; i < this.fNumElements; ++i) {
            if (nArray[i + 1] != 0) continue;
            vector.addElement(this.fAllElements[i]);
        }
        return vector;
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

