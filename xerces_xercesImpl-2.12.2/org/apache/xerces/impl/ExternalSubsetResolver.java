/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLDTDDescription;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;

public interface ExternalSubsetResolver
extends XMLEntityResolver {
    public XMLInputSource getExternalSubset(XMLDTDDescription var1) throws XNIException, IOException;
}

