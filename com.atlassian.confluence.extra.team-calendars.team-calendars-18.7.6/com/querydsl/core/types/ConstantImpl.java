/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.Visitor;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ConstantImpl<T>
extends ExpressionBase<T>
implements Constant<T> {
    private static final long serialVersionUID = -3898138057967814118L;
    private static final int CACHE_SIZE = 256;
    private final T constant;

    public static Constant<Boolean> create(boolean b) {
        return b ? Constants.TRUE : Constants.FALSE;
    }

    public static Constant<Byte> create(byte i) {
        if (i >= 0) {
            return Constants.BYTES[i];
        }
        return new ConstantImpl<Byte>(Byte.class, i);
    }

    public static Constant<Character> create(char i) {
        if (i < '\u0100') {
            return Constants.CHARACTERS[i];
        }
        return new ConstantImpl<Character>(Character.class, Character.valueOf(i));
    }

    public static Constant<Integer> create(int i) {
        if (i >= 0 && i < 256) {
            return Constants.INTEGERS[i];
        }
        return new ConstantImpl<Integer>(Integer.class, i);
    }

    public static Constant<Long> create(long i) {
        if (i >= 0L && i < 256L) {
            return Constants.LONGS[(int)i];
        }
        return new ConstantImpl<Long>(Long.class, i);
    }

    public static Constant<Short> create(short i) {
        if (i >= 0 && i < 256) {
            return Constants.SHORTS[i];
        }
        return new ConstantImpl<Short>(Short.class, i);
    }

    public static <T> Constant<T> create(T obj) {
        return new ConstantImpl<T>(obj);
    }

    public static <T> Constant<T> create(Class<T> type, T constant) {
        return new ConstantImpl<T>(type, constant);
    }

    private ConstantImpl(T constant) {
        this(constant.getClass(), constant);
    }

    private ConstantImpl(Class<T> type, T constant) {
        super(type);
        this.constant = constant;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Constant) {
            return ((Constant)o).getConstant().equals(this.constant);
        }
        return false;
    }

    @Override
    public T getConstant() {
        return this.constant;
    }

    private static class Constants {
        private static final Constant<Character>[] CHARACTERS = new Constant[256];
        private static final Constant<Byte>[] BYTES = new Constant[256];
        private static final Constant<Integer>[] INTEGERS = new Constant[256];
        private static final Constant<Long>[] LONGS = new Constant[256];
        private static final Constant<Short>[] SHORTS = new Constant[256];
        private static final Constant<Boolean> FALSE = new ConstantImpl<Boolean>(Boolean.FALSE);
        private static final Constant<Boolean> TRUE = new ConstantImpl<Boolean>(Boolean.TRUE);

        private Constants() {
        }

        static {
            for (int i = 0; i < 256; ++i) {
                Constants.INTEGERS[i] = new ConstantImpl<Integer>(Integer.class, i);
                Constants.SHORTS[i] = new ConstantImpl<Short>(Short.class, (short)i);
                Constants.BYTES[i] = new ConstantImpl<Byte>(Byte.class, (byte)i);
                Constants.CHARACTERS[i] = new ConstantImpl<Character>(Character.class, Character.valueOf((char)i));
                Constants.LONGS[i] = new ConstantImpl<Long>(Long.class, i);
            }
        }
    }
}

