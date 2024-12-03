/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Fields
 */
package org.eclipse.jetty.client.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.Fields;

@Deprecated
public class FormContentProvider
extends StringContentProvider {
    public FormContentProvider(Fields fields) {
        this(fields, StandardCharsets.UTF_8);
    }

    public FormContentProvider(Fields fields, Charset charset) {
        super("application/x-www-form-urlencoded", FormContentProvider.convert(fields, charset), charset);
    }

    public static String convert(Fields fields) {
        return FormContentProvider.convert(fields, StandardCharsets.UTF_8);
    }

    public static String convert(Fields fields, Charset charset) {
        return FormRequestContent.convert(fields, charset);
    }
}

