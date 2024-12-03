/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import freemarker.core.Environment;
import freemarker.ext.dom.NodeListModel;
import freemarker.ext.dom.XPathSupport;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

class SunInternalXalanXPathSupport
implements XPathSupport {
    private XPathContext xpathContext = new XPathContext();
    private static final PrefixResolver CUSTOM_PREFIX_RESOLVER = new PrefixResolver(){

        @Override
        public String getNamespaceForPrefix(String prefix, Node node) {
            return this.getNamespaceForPrefix(prefix);
        }

        @Override
        public String getNamespaceForPrefix(String prefix) {
            if (prefix.equals("D")) {
                return Environment.getCurrentEnvironment().getDefaultNS();
            }
            return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
        }

        @Override
        public String getBaseIdentifier() {
            return null;
        }

        @Override
        public boolean handlesNullPrefixes() {
            return false;
        }
    };

    SunInternalXalanXPathSupport() {
    }

    @Override
    public synchronized TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
        if (!(context instanceof Node)) {
            if (context == null || SunInternalXalanXPathSupport.isNodeList(context)) {
                int cnt = context != null ? ((List)context).size() : 0;
                throw new TemplateModelException((cnt != 0 ? "Xalan can't perform an XPath query against a Node Set (contains " + cnt + " node(s)). Expecting a single Node." : "Xalan can't perform an XPath query against an empty Node Set.") + " (There's no such restriction if you configure FreeMarker to use Jaxen for XPath.)");
            }
            throw new TemplateModelException("Can't perform an XPath query against a " + context.getClass().getName() + ". Expecting a single org.w3c.dom.Node.");
        }
        Node node = (Node)context;
        try {
            XPath xpath = new XPath(xpathQuery, null, CUSTOM_PREFIX_RESOLVER, 0, null);
            int ctxtNode = this.xpathContext.getDTMHandleFromNode(node);
            XObject xresult = xpath.execute(this.xpathContext, ctxtNode, CUSTOM_PREFIX_RESOLVER);
            if (xresult instanceof XNodeSet) {
                Node n;
                NodeListModel result = new NodeListModel(node);
                result.xpathSupport = this;
                NodeIterator nodeIterator = xresult.nodeset();
                do {
                    if ((n = nodeIterator.nextNode()) == null) continue;
                    result.add(n);
                } while (n != null);
                return result.size() == 1 ? result.get(0) : result;
            }
            if (xresult instanceof XBoolean) {
                return ((XBoolean)xresult).bool() ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
            if (xresult instanceof XNull) {
                return null;
            }
            if (xresult instanceof XString) {
                return new SimpleScalar(xresult.toString());
            }
            if (xresult instanceof XNumber) {
                return new SimpleNumber((Number)((XNumber)xresult).num());
            }
            throw new TemplateModelException("Cannot deal with type: " + xresult.getClass().getName());
        }
        catch (TransformerException te) {
            throw new TemplateModelException(te);
        }
    }

    private static boolean isNodeList(Object context) {
        if (!(context instanceof List)) {
            return false;
        }
        List ls = (List)context;
        int ln = ls.size();
        for (int i = 0; i < ln; ++i) {
            if (ls.get(i) instanceof Node) continue;
            return false;
        }
        return true;
    }
}

