/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.codec;

import java.io.Reader;
import java.lang.reflect.Type;

public interface Codec {
    public Object decode(Reader var1, Type var2) throws Exception;

    public void encode(Type var1, Object var2, Appendable var3) throws Exception;
}

