/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import org.apache.velocity.context.ChainedInternalContextAdapter;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Break;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;

public class Foreach
extends Directive {
    private String counterName;
    private String hasNextName;
    private int counterInitialValue;
    private int maxNbrLoops;
    private boolean skipInvalidIterator;
    private String elementKey;
    protected Info uberInfo;

    @Override
    public String getName() {
        return "foreach";
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        SimpleNode sn;
        super.init(rs, context, node);
        this.counterName = this.rsvc.getString("directive.foreach.counter.name");
        this.hasNextName = this.rsvc.getString("directive.foreach.iterator.name");
        this.counterInitialValue = this.rsvc.getInt("directive.foreach.counter.initial.value");
        this.maxNbrLoops = this.rsvc.getInt("directive.foreach.maxloops", Integer.MAX_VALUE);
        if (this.maxNbrLoops < 1) {
            this.maxNbrLoops = Integer.MAX_VALUE;
        }
        this.skipInvalidIterator = this.rsvc.getBoolean("directive.foreach.skip.invalid", true);
        if (this.rsvc.getBoolean("runtime.references.strict", false)) {
            this.skipInvalidIterator = this.rsvc.getBoolean("directive.foreach.skip.invalid", false);
        }
        this.elementKey = (sn = (SimpleNode)node.jjtGetChild(0)) instanceof ASTReference ? ((ASTReference)sn).getRootString() : sn.getFirstToken().image.substring(1);
        this.uberInfo = new Info(this.getTemplateName(), this.getLine(), this.getColumn());
    }

    protected void put(InternalContextAdapter context, String key, Object value) {
        context.put(key, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, ResourceNotFoundException, ParseErrorException {
        Object listObject = node.jjtGetChild(2).value(context);
        if (listObject == null) {
            return false;
        }
        Iterator i = this.getIterator(listObject);
        if (i == null) {
            if (this.skipInvalidIterator) {
                return false;
            }
            Node pnode = node.jjtGetChild(2);
            String msg = "#foreach parameter " + pnode.literal() + " at " + Log.formatFileString(pnode) + " is of type " + listObject.getClass().getName() + " and is either of wrong type or cannot be iterated.";
            this.rsvc.getLog().error(msg);
            throw new VelocityException(msg);
        }
        Object savedElementKey = context.get(this.elementKey);
        Object savedCounter = context.get(this.counterName);
        Object nextFlag = context.get(this.hasNextName);
        try {
            this.performIteration(i, node, context, writer);
        }
        finally {
            if (savedCounter != null) {
                context.put(this.counterName, savedCounter);
            } else {
                context.remove(this.counterName);
            }
            if (savedElementKey != null) {
                context.put(this.elementKey, savedElementKey);
            } else {
                context.remove(this.elementKey);
            }
            if (nextFlag != null) {
                context.put(this.hasNextName, nextFlag);
            } else {
                context.remove(this.hasNextName);
            }
        }
        return true;
    }

    private void performIteration(Iterator i, Node node, InternalContextAdapter context, Writer writer) throws IOException {
        NullHolderContext nullHolderContext = null;
        int counter = this.counterInitialValue;
        while (i.hasNext()) {
            this.put(context, this.counterName, counter);
            Object value = i.next();
            this.put(context, this.hasNextName, i.hasNext());
            this.put(context, this.elementKey, value);
            try {
                if (value == null) {
                    if (nullHolderContext == null) {
                        nullHolderContext = new NullHolderContext(this.elementKey, context);
                    }
                    node.jjtGetChild(3).render(nullHolderContext, writer);
                } else {
                    node.jjtGetChild(3).render(context, writer);
                }
            }
            catch (Break.BreakException ex) {
                break;
            }
            if (++counter - this.counterInitialValue < this.maxNbrLoops) continue;
            break;
        }
    }

    private Iterator getIterator(Object listObject) {
        Iterator i;
        try {
            i = this.rsvc.getUberspect().getIterator(listObject, this.uberInfo);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception ee) {
            String msg = "Error getting iterator for #foreach at " + this.uberInfo;
            this.rsvc.getLog().error(msg, ee);
            throw new VelocityException(msg, ee);
        }
        return i;
    }

    protected static class NullHolderContext
    extends ChainedInternalContextAdapter {
        private String loopVariableKey = "";
        private boolean active = true;

        private NullHolderContext(String key, InternalContextAdapter context) {
            super(context);
            if (key != null) {
                this.loopVariableKey = key;
            }
        }

        @Override
        public Object get(String key) throws MethodInvocationException {
            return this.active && this.loopVariableKey.equals(key) ? null : super.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            if (this.loopVariableKey.equals(key) && value == null) {
                this.active = true;
            }
            return super.put(key, value);
        }

        @Override
        public Object localPut(String key, Object value) {
            return this.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            if (this.loopVariableKey.equals(key)) {
                this.active = false;
            }
            return super.remove(key);
        }
    }
}

