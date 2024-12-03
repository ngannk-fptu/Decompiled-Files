/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.util.IndentPrinter;
import groovy.util.Node;
import groovy.xml.QName;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

public class XmlNodePrinter {
    protected final IndentPrinter out;
    private String quote;
    private boolean namespaceAware = true;
    private boolean preserveWhitespace = false;
    private boolean expandEmptyElements = false;

    public XmlNodePrinter(PrintWriter out) {
        this(out, "  ");
    }

    public XmlNodePrinter(PrintWriter out, String indent) {
        this(out, indent, "\"");
    }

    public XmlNodePrinter(PrintWriter out, String indent, String quote) {
        this(new IndentPrinter(out, indent), quote);
    }

    public XmlNodePrinter(IndentPrinter out) {
        this(out, "\"");
    }

    public XmlNodePrinter(IndentPrinter out, String quote) {
        if (out == null) {
            throw new IllegalArgumentException("Argument 'IndentPrinter out' must not be null!");
        }
        this.out = out;
        this.quote = quote;
    }

    public XmlNodePrinter() {
        this(new PrintWriter(new OutputStreamWriter(System.out)));
    }

    public void print(Node node) {
        this.print(node, new NamespaceContext());
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public boolean isPreserveWhitespace() {
        return this.preserveWhitespace;
    }

    public void setPreserveWhitespace(boolean preserveWhitespace) {
        this.preserveWhitespace = preserveWhitespace;
    }

    public String getQuote() {
        return this.quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public boolean isExpandEmptyElements() {
        return this.expandEmptyElements;
    }

    public void setExpandEmptyElements(boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
    }

    protected void print(Node node, NamespaceContext ctx) {
        if (XmlNodePrinter.isEmptyElement(node)) {
            this.printLineBegin();
            this.out.print("<");
            this.out.print(this.getName(node));
            if (ctx != null) {
                this.printNamespace(node, ctx);
            }
            this.printNameAttributes(node.attributes(), ctx);
            if (this.expandEmptyElements) {
                this.out.print("></");
                this.out.print(this.getName(node));
                this.out.print(">");
            } else {
                this.out.print("/>");
            }
            this.printLineEnd();
            this.out.flush();
            return;
        }
        if (this.printSpecialNode(node)) {
            this.out.flush();
            return;
        }
        Object value = node.value();
        if (value instanceof List) {
            this.printName(node, ctx, true, this.isListOfSimple((List)value));
            this.printList((List)value, ctx);
            this.printName(node, ctx, false, this.isListOfSimple((List)value));
            this.out.flush();
            return;
        }
        this.printName(node, ctx, true, this.preserveWhitespace);
        this.printSimpleItemWithIndent(value);
        this.printName(node, ctx, false, this.preserveWhitespace);
        this.out.flush();
    }

    private boolean isListOfSimple(List value) {
        for (Object p : value) {
            if (!(p instanceof Node)) continue;
            return false;
        }
        return this.preserveWhitespace;
    }

    protected void printLineBegin() {
        this.out.printIndent();
    }

    protected void printLineEnd() {
        this.printLineEnd(null);
    }

    protected void printLineEnd(String comment) {
        if (comment != null) {
            this.out.print(" <!-- ");
            this.out.print(comment);
            this.out.print(" -->");
        }
        this.out.println();
        this.out.flush();
    }

    protected void printList(List list, NamespaceContext ctx) {
        this.out.incrementIndent();
        for (Object value : list) {
            NamespaceContext context = new NamespaceContext(ctx);
            if (value instanceof Node) {
                this.print((Node)value, context);
                continue;
            }
            this.printSimpleItem(value);
        }
        this.out.decrementIndent();
    }

    protected void printSimpleItem(Object value) {
        if (!this.preserveWhitespace) {
            this.printLineBegin();
        }
        this.printEscaped(InvokerHelper.toString(value), false);
        if (!this.preserveWhitespace) {
            this.printLineEnd();
        }
    }

    protected void printName(Node node, NamespaceContext ctx, boolean begin, boolean preserve) {
        if (node == null) {
            throw new NullPointerException("Node must not be null.");
        }
        Object name = node.name();
        if (name == null) {
            throw new NullPointerException("Name must not be null.");
        }
        if (!preserve || begin) {
            this.printLineBegin();
        }
        this.out.print("<");
        if (!begin) {
            this.out.print("/");
        }
        this.out.print(this.getName(node));
        if (ctx != null) {
            this.printNamespace(node, ctx);
        }
        if (begin) {
            this.printNameAttributes(node.attributes(), ctx);
        }
        this.out.print(">");
        if (!preserve || !begin) {
            this.printLineEnd();
        }
    }

    protected boolean printSpecialNode(Node node) {
        return false;
    }

    protected void printNamespace(Object object, NamespaceContext ctx) {
        if (this.namespaceAware) {
            String prefix;
            QName qname;
            String namespaceUri;
            if (object instanceof Node) {
                this.printNamespace(((Node)object).name(), ctx);
            } else if (object instanceof QName && (namespaceUri = (qname = (QName)object).getNamespaceURI()) != null && !ctx.isPrefixRegistered(prefix = qname.getPrefix(), namespaceUri)) {
                ctx.registerNamespacePrefix(prefix, namespaceUri);
                this.out.print(" ");
                this.out.print("xmlns");
                if (prefix.length() > 0) {
                    this.out.print(":");
                    this.out.print(prefix);
                }
                this.out.print("=" + this.quote);
                this.out.print(namespaceUri);
                this.out.print(this.quote);
            }
        }
    }

    protected void printNameAttributes(Map attributes, NamespaceContext ctx) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        Iterator iterator = attributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry p;
            Map.Entry entry = p = iterator.next();
            this.out.print(" ");
            this.out.print(this.getName(entry.getKey()));
            this.out.print("=");
            Object value = entry.getValue();
            this.out.print(this.quote);
            if (value instanceof String) {
                this.printEscaped((String)value, true);
            } else {
                this.printEscaped(InvokerHelper.toString(value), true);
            }
            this.out.print(this.quote);
            this.printNamespace(entry.getKey(), ctx);
        }
    }

    private static boolean isEmptyElement(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node must not be null!");
        }
        if (!node.children().isEmpty()) {
            return false;
        }
        return node.text().length() == 0;
    }

    private String getName(Object object) {
        if (object instanceof String) {
            return (String)object;
        }
        if (object instanceof QName) {
            QName qname = (QName)object;
            if (!this.namespaceAware) {
                return qname.getLocalPart();
            }
            return qname.getQualifiedName();
        }
        if (object instanceof Node) {
            Object name = ((Node)object).name();
            return this.getName(name);
        }
        return object.toString();
    }

    private void printSimpleItemWithIndent(Object value) {
        if (!this.preserveWhitespace) {
            this.out.incrementIndent();
        }
        this.printSimpleItem(value);
        if (!this.preserveWhitespace) {
            this.out.decrementIndent();
        }
    }

    private void printEscaped(String s, boolean isAttributeValue) {
        block9: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<': {
                    this.out.print("&lt;");
                    continue block9;
                }
                case '>': {
                    this.out.print("&gt;");
                    continue block9;
                }
                case '&': {
                    this.out.print("&amp;");
                    continue block9;
                }
                case '\'': {
                    if (isAttributeValue && this.quote.equals("'")) {
                        this.out.print("&apos;");
                        continue block9;
                    }
                    this.out.print(c);
                    continue block9;
                }
                case '\"': {
                    if (isAttributeValue && this.quote.equals("\"")) {
                        this.out.print("&quot;");
                        continue block9;
                    }
                    this.out.print(c);
                    continue block9;
                }
                case '\n': {
                    if (isAttributeValue) {
                        this.out.print("&#10;");
                        continue block9;
                    }
                    this.out.print(c);
                    continue block9;
                }
                case '\r': {
                    if (isAttributeValue) {
                        this.out.print("&#13;");
                        continue block9;
                    }
                    this.out.print(c);
                    continue block9;
                }
                default: {
                    this.out.print(c);
                }
            }
        }
    }

    protected static class NamespaceContext {
        private final Map<String, String> namespaceMap = new HashMap<String, String>();

        public NamespaceContext() {
        }

        public NamespaceContext(NamespaceContext context) {
            this();
            this.namespaceMap.putAll(context.namespaceMap);
        }

        public boolean isPrefixRegistered(String prefix, String uri) {
            return this.namespaceMap.containsKey(prefix) && this.namespaceMap.get(prefix).equals(uri);
        }

        public void registerNamespacePrefix(String prefix, String uri) {
            if (!this.isPrefixRegistered(prefix, uri)) {
                this.namespaceMap.put(prefix, uri);
            }
        }

        public String getNamespace(String prefix) {
            String uri = this.namespaceMap.get(prefix);
            return uri == null ? null : uri.toString();
        }
    }
}

