/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

public class ResolveFunction
implements Function {
    public static final QName QNAME = new QName("http://abdera.apache.org", "resolve");

    public Object call(Context context, List args) throws FunctionCallException {
        ArrayList<String> results = new ArrayList<String>();
        if (args.isEmpty()) {
            return null;
        }
        Navigator navigator = context.getNavigator();
        for (Object obj : args) {
            if (!(obj instanceof List)) continue;
            for (Object o : (List)obj) {
                try {
                    String value = StringFunction.evaluate(o, navigator);
                    IRI resolved = null;
                    IRI baseUri = null;
                    if (o instanceof OMNode) {
                        OMNode node = (OMNode)o;
                        OMContainer el = node.getParent();
                        if (el instanceof Document) {
                            Document doc = (Document)((Object)el);
                            baseUri = doc.getBaseUri();
                        } else if (el instanceof Element) {
                            Element element = (Element)((Object)el);
                            baseUri = element.getBaseUri();
                        }
                    } else if (o instanceof OMAttribute) {
                        OMAttribute attr = (OMAttribute)o;
                        Element element = (Element)context.getNavigator().getParentNode(attr);
                        baseUri = element.getBaseUri();
                    }
                    if (baseUri == null) continue;
                    resolved = baseUri.resolve(value);
                    results.add(resolved.toString());
                }
                catch (Exception e) {}
            }
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        if (results.size() > 1) {
            return results;
        }
        return null;
    }
}

