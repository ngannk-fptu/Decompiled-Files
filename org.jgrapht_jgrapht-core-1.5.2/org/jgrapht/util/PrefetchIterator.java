/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrefetchIterator<E>
implements Iterator<E>,
Enumeration<E> {
    private NextElementFunctor<E> innerEnum;
    private E getNextLastResult;
    private boolean isGetNextLastResultUpToDate = false;
    private boolean endOfEnumerationReached = false;
    private boolean flagIsEnumerationStartedEmpty = true;
    private int innerFunctorUsageCounter = 0;

    public PrefetchIterator(NextElementFunctor<E> aEnum) {
        this.innerEnum = aEnum;
    }

    private E getNextElementFromInnerFunctor() {
        ++this.innerFunctorUsageCounter;
        E result = this.innerEnum.nextElement();
        this.flagIsEnumerationStartedEmpty = false;
        return result;
    }

    @Override
    public E nextElement() {
        E result = this.isGetNextLastResultUpToDate ? this.getNextLastResult : this.getNextElementFromInnerFunctor();
        this.isGetNextLastResultUpToDate = false;
        return result;
    }

    @Override
    public boolean hasMoreElements() {
        if (this.endOfEnumerationReached) {
            return false;
        }
        if (this.isGetNextLastResultUpToDate) {
            return true;
        }
        try {
            this.getNextLastResult = this.getNextElementFromInnerFunctor();
            this.isGetNextLastResultUpToDate = true;
            return true;
        }
        catch (NoSuchElementException noSuchE) {
            this.endOfEnumerationReached = true;
            return false;
        }
    }

    public boolean isEnumerationStartedEmpty() {
        if (this.innerFunctorUsageCounter == 0) {
            return !this.hasMoreElements();
        }
        return this.flagIsEnumerationStartedEmpty;
    }

    @Override
    public boolean hasNext() {
        return this.hasMoreElements();
    }

    @Override
    public E next() {
        return this.nextElement();
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static interface NextElementFunctor<EE> {
        public EE nextElement() throws NoSuchElementException;
    }
}

