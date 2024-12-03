/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.FormalBinding;

public interface IScope {
    public UnresolvedType lookupType(String var1, IHasPosition var2);

    public World getWorld();

    public ResolvedType getEnclosingType();

    public IMessageHandler getMessageHandler();

    public FormalBinding lookupFormal(String var1);

    public FormalBinding getFormal(int var1);

    public int getFormalCount();

    public String[] getImportedPrefixes();

    public String[] getImportedNames();

    public void message(IMessage.Kind var1, IHasPosition var2, String var3);

    public void message(IMessage.Kind var1, IHasPosition var2, IHasPosition var3, String var4);

    public void message(IMessage var1);
}

