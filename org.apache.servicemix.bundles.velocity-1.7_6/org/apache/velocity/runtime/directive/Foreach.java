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
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.ForeachScope;
import org.apache.velocity.runtime.directive.StopCommand;
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
    private boolean warned = false;
    protected Info uberInfo;

    public String getName() {
        return "foreach";
    }

    public int getType() {
        return 1;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        SimpleNode sn;
        super.init(rs, context, node);
        this.counterName = this.rsvc.getString("directive.foreach.counter.name");
        this.hasNextName = this.rsvc.getString("directive.foreach.iterator.name");
        this.counterInitialValue = this.rsvc.getInt("directive.foreach.counter.initial.value");
        if (!this.warned && this.rsvc.getLog().isWarnEnabled()) {
            this.warned = true;
            if (!"velocityCount".equals(this.counterName)) {
                this.rsvc.getLog().warn("The directive.foreach.counter.name property has been deprecated. It will be removed (along with $velocityCount itself) in Velocity 2.0.  Instead, please use $foreach.count to access the loop counter.");
            }
            if (!"velocityHasNext".equals(this.hasNextName)) {
                this.rsvc.getLog().warn("The directive.foreach.iterator.name property has been deprecated. It will be removed (along with $velocityHasNext itself ) in Velocity 2.0.  Instead, please use $foreach.hasNext to access this value from now on.");
            }
            if (this.counterInitialValue != 1) {
                this.rsvc.getLog().warn("The directive.foreach.counter.initial.value property has been deprecated. It will be removed (along with $velocityCount itself) in Velocity 2.0.  Instead, please use $foreach.index to access the 0-based loop index and $foreach.count to access the 1-based loop counter.");
            }
        }
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

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, ResourceNotFoundException, ParseErrorException {
        Object listObject = node.jjtGetChild(2).value(context);
        if (listObject == null) {
            return false;
        }
        Iterator i = null;
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
        if (i == null) {
            if (this.skipInvalidIterator) {
                return false;
            }
            Node pnode = node.jjtGetChild(2);
            String msg = "#foreach parameter " + pnode.literal() + " at " + Log.formatFileString(pnode) + " is of type " + listObject.getClass().getName() + " and is either of wrong type or cannot be iterated.";
            this.rsvc.getLog().error(msg);
            throw new VelocityException(msg);
        }
        int counter = this.counterInitialValue;
        boolean maxNbrLoopsExceeded = false;
        Object o = context.get(this.elementKey);
        Object savedCounter = context.get(this.counterName);
        Object nextFlag = context.get(this.hasNextName);
        ForeachScope foreach = null;
        if (this.isScopeProvided()) {
            String name = this.getScopeName();
            foreach = new ForeachScope(this, context.get(name));
            context.put(name, foreach);
        }
        NullHolderContext nullHolderContext = null;
        while (!maxNbrLoopsExceeded && i.hasNext()) {
            this.put(context, this.counterName, new Integer(counter));
            Object value = i.next();
            this.put(context, this.hasNextName, i.hasNext());
            this.put(context, this.elementKey, value);
            if (this.isScopeProvided()) {
                ++foreach.index;
                foreach.hasNext = i.hasNext();
            }
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
            catch (StopCommand stop) {
                if (stop.isFor(this)) break;
                this.clean(context, o, savedCounter, nextFlag);
                throw stop;
            }
            maxNbrLoopsExceeded = ++counter - this.counterInitialValue >= this.maxNbrLoops;
        }
        this.clean(context, o, savedCounter, nextFlag);
        return true;
    }

    protected void clean(InternalContextAdapter context, Object o, Object savedCounter, Object nextFlag) {
        if (o != null) {
            context.put(this.elementKey, o);
        } else {
            context.remove(this.elementKey);
        }
        if (savedCounter != null) {
            context.put(this.counterName, savedCounter);
        } else {
            context.remove(this.counterName);
        }
        if (nextFlag != null) {
            context.put(this.hasNextName, nextFlag);
        } else {
            context.remove(this.hasNextName);
        }
        this.postRender(context);
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

        public Object get(String key) throws MethodInvocationException {
            return this.active && this.loopVariableKey.equals(key) ? null : super.get(key);
        }

        public Object put(String key, Object value) {
            if (this.loopVariableKey.equals(key) && value == null) {
                this.active = true;
            }
            return super.put(key, value);
        }

        public Object localPut(String key, Object value) {
            return this.put(key, value);
        }

        public Object remove(Object key) {
            if (this.loopVariableKey.equals(key)) {
                this.active = false;
            }
            return super.remove(key);
        }
    }
}

