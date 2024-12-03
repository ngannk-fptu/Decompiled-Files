/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import org.w3c.dom.Node;

public interface NodeFilter {
    public int isNodeInclude(Node var1);

    public int isNodeIncludeDO(Node var1, int var2);
}

