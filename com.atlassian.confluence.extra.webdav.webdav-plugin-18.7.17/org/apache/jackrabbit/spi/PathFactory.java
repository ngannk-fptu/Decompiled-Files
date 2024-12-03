/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;

public interface PathFactory {
    public Path create(Path var1, Path var2, boolean var3) throws IllegalArgumentException, RepositoryException;

    public Path create(Path var1, Name var2, boolean var3) throws RepositoryException;

    public Path create(Path var1, Name var2, int var3, boolean var4) throws IllegalArgumentException, RepositoryException;

    public Path create(Name var1) throws IllegalArgumentException;

    public Path create(Name var1, int var2) throws IllegalArgumentException;

    public Path create(Path.Element var1) throws IllegalArgumentException;

    public Path create(Path.Element[] var1) throws IllegalArgumentException;

    public Path create(String var1) throws IllegalArgumentException;

    public Path.Element createElement(Name var1) throws IllegalArgumentException;

    public Path.Element createElement(Name var1, int var2) throws IllegalArgumentException;

    public Path.Element createElement(String var1) throws IllegalArgumentException;

    public Path.Element getCurrentElement();

    public Path.Element getParentElement();

    public Path.Element getRootElement();

    public Path getRootPath();
}

