/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.Event;

public interface MutationEvent
extends Event {
    public static final short MODIFICATION = 1;
    public static final short ADDITION = 2;
    public static final short REMOVAL = 3;

    public Node getRelatedNode();

    public String getPrevValue();

    public String getNewValue();

    public String getAttrName();

    public short getAttrChange();

    public void initMutationEvent(String var1, boolean var2, boolean var3, Node var4, String var5, String var6, String var7, short var8);
}

