/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;

public class GroupMembers
extends ObjectOpenHashSet<String>
implements Copiable<GroupMembers> {
    public GroupMembers() {
    }

    public GroupMembers(Collection<String> c) {
        super(c);
    }

    public boolean add(String s) {
        return super.add((Object)s.intern());
    }

    @Override
    public GroupMembers copy() {
        return new GroupMembers((Collection<String>)((Object)this));
    }

    public void renameUser(String oldUsername, String newUsername) {
        if (this.remove(oldUsername)) {
            super.add((Object)newUsername.intern());
        }
    }
}

