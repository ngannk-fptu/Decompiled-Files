/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncSubstringBefore
extends Function2Args {
    static final long serialVersionUID = 4110547161672431775L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        String s2;
        String s1 = this.m_arg0.execute(xctxt).str();
        int index = s1.indexOf(s2 = this.m_arg1.execute(xctxt).str());
        return -1 == index ? XString.EMPTYSTRING : new XString(s1.substring(0, index));
    }
}

