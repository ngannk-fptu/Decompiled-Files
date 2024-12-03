/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class Suppliers {
    public static <A> Supplier<A> ofInstance(A instance) {
        return com.google.common.base.Suppliers.ofInstance(instance);
    }

    public static Supplier<Boolean> alwaysTrue() {
        return SupplyTrue.INSTANCE;
    }

    public static Supplier<Boolean> alwaysFalse() {
        return SupplyFalse.INSTANCE;
    }

    public static <A> Supplier<A> alwaysNull() {
        Nulls result = Nulls.NULL;
        return result;
    }

    public static <A> Supplier<A> fromOption(final Option<A> option) {
        return new Supplier<A>(){

            public A get() {
                return option.get();
            }
        };
    }

    public static <A, B> Supplier<B> fromFunction(final Function<? super A, ? extends B> f, final A a) {
        return new Supplier<B>(){

            public B get() {
                return f.apply(a);
            }
        };
    }

    static enum Nulls implements Supplier<Object>
    {
        NULL;


        public Object get() {
            return null;
        }
    }

    private static enum SupplyFalse implements Supplier<Boolean>
    {
        INSTANCE;


        public Boolean get() {
            return false;
        }
    }

    private static enum SupplyTrue implements Supplier<Boolean>
    {
        INSTANCE;


        public Boolean get() {
            return true;
        }
    }
}

