/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.xmlrules;

import org.apache.commons.digester.xmlrules.XmlLoadException;

public class CircularIncludeException
extends XmlLoadException {
    public CircularIncludeException(String fileName) {
        super("Circular file inclusion detected for file: " + fileName);
    }
}

