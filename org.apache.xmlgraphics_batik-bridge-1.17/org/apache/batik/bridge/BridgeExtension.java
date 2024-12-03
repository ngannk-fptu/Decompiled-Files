/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.util.Iterator;
import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Element;

public interface BridgeExtension {
    public float getPriority();

    public Iterator getImplementedExtensions();

    public String getAuthor();

    public String getContactAddress();

    public String getURL();

    public String getDescription();

    public void registerTags(BridgeContext var1);

    public boolean isDynamicElement(Element var1);
}

