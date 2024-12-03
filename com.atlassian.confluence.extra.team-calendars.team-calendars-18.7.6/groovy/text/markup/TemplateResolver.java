/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.text.markup.TemplateConfiguration;
import java.io.IOException;
import java.net.URL;

public interface TemplateResolver {
    public void configure(ClassLoader var1, TemplateConfiguration var2);

    public URL resolveTemplate(String var1) throws IOException;
}

