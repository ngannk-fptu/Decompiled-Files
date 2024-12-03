/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncSubstringAfter
extends Function2Args {
    static final long serialVersionUID = -8119731889862512194L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XMLString s2;
        XMLString s1 = this.m_arg0.execute(xctxt).xstr();
        int index = s1.indexOf(s2 = this.m_arg1.execute(xctxt).xstr());
        return -1 == index ? XString.EMPTYSTRING : (XString)s1.substring(index + s2.length());
    }
}

