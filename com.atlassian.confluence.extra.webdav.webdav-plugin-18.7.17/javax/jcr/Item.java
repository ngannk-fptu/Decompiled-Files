/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

public interface Item {
    public String getPath() throws RepositoryException;

    public String getName() throws RepositoryException;

    public Item getAncestor(int var1) throws ItemNotFoundException, AccessDeniedException, RepositoryException;

    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException;

    public int getDepth() throws RepositoryException;

    public Session getSession() throws RepositoryException;

    public boolean isNode();

    public boolean isNew();

    public boolean isModified();

    public boolean isSame(Item var1) throws RepositoryException;

    public void accept(ItemVisitor var1) throws RepositoryException;

    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException;

    public void refresh(boolean var1) throws InvalidItemStateException, RepositoryException;

    public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException;
}

