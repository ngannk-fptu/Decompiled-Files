/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.management;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface MarkEventListener {
    public void beforeScanning(Node var1) throws RepositoryException;
}

