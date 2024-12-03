/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.w3c.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public interface MutationNameEvent
extends MutationEvent {
    public String getPrevNamespaceURI();

    public String getPrevNodeName();

    public void initMutationNameEvent(String var1, boolean var2, boolean var3, Node var4, String var5, String var6);

    public void initMutationNameEventNS(String var1, String var2, boolean var3, boolean var4, Node var5, String var6, String var7);
}

