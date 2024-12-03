/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.ObjectFactory;
import org.apache.xpath.functions.SecuritySupport;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncSystemProperty
extends FunctionOneArg {
    static final long serialVersionUID = 3694874980992204867L;
    static final String XSLT_PROPERTIES = "org/apache/xalan/res/XSLTInfo.properties";

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        String propName;
        String result;
        block16: {
            String fullName;
            block17: {
                fullName = this.m_arg0.execute(xctxt).str();
                int indexOfNSSep = fullName.indexOf(58);
                result = null;
                propName = "";
                Properties xsltInfo = new Properties();
                this.loadPropertyFile(XSLT_PROPERTIES, xsltInfo);
                if (indexOfNSSep <= 0) break block17;
                String prefix = indexOfNSSep >= 0 ? fullName.substring(0, indexOfNSSep) : "";
                String namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
                String string = propName = indexOfNSSep < 0 ? fullName : fullName.substring(indexOfNSSep + 1);
                if (namespace.startsWith("http://www.w3.org/XSL/Transform") || namespace.equals("http://www.w3.org/1999/XSL/Transform")) {
                    result = xsltInfo.getProperty(propName);
                    if (null == result) {
                        this.warn(xctxt, "WG_PROPERTY_NOT_SUPPORTED", new Object[]{fullName});
                        return XString.EMPTYSTRING;
                    }
                    break block16;
                } else {
                    this.warn(xctxt, "WG_DONT_DO_ANYTHING_WITH_NS", new Object[]{namespace, fullName});
                    try {
                        if (!xctxt.isSecureProcessing()) {
                            result = System.getProperty(propName);
                        } else {
                            this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[]{fullName});
                        }
                        if (null == result) {
                            return XString.EMPTYSTRING;
                        }
                        break block16;
                    }
                    catch (SecurityException se) {
                        this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[]{fullName});
                        return XString.EMPTYSTRING;
                    }
                }
            }
            try {
                if (!xctxt.isSecureProcessing()) {
                    result = System.getProperty(fullName);
                } else {
                    this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[]{fullName});
                }
                if (null == result) {
                    return XString.EMPTYSTRING;
                }
            }
            catch (SecurityException se) {
                this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[]{fullName});
                return XString.EMPTYSTRING;
            }
        }
        if (propName.equals("version") && result.length() > 0) {
            try {
                return new XString("1.0");
            }
            catch (Exception ex) {
                return new XString(result);
            }
        }
        return new XString(result);
    }

    public void loadPropertyFile(String file, Properties target) {
        try {
            InputStream is = SecuritySupport.getResourceAsStream(ObjectFactory.findClassLoader(), file);
            BufferedInputStream bis = new BufferedInputStream(is);
            target.load(bis);
            bis.close();
        }
        catch (Exception ex) {
            throw new WrappedRuntimeException(ex);
        }
    }
}

