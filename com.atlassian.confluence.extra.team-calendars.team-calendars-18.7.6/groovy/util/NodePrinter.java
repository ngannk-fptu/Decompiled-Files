/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.util.IndentPrinter;
import groovy.util.Node;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

public class NodePrinter {
    protected final IndentPrinter out;

    public NodePrinter() {
        this(new IndentPrinter(new PrintWriter(new OutputStreamWriter(System.out))));
    }

    public NodePrinter(PrintWriter out) {
        this(new IndentPrinter(out));
    }

    public NodePrinter(IndentPrinter out) {
        if (out == null) {
            throw new NullPointerException("IndentPrinter 'out' must not be null!");
        }
        this.out = out;
    }

    public void print(Node node) {
        Object value;
        boolean hasAttributes;
        this.out.printIndent();
        this.printName(node);
        Map attributes = node.attributes();
        boolean bl = hasAttributes = attributes != null && !attributes.isEmpty();
        if (hasAttributes) {
            this.printAttributes(attributes);
        }
        if ((value = node.value()) instanceof List) {
            if (!hasAttributes) {
                this.out.print("()");
            }
            this.printList((List)value);
        } else if (value instanceof String) {
            this.out.print("('");
            this.out.print((String)value);
            this.out.println("')");
        } else {
            this.out.println("()");
        }
        this.out.flush();
    }

    protected void printName(Node node) {
        Object name = node.name();
        if (name != null) {
            this.out.print(name.toString());
        } else {
            this.out.print("null");
        }
    }

    protected void printList(List list) {
        if (list.isEmpty()) {
            this.out.println("");
        } else {
            this.out.println(" {");
            this.out.incrementIndent();
            for (Object value : list) {
                if (value instanceof Node) {
                    this.print((Node)value);
                    continue;
                }
                this.out.printIndent();
                this.out.println(InvokerHelper.toString(value));
            }
            this.out.decrementIndent();
            this.out.printIndent();
            this.out.println("}");
        }
    }

    protected void printAttributes(Map attributes) {
        this.out.print("(");
        boolean first = true;
        Iterator iterator = attributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            if (first) {
                first = false;
            } else {
                this.out.print(", ");
            }
            this.out.print(entry.getKey().toString());
            this.out.print(":");
            if (entry.getValue() instanceof String) {
                this.out.print("'" + entry.getValue() + "'");
                continue;
            }
            this.out.print(InvokerHelper.toString(entry.getValue()));
        }
        this.out.print(")");
    }
}

