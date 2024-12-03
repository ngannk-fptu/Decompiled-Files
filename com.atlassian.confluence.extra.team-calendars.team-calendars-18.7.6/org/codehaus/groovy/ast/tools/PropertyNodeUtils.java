/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import org.codehaus.groovy.ast.PropertyNode;

public class PropertyNodeUtils {
    public static int adjustPropertyModifiersForMethod(PropertyNode propNode) {
        return 0xFFFFFF3F & propNode.getModifiers();
    }
}

