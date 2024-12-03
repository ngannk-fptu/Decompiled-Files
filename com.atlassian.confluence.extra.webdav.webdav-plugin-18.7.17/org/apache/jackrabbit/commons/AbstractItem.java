/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;

public abstract class AbstractItem
implements Item {
    @Override
    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
        if (depth < 0) {
            throw new ItemNotFoundException(this + ": Invalid ancestor depth (" + depth + ")");
        }
        if (depth == 0) {
            return this.getSession().getRootNode();
        }
        String path = this.getPath();
        int slash = 0;
        for (int i = 0; i < depth - 1; ++i) {
            if ((slash = path.indexOf(47, slash + 1)) != -1) continue;
            throw new ItemNotFoundException(this + ": Invalid ancestor depth (" + depth + ")");
        }
        if ((slash = path.indexOf(47, slash + 1)) == -1) {
            return this;
        }
        try {
            return this.getSession().getItem(path.substring(0, slash));
        }
        catch (ItemNotFoundException e) {
            throw new AccessDeniedException(this + ": Ancestor access denied (" + depth + ")");
        }
    }

    @Override
    public int getDepth() throws RepositoryException {
        String path = this.getPath();
        if (path.length() == 1) {
            return 0;
        }
        int depth = 1;
        int slash = path.indexOf(47, 1);
        while (slash != -1) {
            ++depth;
            slash = path.indexOf(47, slash + 1);
        }
        return depth;
    }

    public String toString() {
        try {
            return this.getPath();
        }
        catch (RepositoryException e) {
            return super.toString();
        }
    }
}

