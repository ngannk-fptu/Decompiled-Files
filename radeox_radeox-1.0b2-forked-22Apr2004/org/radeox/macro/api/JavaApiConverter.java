/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.api;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.api.BaseApiConverter;

public class JavaApiConverter
extends BaseApiConverter {
    public void appendUrl(Writer writer, String className) throws IOException {
        writer.write(this.baseUrl);
        writer.write(className.replace('.', '/'));
        writer.write(".html");
    }

    public String getName() {
        return "Java";
    }
}

