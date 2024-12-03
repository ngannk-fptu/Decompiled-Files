/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.EntityResolver;

public interface XMLBuilderProperties<T> {
    public T setDocumentBuilder(DocumentBuilder var1);

    public T setEntityResolver(EntityResolver var1);

    public T setPublicID(String var1);

    public T setSystemID(String var1);

    public T setValidating(boolean var1);

    public T setSchemaValidation(boolean var1);
}

