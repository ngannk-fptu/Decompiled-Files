/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.ObjectFactory;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class FunctionAvailableCall
extends FunctionCall {
    private Expression _arg;
    private String _nameOfFunct = null;
    private String _namespaceOfFunct = null;
    private boolean _isFunctionAvailable = false;

    public FunctionAvailableCall(QName fname, Vector arguments) {
        super(fname, arguments);
        this._arg = (Expression)arguments.elementAt(0);
        this._type = null;
        if (this._arg instanceof LiteralExpr) {
            LiteralExpr arg = (LiteralExpr)this._arg;
            this._namespaceOfFunct = arg.getNamespace();
            this._nameOfFunct = arg.getValue();
            if (!this.isInternalNamespace()) {
                this._isFunctionAvailable = this.hasMethods();
            }
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._type != null) {
            return this._type;
        }
        if (this._arg instanceof LiteralExpr) {
            this._type = Type.Boolean;
            return this._type;
        }
        ErrorMsg err = new ErrorMsg("NEED_LITERAL_ERR", (Object)"function-available", this);
        throw new TypeCheckError(err);
    }

    @Override
    public Object evaluateAtCompileTime() {
        return this.getResult() ? Boolean.TRUE : Boolean.FALSE;
    }

    private boolean hasMethods() {
        String className = this.getClassNameFromUri(this._namespaceOfFunct);
        String methodName = null;
        int colonIndex = this._nameOfFunct.indexOf(":");
        if (colonIndex > 0) {
            String functionName = this._nameOfFunct.substring(colonIndex + 1);
            int lastDotIndex = functionName.lastIndexOf(46);
            if (lastDotIndex > 0) {
                methodName = functionName.substring(lastDotIndex + 1);
                className = className != null && className.length() != 0 ? className + "." + functionName.substring(0, lastDotIndex) : functionName.substring(0, lastDotIndex);
            } else {
                methodName = functionName;
            }
        } else {
            methodName = this._nameOfFunct;
        }
        if (className == null || methodName == null) {
            return false;
        }
        if (methodName.indexOf(45) > 0) {
            methodName = FunctionAvailableCall.replaceDash(methodName);
        }
        try {
            Class clazz = ObjectFactory.findProviderClass(className, ObjectFactory.findClassLoader(), true);
            if (clazz == null) {
                return false;
            }
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                int mods = methods[i].getModifiers();
                if (!Modifier.isPublic(mods) || !Modifier.isStatic(mods) || !methods[i].getName().equals(methodName)) continue;
                return true;
            }
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return false;
    }

    public boolean getResult() {
        if (this._nameOfFunct == null) {
            return false;
        }
        if (this.isInternalNamespace()) {
            Parser parser = this.getParser();
            this._isFunctionAvailable = parser.functionSupported(Util.getLocalName(this._nameOfFunct));
        }
        return this._isFunctionAvailable;
    }

    private boolean isInternalNamespace() {
        return this._namespaceOfFunct == null || this._namespaceOfFunct.equals("") || this._namespaceOfFunct.equals("http://xml.apache.org/xalan/xsltc");
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        methodGen.getInstructionList().append(new PUSH(cpg, this.getResult()));
    }
}

