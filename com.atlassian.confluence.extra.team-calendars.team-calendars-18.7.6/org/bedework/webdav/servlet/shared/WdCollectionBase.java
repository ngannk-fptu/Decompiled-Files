/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class WdCollectionBase<T>
extends WdCollection<T> {
    @Override
    public boolean isAlias() {
        return false;
    }

    @Override
    public String getAliasUri() throws WebdavException {
        return null;
    }
}

