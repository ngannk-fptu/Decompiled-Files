/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.IOException;
import java.io.Writer;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.repackage.Repackager;

public interface SchemaCodePrinter {
    @Deprecated
    default public void printTypeImpl(Writer writer, SchemaType sType) throws IOException {
        this.printTypeImpl(writer, sType, null);
    }

    default public void printTypeImpl(Writer writer, SchemaType sType, XmlOptions opt) throws IOException {
        this.printTypeImpl(writer, sType);
    }

    @Deprecated
    default public void printType(Writer writer, SchemaType sType) throws IOException {
        this.printType(writer, sType, null);
    }

    default public void printType(Writer writer, SchemaType sType, XmlOptions opt) throws IOException {
        this.printType(writer, sType);
    }

    public void printHolder(Writer var1, SchemaTypeSystem var2, XmlOptions var3, Repackager var4) throws IOException;
}

