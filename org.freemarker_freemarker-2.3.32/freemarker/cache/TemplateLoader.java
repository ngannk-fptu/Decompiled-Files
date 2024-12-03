/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import java.io.IOException;
import java.io.Reader;

public interface TemplateLoader {
    public Object findTemplateSource(String var1) throws IOException;

    public long getLastModified(Object var1);

    public Reader getReader(Object var1, String var2) throws IOException;

    public void closeTemplateSource(Object var1) throws IOException;
}

