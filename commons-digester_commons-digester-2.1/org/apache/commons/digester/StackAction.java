/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;

public interface StackAction {
    public Object onPush(Digester var1, String var2, Object var3);

    public Object onPop(Digester var1, String var2, Object var3);
}

