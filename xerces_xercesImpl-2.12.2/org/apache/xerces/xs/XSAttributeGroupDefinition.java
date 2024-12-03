/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSWildcard;

public interface XSAttributeGroupDefinition
extends XSObject {
    public XSObjectList getAttributeUses();

    public XSWildcard getAttributeWildcard();

    public XSAnnotation getAnnotation();

    public XSObjectList getAnnotations();
}

