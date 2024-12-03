/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Comparator;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.flat.ItemSequence;

public interface TreeManager {
    public Node getRoot();

    public boolean isRoot(Node var1) throws RepositoryException;

    public boolean isLeaf(Node var1) throws RepositoryException;

    public Set<String> getIgnoredProperties();

    public Comparator<String> getOrder();

    public void split(ItemSequence var1, Node var2, Node var3) throws RepositoryException;

    public void split(ItemSequence var1, Node var2, Property var3) throws RepositoryException;

    public void join(ItemSequence var1, Node var2, Node var3) throws RepositoryException;

    public void join(ItemSequence var1, Node var2, Property var3) throws RepositoryException;

    public boolean getAutoSave();
}

