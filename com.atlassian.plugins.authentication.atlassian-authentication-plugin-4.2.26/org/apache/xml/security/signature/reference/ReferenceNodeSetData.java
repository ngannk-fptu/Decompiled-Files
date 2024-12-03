/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature.reference;

import java.util.Iterator;
import org.apache.xml.security.signature.reference.ReferenceData;
import org.w3c.dom.Node;

public interface ReferenceNodeSetData
extends ReferenceData {
    public Iterator<Node> iterator();
}

