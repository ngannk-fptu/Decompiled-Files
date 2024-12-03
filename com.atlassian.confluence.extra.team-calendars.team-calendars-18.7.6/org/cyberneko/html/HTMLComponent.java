/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.parser.XMLComponent
 */
package org.cyberneko.html;

import org.apache.xerces.xni.parser.XMLComponent;

public interface HTMLComponent
extends XMLComponent {
    public Boolean getFeatureDefault(String var1);

    public Object getPropertyDefault(String var1);
}

