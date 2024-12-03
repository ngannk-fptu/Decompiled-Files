/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.ObjectFactory;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.IntType;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodType;
import org.apache.xalan.xsltc.compiler.util.MultiHashtable;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

class FunctionCall
extends Expression {
    private QName _fname;
    private final Vector _arguments;
    private static final Vector EMPTY_ARG_LIST = new Vector(0);
    protected static final String EXT_XSLTC = "http://xml.apache.org/xalan/xsltc";
    protected static final String JAVA_EXT_XSLTC = "http://xml.apache.org/xalan/xsltc/java";
    protected static final String EXT_XALAN = "http://xml.apache.org/xalan";
    protected static final String JAVA_EXT_XALAN = "http://xml.apache.org/xalan/java";
    protected static final String JAVA_EXT_XALAN_OLD = "http://xml.apache.org/xslt/java";
    protected static final String EXSLT_COMMON = "http://exslt.org/common";
    protected static final String EXSLT_MATH = "http://exslt.org/math";
    protected static final String EXSLT_SETS = "http://exslt.org/sets";
    protected static final String EXSLT_DATETIME = "http://exslt.org/dates-and-times";
    protected static final String EXSLT_STRINGS = "http://exslt.org/strings";
    protected static final int NAMESPACE_FORMAT_JAVA = 0;
    protected static final int NAMESPACE_FORMAT_CLASS = 1;
    protected static final int NAMESPACE_FORMAT_PACKAGE = 2;
    protected static final int NAMESPACE_FORMAT_CLASS_OR_PACKAGE = 3;
    private int _namespace_format = 0;
    Expression _thisArgument = null;
    private String _className;
    private Class _clazz;
    private Method _chosenMethod;
    private Constructor _chosenConstructor;
    private MethodType _chosenMethodType;
    private boolean unresolvedExternal;
    private boolean _isExtConstructor = false;
    private boolean _isStatic = false;
    private static final MultiHashtable _internal2Java = new MultiHashtable();
    private static final Hashtable _java2Internal = new Hashtable();
    private static final Hashtable _extensionNamespaceTable = new Hashtable();
    private static final Hashtable _extensionFunctionTable = new Hashtable();

    public FunctionCall(QName fname, Vector arguments) {
        this._fname = fname;
        this._arguments = arguments;
        this._type = null;
    }

    public FunctionCall(QName fname) {
        this(fname, EMPTY_ARG_LIST);
    }

    public String getName() {
        return this._fname.toString();
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        if (this._arguments != null) {
            int n = this._arguments.size();
            for (int i = 0; i < n; ++i) {
                Expression exp = (Expression)this._arguments.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }

    public String getClassNameFromUri(String uri) {
        String className = (String)_extensionNamespaceTable.get(uri);
        if (className != null) {
            return className;
        }
        if (uri.startsWith(JAVA_EXT_XSLTC)) {
            int length = JAVA_EXT_XSLTC.length() + 1;
            return uri.length() > length ? uri.substring(length) : "";
        }
        if (uri.startsWith(JAVA_EXT_XALAN)) {
            int length = JAVA_EXT_XALAN.length() + 1;
            return uri.length() > length ? uri.substring(length) : "";
        }
        if (uri.startsWith(JAVA_EXT_XALAN_OLD)) {
            int length = JAVA_EXT_XALAN_OLD.length() + 1;
            return uri.length() > length ? uri.substring(length) : "";
        }
        int index = uri.lastIndexOf(47);
        return index > 0 ? uri.substring(index + 1) : uri;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._type != null) {
            return this._type;
        }
        String namespace = this._fname.getNamespace();
        String local = this._fname.getLocalPart();
        if (this.isExtension()) {
            this._fname = new QName(null, null, local);
            return this.typeCheckStandard(stable);
        }
        if (this.isStandard()) {
            return this.typeCheckStandard(stable);
        }
        try {
            this._className = this.getClassNameFromUri(namespace);
            int pos = local.lastIndexOf(46);
            if (pos > 0) {
                this._isStatic = true;
                if (this._className != null && this._className.length() > 0) {
                    this._namespace_format = 2;
                    this._className = this._className + "." + local.substring(0, pos);
                } else {
                    this._namespace_format = 0;
                    this._className = local.substring(0, pos);
                }
                this._fname = new QName(namespace, null, local.substring(pos + 1));
            } else {
                String extFunction;
                if (this._className != null && this._className.length() > 0) {
                    try {
                        this._clazz = ObjectFactory.findProviderClass(this._className, ObjectFactory.findClassLoader(), true);
                        this._namespace_format = 1;
                    }
                    catch (ClassNotFoundException e) {
                        this._namespace_format = 2;
                    }
                } else {
                    this._namespace_format = 0;
                }
                if (local.indexOf(45) > 0) {
                    local = FunctionCall.replaceDash(local);
                }
                if ((extFunction = (String)_extensionFunctionTable.get(namespace + ":" + local)) != null) {
                    this._fname = new QName(null, null, extFunction);
                    return this.typeCheckStandard(stable);
                }
                this._fname = new QName(namespace, null, local);
            }
            return this.typeCheckExternal(stable);
        }
        catch (TypeCheckError e) {
            ErrorMsg errorMsg = e.getErrorMsg();
            if (errorMsg == null) {
                String name = this._fname.getLocalPart();
                errorMsg = new ErrorMsg("METHOD_NOT_FOUND_ERR", name);
            }
            this.getParser().reportError(3, errorMsg);
            this._type = Type.Void;
            return this._type;
        }
    }

    public Type typeCheckStandard(SymbolTable stable) throws TypeCheckError {
        this._fname.clearNamespace();
        int n = this._arguments.size();
        Vector argsType = this.typeCheckArgs(stable);
        MethodType args = new MethodType(Type.Void, argsType);
        MethodType ptype = this.lookupPrimop(stable, this._fname.getLocalPart(), args);
        if (ptype != null) {
            for (int i = 0; i < n; ++i) {
                Expression exp;
                Type argType = (Type)ptype.argsType().elementAt(i);
                if (argType.identicalTo((exp = (Expression)this._arguments.elementAt(i)).getType())) continue;
                try {
                    this._arguments.setElementAt(new CastExpr(exp, argType), i);
                    continue;
                }
                catch (TypeCheckError e) {
                    throw new TypeCheckError(this);
                }
            }
            this._chosenMethodType = ptype;
            this._type = ptype.resultType();
            return this._type;
        }
        throw new TypeCheckError(this);
    }

    public Type typeCheckConstructor(SymbolTable stable) throws TypeCheckError {
        Vector constructors = this.findConstructors();
        if (constructors == null) {
            throw new TypeCheckError("CONSTRUCTOR_NOT_FOUND", this._className);
        }
        int nConstructors = constructors.size();
        int nArgs = this._arguments.size();
        Vector argsType = this.typeCheckArgs(stable);
        int bestConstrDistance = Integer.MAX_VALUE;
        this._type = null;
        for (int i = 0; i < nConstructors; ++i) {
            int j;
            Constructor constructor = (Constructor)constructors.elementAt(i);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Class<?> extType = null;
            int currConstrDistance = 0;
            for (j = 0; j < nArgs; ++j) {
                extType = paramTypes[j];
                Type intType = (Type)argsType.elementAt(j);
                Object match = _internal2Java.maps(intType, extType);
                if (match != null) {
                    currConstrDistance += ((JavaType)match).distance;
                    continue;
                }
                if (intType instanceof ObjectType) {
                    ObjectType objectType = (ObjectType)intType;
                    if (objectType.getJavaClass() == extType) continue;
                    if (extType.isAssignableFrom(objectType.getJavaClass())) {
                        ++currConstrDistance;
                        continue;
                    }
                    currConstrDistance = Integer.MAX_VALUE;
                    break;
                }
                currConstrDistance = Integer.MAX_VALUE;
                break;
            }
            if (j != nArgs || currConstrDistance >= bestConstrDistance) continue;
            this._chosenConstructor = constructor;
            this._isExtConstructor = true;
            bestConstrDistance = currConstrDistance;
            this._type = this._clazz != null ? Type.newObjectType(this._clazz) : Type.newObjectType(this._className);
        }
        if (this._type != null) {
            return this._type;
        }
        throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", this.getMethodSignature(argsType));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Type typeCheckExternal(SymbolTable stable) throws TypeCheckError {
        Vector methods;
        int nArgs = this._arguments.size();
        String name = this._fname.getLocalPart();
        if (this._fname.getLocalPart().equals("new")) {
            return this.typeCheckConstructor(stable);
        }
        boolean hasThisArgument = false;
        if (nArgs == 0) {
            this._isStatic = true;
        }
        if (!this._isStatic) {
            if (this._namespace_format == 0 || this._namespace_format == 2) {
                hasThisArgument = true;
            }
            Expression firstArg = (Expression)this._arguments.elementAt(0);
            Type firstArgType = firstArg.typeCheck(stable);
            if (this._namespace_format == 1 && firstArgType instanceof ObjectType && this._clazz != null && this._clazz.isAssignableFrom(((ObjectType)firstArgType).getJavaClass())) {
                hasThisArgument = true;
            }
            if (hasThisArgument) {
                this._thisArgument = (Expression)this._arguments.elementAt(0);
                this._arguments.remove(0);
                --nArgs;
                if (!(firstArgType instanceof ObjectType)) throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", name);
                this._className = ((ObjectType)firstArgType).getJavaClassName();
            }
        } else if (this._className.length() == 0) {
            Parser parser = this.getParser();
            if (parser != null) {
                this.reportWarning(this, parser, "FUNCTION_RESOLVE_ERR", this._fname.toString());
            }
            this.unresolvedExternal = true;
            this._type = Type.Int;
            return this._type;
        }
        if ((methods = this.findMethods()) == null) {
            throw new TypeCheckError("METHOD_NOT_FOUND_ERR", this._className + "." + name);
        }
        Class<?> extType = null;
        int nMethods = methods.size();
        Vector argsType = this.typeCheckArgs(stable);
        int bestMethodDistance = Integer.MAX_VALUE;
        this._type = null;
        for (int i = 0; i < nMethods; ++i) {
            int j;
            Method method = (Method)methods.elementAt(i);
            Class<?>[] paramTypes = method.getParameterTypes();
            int currMethodDistance = 0;
            for (j = 0; j < nArgs; ++j) {
                extType = paramTypes[j];
                Type intType = (Type)argsType.elementAt(j);
                Object match = _internal2Java.maps(intType, extType);
                if (match != null) {
                    currMethodDistance += ((JavaType)match).distance;
                    continue;
                }
                if (intType instanceof ReferenceType) {
                    ++currMethodDistance;
                    continue;
                }
                if (intType instanceof ObjectType) {
                    ObjectType object = (ObjectType)intType;
                    if (extType.getName().equals(object.getJavaClassName())) {
                        currMethodDistance += 0;
                        continue;
                    }
                    if (extType.isAssignableFrom(object.getJavaClass())) {
                        ++currMethodDistance;
                        continue;
                    }
                    currMethodDistance = Integer.MAX_VALUE;
                    break;
                }
                currMethodDistance = Integer.MAX_VALUE;
                break;
            }
            if (j != nArgs) continue;
            extType = method.getReturnType();
            this._type = (Type)_java2Internal.get(extType);
            if (this._type == null) {
                this._type = Type.newObjectType(extType);
            }
            if (this._type == null || currMethodDistance >= bestMethodDistance) continue;
            this._chosenMethod = method;
            bestMethodDistance = currMethodDistance;
        }
        if (this._chosenMethod != null && this._thisArgument == null && !Modifier.isStatic(this._chosenMethod.getModifiers())) {
            throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", this.getMethodSignature(argsType));
        }
        if (this._type == null) throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", this.getMethodSignature(argsType));
        if (this._type != Type.NodeSet) return this._type;
        this.getXSLTC().setMultiDocument(true);
        return this._type;
    }

    public Vector typeCheckArgs(SymbolTable stable) throws TypeCheckError {
        Vector<Type> result = new Vector<Type>();
        Enumeration e = this._arguments.elements();
        while (e.hasMoreElements()) {
            Expression exp = (Expression)e.nextElement();
            result.addElement(exp.typeCheck(stable));
        }
        return result;
    }

    protected final Expression argument(int i) {
        return (Expression)this._arguments.elementAt(i);
    }

    protected final Expression argument() {
        return this.argument(0);
    }

    protected final int argumentCount() {
        return this._arguments.size();
    }

    protected final void setArgument(int i, Expression exp) {
        this._arguments.setElementAt(exp, i);
    }

    @Override
    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        Type type = Type.Boolean;
        if (this._chosenMethodType != null) {
            type = this._chosenMethodType.resultType();
        }
        InstructionList il = methodGen.getInstructionList();
        this.translate(classGen, methodGen);
        if (type instanceof BooleanType || type instanceof IntType) {
            this._falseList.add(il.append(new IFEQ(null)));
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        int n = this.argumentCount();
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        boolean isSecureProcessing = classGen.getParser().getXSLTC().isSecureProcessing();
        if (this.isStandard() || this.isExtension()) {
            for (int i = 0; i < n; ++i) {
                Expression exp = this.argument(i);
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
            }
            String name = this._fname.toString().replace('-', '_') + "F";
            String args = "";
            if (name.equals("sumF")) {
                args = "Lorg/apache/xalan/xsltc/DOM;";
                il.append(methodGen.loadDOM());
            } else if (name.equals("normalize_spaceF") && this._chosenMethodType.toSignature(args).equals("()Ljava/lang/String;")) {
                args = "ILorg/apache/xalan/xsltc/DOM;";
                il.append(methodGen.loadContextNode());
                il.append(methodGen.loadDOM());
            }
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", name, this._chosenMethodType.toSignature(args));
            il.append(new INVOKESTATIC(index));
        } else if (this.unresolvedExternal) {
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "unresolved_externalF", "(Ljava/lang/String;)V");
            il.append(new PUSH(cpg, this._fname.toString()));
            il.append(new INVOKESTATIC(index));
        } else if (this._isExtConstructor) {
            int i;
            if (isSecureProcessing) {
                this.translateUnallowedExtension(cpg, il);
            }
            String clazz = this._chosenConstructor.getDeclaringClass().getName();
            Class<?>[] paramTypes = this._chosenConstructor.getParameterTypes();
            LocalVariableGen[] paramTemp = new LocalVariableGen[n];
            for (i = 0; i < n; ++i) {
                Expression exp = this.argument(i);
                Type expType = exp.getType();
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
                expType.translateTo(classGen, methodGen, paramTypes[i]);
                paramTemp[i] = methodGen.addLocalVariable("function_call_tmp" + i, expType.toJCType(), null, null);
                paramTemp[i].setStart(il.append(expType.STORE(paramTemp[i].getIndex())));
            }
            il.append(new NEW(cpg.addClass(this._className)));
            il.append(InstructionConstants.DUP);
            for (i = 0; i < n; ++i) {
                Expression arg = this.argument(i);
                paramTemp[i].setEnd(il.append(arg.getType().LOAD(paramTemp[i].getIndex())));
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append('(');
            for (int i2 = 0; i2 < paramTypes.length; ++i2) {
                buffer.append(FunctionCall.getSignature(paramTypes[i2]));
            }
            buffer.append(')');
            buffer.append("V");
            int index = cpg.addMethodref(clazz, "<init>", buffer.toString());
            il.append(new INVOKESPECIAL(index));
            Type.Object.translateFrom(classGen, methodGen, this._chosenConstructor.getDeclaringClass());
        } else {
            if (isSecureProcessing) {
                this.translateUnallowedExtension(cpg, il);
            }
            String clazz = this._chosenMethod.getDeclaringClass().getName();
            Class<?>[] paramTypes = this._chosenMethod.getParameterTypes();
            if (this._thisArgument != null) {
                this._thisArgument.translate(classGen, methodGen);
            }
            for (int i = 0; i < n; ++i) {
                Expression exp = this.argument(i);
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
                exp.getType().translateTo(classGen, methodGen, paramTypes[i]);
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append('(');
            for (int i = 0; i < paramTypes.length; ++i) {
                buffer.append(FunctionCall.getSignature(paramTypes[i]));
            }
            buffer.append(')');
            buffer.append(FunctionCall.getSignature(this._chosenMethod.getReturnType()));
            if (this._thisArgument != null && this._clazz.isInterface()) {
                int index = cpg.addInterfaceMethodref(clazz, this._fname.getLocalPart(), buffer.toString());
                il.append(new INVOKEINTERFACE(index, n + 1));
            } else {
                int index = cpg.addMethodref(clazz, this._fname.getLocalPart(), buffer.toString());
                il.append(this._thisArgument != null ? new INVOKEVIRTUAL(index) : new INVOKESTATIC(index));
            }
            this._type.translateFrom(classGen, methodGen, this._chosenMethod.getReturnType());
        }
    }

    @Override
    public String toString() {
        return "funcall(" + this._fname + ", " + this._arguments + ')';
    }

    public boolean isStandard() {
        String namespace = this._fname.getNamespace();
        return namespace == null || namespace.equals("");
    }

    public boolean isExtension() {
        String namespace = this._fname.getNamespace();
        return namespace != null && namespace.equals(EXT_XSLTC);
    }

    private Vector findMethods() {
        Vector<Method> result = null;
        String namespace = this._fname.getNamespace();
        if (this._className != null && this._className.length() > 0) {
            int nArgs = this._arguments.size();
            try {
                if (this._clazz == null) {
                    this._clazz = ObjectFactory.findProviderClass(this._className, ObjectFactory.findClassLoader(), true);
                    if (this._clazz == null) {
                        ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
                        this.getParser().reportError(3, msg);
                    }
                }
                String methodName = this._fname.getLocalPart();
                Method[] methods = this._clazz.getMethods();
                for (int i = 0; i < methods.length; ++i) {
                    int mods = methods[i].getModifiers();
                    if (!Modifier.isPublic(mods) || !methods[i].getName().equals(methodName) || methods[i].getParameterTypes().length != nArgs) continue;
                    if (result == null) {
                        result = new Vector<Method>();
                    }
                    result.addElement(methods[i]);
                }
            }
            catch (ClassNotFoundException e) {
                ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
                this.getParser().reportError(3, msg);
            }
        }
        return result;
    }

    private Vector findConstructors() {
        Vector result = null;
        String namespace = this._fname.getNamespace();
        int nArgs = this._arguments.size();
        try {
            if (this._clazz == null) {
                this._clazz = ObjectFactory.findProviderClass(this._className, ObjectFactory.findClassLoader(), true);
                if (this._clazz == null) {
                    ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
                    this.getParser().reportError(3, msg);
                }
            }
            Constructor<?>[] constructors = this._clazz.getConstructors();
            for (int i = 0; i < constructors.length; ++i) {
                int mods = constructors[i].getModifiers();
                if (!Modifier.isPublic(mods) || constructors[i].getParameterTypes().length != nArgs) continue;
                if (result == null) {
                    result = new Vector();
                }
                result.addElement(constructors[i]);
            }
        }
        catch (ClassNotFoundException e) {
            ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
            this.getParser().reportError(3, msg);
        }
        return result;
    }

    static final String getSignature(Class clazz) {
        if (clazz.isArray()) {
            StringBuffer sb = new StringBuffer();
            Class<?> cl = clazz;
            while (cl.isArray()) {
                sb.append("[");
                cl = cl.getComponentType();
            }
            sb.append(FunctionCall.getSignature(cl));
            return sb.toString();
        }
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return "I";
            }
            if (clazz == Byte.TYPE) {
                return "B";
            }
            if (clazz == Long.TYPE) {
                return "J";
            }
            if (clazz == Float.TYPE) {
                return "F";
            }
            if (clazz == Double.TYPE) {
                return "D";
            }
            if (clazz == Short.TYPE) {
                return "S";
            }
            if (clazz == Character.TYPE) {
                return "C";
            }
            if (clazz == Boolean.TYPE) {
                return "Z";
            }
            if (clazz == Void.TYPE) {
                return "V";
            }
            String name = clazz.toString();
            ErrorMsg err = new ErrorMsg("UNKNOWN_SIG_TYPE_ERR", name);
            throw new Error(err.toString());
        }
        return "L" + clazz.getName().replace('.', '/') + ';';
    }

    static final String getSignature(Method meth) {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        Class<?>[] params = meth.getParameterTypes();
        for (int j = 0; j < params.length; ++j) {
            sb.append(FunctionCall.getSignature(params[j]));
        }
        return sb.append(')').append(FunctionCall.getSignature(meth.getReturnType())).toString();
    }

    static final String getSignature(Constructor cons) {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        Class<?>[] params = cons.getParameterTypes();
        for (int j = 0; j < params.length; ++j) {
            sb.append(FunctionCall.getSignature(params[j]));
        }
        return sb.append(")V").toString();
    }

    private String getMethodSignature(Vector argsType) {
        StringBuffer buf = new StringBuffer(this._className);
        buf.append('.').append(this._fname.getLocalPart()).append('(');
        int nArgs = argsType.size();
        for (int i = 0; i < nArgs; ++i) {
            Type intType = (Type)argsType.elementAt(i);
            buf.append(intType.toString());
            if (i >= nArgs - 1) continue;
            buf.append(", ");
        }
        buf.append(')');
        return buf.toString();
    }

    protected static String replaceDash(String name) {
        char dash = '-';
        StringBuffer buff = new StringBuffer("");
        for (int i = 0; i < name.length(); ++i) {
            if (i > 0 && name.charAt(i - 1) == dash) {
                buff.append(Character.toUpperCase(name.charAt(i)));
                continue;
            }
            if (name.charAt(i) == dash) continue;
            buff.append(name.charAt(i));
        }
        return buff.toString();
    }

    private void translateUnallowedExtension(ConstantPoolGen cpg, InstructionList il) {
        int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "unallowed_extension_functionF", "(Ljava/lang/String;)V");
        il.append(new PUSH(cpg, this._fname.toString()));
        il.append(new INVOKESTATIC(index));
    }

    static {
        try {
            Class<?> nodeClass = Class.forName("org.w3c.dom.Node");
            Class<?> nodeListClass = Class.forName("org.w3c.dom.NodeList");
            _internal2Java.put(Type.Boolean, new JavaType(Boolean.TYPE, 0));
            _internal2Java.put(Type.Boolean, new JavaType(Boolean.class, 1));
            _internal2Java.put(Type.Boolean, new JavaType(Object.class, 2));
            _internal2Java.put(Type.Real, new JavaType(Double.TYPE, 0));
            _internal2Java.put(Type.Real, new JavaType(Double.class, 1));
            _internal2Java.put(Type.Real, new JavaType(Float.TYPE, 2));
            _internal2Java.put(Type.Real, new JavaType(Long.TYPE, 3));
            _internal2Java.put(Type.Real, new JavaType(Integer.TYPE, 4));
            _internal2Java.put(Type.Real, new JavaType(Short.TYPE, 5));
            _internal2Java.put(Type.Real, new JavaType(Byte.TYPE, 6));
            _internal2Java.put(Type.Real, new JavaType(Character.TYPE, 7));
            _internal2Java.put(Type.Real, new JavaType(Object.class, 8));
            _internal2Java.put(Type.Int, new JavaType(Double.TYPE, 0));
            _internal2Java.put(Type.Int, new JavaType(Double.class, 1));
            _internal2Java.put(Type.Int, new JavaType(Float.TYPE, 2));
            _internal2Java.put(Type.Int, new JavaType(Long.TYPE, 3));
            _internal2Java.put(Type.Int, new JavaType(Integer.TYPE, 4));
            _internal2Java.put(Type.Int, new JavaType(Short.TYPE, 5));
            _internal2Java.put(Type.Int, new JavaType(Byte.TYPE, 6));
            _internal2Java.put(Type.Int, new JavaType(Character.TYPE, 7));
            _internal2Java.put(Type.Int, new JavaType(Object.class, 8));
            _internal2Java.put(Type.String, new JavaType(String.class, 0));
            _internal2Java.put(Type.String, new JavaType(Object.class, 1));
            _internal2Java.put(Type.NodeSet, new JavaType(nodeListClass, 0));
            _internal2Java.put(Type.NodeSet, new JavaType(nodeClass, 1));
            _internal2Java.put(Type.NodeSet, new JavaType(Object.class, 2));
            _internal2Java.put(Type.NodeSet, new JavaType(String.class, 3));
            _internal2Java.put(Type.Node, new JavaType(nodeListClass, 0));
            _internal2Java.put(Type.Node, new JavaType(nodeClass, 1));
            _internal2Java.put(Type.Node, new JavaType(Object.class, 2));
            _internal2Java.put(Type.Node, new JavaType(String.class, 3));
            _internal2Java.put(Type.ResultTree, new JavaType(nodeListClass, 0));
            _internal2Java.put(Type.ResultTree, new JavaType(nodeClass, 1));
            _internal2Java.put(Type.ResultTree, new JavaType(Object.class, 2));
            _internal2Java.put(Type.ResultTree, new JavaType(String.class, 3));
            _internal2Java.put(Type.Reference, new JavaType(Object.class, 0));
            _java2Internal.put(Boolean.TYPE, Type.Boolean);
            _java2Internal.put(Void.TYPE, Type.Void);
            _java2Internal.put(Character.TYPE, Type.Real);
            _java2Internal.put(Byte.TYPE, Type.Real);
            _java2Internal.put(Short.TYPE, Type.Real);
            _java2Internal.put(Integer.TYPE, Type.Real);
            _java2Internal.put(Long.TYPE, Type.Real);
            _java2Internal.put(Float.TYPE, Type.Real);
            _java2Internal.put(Double.TYPE, Type.Real);
            _java2Internal.put(String.class, Type.String);
            _java2Internal.put(Object.class, Type.Reference);
            _java2Internal.put(nodeListClass, Type.NodeSet);
            _java2Internal.put(nodeClass, Type.NodeSet);
            _extensionNamespaceTable.put(EXT_XALAN, "org.apache.xalan.lib.Extensions");
            _extensionNamespaceTable.put(EXSLT_COMMON, "org.apache.xalan.lib.ExsltCommon");
            _extensionNamespaceTable.put(EXSLT_MATH, "org.apache.xalan.lib.ExsltMath");
            _extensionNamespaceTable.put(EXSLT_SETS, "org.apache.xalan.lib.ExsltSets");
            _extensionNamespaceTable.put(EXSLT_DATETIME, "org.apache.xalan.lib.ExsltDatetime");
            _extensionNamespaceTable.put(EXSLT_STRINGS, "org.apache.xalan.lib.ExsltStrings");
            _extensionFunctionTable.put("http://exslt.org/common:nodeSet", "nodeset");
            _extensionFunctionTable.put("http://exslt.org/common:objectType", "objectType");
            _extensionFunctionTable.put("http://xml.apache.org/xalan:nodeset", "nodeset");
        }
        catch (ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    static class JavaType {
        public Class type;
        public int distance;

        public JavaType(Class type, int distance) {
            this.type = type;
            this.distance = distance;
        }

        public boolean equals(Object query) {
            return query.equals(this.type);
        }
    }
}

