/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.DelegatingParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class CryptoParser
extends DelegatingParser {
    private static final long serialVersionUID = -3507995752666557731L;
    private final String transformation;
    private final Provider provider;
    private final Set<MediaType> types;

    public CryptoParser(String transformation, Provider provider, Set<MediaType> types) {
        this.transformation = transformation;
        this.provider = provider;
        this.types = types;
    }

    public CryptoParser(String transformation, Set<MediaType> types) {
        this(transformation, null, types);
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.types;
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        try {
            Cipher cipher = this.provider != null ? Cipher.getInstance(this.transformation, this.provider) : Cipher.getInstance(this.transformation);
            Key key = context.get(Key.class);
            if (key == null) {
                throw new EncryptedDocumentException("No decryption key provided");
            }
            AlgorithmParameters params = context.get(AlgorithmParameters.class);
            SecureRandom random = context.get(SecureRandom.class);
            if (params != null && random != null) {
                cipher.init(2, key, params, random);
            } else if (params != null) {
                cipher.init(2, key, params);
            } else if (random != null) {
                cipher.init(2, key, random);
            } else {
                cipher.init(2, key);
            }
            super.parse(new CipherInputStream(stream, cipher), handler, metadata, context);
        }
        catch (GeneralSecurityException e) {
            throw new TikaException("Unable to decrypt document stream", e);
        }
    }
}

