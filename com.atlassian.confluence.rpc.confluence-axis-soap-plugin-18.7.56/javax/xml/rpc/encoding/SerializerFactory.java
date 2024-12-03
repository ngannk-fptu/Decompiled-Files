/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.encoding;

import java.io.Serializable;
import java.util.Iterator;
import javax.xml.rpc.encoding.Serializer;

public interface SerializerFactory
extends Serializable {
    public Serializer getSerializerAs(String var1);

    public Iterator getSupportedMechanismTypes();
}

