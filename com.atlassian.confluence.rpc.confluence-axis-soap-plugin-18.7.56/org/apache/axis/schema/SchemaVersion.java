/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.schema;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.schema.SchemaVersion1999;
import org.apache.axis.schema.SchemaVersion2000;
import org.apache.axis.schema.SchemaVersion2001;

public interface SchemaVersion
extends Serializable {
    public static final SchemaVersion SCHEMA_1999 = new SchemaVersion1999();
    public static final SchemaVersion SCHEMA_2000 = new SchemaVersion2000();
    public static final SchemaVersion SCHEMA_2001 = new SchemaVersion2001();

    public QName getNilQName();

    public String getXsiURI();

    public String getXsdURI();

    public void registerSchemaSpecificTypes(TypeMappingImpl var1);
}

