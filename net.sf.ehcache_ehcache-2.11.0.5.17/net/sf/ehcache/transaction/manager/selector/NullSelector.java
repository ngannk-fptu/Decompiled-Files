/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 */
package net.sf.ehcache.transaction.manager.selector;

import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.manager.selector.Selector;

public class NullSelector
extends Selector {
    public NullSelector() {
        super("null");
    }

    @Override
    protected TransactionManager doLookup() {
        return null;
    }
}

