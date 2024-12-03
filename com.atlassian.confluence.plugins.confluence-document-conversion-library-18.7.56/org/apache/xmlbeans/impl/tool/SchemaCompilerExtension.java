/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.util.Map;
import org.apache.xmlbeans.SchemaTypeSystem;

public interface SchemaCompilerExtension {
    public void schemaCompilerExtension(SchemaTypeSystem var1, Map var2);

    public String getExtensionName();
}

