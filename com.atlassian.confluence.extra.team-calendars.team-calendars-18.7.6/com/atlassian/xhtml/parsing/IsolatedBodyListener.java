/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XNIException
 */
package com.atlassian.xhtml.parsing;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;

public interface IsolatedBodyListener {
    public void completeForEndElement(QName var1, Augmentations var2) throws XNIException;
}

