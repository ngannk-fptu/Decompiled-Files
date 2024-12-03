/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.xml.ws.util;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public abstract class Pool<T> {
    private volatile SoftReference<ConcurrentLinkedQueue<T>> queue;

    public final T take() {
        T t = this.getQueue().poll();
        if (t == null) {
            return this.create();
        }
        return t;
    }

    private ConcurrentLinkedQueue<T> getQueue() {
        ConcurrentLinkedQueue<Object> d;
        SoftReference<ConcurrentLinkedQueue<T>> q = this.queue;
        if (q != null && (d = q.get()) != null) {
            return d;
        }
        d = new ConcurrentLinkedQueue();
        this.queue = new SoftReference<ConcurrentLinkedQueue<ConcurrentLinkedQueue<T>>>(d);
        return d;
    }

    public final void recycle(T t) {
        this.getQueue().offer(t);
    }

    protected abstract T create();

    public static final class TubePool
    extends Pool<Tube> {
        private final Tube master;

        public TubePool(Tube master) {
            this.master = master;
            this.recycle(master);
        }

        @Override
        protected Tube create() {
            return TubeCloner.clone(this.master);
        }

        @Deprecated
        public final Tube takeMaster() {
            return this.master;
        }
    }

    public static final class Unmarshaller
    extends Pool<javax.xml.bind.Unmarshaller> {
        private final JAXBContext context;

        public Unmarshaller(JAXBContext context) {
            this.context = context;
        }

        @Override
        protected javax.xml.bind.Unmarshaller create() {
            try {
                return this.context.createUnmarshaller();
            }
            catch (JAXBException e) {
                throw new AssertionError((Object)e);
            }
        }
    }

    public static final class Marshaller
    extends Pool<javax.xml.bind.Marshaller> {
        private final JAXBContext context;

        public Marshaller(JAXBContext context) {
            this.context = context;
        }

        @Override
        protected javax.xml.bind.Marshaller create() {
            try {
                return this.context.createMarshaller();
            }
            catch (JAXBException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
}

