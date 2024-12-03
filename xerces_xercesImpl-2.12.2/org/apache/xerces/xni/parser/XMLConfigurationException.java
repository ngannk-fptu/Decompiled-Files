/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;

public class XMLConfigurationException
extends XNIException {
    static final long serialVersionUID = -5437427404547669188L;
    public static final short NOT_RECOGNIZED = 0;
    public static final short NOT_SUPPORTED = 1;
    protected short fType;
    protected String fIdentifier;

    public XMLConfigurationException(short s, String string) {
        super(string);
        this.fType = s;
        this.fIdentifier = string;
    }

    public XMLConfigurationException(short s, String string, String string2) {
        super(string2);
        this.fType = s;
        this.fIdentifier = string;
    }

    public short getType() {
        return this.fType;
    }

    public String getIdentifier() {
        return this.fIdentifier;
    }
}

