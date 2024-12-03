/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.lang.ref.ReferenceQueue;
import org.codehaus.groovy.util.ReferenceManager;
import org.codehaus.groovy.util.ReferenceType;

public class ReferenceBundle {
    private ReferenceManager manager;
    private ReferenceType type;
    private static final ReferenceBundle softReferences;
    private static final ReferenceBundle weakReferences;
    private static final ReferenceBundle hardReferences;
    private static final ReferenceBundle phantomReferences;

    public ReferenceBundle(ReferenceManager manager, ReferenceType type) {
        this.manager = manager;
        this.type = type;
    }

    public ReferenceType getType() {
        return this.type;
    }

    public ReferenceManager getManager() {
        return this.manager;
    }

    public static ReferenceBundle getSoftBundle() {
        return softReferences;
    }

    public static ReferenceBundle getWeakBundle() {
        return weakReferences;
    }

    public static ReferenceBundle getHardBundle() {
        return hardReferences;
    }

    public static ReferenceBundle getPhantomBundle() {
        return phantomReferences;
    }

    static {
        ReferenceQueue queue = new ReferenceQueue();
        ReferenceManager callBack = ReferenceManager.createCallBackedManager(queue);
        ReferenceManager manager = ReferenceManager.createThresholdedIdlingManager(queue, callBack, 500);
        softReferences = new ReferenceBundle(manager, ReferenceType.SOFT);
        weakReferences = new ReferenceBundle(manager, ReferenceType.WEAK);
        phantomReferences = new ReferenceBundle(manager, ReferenceType.PHANTOM);
        hardReferences = new ReferenceBundle(ReferenceManager.createIdlingManager(null), ReferenceType.HARD);
    }
}

