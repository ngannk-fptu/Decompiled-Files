/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

interface ValidatorHelper {
    public void validate(Source var1, Result var2) throws SAXException, IOException;
}

