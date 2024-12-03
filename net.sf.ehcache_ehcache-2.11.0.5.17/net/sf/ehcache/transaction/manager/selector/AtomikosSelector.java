/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.manager.selector;

import net.sf.ehcache.transaction.manager.selector.ClassSelector;

public class AtomikosSelector
extends ClassSelector {
    public AtomikosSelector() {
        super("Atomikos", "com.atomikos.icatch.jta.UserTransactionManager");
    }
}

