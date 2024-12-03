/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.plugins.templates.transformer;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.io.IOUtils;

public class TransformUtils {
    public static String unspoilt(Reader reader) throws XhtmlException {
        StringWriter writer = new StringWriter();
        try {
            reader.reset();
            IOUtils.copy((Reader)reader, (Writer)writer);
        }
        catch (IOException ioException) {
            throw new XhtmlException(ioException.getMessage(), (Throwable)ioException);
        }
        return writer.toString();
    }
}

