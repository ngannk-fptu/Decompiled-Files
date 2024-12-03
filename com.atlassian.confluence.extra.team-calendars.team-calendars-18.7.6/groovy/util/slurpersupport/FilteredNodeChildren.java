/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Closure;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.Node;
import groovy.util.slurpersupport.NodeChild;
import groovy.util.slurpersupport.NodeChildren;
import groovy.util.slurpersupport.NodeIterator;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class FilteredNodeChildren
extends NodeChildren {
    private final Closure closure;

    public FilteredNodeChildren(GPathResult parent, Closure closure, Map<String, String> namespaceTagHints) {
        super(parent, parent.name, namespaceTagHints);
        this.closure = closure;
    }

    @Override
    public GPathResult pop() {
        return this.parent.parent;
    }

    @Override
    public Iterator nodeIterator() {
        return new NodeIterator(this.parent.nodeIterator()){

            @Override
            protected Object getNextNode(Iterator iter) {
                while (iter.hasNext()) {
                    Object node = iter.next();
                    if (!FilteredNodeChildren.this.closureYieldsTrueForNode(new NodeChild((Node)node, FilteredNodeChildren.this.parent, FilteredNodeChildren.this.namespaceTagHints))) continue;
                    return node;
                }
                return null;
            }
        };
    }

    private boolean closureYieldsTrueForNode(Object childNode) {
        return DefaultTypeTransformation.castToBoolean(this.closure.call(new Object[]{childNode}));
    }
}

