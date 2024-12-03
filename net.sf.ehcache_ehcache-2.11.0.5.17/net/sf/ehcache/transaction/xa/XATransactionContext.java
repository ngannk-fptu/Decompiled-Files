/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.xa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.xa.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XATransactionContext {
    private static final Logger LOG = LoggerFactory.getLogger((String)XATransactionContext.class.getName());
    private final ConcurrentMap<Object, Element> commandElements = new ConcurrentHashMap<Object, Element>();
    private final Set<Object> removedKeys = new HashSet<Object>();
    private final Set<Object> addedKeys = new HashSet<Object>();
    private int sizeModifier;
    private final Map<Object, Command> commands = new LinkedHashMap<Object, Command>();
    private final Store underlyingStore;

    public XATransactionContext(Store underlyingStore) {
        this.underlyingStore = underlyingStore;
    }

    public void addCommand(Command command, Element element) {
        Object key = command.getObjectKey();
        if (element != null) {
            this.commandElements.put(key, element);
        } else {
            this.commandElements.remove(key);
        }
        if (command.isPut(key)) {
            boolean removed = this.removedKeys.remove(key);
            boolean added = this.addedKeys.add(key);
            if (removed || added && !this.underlyingStore.containsKey(key)) {
                ++this.sizeModifier;
            }
        } else if (command.isRemove(key)) {
            this.removedKeys.add(key);
            if (this.addedKeys.remove(key) || this.underlyingStore.containsKey(key)) {
                --this.sizeModifier;
            }
        }
        this.commands.put(key, command);
        LOG.debug("XA context added new command [{}], it now contains {} command(s)", (Object)command, (Object)this.commands.size());
    }

    public List<Command> getCommands() {
        return new ArrayList<Command>(this.commands.values());
    }

    public Element get(Object key) {
        return this.removedKeys.contains(key) ? null : (Element)this.commandElements.get(key);
    }

    public boolean isRemoved(Object key) {
        return this.removedKeys.contains(key);
    }

    public Collection getAddedKeys() {
        return Collections.unmodifiableSet(this.addedKeys);
    }

    public Collection getRemovedKeys() {
        return Collections.unmodifiableSet(this.removedKeys);
    }

    public int getSizeModifier() {
        return this.sizeModifier;
    }

    public String toString() {
        return "XATransactionContext with " + this.commands.size() + " command(s)";
    }
}

