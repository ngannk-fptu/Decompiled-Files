/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponent;

public interface HTMLComponent
extends XMLComponent {
    @Override
    public Boolean getFeatureDefault(String var1);

    @Override
    public Object getPropertyDefault(String var1);
}

