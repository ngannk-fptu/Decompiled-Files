/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;

public interface XSNotationDeclaration
extends XSObject {
    public String getSystemId();

    public String getPublicId();

    public XSAnnotation getAnnotation();

    public XSObjectList getAnnotations();
}

