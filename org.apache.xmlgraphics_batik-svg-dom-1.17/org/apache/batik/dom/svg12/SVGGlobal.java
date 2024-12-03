/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg12;

import org.apache.batik.dom.svg12.Global;
import org.w3c.dom.events.EventTarget;

public interface SVGGlobal
extends Global {
    public void startMouseCapture(EventTarget var1, boolean var2, boolean var3);

    public void stopMouseCapture();
}

