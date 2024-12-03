/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.version.LabelExistsVersionException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionIterator;

public interface VersionHistory
extends Node {
    public String getVersionableUUID() throws RepositoryException;

    public String getVersionableIdentifier() throws RepositoryException;

    public Version getRootVersion() throws RepositoryException;

    public VersionIterator getAllLinearVersions() throws RepositoryException;

    public VersionIterator getAllVersions() throws RepositoryException;

    public NodeIterator getAllLinearFrozenNodes() throws RepositoryException;

    public NodeIterator getAllFrozenNodes() throws RepositoryException;

    public Version getVersion(String var1) throws VersionException, RepositoryException;

    public Version getVersionByLabel(String var1) throws VersionException, RepositoryException;

    public void addVersionLabel(String var1, String var2, boolean var3) throws LabelExistsVersionException, VersionException, RepositoryException;

    public void removeVersionLabel(String var1) throws VersionException, RepositoryException;

    public boolean hasVersionLabel(String var1) throws RepositoryException;

    public boolean hasVersionLabel(Version var1, String var2) throws VersionException, RepositoryException;

    public String[] getVersionLabels() throws RepositoryException;

    public String[] getVersionLabels(Version var1) throws VersionException, RepositoryException;

    public void removeVersion(String var1) throws ReferentialIntegrityException, AccessDeniedException, UnsupportedRepositoryOperationException, VersionException, RepositoryException;
}

