/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import org.codehaus.groovy.util.Finalizable;
import org.codehaus.groovy.util.Reference;

public enum ReferenceType {
    SOFT{

        @Override
        protected <T, V extends Finalizable> Reference<T, V> createReference(T value, V handler, ReferenceQueue queue) {
            return new SoftRef<T, V>(value, handler, queue);
        }
    }
    ,
    WEAK{

        @Override
        protected <T, V extends Finalizable> Reference<T, V> createReference(T value, V handler, ReferenceQueue queue) {
            return new WeakRef<T, V>(value, handler, queue);
        }
    }
    ,
    PHANTOM{

        @Override
        protected <T, V extends Finalizable> Reference<T, V> createReference(T value, V handler, ReferenceQueue queue) {
            return new PhantomRef<T, V>(value, handler, queue);
        }
    }
    ,
    HARD{

        @Override
        protected <T, V extends Finalizable> Reference<T, V> createReference(T value, V handler, ReferenceQueue queue) {
            return new HardRef<T, V>(value, handler, queue);
        }
    };


    protected abstract <T, V extends Finalizable> Reference<T, V> createReference(T var1, V var2, ReferenceQueue var3);

    private static class HardRef<TT, V extends Finalizable>
    implements Reference<TT, V> {
        private TT ref;
        private final V handler;

        public HardRef(TT referent, V handler, ReferenceQueue<? super TT> q) {
            this.ref = referent;
            this.handler = handler;
        }

        @Override
        public V getHandler() {
            return this.handler;
        }

        @Override
        public TT get() {
            return this.ref;
        }

        @Override
        public void clear() {
            this.ref = null;
        }
    }

    private static class PhantomRef<TT, V extends Finalizable>
    extends PhantomReference<TT>
    implements Reference<TT, V> {
        private final V handler;

        public PhantomRef(TT referent, V handler, ReferenceQueue<? super TT> q) {
            super(referent, q);
            this.handler = handler;
        }

        @Override
        public V getHandler() {
            return this.handler;
        }
    }

    private static class WeakRef<TT, V extends Finalizable>
    extends WeakReference<TT>
    implements Reference<TT, V> {
        private final V handler;

        public WeakRef(TT referent, V handler, ReferenceQueue<? super TT> q) {
            super(referent, q);
            this.handler = handler;
        }

        @Override
        public V getHandler() {
            return this.handler;
        }
    }

    private static class SoftRef<TT, V extends Finalizable>
    extends SoftReference<TT>
    implements Reference<TT, V> {
        private final V handler;

        public SoftRef(TT referent, V handler, ReferenceQueue<? super TT> q) {
            super(referent, q);
            this.handler = handler;
        }

        @Override
        public V getHandler() {
            return this.handler;
        }
    }
}

