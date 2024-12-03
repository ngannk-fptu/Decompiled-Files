/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;

public interface IRelationshipMap
extends Serializable {
    public List<IRelationship> get(IProgramElement var1);

    public List<IRelationship> get(String var1);

    public IRelationship get(IProgramElement var1, IRelationship.Kind var2, String var3, boolean var4, boolean var5);

    public IRelationship get(IProgramElement var1, IRelationship.Kind var2, String var3);

    public IRelationship get(String var1, IRelationship.Kind var2, String var3, boolean var4, boolean var5);

    public void put(IProgramElement var1, IRelationship var2);

    public void put(String var1, IRelationship var2);

    public boolean remove(String var1, IRelationship var2);

    public void removeAll(String var1);

    public void clear();

    public Set<String> getEntries();
}

