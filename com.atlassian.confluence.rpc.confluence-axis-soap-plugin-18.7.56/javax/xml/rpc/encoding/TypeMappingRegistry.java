/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.encoding;

import java.io.Serializable;
import javax.xml.rpc.encoding.TypeMapping;

public interface TypeMappingRegistry
extends Serializable {
    public TypeMapping register(String var1, TypeMapping var2);

    public void registerDefault(TypeMapping var1);

    public TypeMapping getDefaultTypeMapping();

    public String[] getRegisteredEncodingStyleURIs();

    public TypeMapping getTypeMapping(String var1);

    public TypeMapping createTypeMapping();

    public TypeMapping unregisterTypeMapping(String var1);

    public boolean removeTypeMapping(TypeMapping var1);

    public void clear();
}

