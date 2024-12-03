/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.JDOMException;

public class DataConversionException
extends JDOMException {
    private static final long serialVersionUID = 200L;

    public DataConversionException(String name, String dataType) {
        super("The XML construct " + name + " could not be converted to a " + dataType);
    }
}

