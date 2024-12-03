/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

import java.util.Arrays;
import org.hibernate.engine.spi.CompositeOwner;

public final class CompositeOwnerTracker {
    private String[] names = new String[0];
    private CompositeOwner[] owners = new CompositeOwner[0];

    public void add(String name, CompositeOwner owner) {
        for (int i = 0; i < this.names.length; ++i) {
            if (!this.names[i].equals(name)) continue;
            this.owners[i] = owner;
            return;
        }
        this.names = Arrays.copyOf(this.names, this.names.length + 1);
        this.names[this.names.length - 1] = name;
        this.owners = Arrays.copyOf(this.owners, this.owners.length + 1);
        this.owners[this.owners.length - 1] = owner;
    }

    public void callOwner(String fieldName) {
        for (int i = 0; i < this.owners.length; ++i) {
            if (this.owners[i] == null) continue;
            this.owners[i].$$_hibernate_trackChange(this.names[i] + fieldName);
        }
    }

    public void removeOwner(String name) {
        for (int i = 0; i < this.names.length; ++i) {
            if (!name.equals(this.names[i])) continue;
            String[] newNames = Arrays.copyOf(this.names, this.names.length - 1);
            System.arraycopy(this.names, i + 1, newNames, i, newNames.length - i);
            this.names = newNames;
            CompositeOwner[] newOwners = Arrays.copyOf(this.owners, this.owners.length - 1);
            System.arraycopy(this.owners, i + 1, newOwners, i, newOwners.length - i);
            this.owners = newOwners;
            return;
        }
    }
}

