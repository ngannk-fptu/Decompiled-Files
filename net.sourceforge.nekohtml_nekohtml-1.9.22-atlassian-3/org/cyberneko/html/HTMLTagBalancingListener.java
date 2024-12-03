/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 */
package org.cyberneko.html;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

public interface HTMLTagBalancingListener {
    public void ignoredStartElement(QName var1, XMLAttributes var2, Augmentations var3);

    public void ignoredEndElement(QName var1, Augmentations var2);
}

