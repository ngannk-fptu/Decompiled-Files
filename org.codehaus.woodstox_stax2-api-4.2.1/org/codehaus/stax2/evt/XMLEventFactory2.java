/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.evt;

import javax.xml.stream.XMLEventFactory;
import org.codehaus.stax2.evt.DTD2;

public abstract class XMLEventFactory2
extends XMLEventFactory {
    protected XMLEventFactory2() {
    }

    public abstract DTD2 createDTD(String var1, String var2, String var3, String var4);

    public abstract DTD2 createDTD(String var1, String var2, String var3, String var4, Object var5);
}

