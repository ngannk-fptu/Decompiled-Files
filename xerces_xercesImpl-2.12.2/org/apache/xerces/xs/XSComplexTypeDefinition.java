/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

public interface XSComplexTypeDefinition
extends XSTypeDefinition {
    public static final short CONTENTTYPE_EMPTY = 0;
    public static final short CONTENTTYPE_SIMPLE = 1;
    public static final short CONTENTTYPE_ELEMENT = 2;
    public static final short CONTENTTYPE_MIXED = 3;

    public short getDerivationMethod();

    public boolean getAbstract();

    public XSObjectList getAttributeUses();

    public XSWildcard getAttributeWildcard();

    public short getContentType();

    public XSSimpleTypeDefinition getSimpleType();

    public XSParticle getParticle();

    public boolean isProhibitedSubstitution(short var1);

    public short getProhibitedSubstitutions();

    public XSObjectList getAnnotations();
}

