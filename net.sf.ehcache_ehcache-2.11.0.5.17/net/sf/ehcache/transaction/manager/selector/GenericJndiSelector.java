/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.manager.selector;

import net.sf.ehcache.transaction.manager.selector.JndiSelector;

public class GenericJndiSelector
extends JndiSelector {
    public GenericJndiSelector() {
        super("genericJNDI", "java:/TransactionManager");
    }
}

