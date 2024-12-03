/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JackrabbitNode
extends Node {
    public void rename(String var1) throws RepositoryException;

    public void setMixins(String[] var1) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    @Nullable
    default public JackrabbitNode getNodeOrNull(@NotNull String relPath) throws RepositoryException {
        if (this.hasNode(relPath)) {
            Node n = this.getNode(relPath);
            return n instanceof JackrabbitNode ? (JackrabbitNode)n : null;
        }
        return null;
    }

    @Nullable
    default public Property getPropertyOrNull(@NotNull String relPath) throws RepositoryException {
        if (this.hasProperty(relPath)) {
            return this.getProperty(relPath);
        }
        return null;
    }
}

