/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;

public interface HTMLTagBalancingListener {
    public void ignoredStartElement(QName var1, XMLAttributes var2, Augmentations var3);

    public void ignoredEndElement(QName var1, Augmentations var2);
}

