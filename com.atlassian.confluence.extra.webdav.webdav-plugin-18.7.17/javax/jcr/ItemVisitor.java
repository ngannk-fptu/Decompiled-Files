/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public interface ItemVisitor {
    public void visit(Property var1) throws RepositoryException;

    public void visit(Node var1) throws RepositoryException;
}

