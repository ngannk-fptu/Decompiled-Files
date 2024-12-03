/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

public interface JsonParser {
    public Object parse(String var1);

    public Object parse(byte[] var1);

    public Object parse(byte[] var1, String var2);

    public Object parse(CharSequence var1);

    public Object parse(char[] var1);

    public Object parse(Reader var1);

    public Object parse(InputStream var1);

    public Object parse(InputStream var1, String var2);

    public Object parse(File var1, String var2);
}

