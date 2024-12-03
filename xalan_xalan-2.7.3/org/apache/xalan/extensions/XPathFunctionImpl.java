/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.util.List;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import org.apache.xalan.extensions.ExtensionHandler;

public class XPathFunctionImpl
implements XPathFunction {
    private ExtensionHandler m_handler;
    private String m_funcName;

    public XPathFunctionImpl(ExtensionHandler handler, String funcName) {
        this.m_handler = handler;
        this.m_funcName = funcName;
    }

    public Object evaluate(List args) throws XPathFunctionException {
        Vector argsVec = XPathFunctionImpl.listToVector(args);
        try {
            return this.m_handler.callFunction(this.m_funcName, argsVec, null, null);
        }
        catch (TransformerException e) {
            throw new XPathFunctionException(e);
        }
    }

    private static Vector listToVector(List args) {
        if (args == null) {
            return null;
        }
        if (args instanceof Vector) {
            return (Vector)args;
        }
        Vector result = new Vector();
        result.addAll(args);
        return result;
    }
}

