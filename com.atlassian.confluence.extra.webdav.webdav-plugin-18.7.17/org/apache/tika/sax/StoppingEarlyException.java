/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.xml.sax.SAXException;

public class StoppingEarlyException
extends SAXException {
    public static final StoppingEarlyException INSTANCE = new StoppingEarlyException();
}

