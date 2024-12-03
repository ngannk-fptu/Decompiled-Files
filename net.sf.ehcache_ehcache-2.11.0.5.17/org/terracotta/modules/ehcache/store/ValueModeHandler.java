/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store;

import java.io.IOException;
import java.io.Serializable;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementData;

public interface ValueModeHandler {
    public Object getRealKeyObject(String var1);

    public Object getRealKeyObject(String var1, ClassLoader var2);

    public String createPortableKey(Object var1) throws IOException;

    public ElementData createElementData(Element var1);

    public Element createElement(Object var1, Serializable var2);
}

