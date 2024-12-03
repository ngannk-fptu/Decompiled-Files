/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.xhtml.parsing;

import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XNIException;

public interface IsolatedBodyListener {
    public void completeForEndElement(QName var1, Augmentations var2) throws XNIException;
}

