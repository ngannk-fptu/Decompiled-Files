/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncSubstring
extends Function3Args {
    static final long serialVersionUID = -5996676095024715502L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XMLString substr;
        int startIndex;
        XMLString s1 = this.m_arg0.execute(xctxt).xstr();
        double start = this.m_arg1.execute(xctxt).num();
        int lenOfS1 = s1.length();
        if (lenOfS1 <= 0) {
            return XString.EMPTYSTRING;
        }
        if (Double.isNaN(start)) {
            start = -1000000.0;
            startIndex = 0;
        } else {
            int n = startIndex = (start = (double)Math.round(start)) > 0.0 ? (int)start - 1 : 0;
        }
        if (null != this.m_arg2) {
            double len = this.m_arg2.num(xctxt);
            int end = (int)((double)Math.round(len) + start) - 1;
            if (end < 0) {
                end = 0;
            } else if (end > lenOfS1) {
                end = lenOfS1;
            }
            if (startIndex > lenOfS1) {
                startIndex = lenOfS1;
            }
            substr = s1.substring(startIndex, end);
        } else {
            if (startIndex > lenOfS1) {
                startIndex = lenOfS1;
            }
            substr = s1.substring(startIndex);
        }
        return (XString)substr;
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
        if (argNum < 2) {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("ER_TWO_OR_THREE", null));
    }
}

