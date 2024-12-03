/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;
import java.util.Collection;
import org.terracotta.modules.ehcache.async.exceptions.ProcessingException;

public interface ItemProcessor<E extends Serializable> {
    public void process(E var1) throws ProcessingException;

    public void process(Collection<E> var1) throws ProcessingException;

    public void throwAway(E var1, RuntimeException var2);
}

