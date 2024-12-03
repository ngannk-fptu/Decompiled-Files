/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.Provider;
import com.opensymphony.provider.ProviderInvocationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XPathProvider
extends Provider {
    public Node getNode(Node var1, String var2) throws ProviderInvocationException;

    public NodeList getNodes(Node var1, String var2) throws ProviderInvocationException;
}

