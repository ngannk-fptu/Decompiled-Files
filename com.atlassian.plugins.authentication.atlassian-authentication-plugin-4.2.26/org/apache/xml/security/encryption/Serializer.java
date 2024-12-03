/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.io.IOException;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface Serializer {
    public byte[] serializeToByteArray(Element var1) throws Exception;

    public byte[] serializeToByteArray(NodeList var1) throws Exception;

    public Node deserialize(byte[] var1, Node var2) throws XMLEncryptionException, IOException;
}

