/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

public interface Converter<R, T> {
    public R convert(T var1) throws IllegalArgumentException;

    public R error(String var1);
}

