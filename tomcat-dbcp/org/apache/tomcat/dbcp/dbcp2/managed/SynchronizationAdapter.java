/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import javax.transaction.Synchronization;

class SynchronizationAdapter
implements Synchronization {
    SynchronizationAdapter() {
    }

    public void afterCompletion(int status) {
    }

    public void beforeCompletion() {
    }
}

