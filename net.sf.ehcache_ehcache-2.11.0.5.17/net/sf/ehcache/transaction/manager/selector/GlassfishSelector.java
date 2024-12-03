/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.manager.selector;

import net.sf.ehcache.transaction.manager.selector.JndiSelector;

public class GlassfishSelector
extends JndiSelector {
    public GlassfishSelector() {
        super("Glassfish", "java:appserver/TransactionManager");
    }
}

