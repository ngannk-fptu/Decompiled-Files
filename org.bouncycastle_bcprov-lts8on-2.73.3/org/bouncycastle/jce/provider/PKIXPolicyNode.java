/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXPolicyNode
implements PolicyNode {
    protected List children;
    protected int depth;
    protected Set expectedPolicies;
    protected PolicyNode parent;
    protected Set policyQualifiers;
    protected String validPolicy;
    protected boolean critical;

    public PKIXPolicyNode(List _children, int _depth, Set _expectedPolicies, PolicyNode _parent, Set _policyQualifiers, String _validPolicy, boolean _critical) {
        this.children = _children;
        this.depth = _depth;
        this.expectedPolicies = _expectedPolicies;
        this.parent = _parent;
        this.policyQualifiers = _policyQualifiers;
        this.validPolicy = _validPolicy;
        this.critical = _critical;
    }

    public void addChild(PKIXPolicyNode _child) {
        this.children.add(_child);
        _child.setParent(this);
    }

    public Iterator getChildren() {
        return this.children.iterator();
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    public Set getExpectedPolicies() {
        return this.expectedPolicies;
    }

    @Override
    public PolicyNode getParent() {
        return this.parent;
    }

    public Set getPolicyQualifiers() {
        return this.policyQualifiers;
    }

    @Override
    public String getValidPolicy() {
        return this.validPolicy;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    @Override
    public boolean isCritical() {
        return this.critical;
    }

    public void removeChild(PKIXPolicyNode _child) {
        this.children.remove(_child);
    }

    public void setCritical(boolean _critical) {
        this.critical = _critical;
    }

    public void setParent(PKIXPolicyNode _parent) {
        this.parent = _parent;
    }

    public String toString() {
        return this.toString("");
    }

    public String toString(String _indent) {
        StringBuffer _buf = new StringBuffer();
        _buf.append(_indent);
        _buf.append(this.validPolicy);
        _buf.append(" {\n");
        for (int i = 0; i < this.children.size(); ++i) {
            _buf.append(((PKIXPolicyNode)this.children.get(i)).toString(_indent + "    "));
        }
        _buf.append(_indent);
        _buf.append("}\n");
        return _buf.toString();
    }

    public Object clone() {
        return this.copy();
    }

    public PKIXPolicyNode copy() {
        HashSet<String> _expectedPolicies = new HashSet<String>();
        Iterator _iter = this.expectedPolicies.iterator();
        while (_iter.hasNext()) {
            _expectedPolicies.add(new String((String)_iter.next()));
        }
        HashSet<String> _policyQualifiers = new HashSet<String>();
        _iter = this.policyQualifiers.iterator();
        while (_iter.hasNext()) {
            _policyQualifiers.add(new String((String)_iter.next()));
        }
        PKIXPolicyNode _node = new PKIXPolicyNode(new ArrayList(), this.depth, _expectedPolicies, null, _policyQualifiers, new String(this.validPolicy), this.critical);
        _iter = this.children.iterator();
        while (_iter.hasNext()) {
            PKIXPolicyNode _child = ((PKIXPolicyNode)_iter.next()).copy();
            _child.setParent(_node);
            _node.addChild(_child);
        }
        return _node;
    }

    public void setExpectedPolicies(Set expectedPolicies) {
        this.expectedPolicies = expectedPolicies;
    }
}

