/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.COM.EnumVariant;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Iterator;

public class IComEnumVariantIterator
implements Iterable<Variant.VARIANT>,
Iterator<Variant.VARIANT>,
Closeable {
    private Variant.VARIANT nextValue;
    private EnumVariant backingIteration;

    public static IComEnumVariantIterator wrap(IDispatch dispatch) {
        PointerByReference pbr = new PointerByReference();
        IUnknown unknwn = dispatch.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM, new Object[0]);
        unknwn.QueryInterface(EnumVariant.REFIID, pbr);
        unknwn.Release();
        EnumVariant variant = new EnumVariant(pbr.getValue());
        return new IComEnumVariantIterator(variant);
    }

    public IComEnumVariantIterator(EnumVariant backingIteration) {
        this.backingIteration = backingIteration;
        this.retrieveNext();
    }

    @Override
    public boolean hasNext() {
        return this.nextValue != null;
    }

    @Override
    public Variant.VARIANT next() {
        Variant.VARIANT current = this.nextValue;
        this.retrieveNext();
        return current;
    }

    private void retrieveNext() {
        if (this.backingIteration == null) {
            return;
        }
        Variant.VARIANT[] variants = this.backingIteration.Next(1);
        if (variants.length == 0) {
            this.close();
        } else {
            this.nextValue = variants[0];
        }
    }

    @Override
    public void close() {
        if (this.backingIteration != null) {
            this.nextValue = null;
            this.backingIteration.Release();
            this.backingIteration = null;
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public Iterator<Variant.VARIANT> iterator() {
        return this;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}

