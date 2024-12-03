/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.encoding;

import java.io.Serializable;
import java.util.Iterator;
import javax.xml.rpc.encoding.Deserializer;

public interface DeserializerFactory
extends Serializable {
    public Deserializer getDeserializerAs(String var1);

    public Iterator getSupportedMechanismTypes();
}

