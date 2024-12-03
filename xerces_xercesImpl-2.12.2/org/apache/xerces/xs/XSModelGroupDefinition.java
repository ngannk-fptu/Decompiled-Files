/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;

public interface XSModelGroupDefinition
extends XSObject {
    public XSModelGroup getModelGroup();

    public XSAnnotation getAnnotation();

    public XSObjectList getAnnotations();
}

