/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.schema;

import java.io.Serializable;
import javax.wsdl.extensions.schema.Schema;

public interface SchemaReference
extends Serializable {
    public String getId();

    public void setId(String var1);

    public String getSchemaLocationURI();

    public void setSchemaLocationURI(String var1);

    public Schema getReferencedSchema();

    public void setReferencedSchema(Schema var1);
}

