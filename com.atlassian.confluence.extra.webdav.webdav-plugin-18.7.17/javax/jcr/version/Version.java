/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import java.util.Calendar;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.VersionHistory;

public interface Version
extends Node {
    public VersionHistory getContainingHistory() throws RepositoryException;

    public Calendar getCreated() throws RepositoryException;

    public Version getLinearSuccessor() throws RepositoryException;

    public Version[] getSuccessors() throws RepositoryException;

    public Version getLinearPredecessor() throws RepositoryException;

    public Version[] getPredecessors() throws RepositoryException;

    public Node getFrozenNode() throws RepositoryException;
}

