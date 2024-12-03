/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.Serializable;
import org.apache.axis.encoding.TypeMapping;

public interface TypeMappingRegistry
extends javax.xml.rpc.encoding.TypeMappingRegistry,
Serializable {
    public void delegate(TypeMappingRegistry var1);

    public TypeMapping getOrMakeTypeMapping(String var1);
}

