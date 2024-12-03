/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.velocity.context.ChainedInternalContextAdapter;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;

public class ProxyVMContext
extends ChainedInternalContextAdapter {
    Map vmproxyhash = new HashMap(8, 0.8f);
    Map localcontext = new HashMap(8, 0.8f);
    private boolean localContextScope;
    private RuntimeServices rsvc;

    public ProxyVMContext(InternalContextAdapter inner, RuntimeServices rsvc, boolean localContextScope) {
        super(inner);
        this.localContextScope = localContextScope;
        this.rsvc = rsvc;
    }

    public void addVMProxyArg(InternalContextAdapter context, String macroArgumentName, String literalMacroArgumentName, Node argumentValue) throws MethodInvocationException {
        if (this.isConstant(argumentValue)) {
            this.localcontext.put(macroArgumentName, argumentValue.value(context));
        } else {
            this.vmproxyhash.put(macroArgumentName, argumentValue);
            this.localcontext.put(literalMacroArgumentName, argumentValue);
        }
    }

    private boolean isConstant(Node node) {
        switch (node.getType()) {
            case 7: 
            case 12: 
            case 13: 
            case 14: 
            case 16: 
            case 19: {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object put(String key, Object value) {
        return this.put(key, value, this.localContextScope);
    }

    @Override
    public Object localPut(String key, Object value) {
        return this.put(key, value, true);
    }

    protected Object put(String key, Object value, boolean forceLocal) {
        Node astNode = (Node)this.vmproxyhash.get(key);
        if (astNode != null && astNode.getType() == 16) {
            ASTReference ref = (ASTReference)astNode;
            if (ref.jjtGetNumChildren() > 0) {
                ref.setValue(this.innerContext, value);
                return null;
            }
            return this.innerContext.put(ref.getRootString(), value);
        }
        Object old = this.localcontext.put(key, value);
        if (!forceLocal) {
            old = super.put(key, value);
        }
        return old;
    }

    @Override
    public Object get(String key) {
        Object o = this.localcontext.get(key);
        if (o != null) {
            return o;
        }
        Node astNode = (Node)this.vmproxyhash.get(key);
        if (astNode != null) {
            int type = astNode.getType();
            if (type == 16) {
                ASTReference ref = (ASTReference)astNode;
                if (ref.jjtGetNumChildren() > 0) {
                    return ref.execute(null, this.innerContext);
                }
                Object obj = this.innerContext.get(ref.getRootString());
                if (obj == null && ref.strictRef && !this.innerContext.containsKey(ref.getRootString())) {
                    throw new MethodInvocationException("Parameter '" + ref.getRootString() + "' not defined", null, key, ref.getTemplateName(), ref.getLine(), ref.getColumn());
                }
                return obj;
            }
            if (type == 19) {
                try {
                    StringWriter writer = new StringWriter();
                    astNode.render(this.innerContext, writer);
                    return writer.toString();
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    String msg = "ProxyVMContext.get() : error rendering reference";
                    this.rsvc.getLog().error(msg, e);
                    throw new VelocityException(msg, e);
                }
            }
            return astNode.value(this.innerContext);
        }
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.vmproxyhash.containsKey(key) || this.localcontext.containsKey(key) || super.containsKey(key);
    }

    @Override
    public Object[] getKeys() {
        if (this.localcontext.isEmpty()) {
            return this.vmproxyhash.keySet().toArray();
        }
        if (this.vmproxyhash.isEmpty()) {
            return this.localcontext.keySet().toArray();
        }
        HashSet keys = new HashSet(this.localcontext.keySet());
        keys.addAll(this.vmproxyhash.keySet());
        return keys.toArray();
    }

    @Override
    public Object remove(Object key) {
        Object loc = this.localcontext.remove(key);
        Object arg = this.vmproxyhash.remove(key);
        Object glo = null;
        if (!this.localContextScope) {
            glo = super.remove(key);
        }
        if (loc != null) {
            return loc;
        }
        return glo;
    }
}

