/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.directive.Block;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIndex;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertySet;

public class ASTReference
extends SimpleNode {
    private static final int NORMAL_REFERENCE = 1;
    private static final int FORMAL_REFERENCE = 2;
    private static final int QUIET_REFERENCE = 3;
    private static final int RUNT = 4;
    private int referenceType;
    private String nullString;
    private String rootString;
    private boolean escaped = false;
    private boolean computableReference = true;
    private boolean logOnNull = true;
    private String escPrefix = "";
    private String morePrefix = "";
    private String identifier = "";
    private String literal = null;
    public boolean strictRef = false;
    private ASTIndex astIndex = null;
    public boolean strictEscape = false;
    public boolean toStringNullCheck = true;
    private int numChildren = 0;
    protected Info uberInfo;

    public ASTReference(int id) {
        super(id);
    }

    public ASTReference(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.strictEscape = this.rsvc.getBoolean("runtime.references.strict.escape", false);
        this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
        this.toStringNullCheck = this.rsvc.getBoolean("directive.if.tostring.nullcheck", true);
        this.rootString = this.getRoot().intern();
        this.numChildren = this.jjtGetNumChildren();
        this.literal = this.literal();
        if (this.numChildren > 0) {
            Node lastNode = this.jjtGetChild(this.numChildren - 1);
            if (lastNode instanceof ASTIndex) {
                this.astIndex = (ASTIndex)lastNode;
            } else {
                this.identifier = lastNode.getFirstToken().image;
            }
        }
        this.uberInfo = new Info(this.getTemplateName(), this.getLine(), this.getColumn());
        this.logOnNull = this.rsvc.getBoolean("runtime.log.invalid.references", true);
        if (this.strictRef && this.numChildren == 0) {
            this.logOnNull = false;
            Node node = this.jjtGetParent();
            if (node instanceof ASTNotNode || node instanceof ASTExpression || node instanceof ASTOrNode || node instanceof ASTAndNode) {
                while (node != null) {
                    if (node instanceof ASTIfStatement) {
                        this.strictRef = false;
                        break;
                    }
                    node = node.jjtGetParent();
                }
            }
        }
        return data;
    }

    public String getRootString() {
        return this.rootString;
    }

    public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException {
        if (this.referenceType == 4) {
            return null;
        }
        Object result = this.getVariableValue(context, this.rootString);
        if (result == null && !this.strictRef) {
            return EventHandlerUtil.invalidGetMethod(this.rsvc, context, this.getDollarBang() + this.rootString, null, null, this.uberInfo);
        }
        try {
            Object previousResult = result;
            int failedChild = -1;
            for (int i = 0; i < this.numChildren; ++i) {
                if (this.strictRef && result == null) {
                    String name = this.jjtGetChild((int)i).getFirstToken().image;
                    throw new VelocityException("Attempted to access '" + name + "' on a null value at " + Log.formatFileString(this.uberInfo.getTemplateName(), this.jjtGetChild(i).getLine(), this.jjtGetChild(i).getColumn()));
                }
                previousResult = result;
                result = this.jjtGetChild(i).execute(result, context);
                if (result != null || this.strictRef) continue;
                failedChild = i;
                break;
            }
            if (result == null) {
                if (failedChild == -1) {
                    result = EventHandlerUtil.invalidGetMethod(this.rsvc, context, this.getDollarBang() + this.rootString, previousResult, null, this.uberInfo);
                } else {
                    StringBuffer name = new StringBuffer(this.getDollarBang()).append(this.rootString);
                    for (int i = 0; i <= failedChild; ++i) {
                        Node node = this.jjtGetChild(i);
                        if (node instanceof ASTMethod) {
                            name.append(".").append(((ASTMethod)node).getMethodName()).append("()");
                            continue;
                        }
                        name.append(".").append(node.getFirstToken().image);
                    }
                    if (this.jjtGetChild(failedChild) instanceof ASTMethod) {
                        String methodName = ((ASTMethod)this.jjtGetChild(failedChild)).getMethodName();
                        result = EventHandlerUtil.invalidMethod(this.rsvc, context, name.toString(), previousResult, methodName, this.uberInfo);
                    } else {
                        String property = this.jjtGetChild((int)failedChild).getFirstToken().image;
                        result = EventHandlerUtil.invalidGetMethod(this.rsvc, context, name.toString(), previousResult, property, this.uberInfo);
                    }
                }
            }
            return result;
        }
        catch (MethodInvocationException mie) {
            mie.setReferenceName(this.rootString);
            throw mie;
        }
    }

    public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException {
        if (this.referenceType == 4) {
            writer.write(this.rootString);
            return true;
        }
        Object value = null;
        value = this.escaped && this.strictEscape ? Boolean.TRUE : this.execute(null, context);
        String localNullString = null;
        if (this.escaped) {
            localNullString = this.getNullString(context);
            if (value == null) {
                writer.write(this.escPrefix);
                writer.write("\\");
                writer.write(localNullString);
            } else {
                writer.write(this.escPrefix);
                writer.write(localNullString);
            }
            return true;
        }
        value = EventHandlerUtil.referenceInsert(this.rsvc, context, this.literal, value);
        String toString = null;
        if (value != null) {
            if (value instanceof Renderable) {
                Renderable renderable = (Renderable)value;
                try {
                    if (renderable.render(context, writer)) {
                        return true;
                    }
                }
                catch (RuntimeException e) {
                    this.log.error("Exception rendering " + (renderable instanceof Block.Reference ? "block " : "Renderable ") + this.rootString + " at " + Log.formatFileString(this));
                    throw e;
                }
            }
            toString = value.toString();
        }
        if (value == null || toString == null) {
            if (this.strictRef) {
                if (this.referenceType != 3) {
                    this.log.error("Prepend the reference with '$!' e.g., $!" + this.literal().substring(1) + " if you want Velocity to ignore the reference when it evaluates to null");
                    if (value == null) {
                        throw new VelocityException("Reference " + this.literal() + " evaluated to null when attempting to render at " + Log.formatFileString(this));
                    }
                    throw new VelocityException("Reference " + this.literal() + " evaluated to object " + value.getClass().getName() + " whose toString() method returned null at " + Log.formatFileString(this));
                }
                return true;
            }
            localNullString = this.getNullString(context);
            if (!this.strictEscape) {
                writer.write(this.escPrefix);
            }
            writer.write(this.escPrefix);
            writer.write(this.morePrefix);
            writer.write(localNullString);
            if (this.logOnNull && this.referenceType != 3 && this.log.isDebugEnabled()) {
                this.log.debug("Null reference [template '" + this.getTemplateName() + "', line " + this.getLine() + ", column " + this.getColumn() + "] : " + this.literal() + " cannot be resolved.");
            }
            return true;
        }
        writer.write(this.escPrefix);
        writer.write(this.morePrefix);
        writer.write(toString);
        return true;
    }

    private String getNullString(InternalContextAdapter context) {
        Object callingArgument = context.get(".literal." + this.nullString);
        if (callingArgument != null) {
            return ((Node)callingArgument).literal();
        }
        return this.nullString;
    }

    public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException {
        Object value = this.execute(null, context);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean)value != false;
        }
        if (this.toStringNullCheck) {
            try {
                return value.toString() != null;
            }
            catch (Exception e) {
                throw new VelocityException("Reference evaluation threw an exception at " + Log.formatFileString(this), e);
            }
        }
        return true;
    }

    public Object value(InternalContextAdapter context) throws MethodInvocationException {
        return this.computableReference ? this.execute(null, context) : null;
    }

    public static String printClass(Class clazz) {
        return clazz == null ? "null" : clazz.getName();
    }

    public boolean setValue(InternalContextAdapter context, Object value) throws MethodInvocationException {
        if (this.jjtGetNumChildren() == 0) {
            context.put(this.rootString, value);
            return true;
        }
        Object result = this.getVariableValue(context, this.rootString);
        if (result == null) {
            String msg = "reference set is not a valid reference at " + Log.formatFileString(this.uberInfo);
            this.log.error(msg);
            return false;
        }
        for (int i = 0; i < this.numChildren - 1; ++i) {
            result = this.jjtGetChild(i).execute(result, context);
            if (result != null) continue;
            if (this.strictRef) {
                String name = this.jjtGetChild((int)(i + 1)).getFirstToken().image;
                throw new MethodInvocationException("Attempted to access '" + name + "' on a null value", null, name, this.uberInfo.getTemplateName(), this.jjtGetChild(i + 1).getLine(), this.jjtGetChild(i + 1).getColumn());
            }
            String msg = "reference set is not a valid reference at " + Log.formatFileString(this.uberInfo);
            this.log.error(msg);
            return false;
        }
        if (this.astIndex != null) {
            Object argument = this.astIndex.jjtGetChild(0).value(context);
            argument = ASTIndex.adjMinusIndexArg(argument, result, context, this.astIndex);
            Object[] params = new Object[]{argument, value};
            String methodName = "set";
            Class[] paramClasses = new Class[]{params[0] == null ? null : params[0].getClass(), params[1] == null ? null : params[1].getClass()};
            VelMethod method = ClassUtils.getMethod(methodName, params, paramClasses, result, context, this.astIndex, false);
            if (method == null) {
                methodName = "put";
                method = ClassUtils.getMethod(methodName, params, paramClasses, result, context, this.astIndex, false);
            }
            if (method == null) {
                if (this.strictRef) {
                    throw new VelocityException("Found neither a 'set' or 'put' method with param types '(" + ASTReference.printClass(paramClasses[0]) + "," + ASTReference.printClass(paramClasses[1]) + ")' on class '" + result.getClass().getName() + "' at " + Log.formatFileString(this.astIndex));
                }
                return false;
            }
            try {
                method.invoke(result, params);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new MethodInvocationException("Exception calling method '" + methodName + "(" + ASTReference.printClass(paramClasses[0]) + "," + ASTReference.printClass(paramClasses[1]) + ")' in  " + result.getClass(), e.getCause(), this.identifier, this.astIndex.getTemplateName(), this.astIndex.getLine(), this.astIndex.getColumn());
            }
            return true;
        }
        try {
            VelPropertySet vs = this.rsvc.getUberspect().getPropertySet(result, this.identifier, value, this.uberInfo);
            if (vs == null) {
                if (this.strictRef) {
                    throw new MethodInvocationException("Object '" + result.getClass().getName() + "' does not contain property '" + this.identifier + "'", null, this.identifier, this.uberInfo.getTemplateName(), this.uberInfo.getLine(), this.uberInfo.getColumn());
                }
                return false;
            }
            vs.invoke(result, value);
        }
        catch (InvocationTargetException ite) {
            throw new MethodInvocationException("ASTReference : Invocation of method '" + this.identifier + "' in  " + result.getClass() + " threw exception " + ite.getTargetException().toString(), ite.getTargetException(), this.identifier, this.getTemplateName(), this.getLine(), this.getColumn());
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTReference setValue() : exception : " + e + " template at " + Log.formatFileString(this.uberInfo);
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        return true;
    }

    private String getRoot() {
        int loc1;
        Token t = this.getFirstToken();
        int slashbang = t.image.indexOf("\\!");
        if (slashbang != -1) {
            if (this.strictEscape) {
                this.nullString = this.literal();
                this.escaped = true;
                return this.literal();
            }
            int i = 0;
            int len = t.image.length();
            i = t.image.indexOf(36);
            if (i == -1) {
                this.log.error("ASTReference.getRoot() : internal error : no $ found for slashbang.");
                this.computableReference = false;
                this.nullString = t.image;
                return this.nullString;
            }
            while (i < len && t.image.charAt(i) != '\\') {
                ++i;
            }
            int start = i;
            int count = 0;
            while (i < len && t.image.charAt(i++) == '\\') {
                ++count;
            }
            this.nullString = t.image.substring(0, start);
            this.nullString = this.nullString + t.image.substring(start, start + count - 1);
            this.nullString = this.nullString + t.image.substring(start + count);
            this.computableReference = false;
            return this.nullString;
        }
        this.escaped = false;
        if (t.image.startsWith("\\")) {
            int i;
            int len = t.image.length();
            for (i = 0; i < len && t.image.charAt(i) == '\\'; ++i) {
            }
            if (i % 2 != 0) {
                this.escaped = true;
            }
            if (i > 0) {
                this.escPrefix = t.image.substring(0, i / 2);
            }
            t.image = t.image.substring(i);
        }
        if ((loc1 = t.image.lastIndexOf(36)) > 0) {
            this.morePrefix = this.morePrefix + t.image.substring(0, loc1);
            t.image = t.image.substring(loc1);
        }
        this.nullString = this.literal();
        if (t.image.startsWith("$!")) {
            this.referenceType = 3;
            if (!this.escaped) {
                this.nullString = "";
            }
            if (t.image.startsWith("$!{")) {
                return t.next.image;
            }
            return t.image.substring(2);
        }
        if (t.image.equals("${")) {
            this.referenceType = 2;
            return t.next.image;
        }
        if (t.image.startsWith("$")) {
            this.referenceType = 1;
            return t.image.substring(1);
        }
        this.referenceType = 4;
        return t.image;
    }

    public Object getVariableValue(Context context, String variable) throws MethodInvocationException {
        Object obj = null;
        try {
            obj = context.get(variable);
        }
        catch (RuntimeException e) {
            this.log.error("Exception calling reference $" + variable + " at " + Log.formatFileString(this.uberInfo));
            throw e;
        }
        if (this.strictRef && obj == null && !context.containsKey(variable)) {
            this.log.error("Variable $" + variable + " has not been set at " + Log.formatFileString(this.uberInfo));
            throw new MethodInvocationException("Variable $" + variable + " has not been set", null, this.identifier, this.uberInfo.getTemplateName(), this.uberInfo.getLine(), this.uberInfo.getColumn());
        }
        return obj;
    }

    public String getDollarBang() {
        return this.referenceType == 3 ? "$!" : "$";
    }
}

