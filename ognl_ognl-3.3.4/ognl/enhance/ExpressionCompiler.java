/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javassist.CannotCompileException
 *  javassist.ClassPath
 *  javassist.ClassPool
 *  javassist.CtClass
 *  javassist.CtField
 *  javassist.CtMethod
 *  javassist.CtNewConstructor
 *  javassist.CtNewMethod
 *  javassist.LoaderClassPath
 *  javassist.NotFoundException
 */
package ognl.enhance;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import ognl.ASTAnd;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.ASTList;
import ognl.ASTMethod;
import ognl.ASTOr;
import ognl.ASTProperty;
import ognl.ASTRootVarRef;
import ognl.ASTStaticField;
import ognl.ASTStaticMethod;
import ognl.ASTThisVarRef;
import ognl.ASTVarRef;
import ognl.ExpressionNode;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.enhance.ContextClassLoader;
import ognl.enhance.EnhancedClassLoader;
import ognl.enhance.ExpressionAccessor;
import ognl.enhance.LocalReference;
import ognl.enhance.LocalReferenceImpl;
import ognl.enhance.OgnlExpressionCompiler;
import ognl.enhance.OrderedReturn;
import ognl.enhance.UnsupportedCompilationException;

public class ExpressionCompiler
implements OgnlExpressionCompiler {
    public static final String PRE_CAST = "_preCast";
    protected Map _loaders = new HashMap();
    protected ClassPool _pool;
    protected int _classCounter = 0;

    public static void addCastString(OgnlContext context, String cast) {
        String value = (String)context.get(PRE_CAST);
        value = value != null ? cast + value : cast;
        context.put(PRE_CAST, value);
    }

    public static String getCastString(Class type) {
        if (type == null) {
            return null;
        }
        return type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();
    }

    public static String getRootExpression(Node expression, Object root, OgnlContext context) {
        String rootExpr = "";
        if (!ExpressionCompiler.shouldCast(expression)) {
            return rootExpr;
        }
        if (!((ASTList.class.isInstance(expression) || ASTVarRef.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || ASTStaticField.class.isInstance(expression) || ASTConst.class.isInstance(expression) || ExpressionNode.class.isInstance(expression) || ASTCtor.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || root == null) && (root == null || !ASTRootVarRef.class.isInstance(expression)))) {
            Class castClass = OgnlRuntime.getCompiler().getRootExpressionClass(expression, context);
            if (castClass.isArray() || ASTRootVarRef.class.isInstance(expression) || ASTThisVarRef.class.isInstance(expression)) {
                rootExpr = "((" + ExpressionCompiler.getCastString(castClass) + ")$2)";
                if (ASTProperty.class.isInstance(expression) && !((ASTProperty)expression).isIndexedAccess()) {
                    rootExpr = rootExpr + ".";
                }
            } else {
                rootExpr = ASTProperty.class.isInstance(expression) && ((ASTProperty)expression).isIndexedAccess() || ASTChain.class.isInstance(expression) ? "((" + ExpressionCompiler.getCastString(castClass) + ")$2)" : "((" + ExpressionCompiler.getCastString(castClass) + ")$2).";
            }
        }
        return rootExpr;
    }

    public static boolean shouldCast(Node expression) {
        Node child;
        if (ASTChain.class.isInstance(expression) && (ASTConst.class.isInstance(child = expression.jjtGetChild(0)) || ASTStaticMethod.class.isInstance(child) || ASTStaticField.class.isInstance(child) || ASTVarRef.class.isInstance(child) && !ASTRootVarRef.class.isInstance(child))) {
            return false;
        }
        return !ASTConst.class.isInstance(expression);
    }

    @Override
    public String castExpression(OgnlContext context, Node expression, String body) {
        if (context.getCurrentAccessor() == null || context.getPreviousType() == null || context.getCurrentAccessor().isAssignableFrom(context.getPreviousType()) || context.getCurrentType() != null && context.getCurrentObject() != null && context.getCurrentType().isAssignableFrom(context.getCurrentObject().getClass()) && context.getCurrentAccessor().isAssignableFrom(context.getPreviousType()) || body == null || body.trim().length() < 1 || context.getCurrentType() != null && context.getCurrentType().isArray() && (context.getPreviousType() == null || context.getPreviousType() != Object.class) || ASTOr.class.isInstance(expression) || ASTAnd.class.isInstance(expression) || ASTRootVarRef.class.isInstance(expression) || context.getCurrentAccessor() == Class.class || context.get(PRE_CAST) != null && ((String)context.get(PRE_CAST)).startsWith("new") || ASTStaticField.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || OrderedReturn.class.isInstance(expression) && ((OrderedReturn)((Object)expression)).getLastExpression() != null) {
            return body;
        }
        ExpressionCompiler.addCastString(context, "((" + ExpressionCompiler.getCastString(context.getCurrentAccessor()) + ")");
        return ")" + body;
    }

    @Override
    public String getClassName(Class clazz) {
        if (clazz.getName().equals("java.util.AbstractList$Itr")) {
            return Iterator.class.getName();
        }
        if (Modifier.isPublic(clazz.getModifiers()) && clazz.isInterface()) {
            return clazz.getName();
        }
        return this._getClassName(clazz, clazz.getInterfaces());
    }

    private String _getClassName(Class clazz, Class[] intf) {
        Class[] superclazzIntf;
        for (int i = 0; i < intf.length; ++i) {
            if (intf[i].getName().indexOf("util.List") > 0) {
                return intf[i].getName();
            }
            if (intf[i].getName().indexOf("Iterator") <= 0) continue;
            return intf[i].getName();
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != null && (superclazzIntf = superclazz.getInterfaces()).length > 0) {
            return this._getClassName(superclazz, superclazzIntf);
        }
        return clazz.getName();
    }

    @Override
    public Class getSuperOrInterfaceClass(Method m, Class clazz) {
        Class superClass;
        Class<?>[] intfs = clazz.getInterfaces();
        if (intfs != null && intfs.length > 0) {
            for (int i = 0; i < intfs.length; ++i) {
                Class intClass = this.getSuperOrInterfaceClass(m, intfs[i]);
                if (intClass != null) {
                    return intClass;
                }
                if (!Modifier.isPublic(intfs[i].getModifiers()) || !this.containsMethod(m, intfs[i])) continue;
                return intfs[i];
            }
        }
        if (clazz.getSuperclass() != null && (superClass = this.getSuperOrInterfaceClass(m, clazz.getSuperclass())) != null) {
            return superClass;
        }
        if (Modifier.isPublic(clazz.getModifiers()) && this.containsMethod(m, clazz)) {
            return clazz;
        }
        return null;
    }

    public boolean containsMethod(Method m, Class clazz) {
        Method[] methods = clazz.getMethods();
        if (methods == null) {
            return false;
        }
        for (int i = 0; i < methods.length; ++i) {
            Class<?>[] mexceptions;
            Class<?>[] exceptions;
            Class<?>[] mparms;
            Class<?>[] parms;
            if (!methods[i].getName().equals(m.getName()) || methods[i].getReturnType() != m.getReturnType() || (parms = m.getParameterTypes()) == null || (mparms = methods[i].getParameterTypes()) == null || mparms.length != parms.length) continue;
            boolean parmsMatch = true;
            for (int p = 0; p < parms.length; ++p) {
                if (parms[p] == mparms[p]) continue;
                parmsMatch = false;
                break;
            }
            if (!parmsMatch || (exceptions = m.getExceptionTypes()) == null || (mexceptions = methods[i].getExceptionTypes()) == null || mexceptions.length != exceptions.length) continue;
            boolean exceptionsMatch = true;
            for (int e = 0; e < exceptions.length; ++e) {
                if (exceptions[e] == mexceptions[e]) continue;
                exceptionsMatch = false;
                break;
            }
            if (!exceptionsMatch) continue;
            return true;
        }
        return false;
    }

    @Override
    public Class getInterfaceClass(Class clazz) {
        if (clazz.getName().equals("java.util.AbstractList$Itr")) {
            return Iterator.class;
        }
        if (Modifier.isPublic(clazz.getModifiers()) && clazz.isInterface() || clazz.isPrimitive()) {
            return clazz;
        }
        return this._getInterfaceClass(clazz, clazz.getInterfaces());
    }

    private Class _getInterfaceClass(Class clazz, Class[] intf) {
        Class[] superclazzIntf;
        for (int i = 0; i < intf.length; ++i) {
            if (List.class.isAssignableFrom(intf[i])) {
                return List.class;
            }
            if (Iterator.class.isAssignableFrom(intf[i])) {
                return Iterator.class;
            }
            if (Map.class.isAssignableFrom(intf[i])) {
                return Map.class;
            }
            if (Set.class.isAssignableFrom(intf[i])) {
                return Set.class;
            }
            if (!Collection.class.isAssignableFrom(intf[i])) continue;
            return Collection.class;
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != null && (superclazzIntf = superclazz.getInterfaces()).length > 0) {
            return this._getInterfaceClass(superclazz, superclazzIntf);
        }
        return clazz;
    }

    @Override
    public Class getRootExpressionClass(Node rootNode, OgnlContext context) {
        if (context.getRoot() == null) {
            return null;
        }
        Class ret = context.getRoot().getClass();
        if (context.getFirstAccessor() != null && context.getFirstAccessor().isInstance(context.getRoot())) {
            ret = context.getFirstAccessor();
        }
        return ret;
    }

    @Override
    public void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
        String setBody;
        String getBody;
        CtField nodeMember;
        CtClass newClass;
        ClassPool pool;
        block10: {
            CtMethod setExpression;
            CtClass nodeClass;
            CtMethod valueSetter;
            CtClass objClass;
            block9: {
                if (expression.getAccessor() != null) {
                    return;
                }
                EnhancedClassLoader loader = this.getClassLoader(context);
                pool = this.getClassPool(context, loader);
                newClass = pool.makeClass(expression.getClass().getName() + expression.hashCode() + this._classCounter++ + "Accessor");
                newClass.addInterface(this.getCtClass(ExpressionAccessor.class));
                CtClass ognlClass = this.getCtClass(OgnlContext.class);
                objClass = this.getCtClass(Object.class);
                CtMethod valueGetter = new CtMethod(objClass, "get", new CtClass[]{ognlClass, objClass}, newClass);
                valueSetter = new CtMethod(CtClass.voidType, "set", new CtClass[]{ognlClass, objClass, objClass}, newClass);
                nodeMember = null;
                nodeClass = this.getCtClass(Node.class);
                setExpression = null;
                try {
                    getBody = this.generateGetter(context, newClass, objClass, pool, valueGetter, expression, root);
                }
                catch (UnsupportedCompilationException uc) {
                    nodeMember = new CtField(nodeClass, "_node", newClass);
                    newClass.addField(nodeMember);
                    getBody = this.generateOgnlGetter(newClass, valueGetter, nodeMember);
                    if (setExpression != null) break block9;
                    setExpression = CtNewMethod.setter((String)"setExpression", (CtField)nodeMember);
                    newClass.addMethod(setExpression);
                }
            }
            try {
                setBody = this.generateSetter(context, newClass, objClass, pool, valueSetter, expression, root);
            }
            catch (UnsupportedCompilationException uc) {
                if (nodeMember == null) {
                    nodeMember = new CtField(nodeClass, "_node", newClass);
                    newClass.addField(nodeMember);
                }
                setBody = this.generateOgnlSetter(newClass, valueSetter, nodeMember);
                if (setExpression != null) break block10;
                setExpression = CtNewMethod.setter((String)"setExpression", (CtField)nodeMember);
                newClass.addMethod(setExpression);
            }
        }
        try {
            newClass.addConstructor(CtNewConstructor.defaultConstructor((CtClass)newClass));
            Class clazz = pool.toClass(newClass);
            newClass.detach();
            expression.setAccessor((ExpressionAccessor)clazz.newInstance());
            if (nodeMember != null) {
                expression.getAccessor().setExpression(expression);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("Error compiling expression on object " + root + " with expression node " + expression + " getter body: " + getBody + " setter body: " + setBody, t);
        }
    }

    protected String generateGetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool pool, CtMethod valueGetter, Node expression, Object root) throws Exception {
        String pre = "";
        String post = "";
        context.setRoot(root);
        context.remove(PRE_CAST);
        String getterCode = expression.toGetSourceString(context, root);
        if (getterCode == null || getterCode.trim().length() <= 0 && !ASTVarRef.class.isAssignableFrom(expression.getClass())) {
            getterCode = "null";
        }
        String castExpression = (String)context.get(PRE_CAST);
        if (context.getCurrentType() == null || context.getCurrentType().isPrimitive() || Character.class.isAssignableFrom(context.getCurrentType()) || Object.class == context.getCurrentType()) {
            pre = pre + " ($w) (";
            post = post + ")";
        }
        String rootExpr = !getterCode.equals("null") ? ExpressionCompiler.getRootExpression(expression, root, context) : "";
        String noRoot = (String)context.remove("_noRoot");
        if (noRoot != null) {
            rootExpr = "";
        }
        this.createLocalReferences(context, pool, newClass, objClass, valueGetter.getParameterTypes());
        String body = OrderedReturn.class.isInstance(expression) && ((OrderedReturn)((Object)expression)).getLastExpression() != null ? "{ " + (ASTMethod.class.isInstance(expression) || ASTChain.class.isInstance(expression) ? rootExpr : "") + (castExpression != null ? castExpression : "") + ((OrderedReturn)((Object)expression)).getCoreExpression() + " return " + pre + ((OrderedReturn)((Object)expression)).getLastExpression() + post + ";}" : "{  return " + pre + (castExpression != null ? castExpression : "") + rootExpr + getterCode + post + ";}";
        if (body.indexOf("..") >= 0) {
            body = body.replaceAll("\\.\\.", ".");
        }
        valueGetter.setBody(body);
        newClass.addMethod(valueGetter);
        return body;
    }

    @Override
    public String createLocalReference(OgnlContext context, String expression, Class type) {
        String referenceName = "ref" + context.incrementLocalReferenceCounter();
        context.addLocalReference(referenceName, new LocalReferenceImpl(referenceName, expression, type));
        String castString = "";
        if (!type.isPrimitive()) {
            castString = "(" + ExpressionCompiler.getCastString(type) + ") ";
        }
        return castString + referenceName + "($$)";
    }

    void createLocalReferences(OgnlContext context, ClassPool pool, CtClass clazz, CtClass objClass, CtClass[] params) throws CannotCompileException, NotFoundException {
        Map referenceMap = context.getLocalReferences();
        if (referenceMap == null || referenceMap.size() < 1) {
            return;
        }
        Iterator it = referenceMap.values().iterator();
        while (it.hasNext()) {
            LocalReference ref = (LocalReference)it.next();
            String widener = ref.getType().isPrimitive() ? " " : " ($w) ";
            String body = "{";
            body = body + " return  " + widener + ref.getExpression() + ";";
            if ((body = body + "}").indexOf("..") >= 0) {
                body = body.replaceAll("\\.\\.", ".");
            }
            CtMethod method = new CtMethod(pool.get(ExpressionCompiler.getCastString(ref.getType())), ref.getName(), params, clazz);
            method.setBody(body);
            clazz.addMethod(method);
            it.remove();
        }
    }

    protected String generateSetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool pool, CtMethod valueSetter, Node expression, Object root) throws Exception {
        if (ExpressionNode.class.isInstance(expression) || ASTConst.class.isInstance(expression)) {
            throw new UnsupportedCompilationException("Can't compile expression/constant setters.");
        }
        context.setRoot(root);
        context.remove(PRE_CAST);
        String setterCode = expression.toSetSourceString(context, root);
        String castExpression = (String)context.get(PRE_CAST);
        if (setterCode == null || setterCode.trim().length() < 1) {
            throw new UnsupportedCompilationException("Can't compile null setter body.");
        }
        if (root == null) {
            throw new UnsupportedCompilationException("Can't compile setters with a null root object.");
        }
        String pre = ExpressionCompiler.getRootExpression(expression, root, context);
        String noRoot = (String)context.remove("_noRoot");
        if (noRoot != null) {
            pre = "";
        }
        this.createLocalReferences(context, pool, newClass, objClass, valueSetter.getParameterTypes());
        String body = "{" + (castExpression != null ? castExpression : "") + pre + setterCode + ";}";
        if (body.indexOf("..") >= 0) {
            body = body.replaceAll("\\.\\.", ".");
        }
        valueSetter.setBody(body);
        newClass.addMethod(valueSetter);
        return body;
    }

    protected String generateOgnlGetter(CtClass clazz, CtMethod valueGetter, CtField node) throws Exception {
        String body = "return " + node.getName() + ".getValue($1, $2);";
        valueGetter.setBody(body);
        clazz.addMethod(valueGetter);
        return body;
    }

    protected String generateOgnlSetter(CtClass clazz, CtMethod valueSetter, CtField node) throws Exception {
        String body = node.getName() + ".setValue($1, $2, $3);";
        valueSetter.setBody(body);
        clazz.addMethod(valueSetter);
        return body;
    }

    protected EnhancedClassLoader getClassLoader(OgnlContext context) {
        EnhancedClassLoader ret = (EnhancedClassLoader)this._loaders.get(context.getClassResolver());
        if (ret != null) {
            return ret;
        }
        ContextClassLoader classLoader = new ContextClassLoader(OgnlContext.class.getClassLoader(), context);
        ret = new EnhancedClassLoader(classLoader);
        this._loaders.put(context.getClassResolver(), ret);
        return ret;
    }

    protected CtClass getCtClass(Class searchClass) throws NotFoundException {
        return this._pool.get(searchClass.getName());
    }

    protected ClassPool getClassPool(OgnlContext context, EnhancedClassLoader loader) {
        if (this._pool != null) {
            return this._pool;
        }
        this._pool = ClassPool.getDefault();
        this._pool.insertClassPath((ClassPath)new LoaderClassPath(loader.getParent()));
        return this._pool;
    }
}

