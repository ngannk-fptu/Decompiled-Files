/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import org.htmlunit.cyberneko.xerces.xni.XNIException;

public class XMLConfigurationException
extends XNIException {
    private static final long serialVersionUID = 8987025467104000713L;
    public static final short NOT_RECOGNIZED = 0;
    public static final short NOT_SUPPORTED = 1;
    private final short type_;
    private final String identifier_;

    public XMLConfigurationException(short type, String identifier) {
        super(identifier);
        this.type_ = type;
        this.identifier_ = identifier;
    }

    public XMLConfigurationException(short type, String identifier, String message) {
        super(message);
        this.type_ = type;
        this.identifier_ = identifier;
    }

    public short getType() {
        return this.type_;
    }

    public String getIdentifier() {
        return this.identifier_;
    }
}

