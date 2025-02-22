/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class PlainObject
extends JOSEObject {
    private static final long serialVersionUID = 1L;
    private final PlainHeader header;

    public PlainObject(Payload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("The payload must not be null");
        }
        this.setPayload(payload);
        this.header = new PlainHeader();
    }

    public PlainObject(PlainHeader header, Payload payload) {
        if (header == null) {
            throw new IllegalArgumentException("The unsecured header must not be null");
        }
        this.header = header;
        if (payload == null) {
            throw new IllegalArgumentException("The payload must not be null");
        }
        this.setPayload(payload);
    }

    public PlainObject(Base64URL firstPart, Base64URL secondPart) throws ParseException {
        if (firstPart == null) {
            throw new IllegalArgumentException("The first part must not be null");
        }
        try {
            this.header = PlainHeader.parse(firstPart);
        }
        catch (ParseException e) {
            throw new ParseException("Invalid unsecured header: " + e.getMessage(), 0);
        }
        if (secondPart == null) {
            throw new IllegalArgumentException("The second part must not be null");
        }
        this.setPayload(new Payload(secondPart));
        this.setParsedParts(firstPart, secondPart, null);
    }

    @Override
    public PlainHeader getHeader() {
        return this.header;
    }

    @Override
    public String serialize() {
        return this.header.toBase64URL().toString() + '.' + this.getPayload().toBase64URL().toString() + '.';
    }

    public static PlainObject parse(String s) throws ParseException {
        Base64URL[] parts = JOSEObject.split(s);
        if (!parts[2].toString().isEmpty()) {
            throw new ParseException("Unexpected third Base64URL part", 0);
        }
        return new PlainObject(parts[0], parts[1]);
    }
}

