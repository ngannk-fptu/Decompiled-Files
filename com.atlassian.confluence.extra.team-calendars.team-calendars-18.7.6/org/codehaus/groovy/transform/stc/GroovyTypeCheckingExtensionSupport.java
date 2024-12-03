/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.transform.stc.AbstractTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.TypeCheckingExtension;

public class GroovyTypeCheckingExtensionSupport
extends AbstractTypeCheckingExtension {
    private static final Map<String, String> METHOD_ALIASES = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put("onMethodSelection", "onMethodSelection");
            this.put("afterMethodCall", "afterMethodCall");
            this.put("beforeMethodCall", "beforeMethodCall");
            this.put("unresolvedVariable", "handleUnresolvedVariableExpression");
            this.put("unresolvedProperty", "handleUnresolvedProperty");
            this.put("unresolvedAttribute", "handleUnresolvedAttribute");
            this.put("ambiguousMethods", "handleAmbiguousMethods");
            this.put("methodNotFound", "handleMissingMethod");
            this.put("afterVisitMethod", "afterVisitMethod");
            this.put("beforeVisitMethod", "beforeVisitMethod");
            this.put("afterVisitClass", "afterVisitClass");
            this.put("beforeVisitClass", "beforeVisitClass");
            this.put("incompatibleAssignment", "handleIncompatibleAssignment");
            this.put("incompatibleReturnType", "handleIncompatibleReturnType");
            this.put("setup", "setup");
            this.put("finish", "finish");
        }
    });
    private final Map<String, List<Closure>> eventHandlers = new HashMap<String, List<Closure>>();
    private final String scriptPath;
    private final CompilationUnit compilationUnit;

    public GroovyTypeCheckingExtensionSupport(StaticTypeCheckingVisitor typeCheckingVisitor, String scriptPath, CompilationUnit compilationUnit) {
        super(typeCheckingVisitor);
        this.scriptPath = scriptPath;
        this.compilationUnit = compilationUnit;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void setup() {
        Script script;
        GroovyClassLoader transformLoader;
        CompilerConfiguration config;
        block20: {
            config = new CompilerConfiguration();
            config.setScriptBaseClass("org.codehaus.groovy.transform.stc.GroovyTypeCheckingExtensionSupport.TypeCheckingDSL");
            ImportCustomizer ic = new ImportCustomizer();
            ic.addStarImports("org.codehaus.groovy.ast.expr");
            ic.addStaticStars("org.codehaus.groovy.ast.ClassHelper");
            ic.addStaticStars("org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport");
            config.addCompilationCustomizers(ic);
            transformLoader = this.compilationUnit != null ? this.compilationUnit.getTransformLoader() : this.typeCheckingVisitor.getSourceUnit().getClassLoader();
            script = null;
            try {
                Class clazz = transformLoader.loadClass(this.scriptPath, false, true);
                if (TypeCheckingDSL.class.isAssignableFrom(clazz)) {
                    script = (TypeCheckingDSL)clazz.newInstance();
                    break block20;
                }
                if (!TypeCheckingExtension.class.isAssignableFrom(clazz)) break block20;
                try {
                    Constructor declaredConstructor = clazz.getDeclaredConstructor(StaticTypeCheckingVisitor.class);
                    TypeCheckingExtension extension = (TypeCheckingExtension)declaredConstructor.newInstance(this.typeCheckingVisitor);
                    this.typeCheckingVisitor.addTypeCheckingExtension(extension);
                    extension.setup();
                    return;
                }
                catch (InstantiationException e) {
                    this.addLoadingError(config);
                }
                catch (IllegalAccessException e) {
                    this.addLoadingError(config);
                }
                catch (NoSuchMethodException e) {
                    this.context.getErrorCollector().addFatalError(new SimpleMessage("Static type checking extension '" + this.scriptPath + "' could not be loaded because it doesn't have a constructor accepting StaticTypeCheckingVisitor.", config.getDebug(), this.typeCheckingVisitor.getSourceUnit()));
                }
                catch (InvocationTargetException e) {
                    this.addLoadingError(config);
                }
            }
            catch (ClassNotFoundException clazz) {
            }
            catch (InstantiationException e) {
                this.addLoadingError(config);
            }
            catch (IllegalAccessException e) {
                this.addLoadingError(config);
            }
        }
        if (script == null) {
            ClassLoader cl = this.typeCheckingVisitor.getSourceUnit().getClassLoader();
            InputStream is = ((ClassLoader)transformLoader).getResourceAsStream(this.scriptPath);
            if (is == null) {
                is = cl.getResourceAsStream(this.scriptPath);
            }
            if (is == null) {
                cl = GroovyTypeCheckingExtensionSupport.class.getClassLoader();
                is = cl.getResourceAsStream(this.scriptPath);
            }
            if (is == null) {
                this.context.getErrorCollector().addFatalError(new SimpleMessage("Static type checking extension '" + this.scriptPath + "' was not found on the classpath.", config.getDebug(), this.typeCheckingVisitor.getSourceUnit()));
            }
            try {
                GroovyShell shell = new GroovyShell(transformLoader, new Binding(), config);
                script = (TypeCheckingDSL)shell.parse(new InputStreamReader(is, this.typeCheckingVisitor.getSourceUnit().getConfiguration().getSourceEncoding()));
            }
            catch (CompilationFailedException e) {
                throw new GroovyBugError("An unexpected error was thrown during custom type checking", e);
            }
            catch (UnsupportedEncodingException e) {
                throw new GroovyBugError("Unsupported encoding found in compiler configuration", e);
            }
        }
        if (script != null) {
            ((TypeCheckingDSL)script).extension = this;
            script.run();
            List<Closure> list = this.eventHandlers.get("setup");
            if (list != null) {
                for (Closure closure : list) {
                    this.safeCall(closure, new Object[0]);
                }
            }
        }
    }

    private void addLoadingError(CompilerConfiguration config) {
        this.context.getErrorCollector().addFatalError(new SimpleMessage("Static type checking extension '" + this.scriptPath + "' could not be loaded.", config.getDebug(), this.typeCheckingVisitor.getSourceUnit()));
    }

    @Override
    public void finish() {
        List<Closure> list = this.eventHandlers.get("finish");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, new Object[0]);
            }
        }
    }

    @Override
    public void onMethodSelection(Expression expression, MethodNode target) {
        List<Closure> onMethodSelection = this.eventHandlers.get("onMethodSelection");
        if (onMethodSelection != null) {
            for (Closure closure : onMethodSelection) {
                this.safeCall(closure, expression, target);
            }
        }
    }

    @Override
    public void afterMethodCall(MethodCall call) {
        List<Closure> onMethodSelection = this.eventHandlers.get("afterMethodCall");
        if (onMethodSelection != null) {
            for (Closure closure : onMethodSelection) {
                this.safeCall(closure, call);
            }
        }
    }

    @Override
    public boolean beforeMethodCall(MethodCall call) {
        this.setHandled(false);
        List<Closure> onMethodSelection = this.eventHandlers.get("beforeMethodCall");
        if (onMethodSelection != null) {
            for (Closure closure : onMethodSelection) {
                this.safeCall(closure, call);
            }
        }
        return this.handled;
    }

    @Override
    public boolean handleUnresolvedVariableExpression(VariableExpression vexp) {
        this.setHandled(false);
        List<Closure> onMethodSelection = this.eventHandlers.get("handleUnresolvedVariableExpression");
        if (onMethodSelection != null) {
            for (Closure closure : onMethodSelection) {
                this.safeCall(closure, vexp);
            }
        }
        return this.handled;
    }

    @Override
    public boolean handleUnresolvedProperty(PropertyExpression pexp) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("handleUnresolvedProperty");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, pexp);
            }
        }
        return this.handled;
    }

    @Override
    public boolean handleUnresolvedAttribute(AttributeExpression aexp) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("handleUnresolvedAttribute");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, aexp);
            }
        }
        return this.handled;
    }

    @Override
    public void afterVisitMethod(MethodNode node) {
        List<Closure> list = this.eventHandlers.get("afterVisitMethod");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, node);
            }
        }
    }

    @Override
    public boolean beforeVisitClass(ClassNode node) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("beforeVisitClass");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, node);
            }
        }
        return this.handled;
    }

    @Override
    public void afterVisitClass(ClassNode node) {
        List<Closure> list = this.eventHandlers.get("afterVisitClass");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, node);
            }
        }
    }

    @Override
    public boolean beforeVisitMethod(MethodNode node) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("beforeVisitMethod");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, node);
            }
        }
        return this.handled;
    }

    @Override
    public boolean handleIncompatibleAssignment(ClassNode lhsType, ClassNode rhsType, Expression assignmentExpression) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("handleIncompatibleAssignment");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, lhsType, rhsType, assignmentExpression);
            }
        }
        return this.handled;
    }

    @Override
    public boolean handleIncompatibleReturnType(ReturnStatement returnStatement, ClassNode inferredReturnType) {
        this.setHandled(false);
        List<Closure> list = this.eventHandlers.get("handleIncompatibleReturnType");
        if (list != null) {
            for (Closure closure : list) {
                this.safeCall(closure, returnStatement, inferredReturnType);
            }
        }
        return this.handled;
    }

    @Override
    public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList, ClassNode[] argumentTypes, MethodCall call) {
        List<Closure> onMethodSelection = this.eventHandlers.get("handleMissingMethod");
        LinkedList<MethodNode> methodList = new LinkedList<MethodNode>();
        if (onMethodSelection != null) {
            for (Closure closure : onMethodSelection) {
                Object result = this.safeCall(closure, receiver, name, argumentList, argumentTypes, call);
                if (result == null) continue;
                if (result instanceof MethodNode) {
                    methodList.add((MethodNode)result);
                    continue;
                }
                if (result instanceof Collection) {
                    methodList.addAll((Collection)result);
                    continue;
                }
                throw new GroovyBugError("Type checking extension returned unexpected method list: " + result);
            }
        }
        return methodList;
    }

    @Override
    public List<MethodNode> handleAmbiguousMethods(List<MethodNode> nodes, Expression origin) {
        List<Closure> onMethodSelection = this.eventHandlers.get("handleAmbiguousMethods");
        List<MethodNode> methodList = nodes;
        if (onMethodSelection != null) {
            Iterator<Closure> iterator = onMethodSelection.iterator();
            while (methodList.size() > 1 && iterator.hasNext()) {
                Closure closure = iterator.next();
                Object result = this.safeCall(closure, methodList, origin);
                if (result == null) continue;
                if (result instanceof MethodNode) {
                    methodList = Collections.singletonList((MethodNode)result);
                    continue;
                }
                if (result instanceof Collection) {
                    methodList = new LinkedList<MethodNode>((Collection)result);
                    continue;
                }
                throw new GroovyBugError("Type checking extension returned unexpected method list: " + result);
            }
        }
        return methodList;
    }

    public static abstract class TypeCheckingDSL
    extends Script {
        private GroovyTypeCheckingExtensionSupport extension;

        @Override
        public Object getProperty(String property) {
            try {
                return InvokerHelper.getProperty(this.extension, property);
            }
            catch (Exception e) {
                return super.getProperty(property);
            }
        }

        @Override
        public void setProperty(String property, Object newValue) {
            try {
                InvokerHelper.setProperty(this.extension, property, newValue);
            }
            catch (Exception e) {
                super.setProperty(property, newValue);
            }
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            if (name.startsWith("is") && name.endsWith("Expression") && args instanceof Object[] && ((Object[])args).length == 1) {
                String type = name.substring(2);
                Object target = ((Object[])args)[0];
                if (target == null) {
                    return false;
                }
                try {
                    Class<?> typeClass = Class.forName("org.codehaus.groovy.ast.expr." + type);
                    return typeClass.isAssignableFrom(target.getClass());
                }
                catch (ClassNotFoundException e) {
                    return false;
                }
            }
            if (args instanceof Object[] && ((Object[])args).length == 1 && ((Object[])args)[0] instanceof Closure) {
                Object[] argsArray = (Object[])args;
                String methodName = (String)METHOD_ALIASES.get(name);
                if (methodName == null) {
                    return InvokerHelper.invokeMethod(this.extension, name, args);
                }
                LinkedList<Closure> closures = (LinkedList<Closure>)this.extension.eventHandlers.get(methodName);
                if (closures == null) {
                    closures = new LinkedList<Closure>();
                    this.extension.eventHandlers.put(methodName, closures);
                }
                closures.add((Closure)argsArray[0]);
                return null;
            }
            return InvokerHelper.invokeMethod(this.extension, name, args);
        }
    }
}

