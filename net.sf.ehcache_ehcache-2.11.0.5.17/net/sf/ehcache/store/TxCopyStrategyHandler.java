/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.transaction.SoftLockID;

public class TxCopyStrategyHandler
extends CopyStrategyHandler {
    public TxCopyStrategyHandler(boolean copyOnRead, boolean copyOnWrite, ReadWriteCopyStrategy<Element> copyStrategy, ClassLoader loader) {
        super(copyOnRead, copyOnWrite, copyStrategy, loader);
    }

    @Override
    public Element copyElementForReadIfNeeded(Element element) {
        Object objectValue = element.getObjectValue();
        if (objectValue instanceof SoftLockID) {
            return super.copyElementForReadIfNeeded(((SoftLockID)objectValue).getOldElement());
        }
        return super.copyElementForReadIfNeeded(element);
    }
}

