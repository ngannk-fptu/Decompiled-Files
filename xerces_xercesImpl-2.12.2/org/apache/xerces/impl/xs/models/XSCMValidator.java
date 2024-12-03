/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import java.util.Vector;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.xni.QName;

public interface XSCMValidator {
    public static final short FIRST_ERROR = -1;
    public static final short SUBSEQUENT_ERROR = -2;

    public int[] startContentModel();

    public Object oneTransition(QName var1, int[] var2, SubstitutionGroupHandler var3);

    public boolean endContentModel(int[] var1);

    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler var1) throws XMLSchemaException;

    public Vector whatCanGoHere(int[] var1);

    public int[] occurenceInfo(int[] var1);

    public String getTermName(int var1);

    public boolean isCompactedForUPA();
}

