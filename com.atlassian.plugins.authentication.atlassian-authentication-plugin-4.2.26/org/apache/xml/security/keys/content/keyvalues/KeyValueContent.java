/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.content.keyvalues;

import java.security.PublicKey;
import org.apache.xml.security.exceptions.XMLSecurityException;

public interface KeyValueContent {
    public PublicKey getPublicKey() throws XMLSecurityException;
}

