/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELManager;
import javax.el.ExpressionFactory;
import javax.el.ImportHandler;
import javax.el.JreCompat;
import javax.el.Util;
import javax.el.ValueExpression;

public class ELProcessor {
    private static final Set<String> PRIMITIVES = new HashSet<String>();
    private static final String[] EMPTY_STRING_ARRAY;
    private final ELManager manager = new ELManager();
    private final ELContext context = this.manager.getELContext();
    private final ExpressionFactory factory = ELManager.getExpressionFactory();

    public ELManager getELManager() {
        return this.manager;
    }

    public Object eval(String expression) {
        return this.getValue(expression, Object.class);
    }

    public Object getValue(String expression, Class<?> expectedType) {
        ValueExpression ve = this.factory.createValueExpression(this.context, ELProcessor.bracket(expression), expectedType);
        return ve.getValue(this.context);
    }

    public void setValue(String expression, Object value) {
        ValueExpression ve = this.factory.createValueExpression(this.context, ELProcessor.bracket(expression), Object.class);
        ve.setValue(this.context, value);
    }

    public void setVariable(String variable, String expression) {
        if (expression == null) {
            this.manager.setVariable(variable, null);
        } else {
            ValueExpression ve = this.factory.createValueExpression(this.context, ELProcessor.bracket(expression), Object.class);
            this.manager.setVariable(variable, ve);
        }
    }

    public void defineFunction(String prefix, String function, String className, String methodName) throws ClassNotFoundException, NoSuchMethodException {
        if (prefix == null || function == null || className == null || methodName == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        Class<?> clazz = this.context.getImportHandler().resolveClass(className);
        if (clazz == null) {
            clazz = Class.forName(className, true, Util.getContextClassLoader());
        }
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new ClassNotFoundException(Util.message(this.context, "elProcessor.defineFunctionInvalidClass", className));
        }
        MethodSignature sig = new MethodSignature(this.context, methodName, className);
        if (function.length() == 0) {
            function = sig.getName();
        }
        Method[] methods = clazz.getMethods();
        JreCompat jreCompat = JreCompat.getInstance();
        for (Method method : methods) {
            String[] typeNames;
            if (!Modifier.isStatic(method.getModifiers()) || !jreCompat.canAccess(null, method) || !method.getName().equals(sig.getName())) continue;
            if (sig.getParamTypeNames() == null) {
                this.manager.mapFunction(prefix, function, method);
                return;
            }
            if (sig.getParamTypeNames().length != method.getParameterTypes().length) continue;
            if (sig.getParamTypeNames().length == 0) {
                this.manager.mapFunction(prefix, function, method);
                return;
            }
            Class<?>[] types = method.getParameterTypes();
            if (types.length != (typeNames = sig.getParamTypeNames()).length) continue;
            boolean match = true;
            for (int i = 0; i < types.length; ++i) {
                if (i == types.length - 1 && method.isVarArgs()) {
                    String typeName = typeNames[i];
                    if (typeName.endsWith("...")) {
                        if ((typeName = typeName.substring(0, typeName.length() - 3)).equals(types[i].getName())) continue;
                        match = false;
                        continue;
                    }
                    match = false;
                    continue;
                }
                if (types[i].getName().equals(typeNames[i])) continue;
                match = false;
                break;
            }
            if (!match) continue;
            this.manager.mapFunction(prefix, function, method);
            return;
        }
        throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionNoMethod", methodName, className));
    }

    public void defineFunction(String prefix, String function, Method method) throws NoSuchMethodException {
        if (prefix == null || function == null || method == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        int modifiers = method.getModifiers();
        JreCompat jreCompat = JreCompat.getInstance();
        if (!Modifier.isStatic(modifiers) || !jreCompat.canAccess(null, method)) {
            throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionInvalidMethod", method.getName(), method.getDeclaringClass().getName()));
        }
        this.manager.mapFunction(prefix, function, method);
    }

    public void defineBean(String name, Object bean) {
        this.manager.defineBean(name, bean);
    }

    private static String bracket(String expression) {
        return "${" + expression + "}";
    }

    static {
        PRIMITIVES.add("boolean");
        PRIMITIVES.add("byte");
        PRIMITIVES.add("char");
        PRIMITIVES.add("double");
        PRIMITIVES.add("float");
        PRIMITIVES.add("int");
        PRIMITIVES.add("long");
        PRIMITIVES.add("short");
        EMPTY_STRING_ARRAY = new String[0];
    }

    private static class MethodSignature {
        private final String name;
        private final String[] parameterTypeNames;

        MethodSignature(ELContext context, String methodName, String className) throws NoSuchMethodException {
            int paramIndex = methodName.indexOf(40);
            if (paramIndex == -1) {
                this.name = methodName.trim();
                this.parameterTypeNames = null;
            } else {
                String returnTypeAndName = methodName.substring(0, paramIndex).trim();
                int wsPos = -1;
                for (int i = 0; i < returnTypeAndName.length(); ++i) {
                    if (!Character.isWhitespace(returnTypeAndName.charAt(i))) continue;
                    wsPos = i;
                    break;
                }
                if (wsPos == -1) {
                    throw new NoSuchMethodException();
                }
                this.name = returnTypeAndName.substring(wsPos).trim();
                String paramString = methodName.substring(paramIndex).trim();
                if (!paramString.endsWith(")")) {
                    throw new NoSuchMethodException(Util.message(context, "elProcessor.defineFunctionInvalidParameterList", paramString, methodName, className));
                }
                if ((paramString = paramString.substring(1, paramString.length() - 1).trim()).length() == 0) {
                    this.parameterTypeNames = EMPTY_STRING_ARRAY;
                } else {
                    this.parameterTypeNames = paramString.split(",");
                    ImportHandler importHandler = context.getImportHandler();
                    for (int i = 0; i < this.parameterTypeNames.length; ++i) {
                        boolean isPrimitive;
                        String parameterTypeName = this.parameterTypeNames[i].trim();
                        int dimension = 0;
                        int bracketPos = parameterTypeName.indexOf(91);
                        if (bracketPos > -1) {
                            String parameterTypeNameOnly = parameterTypeName.substring(0, bracketPos).trim();
                            while (bracketPos > -1) {
                                ++dimension;
                                bracketPos = parameterTypeName.indexOf(91, bracketPos + 1);
                            }
                            parameterTypeName = parameterTypeNameOnly;
                        }
                        boolean varArgs = false;
                        if (parameterTypeName.endsWith("...")) {
                            varArgs = true;
                            dimension = 1;
                            parameterTypeName = parameterTypeName.substring(0, parameterTypeName.length() - 3).trim();
                        }
                        if ((isPrimitive = PRIMITIVES.contains(parameterTypeName)) && dimension > 0) {
                            switch (parameterTypeName) {
                                case "boolean": {
                                    parameterTypeName = "Z";
                                    break;
                                }
                                case "byte": {
                                    parameterTypeName = "B";
                                    break;
                                }
                                case "char": {
                                    parameterTypeName = "C";
                                    break;
                                }
                                case "double": {
                                    parameterTypeName = "D";
                                    break;
                                }
                                case "float": {
                                    parameterTypeName = "F";
                                    break;
                                }
                                case "int": {
                                    parameterTypeName = "I";
                                    break;
                                }
                                case "long": {
                                    parameterTypeName = "J";
                                    break;
                                }
                                case "short": {
                                    parameterTypeName = "S";
                                    break;
                                }
                            }
                        } else if (!isPrimitive && !parameterTypeName.contains(".")) {
                            Class<?> clazz = importHandler.resolveClass(parameterTypeName);
                            if (clazz == null) {
                                throw new NoSuchMethodException(Util.message(context, "elProcessor.defineFunctionInvalidParameterTypeName", this.parameterTypeNames[i], methodName, className));
                            }
                            parameterTypeName = clazz.getName();
                        }
                        if (dimension > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < dimension; ++j) {
                                sb.append('[');
                            }
                            if (!isPrimitive) {
                                sb.append('L');
                            }
                            sb.append(parameterTypeName);
                            if (!isPrimitive) {
                                sb.append(';');
                            }
                            parameterTypeName = sb.toString();
                        }
                        if (varArgs) {
                            parameterTypeName = parameterTypeName + "...";
                        }
                        this.parameterTypeNames[i] = parameterTypeName;
                    }
                }
            }
        }

        public String getName() {
            return this.name;
        }

        public String[] getParamTypeNames() {
            return this.parameterTypeNames;
        }
    }
}

