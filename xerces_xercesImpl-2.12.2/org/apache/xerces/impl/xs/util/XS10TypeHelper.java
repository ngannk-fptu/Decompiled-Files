/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

public class XS10TypeHelper {
    private XS10TypeHelper() {
    }

    public static String getSchemaTypeName(XSTypeDefinition xSTypeDefinition) {
        String string = "";
        string = xSTypeDefinition instanceof XSSimpleTypeDefinition ? ((XSSimpleTypeDecl)xSTypeDefinition).getTypeName() : ((XSComplexTypeDecl)xSTypeDefinition).getTypeName();
        return string;
    }
}

