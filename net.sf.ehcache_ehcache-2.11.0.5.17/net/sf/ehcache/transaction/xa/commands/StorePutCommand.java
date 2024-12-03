/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.commands;

import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.xa.commands.AbstractStoreCommand;

public class StorePutCommand
extends AbstractStoreCommand {
    public StorePutCommand(Element oldElement, Element newElement) {
        super(oldElement, newElement);
    }

    @Override
    public boolean isPut(Object key) {
        return this.getObjectKey().equals(key);
    }

    @Override
    public boolean isRemove(Object key) {
        return false;
    }

    public Element getElement() {
        return this.getNewElement();
    }

    @Override
    public Object getObjectKey() {
        return this.getNewElement().getObjectKey();
    }
}

