/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTerm;

public interface XSParticle
extends XSObject {
    public int getMinOccurs();

    public int getMaxOccurs();

    public boolean getMaxOccursUnbounded();

    public XSTerm getTerm();

    public XSObjectList getAnnotations();
}

