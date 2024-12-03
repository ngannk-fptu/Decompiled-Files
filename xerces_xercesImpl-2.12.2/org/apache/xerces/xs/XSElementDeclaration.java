/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;

public interface XSElementDeclaration
extends XSTerm {
    public XSTypeDefinition getTypeDefinition();

    public short getScope();

    public XSComplexTypeDefinition getEnclosingCTDefinition();

    public short getConstraintType();

    public String getConstraintValue();

    public Object getActualVC() throws XSException;

    public short getActualVCType() throws XSException;

    public ShortList getItemValueTypes() throws XSException;

    public XSValue getValueConstraintValue();

    public boolean getNillable();

    public XSNamedMap getIdentityConstraints();

    public XSElementDeclaration getSubstitutionGroupAffiliation();

    public boolean isSubstitutionGroupExclusion(short var1);

    public short getSubstitutionGroupExclusions();

    public boolean isDisallowedSubstitution(short var1);

    public short getDisallowedSubstitutions();

    public boolean getAbstract();

    public XSAnnotation getAnnotation();

    public XSObjectList getAnnotations();
}

