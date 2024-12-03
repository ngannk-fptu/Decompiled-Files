/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.Serializable;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;

public interface Path
extends Serializable {
    public static final int INDEX_UNDEFINED = 0;
    public static final int INDEX_DEFAULT = 1;
    public static final int ROOT_DEPTH = 0;
    public static final char DELIMITER = '\t';

    public Name getName();

    public int getIndex();

    public int getNormalizedIndex();

    public String getIdentifier();

    public boolean denotesRoot();

    public boolean denotesIdentifier();

    public boolean denotesParent();

    public boolean denotesCurrent();

    public boolean denotesName();

    public boolean isIdentifierBased();

    public boolean isAbsolute();

    public boolean isCanonical();

    public boolean isNormalized();

    public Path getNormalizedPath() throws RepositoryException;

    public Path getCanonicalPath() throws RepositoryException;

    public Path resolve(Element var1);

    public Path resolve(Path var1);

    public Path computeRelativePath(Path var1) throws RepositoryException;

    public Path getAncestor(int var1) throws IllegalArgumentException, PathNotFoundException, RepositoryException;

    public int getAncestorCount();

    public int getLength();

    public int getDepth();

    public boolean isEquivalentTo(Path var1) throws IllegalArgumentException, RepositoryException;

    public boolean isAncestorOf(Path var1) throws IllegalArgumentException, RepositoryException;

    public boolean isDescendantOf(Path var1) throws IllegalArgumentException, RepositoryException;

    public Path subPath(int var1, int var2) throws IllegalArgumentException;

    public Element[] getElements();

    public Element getNameElement();

    public Path getLastElement();

    public Path getFirstElements();

    public String getString();

    public static interface Element
    extends Serializable {
        public Name getName();

        public int getIndex();

        public int getNormalizedIndex();

        public String getIdentifier();

        public boolean denotesRoot();

        public boolean denotesParent();

        public boolean denotesCurrent();

        public boolean denotesName();

        public boolean denotesIdentifier();

        public String getString();
    }
}

