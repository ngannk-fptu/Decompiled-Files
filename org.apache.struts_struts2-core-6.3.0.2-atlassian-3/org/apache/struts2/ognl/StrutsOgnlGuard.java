/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.Node
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.ognl;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ognl.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ognl.OgnlGuard;

public class StrutsOgnlGuard
implements OgnlGuard {
    private static final Logger LOG = LogManager.getLogger(StrutsOgnlGuard.class);
    protected Set<String> excludedNodeTypes = Collections.emptySet();

    @Inject(value="struts.ognl.excludedNodeTypes", required=false)
    public void useExcludedNodeTypes(String excludedNodeTypes) {
        Set<String> incomingExcludedNodeTypes = TextParseUtil.commaDelimitedStringToSet(excludedNodeTypes);
        this.validateExcludedNodeTypes(incomingExcludedNodeTypes);
        HashSet<String> newExcludeNodeTypes = new HashSet<String>(this.excludedNodeTypes);
        newExcludeNodeTypes.addAll(incomingExcludedNodeTypes);
        this.excludedNodeTypes = Collections.unmodifiableSet(newExcludeNodeTypes);
    }

    protected void validateExcludedNodeTypes(Set<String> incomingExcludedNodeTypes) throws ConfigurationException {
        for (String excludedNodeType : incomingExcludedNodeTypes) {
            try {
                if (Node.class.isAssignableFrom(Class.forName(excludedNodeType))) continue;
                throw new ConfigurationException("Excluded node type [" + excludedNodeType + "] is not a subclass of " + Node.class.getName());
            }
            catch (ClassNotFoundException e) {
                throw new ConfigurationException("Excluded node type [" + excludedNodeType + "] does not exist or cannot be loaded");
            }
        }
    }

    @Override
    public boolean isRawExpressionBlocked(String expr) {
        return false;
    }

    @Override
    public boolean isParsedTreeBlocked(Object tree) {
        if (!(tree instanceof Node) || this.skipTreeCheck((Node)tree)) {
            return false;
        }
        return this.recurseNodes((Node)tree);
    }

    protected boolean skipTreeCheck(Node tree) {
        return this.excludedNodeTypes.isEmpty();
    }

    protected boolean recurseNodes(Node node) {
        if (this.checkNode(node)) {
            return true;
        }
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            if (!this.recurseNodes(node.jjtGetChild(i))) continue;
            return true;
        }
        return false;
    }

    protected boolean checkNode(Node node) {
        return this.containsExcludedNodeType(node);
    }

    protected boolean containsExcludedNodeType(Node node) {
        String nodeClassName = node.getClass().getName();
        if (this.excludedNodeTypes.contains(nodeClassName)) {
            LOG.warn("Expression contains blocked node type [{}]", (Object)nodeClassName);
            return true;
        }
        return false;
    }
}

