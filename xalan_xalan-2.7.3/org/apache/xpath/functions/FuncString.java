/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionDef1Arg;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncString
extends FunctionDef1Arg {
    static final long serialVersionUID = -2206677149497712883L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        return (XString)this.getArg0AsString(xctxt);
    }
}

