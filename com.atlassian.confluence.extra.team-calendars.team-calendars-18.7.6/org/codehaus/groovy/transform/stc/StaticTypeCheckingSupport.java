/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.ast.tools.ParameterUtils;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner;
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.tools.GroovyClass;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.UnionTypeClassNode;
import org.codehaus.groovy.transform.trait.Traits;

public abstract class StaticTypeCheckingSupport {
    protected static final ClassNode Collection_TYPE = ClassHelper.makeWithoutCaching(Collection.class);
    protected static final ClassNode Deprecated_TYPE = ClassHelper.makeWithoutCaching(Deprecated.class);
    protected static final ClassNode Matcher_TYPE = ClassHelper.makeWithoutCaching(Matcher.class);
    protected static final ClassNode ArrayList_TYPE = ClassHelper.makeWithoutCaching(ArrayList.class);
    protected static final ExtensionMethodCache EXTENSION_METHOD_CACHE = new ExtensionMethodCache();
    protected static final Map<ClassNode, Integer> NUMBER_TYPES = Collections.unmodifiableMap(new HashMap<ClassNode, Integer>(){
        {
            this.put(ClassHelper.byte_TYPE, 0);
            this.put(ClassHelper.Byte_TYPE, 0);
            this.put(ClassHelper.short_TYPE, 1);
            this.put(ClassHelper.Short_TYPE, 1);
            this.put(ClassHelper.int_TYPE, 2);
            this.put(ClassHelper.Integer_TYPE, 2);
            this.put(ClassHelper.Long_TYPE, 3);
            this.put(ClassHelper.long_TYPE, 3);
            this.put(ClassHelper.float_TYPE, 4);
            this.put(ClassHelper.Float_TYPE, 4);
            this.put(ClassHelper.double_TYPE, 5);
            this.put(ClassHelper.Double_TYPE, 5);
        }
    });
    protected static final Map<String, Integer> NUMBER_OPS = Collections.unmodifiableMap(new HashMap<String, Integer>(){
        {
            this.put("plus", 200);
            this.put("minus", 201);
            this.put("multiply", 202);
            this.put("div", 203);
            this.put("or", 340);
            this.put("and", 341);
            this.put("xor", 342);
            this.put("mod", 205);
            this.put("intdiv", 204);
            this.put("leftShift", 280);
            this.put("rightShift", 281);
            this.put("rightShiftUnsigned", 282);
        }
    });
    protected static final ClassNode GSTRING_STRING_CLASSNODE = WideningCategories.lowestUpperBound(ClassHelper.STRING_TYPE, ClassHelper.GSTRING_TYPE);
    protected static final ClassNode UNKNOWN_PARAMETER_TYPE = ClassHelper.make("<unknown parameter type>");
    protected static final Comparator<MethodNode> DGM_METHOD_NODE_COMPARATOR = new Comparator<MethodNode>(){

        @Override
        public int compare(MethodNode o1, MethodNode o2) {
            if (o1.getName().equals(o2.getName())) {
                Parameter[] o2ps;
                Parameter[] o1ps = o1.getParameters();
                if (o1ps.length == (o2ps = o2.getParameters()).length) {
                    boolean allEqual = true;
                    for (int i = 0; i < o1ps.length && allEqual; ++i) {
                        allEqual = o1ps[i].getType().equals(o2ps[i].getType());
                    }
                    if (allEqual) {
                        if (o1 instanceof ExtensionMethodNode && o2 instanceof ExtensionMethodNode) {
                            return this.compare(((ExtensionMethodNode)o1).getExtensionMethodNode(), ((ExtensionMethodNode)o2).getExtensionMethodNode());
                        }
                        return 0;
                    }
                } else {
                    return o1ps.length - o2ps.length;
                }
            }
            return 1;
        }
    };

    protected static boolean isArrayAccessExpression(Expression expression) {
        return expression instanceof BinaryExpression && StaticTypeCheckingSupport.isArrayOp(((BinaryExpression)expression).getOperation().getType());
    }

    public static boolean isWithCall(String name, Expression callArguments) {
        boolean isWithCall;
        boolean bl = isWithCall = "with".equals(name) && callArguments instanceof ArgumentListExpression;
        if (isWithCall) {
            ArgumentListExpression argList = (ArgumentListExpression)callArguments;
            List<Expression> expressions = argList.getExpressions();
            isWithCall = expressions.size() == 1 && expressions.get(0) instanceof ClosureExpression;
        }
        return isWithCall;
    }

    protected static Variable findTargetVariable(VariableExpression ve) {
        Variable accessedVariable;
        Variable variable = accessedVariable = ve.getAccessedVariable() != null ? ve.getAccessedVariable() : ve;
        if (accessedVariable != ve && accessedVariable instanceof VariableExpression) {
            return StaticTypeCheckingSupport.findTargetVariable(accessedVariable);
        }
        return accessedVariable;
    }

    @Deprecated
    protected static Set<MethodNode> findDGMMethodsForClassNode(ClassNode clazz, String name) {
        return StaticTypeCheckingSupport.findDGMMethodsForClassNode(MetaClassRegistryImpl.class.getClassLoader(), clazz, name);
    }

    protected static Set<MethodNode> findDGMMethodsForClassNode(ClassLoader loader, ClassNode clazz, String name) {
        TreeSet<MethodNode> accumulator = new TreeSet<MethodNode>(DGM_METHOD_NODE_COMPARATOR);
        StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, clazz, name, accumulator);
        return accumulator;
    }

    @Deprecated
    protected static void findDGMMethodsForClassNode(ClassNode clazz, String name, TreeSet<MethodNode> accumulator) {
        StaticTypeCheckingSupport.findDGMMethodsForClassNode(MetaClassRegistryImpl.class.getClassLoader(), clazz, name, accumulator);
    }

    protected static void findDGMMethodsForClassNode(ClassLoader loader, ClassNode clazz, String name, TreeSet<MethodNode> accumulator) {
        ClassNode componentClass;
        List<MethodNode> fromDGM = EXTENSION_METHOD_CACHE.getExtensionMethods(loader).get(clazz.getName());
        if (fromDGM != null) {
            for (MethodNode node : fromDGM) {
                if (!node.getName().equals(name)) continue;
                accumulator.add(node);
            }
        }
        for (ClassNode node : clazz.getInterfaces()) {
            StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, node, name, accumulator);
        }
        if (clazz.isArray() && !(componentClass = clazz.getComponentType()).equals(ClassHelper.OBJECT_TYPE) && !ClassHelper.isPrimitiveType(componentClass)) {
            if (componentClass.isInterface()) {
                StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, ClassHelper.OBJECT_TYPE.makeArray(), name, accumulator);
            } else {
                StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, componentClass.getSuperClass().makeArray(), name, accumulator);
            }
        }
        if (clazz.getSuperClass() != null) {
            StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, clazz.getSuperClass(), name, accumulator);
        } else if (!clazz.equals(ClassHelper.OBJECT_TYPE)) {
            StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, ClassHelper.OBJECT_TYPE, name, accumulator);
        }
    }

    public static int allParametersAndArgumentsMatch(Parameter[] params, ClassNode[] args) {
        if (params == null) {
            params = Parameter.EMPTY_ARRAY;
        }
        int dist = 0;
        if (args.length < params.length) {
            return -1;
        }
        for (int i = 0; i < params.length; ++i) {
            ClassNode argType = args[i];
            ClassNode paramType = params[i].getType();
            if (!StaticTypeCheckingSupport.isAssignableTo(argType, paramType)) {
                return -1;
            }
            if (paramType.equals(argType)) continue;
            dist += StaticTypeCheckingSupport.getDistance(argType, paramType);
        }
        return dist;
    }

    static int allParametersAndArgumentsMatchWithDefaultParams(Parameter[] params, ClassNode[] args) {
        int dist = 0;
        ClassNode ptype = null;
        int j = 0;
        for (int i = 0; i < params.length; ++i) {
            ClassNode arg;
            Parameter param = params[i];
            ClassNode paramType = param.getType();
            ClassNode classNode = arg = j >= args.length ? null : args[j];
            if (arg == null || !StaticTypeCheckingSupport.isAssignableTo(arg, paramType)) {
                if (!(param.hasInitialExpression() || ptype != null && ptype.equals(paramType))) {
                    return -1;
                }
                ptype = null;
                continue;
            }
            ++j;
            if (!paramType.equals(arg)) {
                dist += StaticTypeCheckingSupport.getDistance(arg, paramType);
            }
            ptype = param.hasInitialExpression() ? arg : null;
        }
        return dist;
    }

    static int excessArgumentsMatchesVargsParameter(Parameter[] params, ClassNode[] args) {
        int dist = 0;
        ClassNode vargsBase = params[params.length - 1].getType().getComponentType();
        for (int i = params.length; i < args.length; ++i) {
            if (!StaticTypeCheckingSupport.isAssignableTo(args[i], vargsBase)) {
                return -1;
            }
            dist += StaticTypeCheckingSupport.getClassDistance(vargsBase, args[i]);
        }
        return dist;
    }

    static int lastArgMatchesVarg(Parameter[] params, ClassNode ... args) {
        if (!StaticTypeCheckingSupport.isVargs(params)) {
            return -1;
        }
        ClassNode lastParamType = params[params.length - 1].getType();
        ClassNode ptype = lastParamType.getComponentType();
        ClassNode arg = args[args.length - 1];
        if (ClassHelper.isNumberType(ptype) && ClassHelper.isNumberType(arg) && !ptype.equals(arg)) {
            return -1;
        }
        return StaticTypeCheckingSupport.isAssignableTo(arg, ptype) ? Math.min(StaticTypeCheckingSupport.getDistance(arg, lastParamType), StaticTypeCheckingSupport.getDistance(arg, ptype)) : -1;
    }

    static boolean isAssignableTo(ClassNode type, ClassNode toBeAssignedTo) {
        if (UNKNOWN_PARAMETER_TYPE == type) {
            return true;
        }
        if (type == toBeAssignedTo) {
            return true;
        }
        if (toBeAssignedTo.redirect() == ClassHelper.STRING_TYPE && type.redirect() == ClassHelper.GSTRING_TYPE) {
            return true;
        }
        if (ClassHelper.isPrimitiveType(toBeAssignedTo)) {
            toBeAssignedTo = ClassHelper.getWrapper(toBeAssignedTo);
        }
        if (ClassHelper.isPrimitiveType(type)) {
            type = ClassHelper.getWrapper(type);
        }
        if (NUMBER_TYPES.containsKey(type.redirect()) && NUMBER_TYPES.containsKey(toBeAssignedTo.redirect())) {
            return NUMBER_TYPES.get(type.redirect()) <= NUMBER_TYPES.get(toBeAssignedTo.redirect());
        }
        if (type.isArray() && toBeAssignedTo.isArray()) {
            return StaticTypeCheckingSupport.isAssignableTo(type.getComponentType(), toBeAssignedTo.getComponentType());
        }
        if (type.isDerivedFrom(ClassHelper.GSTRING_TYPE) && ClassHelper.STRING_TYPE.equals(toBeAssignedTo)) {
            return true;
        }
        if (toBeAssignedTo.isDerivedFrom(ClassHelper.GSTRING_TYPE) && ClassHelper.STRING_TYPE.equals(type)) {
            return true;
        }
        if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, toBeAssignedTo)) {
            if (ClassHelper.OBJECT_TYPE.equals(toBeAssignedTo)) {
                return true;
            }
            if (toBeAssignedTo.isUsingGenerics()) {
                GenericsType gt = GenericsUtils.buildWildcardType(toBeAssignedTo);
                return gt.isCompatibleWith(type);
            }
            return true;
        }
        return type.isDerivedFrom(ClassHelper.CLOSURE_TYPE) && ClassHelper.isSAMType(toBeAssignedTo);
    }

    static boolean isVargs(Parameter[] params) {
        if (params.length == 0) {
            return false;
        }
        return params[params.length - 1].getType().isArray();
    }

    public static boolean isCompareToBoolean(int op) {
        return op == 126 || op == 127 || op == 124 || op == 125;
    }

    static boolean isArrayOp(int op) {
        return op == 30;
    }

    static boolean isBoolIntrinsicOp(int op) {
        return op == 164 || op == 162 || op == 122 || op == 121 || op == 94 || op == 544;
    }

    static boolean isPowerOperator(int op) {
        return op == 206 || op == 216;
    }

    static String getOperationName(int op) {
        switch (op) {
            case 120: 
            case 123: {
                return "equals";
            }
            case 124: 
            case 125: 
            case 126: 
            case 127: 
            case 128: {
                return "compareTo";
            }
            case 341: 
            case 351: {
                return "and";
            }
            case 340: 
            case 350: {
                return "or";
            }
            case 342: 
            case 352: {
                return "xor";
            }
            case 200: 
            case 210: {
                return "plus";
            }
            case 201: 
            case 211: {
                return "minus";
            }
            case 202: 
            case 212: {
                return "multiply";
            }
            case 203: 
            case 213: {
                return "div";
            }
            case 204: 
            case 214: {
                return "intdiv";
            }
            case 205: 
            case 215: {
                return "mod";
            }
            case 206: 
            case 216: {
                return "power";
            }
            case 280: 
            case 285: {
                return "leftShift";
            }
            case 281: 
            case 286: {
                return "rightShift";
            }
            case 282: 
            case 287: {
                return "rightShiftUnsigned";
            }
            case 573: {
                return "isCase";
            }
        }
        return null;
    }

    static boolean isShiftOperation(String name) {
        return "leftShift".equals(name) || "rightShift".equals(name) || "rightShiftUnsigned".equals(name);
    }

    static boolean isOperationInGroup(int op) {
        switch (op) {
            case 200: 
            case 201: 
            case 202: 
            case 210: 
            case 211: 
            case 212: {
                return true;
            }
        }
        return false;
    }

    static boolean isBitOperator(int op) {
        switch (op) {
            case 340: 
            case 341: 
            case 342: 
            case 350: 
            case 351: 
            case 352: {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignment(int op) {
        switch (op) {
            case 100: 
            case 166: 
            case 168: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: 
            case 216: 
            case 285: 
            case 286: 
            case 287: 
            case 350: 
            case 351: 
            case 352: {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCompatibleAssignmentTypes(ClassNode left, ClassNode right) {
        return StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(left, right, null);
    }

    public static boolean checkCompatibleAssignmentTypes(ClassNode left, ClassNode right, Expression rightExpression) {
        return StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(left, right, rightExpression, true);
    }

    public static boolean checkCompatibleAssignmentTypes(ClassNode left, ClassNode right, Expression rightExpression, boolean allowConstructorCoercion) {
        GenericsType[] genericsTypes;
        boolean rightExpressionIsNull;
        ClassNode rightRedirect;
        ClassNode leftRedirect = left.redirect();
        if (leftRedirect == (rightRedirect = right.redirect())) {
            return true;
        }
        if (leftRedirect.isArray() && rightRedirect.isArray()) {
            return StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(leftRedirect.getComponentType(), rightRedirect.getComponentType(), rightExpression, false);
        }
        if (right == ClassHelper.VOID_TYPE || right == ClassHelper.void_WRAPPER_TYPE) {
            return left == ClassHelper.VOID_TYPE || left == ClassHelper.void_WRAPPER_TYPE;
        }
        if (ClassHelper.isNumberType(rightRedirect) || WideningCategories.isNumberCategory(rightRedirect)) {
            if (ClassHelper.BigDecimal_TYPE == leftRedirect) {
                return true;
            }
            if (ClassHelper.BigInteger_TYPE == leftRedirect) {
                return WideningCategories.isBigIntCategory(ClassHelper.getUnwrapper(rightRedirect)) || rightRedirect.isDerivedFrom(ClassHelper.BigInteger_TYPE);
            }
        }
        boolean bl = rightExpressionIsNull = rightExpression instanceof ConstantExpression && ((ConstantExpression)rightExpression).getValue() == null;
        if (rightExpressionIsNull && !ClassHelper.isPrimitiveType(left)) {
            return true;
        }
        if (!(!StaticTypeCheckingSupport.isWildcardLeftHandSide(leftRedirect) || ClassHelper.boolean_TYPE.equals(left) && rightExpressionIsNull)) {
            return true;
        }
        if (leftRedirect == ClassHelper.char_TYPE && rightRedirect == ClassHelper.STRING_TYPE && rightExpression != null && rightExpression instanceof ConstantExpression) {
            String value = rightExpression.getText();
            return value.length() == 1;
        }
        if (leftRedirect == ClassHelper.Character_TYPE && (rightRedirect == ClassHelper.STRING_TYPE || rightExpressionIsNull)) {
            return rightExpressionIsNull || rightExpression instanceof ConstantExpression && rightExpression.getText().length() == 1;
        }
        if (leftRedirect.isDerivedFrom(ClassHelper.Enum_Type) && (rightRedirect == ClassHelper.GSTRING_TYPE || rightRedirect == ClassHelper.STRING_TYPE)) {
            return true;
        }
        if (allowConstructorCoercion && StaticTypeCheckingSupport.isGroovyConstructorCompatible(rightExpression)) {
            if (leftRedirect.isArray() && rightRedirect.isArray()) {
                return StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(leftRedirect.getComponentType(), rightRedirect.getComponentType());
            }
            return !rightRedirect.isArray() || leftRedirect.isArray();
        }
        if (right.isDerivedFrom(left) || left.isInterface() && right.implementsInterface(left)) {
            return true;
        }
        if (ClassHelper.isPrimitiveType(leftRedirect) && ClassHelper.isPrimitiveType(rightRedirect)) {
            return true;
        }
        if (ClassHelper.isNumberType(leftRedirect) && ClassHelper.isNumberType(rightRedirect)) {
            return true;
        }
        if (WideningCategories.isFloatingCategory(leftRedirect) && ClassHelper.BigDecimal_TYPE.equals(rightRedirect)) {
            return true;
        }
        if (ClassHelper.GROOVY_OBJECT_TYPE.equals(leftRedirect) && StaticTypeCheckingSupport.isBeingCompiled(right)) {
            return true;
        }
        if (left.isGenericsPlaceHolder() && (genericsTypes = left.getGenericsTypes()) != null && genericsTypes.length == 1) {
            return genericsTypes[0].isCompatibleWith(right);
        }
        return right.isGenericsPlaceHolder();
    }

    private static boolean isGroovyConstructorCompatible(Expression rightExpression) {
        return rightExpression instanceof ListExpression || rightExpression instanceof MapExpression || rightExpression instanceof ArrayExpression;
    }

    public static boolean isWildcardLeftHandSide(ClassNode node) {
        return ClassHelper.OBJECT_TYPE.equals(node) || ClassHelper.STRING_TYPE.equals(node) || ClassHelper.boolean_TYPE.equals(node) || ClassHelper.Boolean_TYPE.equals(node) || ClassHelper.CLASS_Type.equals(node);
    }

    public static boolean isBeingCompiled(ClassNode node) {
        return node.getCompileUnit() != null;
    }

    @Deprecated
    static boolean checkPossibleLooseOfPrecision(ClassNode left, ClassNode right, Expression rightExpr) {
        return StaticTypeCheckingSupport.checkPossibleLossOfPrecision(left, right, rightExpr);
    }

    static boolean checkPossibleLossOfPrecision(ClassNode left, ClassNode right, Expression rightExpr) {
        int rightIndex;
        if (left == right || left.equals(right)) {
            return false;
        }
        int leftIndex = NUMBER_TYPES.get(left);
        if (leftIndex >= (rightIndex = NUMBER_TYPES.get(right).intValue())) {
            return false;
        }
        if (rightExpr instanceof ConstantExpression) {
            Object value = ((ConstantExpression)rightExpr).getValue();
            if (!(value instanceof Number)) {
                return true;
            }
            Number number = (Number)value;
            switch (leftIndex) {
                case 0: {
                    byte val = number.byteValue();
                    if (number instanceof Short) {
                        return !Short.valueOf(val).equals(number);
                    }
                    if (number instanceof Integer) {
                        return !Integer.valueOf(val).equals(number);
                    }
                    if (number instanceof Long) {
                        return !Long.valueOf(val).equals(number);
                    }
                    if (number instanceof Float) {
                        return !Float.valueOf(val).equals(number);
                    }
                    return !Double.valueOf(val).equals(number);
                }
                case 1: {
                    short val = number.shortValue();
                    if (number instanceof Integer) {
                        return !Integer.valueOf(val).equals(number);
                    }
                    if (number instanceof Long) {
                        return !Long.valueOf(val).equals(number);
                    }
                    if (number instanceof Float) {
                        return !Float.valueOf(val).equals(number);
                    }
                    return !Double.valueOf(val).equals(number);
                }
                case 2: {
                    int val = number.intValue();
                    if (number instanceof Long) {
                        return !Long.valueOf(val).equals(number);
                    }
                    if (number instanceof Float) {
                        return !Float.valueOf(val).equals(number);
                    }
                    return !Double.valueOf(val).equals(number);
                }
                case 3: {
                    long val = number.longValue();
                    if (number instanceof Float) {
                        return !Float.valueOf(val).equals(number);
                    }
                    return !Double.valueOf(val).equals(number);
                }
                case 4: {
                    float val = number.floatValue();
                    return !Double.valueOf(val).equals(number);
                }
            }
            return false;
        }
        return true;
    }

    static String toMethodParametersString(String methodName, ClassNode ... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("(");
        if (parameters != null) {
            int parametersLength = parameters.length;
            for (int i = 0; i < parametersLength; ++i) {
                ClassNode parameter = parameters[i];
                sb.append(StaticTypeCheckingSupport.prettyPrintType(parameter));
                if (i >= parametersLength - 1) continue;
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    static String prettyPrintType(ClassNode type) {
        if (type.isArray()) {
            return StaticTypeCheckingSupport.prettyPrintType(type.getComponentType()) + "[]";
        }
        return type.toString(false);
    }

    public static boolean implementsInterfaceOrIsSubclassOf(ClassNode type, ClassNode superOrInterface) {
        boolean result;
        boolean bl = result = type.equals(superOrInterface) || type.isDerivedFrom(superOrInterface) || type.implementsInterface(superOrInterface) || type == UNKNOWN_PARAMETER_TYPE;
        if (result) {
            return true;
        }
        if (superOrInterface instanceof WideningCategories.LowestUpperBoundClassNode) {
            WideningCategories.LowestUpperBoundClassNode cn = (WideningCategories.LowestUpperBoundClassNode)superOrInterface;
            result = StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, cn.getSuperClass());
            if (result) {
                ClassNode interfaceNode;
                ClassNode[] classNodeArray = cn.getInterfaces();
                int n = classNodeArray.length;
                for (int i = 0; i < n && (result = type.implementsInterface(interfaceNode = classNodeArray[i])); ++i) {
                }
            }
            if (result) {
                return true;
            }
        } else if (superOrInterface instanceof UnionTypeClassNode) {
            UnionTypeClassNode union = (UnionTypeClassNode)superOrInterface;
            for (ClassNode delegate : union.getDelegates()) {
                if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, delegate)) continue;
                return true;
            }
        }
        if (type.isArray() && superOrInterface.isArray()) {
            return StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type.getComponentType(), superOrInterface.getComponentType());
        }
        return ClassHelper.GROOVY_OBJECT_TYPE.equals(superOrInterface) && !type.isInterface() && StaticTypeCheckingSupport.isBeingCompiled(type);
    }

    static int getPrimitiveDistance(ClassNode primA, ClassNode primB) {
        return Math.abs(NUMBER_TYPES.get(primA) - NUMBER_TYPES.get(primB));
    }

    static int getDistance(ClassNode receiver, ClassNode compare) {
        if (receiver.isArray() && compare.isArray()) {
            return StaticTypeCheckingSupport.getDistance(receiver.getComponentType(), compare.getComponentType());
        }
        int dist = 0;
        ClassNode unwrapReceiver = ClassHelper.getUnwrapper(receiver);
        ClassNode unwrapCompare = ClassHelper.getUnwrapper(compare);
        if (ClassHelper.isPrimitiveType(unwrapReceiver) && ClassHelper.isPrimitiveType(unwrapCompare) && unwrapReceiver != unwrapCompare) {
            dist = StaticTypeCheckingSupport.getPrimitiveDistance(unwrapReceiver, unwrapCompare);
        }
        if (ClassHelper.isPrimitiveType(receiver) ^ ClassHelper.isPrimitiveType(compare)) {
            dist = dist + 1 << 1;
        }
        if (unwrapCompare.equals(unwrapReceiver)) {
            return dist;
        }
        if (receiver.isArray() && !compare.isArray()) {
            dist += 256;
        }
        if (receiver == UNKNOWN_PARAMETER_TYPE) {
            return dist;
        }
        ClassNode ref = receiver;
        while (ref != null && !compare.equals(ref)) {
            if (compare.isInterface() && ref.implementsInterface(compare)) {
                dist += StaticTypeCheckingSupport.getMaximumInterfaceDistance(ref, compare);
                break;
            }
            ref = ref.getSuperClass();
            ++dist;
            if (ref == null) {
                ++dist;
            }
            dist = dist + 1 << 1;
        }
        return dist;
    }

    private static int getMaximumInterfaceDistance(ClassNode c, ClassNode interfaceClass) {
        if (c == null) {
            return -1;
        }
        if (c.equals(interfaceClass)) {
            return 0;
        }
        ClassNode[] interfaces = c.getInterfaces();
        int max = -1;
        for (int i = 0; i < interfaces.length; ++i) {
            ClassNode anInterface = interfaces[i];
            int sub = StaticTypeCheckingSupport.getMaximumInterfaceDistance(anInterface, interfaceClass);
            if (sub != -1) {
                ++sub;
            }
            max = Math.max(max, sub);
        }
        int superClassMax = StaticTypeCheckingSupport.getMaximumInterfaceDistance(c.getSuperClass(), interfaceClass);
        return Math.max(max, superClassMax);
    }

    @Deprecated
    public static List<MethodNode> findDGMMethodsByNameAndArguments(ClassNode receiver, String name, ClassNode[] args) {
        return StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(MetaClassRegistryImpl.class.getClassLoader(), receiver, name, args);
    }

    public static List<MethodNode> findDGMMethodsByNameAndArguments(ClassLoader loader, ClassNode receiver, String name, ClassNode[] args) {
        return StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(loader, receiver, name, args, new LinkedList<MethodNode>());
    }

    @Deprecated
    public static List<MethodNode> findDGMMethodsByNameAndArguments(ClassNode receiver, String name, ClassNode[] args, List<MethodNode> methods) {
        return StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(MetaClassRegistryImpl.class.getClassLoader(), receiver, name, args, methods);
    }

    public static List<MethodNode> findDGMMethodsByNameAndArguments(ClassLoader loader, ClassNode receiver, String name, ClassNode[] args, List<MethodNode> methods) {
        methods.addAll(StaticTypeCheckingSupport.findDGMMethodsForClassNode(loader, receiver, name));
        if (methods.isEmpty()) {
            return methods;
        }
        List<MethodNode> chosen = StaticTypeCheckingSupport.chooseBestMethod(receiver, methods, args);
        return chosen;
    }

    public static boolean isUsingUncheckedGenerics(ClassNode node) {
        if (node.isArray()) {
            return StaticTypeCheckingSupport.isUsingUncheckedGenerics(node.getComponentType());
        }
        if (node.isUsingGenerics()) {
            GenericsType[] genericsTypes = node.getGenericsTypes();
            if (genericsTypes != null) {
                for (GenericsType genericsType : genericsTypes) {
                    if (genericsType.isPlaceholder()) {
                        return true;
                    }
                    if (!StaticTypeCheckingSupport.isUsingUncheckedGenerics(genericsType.getType())) continue;
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public static List<MethodNode> chooseBestMethod(ClassNode receiver, Collection<MethodNode> methods, ClassNode ... args) {
        if (methods.isEmpty()) {
            return Collections.emptyList();
        }
        if (StaticTypeCheckingSupport.isUsingUncheckedGenerics(receiver)) {
            ClassNode raw = StaticTypeCheckingSupport.makeRawType(receiver);
            return StaticTypeCheckingSupport.chooseBestMethod(raw, methods, args);
        }
        LinkedList<MethodNode> bestChoices = new LinkedList<MethodNode>();
        int bestDist = Integer.MAX_VALUE;
        Collection<MethodNode> choicesLeft = StaticTypeCheckingSupport.removeCovariantsAndInterfaceEquivalents(methods);
        for (MethodNode candidateNode : choicesLeft) {
            Parameter[] params;
            int dist;
            ClassNode declaringClassForDistance = candidateNode.getDeclaringClass();
            ClassNode actualReceiverForDistance = receiver != null ? receiver : candidateNode.getDeclaringClass();
            MethodNode safeNode = candidateNode;
            ClassNode[] safeArgs = args;
            boolean isExtensionMethodNode = candidateNode instanceof ExtensionMethodNode;
            if (isExtensionMethodNode) {
                safeArgs = new ClassNode[args.length + 1];
                System.arraycopy(args, 0, safeArgs, 1, args.length);
                safeArgs[0] = receiver;
                safeNode = ((ExtensionMethodNode)candidateNode).getExtensionMethodNode();
            }
            if ((dist = StaticTypeCheckingSupport.measureParametersAndArgumentsDistance(params = StaticTypeCheckingSupport.makeRawTypes(safeNode.getParameters()), safeArgs)) < 0) continue;
            dist += StaticTypeCheckingSupport.getClassDistance(declaringClassForDistance, actualReceiverForDistance);
            if ((dist += StaticTypeCheckingSupport.getExtensionDistance(isExtensionMethodNode)) < bestDist) {
                bestChoices.clear();
                bestChoices.add(candidateNode);
                bestDist = dist;
                continue;
            }
            if (dist != bestDist) continue;
            bestChoices.add(candidateNode);
        }
        if (bestChoices.size() > 1) {
            LinkedList<MethodNode> onlyExtensionMethods = new LinkedList<MethodNode>();
            for (MethodNode choice : bestChoices) {
                if (!(choice instanceof ExtensionMethodNode)) continue;
                onlyExtensionMethods.add(choice);
            }
            if (onlyExtensionMethods.size() == 1) {
                return onlyExtensionMethods;
            }
        }
        return bestChoices;
    }

    private static int measureParametersAndArgumentsDistance(Parameter[] params, ClassNode[] args) {
        int dist = -1;
        if (params.length == args.length) {
            int lastArgMatch;
            int allPMatch = StaticTypeCheckingSupport.allParametersAndArgumentsMatch(params, args);
            int firstParamDist = StaticTypeCheckingSupport.firstParametersAndArgumentsMatch(params, args);
            int n = lastArgMatch = StaticTypeCheckingSupport.isVargs(params) && firstParamDist >= 0 ? StaticTypeCheckingSupport.lastArgMatchesVarg(params, args) : -1;
            if (lastArgMatch >= 0) {
                lastArgMatch += StaticTypeCheckingSupport.getVarargsDistance(params);
            }
            dist = allPMatch >= 0 ? Math.max(allPMatch, lastArgMatch) : lastArgMatch;
        } else if (StaticTypeCheckingSupport.isVargs(params) && (dist = StaticTypeCheckingSupport.firstParametersAndArgumentsMatch(params, args)) >= 0) {
            dist += StaticTypeCheckingSupport.getVarargsDistance(params);
            if (params.length < args.length) {
                int excessArgumentsDistance = StaticTypeCheckingSupport.excessArgumentsMatchesVargsParameter(params, args);
                dist = excessArgumentsDistance < 0 ? -1 : (dist += excessArgumentsDistance);
            }
        }
        return dist;
    }

    private static int firstParametersAndArgumentsMatch(Parameter[] params, ClassNode[] safeArgs) {
        int dist = 0;
        if (params.length > 0) {
            Parameter[] firstParams = new Parameter[params.length - 1];
            System.arraycopy(params, 0, firstParams, 0, firstParams.length);
            dist = StaticTypeCheckingSupport.allParametersAndArgumentsMatch(firstParams, safeArgs);
        }
        return dist;
    }

    private static int getVarargsDistance(Parameter[] params) {
        return 256 - params.length;
    }

    private static int getClassDistance(ClassNode declaringClassForDistance, ClassNode actualReceiverForDistance) {
        if (actualReceiverForDistance.equals(declaringClassForDistance)) {
            return 0;
        }
        return StaticTypeCheckingSupport.getDistance(actualReceiverForDistance, declaringClassForDistance);
    }

    private static int getExtensionDistance(boolean isExtensionMethodNode) {
        return isExtensionMethodNode ? 0 : 1;
    }

    private static Parameter[] makeRawTypes(Parameter[] params) {
        Parameter[] newParam = new Parameter[params.length];
        for (int i = 0; i < params.length; ++i) {
            Parameter newP;
            Parameter oldP = params[i];
            newParam[i] = newP = new Parameter(StaticTypeCheckingSupport.makeRawType(oldP.getType()), oldP.getName());
        }
        return newParam;
    }

    private static ClassNode makeRawType(ClassNode receiver) {
        if (receiver.isArray()) {
            return StaticTypeCheckingSupport.makeRawType(receiver.getComponentType()).makeArray();
        }
        ClassNode raw = receiver.getPlainNodeReference();
        raw.setUsingGenerics(false);
        raw.setGenericsTypes(null);
        return raw;
    }

    private static Collection<MethodNode> removeCovariantsAndInterfaceEquivalents(Collection<MethodNode> collection) {
        if (collection.size() <= 1) {
            return collection;
        }
        LinkedList<MethodNode> toBeRemoved = new LinkedList<MethodNode>();
        LinkedList<MethodNode> list = new LinkedList<MethodNode>(new LinkedHashSet<MethodNode>(collection));
        for (int i = 0; i < list.size() - 1; ++i) {
            MethodNode one = (MethodNode)list.get(i);
            if (toBeRemoved.contains(one)) continue;
            for (int j = i + 1; j < list.size(); ++j) {
                MethodNode two = (MethodNode)list.get(j);
                if (toBeRemoved.contains(two) || one.getParameters().length != two.getParameters().length) continue;
                if (StaticTypeCheckingSupport.areOverloadMethodsInSameClass(one, two)) {
                    if (ParameterUtils.parametersEqual(one.getParameters(), two.getParameters())) {
                        StaticTypeCheckingSupport.removeMethodWithSuperReturnType(toBeRemoved, one, two);
                        continue;
                    }
                    StaticTypeCheckingSupport.removeSyntheticMethodIfOne(toBeRemoved, one, two);
                    continue;
                }
                if (!StaticTypeCheckingSupport.areEquivalentInterfaceMethods(one, two)) continue;
                StaticTypeCheckingSupport.removeMethodInSuperInterface(toBeRemoved, one, two);
            }
        }
        if (toBeRemoved.isEmpty()) {
            return list;
        }
        LinkedList<MethodNode> result = new LinkedList<MethodNode>(list);
        result.removeAll(toBeRemoved);
        return result;
    }

    private static void removeMethodInSuperInterface(List<MethodNode> toBeRemoved, MethodNode one, MethodNode two) {
        ClassNode twoDC;
        ClassNode oneDC = one.getDeclaringClass();
        if (oneDC.implementsInterface(twoDC = two.getDeclaringClass())) {
            toBeRemoved.add(two);
        } else {
            toBeRemoved.add(one);
        }
    }

    private static boolean areEquivalentInterfaceMethods(MethodNode one, MethodNode two) {
        return one.getName().equals(two.getName()) && one.getDeclaringClass().isInterface() && two.getDeclaringClass().isInterface() && ParameterUtils.parametersEqual(one.getParameters(), two.getParameters());
    }

    private static void removeSyntheticMethodIfOne(List<MethodNode> toBeRemoved, MethodNode one, MethodNode two) {
        if (one.isSynthetic() && !two.isSynthetic()) {
            toBeRemoved.add(one);
        } else if (two.isSynthetic() && !one.isSynthetic()) {
            toBeRemoved.add(two);
        }
    }

    private static void removeMethodWithSuperReturnType(List<MethodNode> toBeRemoved, MethodNode one, MethodNode two) {
        ClassNode twoRT;
        ClassNode oneRT = one.getReturnType();
        if (StaticTypeCheckingSupport.isCovariant(oneRT, twoRT = two.getReturnType())) {
            toBeRemoved.add(two);
        } else if (StaticTypeCheckingSupport.isCovariant(twoRT, oneRT)) {
            toBeRemoved.add(one);
        }
    }

    private static boolean isCovariant(ClassNode left, ClassNode right) {
        if (left.isArray() && right.isArray()) {
            return StaticTypeCheckingSupport.isCovariant(left.getComponentType(), right.getComponentType());
        }
        return left.isDerivedFrom(right) || left.implementsInterface(right);
    }

    private static boolean areOverloadMethodsInSameClass(MethodNode one, MethodNode two) {
        return one.getName().equals(two.getName()) && one.getDeclaringClass() == two.getDeclaringClass();
    }

    public static Parameter[] parameterizeArguments(ClassNode receiver, MethodNode m) {
        Map<String, GenericsType> genericFromReceiver = GenericsUtils.extractPlaceholders(receiver);
        Map<String, GenericsType> contextPlaceholders = StaticTypeCheckingSupport.extractGenericsParameterMapOfThis(m);
        Parameter[] methodParameters = m.getParameters();
        Parameter[] params = new Parameter[methodParameters.length];
        for (int i = 0; i < methodParameters.length; ++i) {
            Parameter methodParameter = methodParameters[i];
            ClassNode paramType = methodParameter.getType();
            params[i] = StaticTypeCheckingSupport.buildParameter(genericFromReceiver, contextPlaceholders, methodParameter, paramType);
        }
        return params;
    }

    private static Parameter buildParameter(Map<String, GenericsType> genericFromReceiver, Map<String, GenericsType> placeholdersFromContext, Parameter methodParameter, ClassNode paramType) {
        if (genericFromReceiver.isEmpty() && (placeholdersFromContext == null || placeholdersFromContext.isEmpty())) {
            return methodParameter;
        }
        if (paramType.isArray()) {
            ClassNode componentType = paramType.getComponentType();
            Parameter subMethodParameter = new Parameter(componentType, methodParameter.getName());
            Parameter component = StaticTypeCheckingSupport.buildParameter(genericFromReceiver, placeholdersFromContext, subMethodParameter, componentType);
            return new Parameter(component.getType().makeArray(), component.getName());
        }
        ClassNode resolved = StaticTypeCheckingSupport.resolveClassNodeGenerics(genericFromReceiver, placeholdersFromContext, paramType);
        return new Parameter(resolved, methodParameter.getName());
    }

    public static boolean isUsingGenericsOrIsArrayUsingGenerics(ClassNode cn) {
        if (cn.isArray()) {
            return StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(cn.getComponentType());
        }
        return cn.isUsingGenerics() && cn.getGenericsTypes() != null;
    }

    protected static GenericsType fullyResolve(GenericsType gt, Map<String, GenericsType> placeholders) {
        ClassNode[] upperBounds;
        GenericsType fromMap = placeholders.get(gt.getName());
        if (gt.isPlaceholder() && fromMap != null) {
            gt = fromMap;
        }
        ClassNode type = StaticTypeCheckingSupport.fullyResolveType(gt.getType(), placeholders);
        ClassNode lowerBound = gt.getLowerBound();
        if (lowerBound != null) {
            lowerBound = StaticTypeCheckingSupport.fullyResolveType(lowerBound, placeholders);
        }
        if ((upperBounds = gt.getUpperBounds()) != null) {
            ClassNode[] copy = new ClassNode[upperBounds.length];
            for (ClassNode upperBound : upperBounds) {
                copy[i] = StaticTypeCheckingSupport.fullyResolveType(upperBound, placeholders);
            }
            upperBounds = copy;
        }
        GenericsType genericsType = new GenericsType(type, upperBounds, lowerBound);
        genericsType.setWildcard(gt.isWildcard());
        return genericsType;
    }

    protected static ClassNode fullyResolveType(ClassNode type, Map<String, GenericsType> placeholders) {
        if (type.isUsingGenerics() && !type.isGenericsPlaceHolder()) {
            GenericsType[] gts = type.getGenericsTypes();
            if (gts != null) {
                GenericsType[] copy = new GenericsType[gts.length];
                for (int i = 0; i < gts.length; ++i) {
                    GenericsType genericsType = gts[i];
                    copy[i] = genericsType.isPlaceholder() && placeholders.containsKey(genericsType.getName()) ? placeholders.get(genericsType.getName()) : StaticTypeCheckingSupport.fullyResolve(genericsType, placeholders);
                }
                gts = copy;
            }
            ClassNode result = type.getPlainNodeReference();
            result.setGenericsTypes(gts);
            return result;
        }
        if (type.isUsingGenerics() && ClassHelper.OBJECT_TYPE.equals(type) && type.getGenericsTypes() != null) {
            GenericsType genericsType = placeholders.get(type.getGenericsTypes()[0].getName());
            if (genericsType != null) {
                return genericsType.getType();
            }
        } else if (type.isArray()) {
            return StaticTypeCheckingSupport.fullyResolveType(type.getComponentType(), placeholders).makeArray();
        }
        return type;
    }

    protected static boolean typeCheckMethodArgumentWithGenerics(ClassNode parameterType, ClassNode argumentType, boolean lastArg) {
        if (UNKNOWN_PARAMETER_TYPE == argumentType) {
            return !ClassHelper.isPrimitiveType(parameterType);
        }
        if (!StaticTypeCheckingSupport.isAssignableTo(argumentType, parameterType) && !lastArg) {
            return false;
        }
        if (!StaticTypeCheckingSupport.isAssignableTo(argumentType, parameterType) && lastArg) {
            if (parameterType.isArray()) {
                if (!StaticTypeCheckingSupport.isAssignableTo(argumentType, parameterType.getComponentType())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (parameterType.isUsingGenerics() && argumentType.isUsingGenerics()) {
            GenericsType gt = GenericsUtils.buildWildcardType(parameterType);
            if (!gt.isCompatibleWith(argumentType)) {
                boolean samCoercion;
                boolean bl = samCoercion = ClassHelper.isSAMType(parameterType) && argumentType.equals(ClassHelper.CLOSURE_TYPE);
                if (!samCoercion) {
                    return false;
                }
            }
        } else {
            if (parameterType.isArray() && argumentType.isArray()) {
                return StaticTypeCheckingSupport.typeCheckMethodArgumentWithGenerics(parameterType.getComponentType(), argumentType.getComponentType(), lastArg);
            }
            if (lastArg && parameterType.isArray()) {
                return StaticTypeCheckingSupport.typeCheckMethodArgumentWithGenerics(parameterType.getComponentType(), argumentType, lastArg);
            }
        }
        return true;
    }

    static void addMethodLevelDeclaredGenerics(MethodNode method, Map<String, GenericsType> resolvedPlaceholders) {
        ClassNode dummy = ClassHelper.OBJECT_TYPE.getPlainNodeReference();
        dummy.setGenericsTypes(method.getGenericsTypes());
        GenericsUtils.extractPlaceholders(dummy, resolvedPlaceholders);
    }

    protected static boolean typeCheckMethodsWithGenerics(ClassNode receiver, ClassNode[] arguments, MethodNode candidateMethod) {
        if (StaticTypeCheckingSupport.isUsingUncheckedGenerics(receiver)) {
            return true;
        }
        if (ClassHelper.CLASS_Type.equals(receiver) && receiver.isUsingGenerics() && !candidateMethod.getDeclaringClass().equals(receiver) && !(candidateMethod instanceof ExtensionMethodNode)) {
            return StaticTypeCheckingSupport.typeCheckMethodsWithGenerics(receiver.getGenericsTypes()[0].getType(), arguments, candidateMethod);
        }
        GenericsType[] genericsTypes = candidateMethod.getGenericsTypes();
        boolean methodUsesGenerics = genericsTypes != null && genericsTypes.length > 0;
        boolean isExtensionMethod = candidateMethod instanceof ExtensionMethodNode;
        if (isExtensionMethod && methodUsesGenerics) {
            ClassNode[] dgmArgs = new ClassNode[arguments.length + 1];
            dgmArgs[0] = receiver;
            System.arraycopy(arguments, 0, dgmArgs, 1, arguments.length);
            MethodNode extensionMethodNode = ((ExtensionMethodNode)candidateMethod).getExtensionMethodNode();
            return StaticTypeCheckingSupport.typeCheckMethodsWithGenerics(extensionMethodNode.getDeclaringClass(), dgmArgs, extensionMethodNode, true);
        }
        return StaticTypeCheckingSupport.typeCheckMethodsWithGenerics(receiver, arguments, candidateMethod, false);
    }

    private static boolean typeCheckMethodsWithGenerics(ClassNode receiver, ClassNode[] arguments, MethodNode candidateMethod, boolean isExtensionMethod) {
        boolean failure = false;
        boolean skipBecauseOfInnerClassNotReceiver = StaticTypeCheckingSupport.isOuterClassOf(receiver, candidateMethod.getDeclaringClass());
        Parameter[] parameters = candidateMethod.getParameters();
        Map<String, GenericsType> classGTs = skipBecauseOfInnerClassNotReceiver ? Collections.EMPTY_MAP : GenericsUtils.extractPlaceholders(receiver);
        if (parameters.length > arguments.length || parameters.length == 0) {
            return true;
        }
        HashMap<String, GenericsType> resolvedMethodGenerics = new HashMap<String, GenericsType>();
        if (!skipBecauseOfInnerClassNotReceiver) {
            StaticTypeCheckingSupport.addMethodLevelDeclaredGenerics(candidateMethod, resolvedMethodGenerics);
        }
        for (String key : resolvedMethodGenerics.keySet()) {
            classGTs.remove(key);
        }
        StaticTypeCheckingSupport.applyGenericsConnections(classGTs, resolvedMethodGenerics);
        if (!skipBecauseOfInnerClassNotReceiver) {
            failure |= StaticTypeCheckingSupport.inferenceCheck(Collections.EMPTY_SET, resolvedMethodGenerics, candidateMethod.getDeclaringClass(), receiver, false);
        }
        Set<String> fixedGenericsPlaceHolders = StaticTypeCheckingSupport.extractResolvedPlaceHolders(resolvedMethodGenerics);
        for (int i = 0; i < arguments.length; ++i) {
            int pindex = Math.min(i, parameters.length - 1);
            ClassNode wrappedArgument = arguments[i];
            ClassNode type = parameters[pindex].getOriginType();
            failure |= StaticTypeCheckingSupport.inferenceCheck(fixedGenericsPlaceHolders, resolvedMethodGenerics, type, wrappedArgument, i >= parameters.length - 1);
            if (!isExtensionMethod || i != 0) continue;
            fixedGenericsPlaceHolders = StaticTypeCheckingSupport.extractResolvedPlaceHolders(resolvedMethodGenerics);
        }
        return !failure;
    }

    private static boolean isOuterClassOf(ClassNode receiver, ClassNode type) {
        return !StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(receiver, type);
    }

    private static Set<String> extractResolvedPlaceHolders(Map<String, GenericsType> resolvedMethodGenerics) {
        if (resolvedMethodGenerics.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        HashSet<String> result = new HashSet<String>();
        for (Map.Entry<String, GenericsType> entry : resolvedMethodGenerics.entrySet()) {
            GenericsType value = entry.getValue();
            if (value.isPlaceholder()) continue;
            result.add(entry.getKey());
        }
        return result;
    }

    private static boolean inferenceCheck(Set<String> fixedGenericsPlaceHolders, Map<String, GenericsType> resolvedMethodGenerics, ClassNode type, ClassNode wrappedArgument, boolean lastArg) {
        HashMap<String, GenericsType> connections = new HashMap<String, GenericsType>();
        if (ClassHelper.isPrimitiveType(wrappedArgument)) {
            wrappedArgument = ClassHelper.getWrapper(wrappedArgument);
        }
        StaticTypeCheckingSupport.extractGenericsConnections(connections, wrappedArgument, type);
        boolean failure = !StaticTypeCheckingSupport.compatibleConnections(connections, resolvedMethodGenerics, fixedGenericsPlaceHolders);
        StaticTypeCheckingSupport.applyGenericsConnections(connections, resolvedMethodGenerics);
        StaticTypeCheckingSupport.addMissingEntries(connections, resolvedMethodGenerics);
        type = StaticTypeCheckingSupport.applyGenericsContext(resolvedMethodGenerics, type);
        return failure |= !StaticTypeCheckingSupport.typeCheckMethodArgumentWithGenerics(type, wrappedArgument, lastArg);
    }

    private static GenericsType buildWildcardType(GenericsType origin) {
        ClassNode lowerBound = origin.getType().getPlainNodeReference();
        if (StaticTypeCheckingSupport.hasNonTrivialBounds(origin)) {
            lowerBound.setGenericsTypes(new GenericsType[]{origin});
        }
        ClassNode base = ClassHelper.makeWithoutCaching("?");
        GenericsType gt = new GenericsType(base, null, lowerBound);
        gt.setWildcard(true);
        return gt;
    }

    private static boolean compatibleConnections(Map<String, GenericsType> connections, Map<String, GenericsType> resolvedMethodGenerics, Set<String> fixedGenericsPlaceHolders) {
        for (Map.Entry<String, GenericsType> entry : connections.entrySet()) {
            GenericsType connection;
            GenericsType resolved = resolvedMethodGenerics.get(entry.getKey());
            if (resolved == null || (connection = entry.getValue()).isPlaceholder() && !StaticTypeCheckingSupport.hasNonTrivialBounds(connection) || StaticTypeCheckingSupport.compatibleConnection(resolved, connection)) continue;
            if (!resolved.isPlaceholder() && !resolved.isWildcard() && !fixedGenericsPlaceHolders.contains(entry.getKey()) && StaticTypeCheckingSupport.compatibleConnection(connection, resolved)) {
                resolvedMethodGenerics.put(entry.getKey(), connection);
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean compatibleConnection(GenericsType resolved, GenericsType connection) {
        ClassNode compareNode;
        GenericsType gt = connection;
        if (!connection.isWildcard()) {
            gt = StaticTypeCheckingSupport.buildWildcardType(connection);
        }
        if (resolved.isPlaceholder() && resolved.getUpperBounds() != null && resolved.getUpperBounds().length == 1 && !resolved.getUpperBounds()[0].isGenericsPlaceHolder() && resolved.getUpperBounds()[0].getName().equals("java.lang.Object")) {
            return true;
        }
        if (StaticTypeCheckingSupport.hasNonTrivialBounds(resolved)) {
            compareNode = StaticTypeCheckingSupport.getCombinedBoundType(resolved);
            compareNode = compareNode.redirect().getPlainNodeReference();
        } else if (!resolved.isPlaceholder()) {
            compareNode = resolved.getType().getPlainNodeReference();
        } else {
            return true;
        }
        return gt.isCompatibleWith(compareNode);
    }

    private static void addMissingEntries(Map<String, GenericsType> connections, Map<String, GenericsType> resolved) {
        for (Map.Entry<String, GenericsType> entry : connections.entrySet()) {
            GenericsType gt;
            ClassNode cn;
            if (resolved.containsKey(entry.getKey()) || (cn = (gt = entry.getValue()).getType()).redirect() == UNKNOWN_PARAMETER_TYPE) continue;
            resolved.put(entry.getKey(), gt);
        }
    }

    public static ClassNode resolveClassNodeGenerics(Map<String, GenericsType> resolvedPlaceholders, Map<String, GenericsType> placeholdersFromContext, ClassNode currentType) {
        ClassNode target = currentType.redirect();
        resolvedPlaceholders = new HashMap<String, GenericsType>(resolvedPlaceholders);
        StaticTypeCheckingSupport.applyContextGenerics(resolvedPlaceholders, placeholdersFromContext);
        HashMap<String, GenericsType> connections = new HashMap<String, GenericsType>();
        StaticTypeCheckingSupport.extractGenericsConnections(connections, currentType, target);
        StaticTypeCheckingSupport.applyGenericsConnections(connections, resolvedPlaceholders);
        currentType = StaticTypeCheckingSupport.applyGenericsContext(resolvedPlaceholders, currentType);
        return currentType;
    }

    static void applyGenericsConnections(Map<String, GenericsType> connections, Map<String, GenericsType> resolvedPlaceholders) {
        if (connections == null) {
            return;
        }
        int count = 0;
        while (count < 10000) {
            ++count;
            boolean checkForMorePlaceHolders = false;
            for (Map.Entry<String, GenericsType> entry : resolvedPlaceholders.entrySet()) {
                ClassNode replacementType;
                GenericsType connectedType;
                boolean placeholderReplacement;
                String name = entry.getKey();
                GenericsType replacement = connections.get(name);
                if (replacement == null) {
                    GenericsType value = entry.getValue();
                    GenericsType newValue = StaticTypeCheckingSupport.applyGenericsContext(connections, value);
                    entry.setValue(newValue);
                    checkForMorePlaceHolders = checkForMorePlaceHolders || !StaticTypeCheckingSupport.equalIncludingGenerics(value, newValue);
                    continue;
                }
                GenericsType original = entry.getValue();
                if (!original.isWildcard() && !original.isPlaceholder() || (placeholderReplacement = replacement.isPlaceholder()) && replacement == (connectedType = resolvedPlaceholders.get(replacement.getName())) || !original.isCompatibleWith(replacementType = StaticTypeCheckingSupport.extractType(replacement))) continue;
                entry.setValue(replacement);
                if (!placeholderReplacement) continue;
                checkForMorePlaceHolders = checkForMorePlaceHolders || !StaticTypeCheckingSupport.equalIncludingGenerics(original, replacement);
            }
            if (checkForMorePlaceHolders) continue;
            break;
        }
        if (count >= 10000) {
            throw new GroovyBugError("unable to handle generics in " + resolvedPlaceholders + " with connections " + connections);
        }
    }

    private static ClassNode extractType(GenericsType gt) {
        if (!gt.isPlaceholder()) {
            return gt.getType();
        }
        ClassNode replacementType = ClassHelper.OBJECT_TYPE;
        if (gt.getType().getGenericsTypes() != null) {
            GenericsType realGt = gt.getType().getGenericsTypes()[0];
            if (realGt.getLowerBound() != null) {
                replacementType = realGt.getLowerBound();
            } else if (realGt.getUpperBounds() != null && realGt.getUpperBounds().length > 0) {
                replacementType = realGt.getUpperBounds()[0];
            }
        }
        return replacementType;
    }

    private static boolean equalIncludingGenerics(GenericsType orig, GenericsType copy) {
        if (orig == copy) {
            return true;
        }
        if (orig.isPlaceholder() != copy.isPlaceholder()) {
            return false;
        }
        if (orig.isWildcard() != copy.isWildcard()) {
            return false;
        }
        if (!StaticTypeCheckingSupport.equalIncludingGenerics(orig.getType(), copy.getType())) {
            return false;
        }
        ClassNode lower1 = orig.getLowerBound();
        ClassNode lower2 = copy.getLowerBound();
        if ((lower1 == null || lower2 == null) && lower1 != lower2) {
            return false;
        }
        if (lower1 == lower2) {
            return true;
        }
        if (!StaticTypeCheckingSupport.equalIncludingGenerics(lower1, lower2)) {
            return false;
        }
        ClassNode[] upper1 = orig.getUpperBounds();
        ClassNode[] upper2 = copy.getUpperBounds();
        if ((upper1 == null || upper2 == null) && upper1 != upper2) {
            return false;
        }
        if (upper1 == upper2) {
            return true;
        }
        if (upper1.length != upper2.length) {
            return false;
        }
        for (int i = 0; i < upper1.length; ++i) {
            if (StaticTypeCheckingSupport.equalIncludingGenerics(upper1[i], upper2[i])) continue;
            return false;
        }
        return true;
    }

    private static boolean equalIncludingGenerics(ClassNode orig, ClassNode copy) {
        GenericsType[] gt2;
        if (orig == copy) {
            return true;
        }
        if (orig.isGenericsPlaceHolder() != copy.isGenericsPlaceHolder()) {
            return false;
        }
        if (!orig.equals(copy)) {
            return false;
        }
        GenericsType[] gt1 = orig.getGenericsTypes();
        if (gt1 == null ^ (gt2 = orig.getGenericsTypes()) == null) {
            return false;
        }
        if (gt1 == gt2) {
            return true;
        }
        if (gt1.length != gt2.length) {
            return false;
        }
        for (int i = 0; i < gt1.length; ++i) {
            if (StaticTypeCheckingSupport.equalIncludingGenerics(gt1[i], gt2[i])) continue;
            return false;
        }
        return true;
    }

    static void extractGenericsConnections(Map<String, GenericsType> connections, ClassNode type, ClassNode target) {
        if (target == null || type == target || !StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(target)) {
            return;
        }
        if (type == null || type == UNKNOWN_PARAMETER_TYPE) {
            return;
        }
        if (type.isArray() && target.isArray()) {
            StaticTypeCheckingSupport.extractGenericsConnections(connections, type.getComponentType(), target.getComponentType());
        } else if (target.isGenericsPlaceHolder() || type.equals(target) || !StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, target)) {
            if (target.isGenericsPlaceHolder()) {
                connections.put(target.getGenericsTypes()[0].getName(), new GenericsType(type));
            } else {
                StaticTypeCheckingSupport.extractGenericsConnections(connections, type.getGenericsTypes(), target.getGenericsTypes());
            }
        } else {
            ClassNode superClass = GenericsUtils.getSuperClass(type, target);
            if (superClass != null) {
                ClassNode corrected = StaticTypeCheckingSupport.getCorrectedClassNode(type, superClass, true);
                StaticTypeCheckingSupport.extractGenericsConnections(connections, corrected, target);
            } else {
                throw new GroovyBugError("The type " + type + " seems not to normally extend " + target + ". Sorry, I cannot handle this.");
            }
        }
    }

    public static ClassNode getCorrectedClassNode(ClassNode type, ClassNode superClass, boolean handlingGenerics) {
        ClassNode corrected = handlingGenerics && StaticTypeCheckingSupport.missesGenericsTypes(type) ? superClass.getPlainNodeReference() : GenericsUtils.correctToGenericsSpecRecurse(GenericsUtils.createGenericsSpec(type), superClass);
        return corrected;
    }

    private static void extractGenericsConnections(Map<String, GenericsType> connections, GenericsType[] usage, GenericsType[] declaration) {
        if (usage == null || declaration == null || declaration.length == 0) {
            return;
        }
        if (usage.length != declaration.length) {
            return;
        }
        for (int i = 0; i < usage.length; ++i) {
            GenericsType ui = usage[i];
            GenericsType di = declaration[i];
            if (di.isPlaceholder()) {
                connections.put(di.getName(), ui);
                continue;
            }
            if (di.isWildcard()) {
                if (ui.isWildcard()) {
                    StaticTypeCheckingSupport.extractGenericsConnections(connections, ui.getLowerBound(), di.getLowerBound());
                    StaticTypeCheckingSupport.extractGenericsConnections(connections, ui.getUpperBounds(), di.getUpperBounds());
                    continue;
                }
                ClassNode cu = ui.getType();
                StaticTypeCheckingSupport.extractGenericsConnections(connections, cu, di.getLowerBound());
                ClassNode[] upperBounds = di.getUpperBounds();
                if (upperBounds == null) continue;
                for (ClassNode cn : upperBounds) {
                    StaticTypeCheckingSupport.extractGenericsConnections(connections, cu, cn);
                }
                continue;
            }
            StaticTypeCheckingSupport.extractGenericsConnections(connections, ui.getType(), di.getType());
        }
    }

    private static void extractGenericsConnections(Map<String, GenericsType> connections, ClassNode[] usage, ClassNode[] declaration) {
        if (usage == null || declaration == null || declaration.length == 0) {
            return;
        }
        for (int i = 0; i < usage.length; ++i) {
            ClassNode ui = usage[i];
            ClassNode di = declaration[i];
            if (di.isGenericsPlaceHolder()) {
                GenericsType gt = new GenericsType(di);
                gt.setPlaceholder(di.isGenericsPlaceHolder());
                connections.put(di.getGenericsTypes()[0].getName(), gt);
                continue;
            }
            if (!di.isUsingGenerics()) continue;
            StaticTypeCheckingSupport.extractGenericsConnections(connections, ui.getGenericsTypes(), di.getGenericsTypes());
        }
    }

    static GenericsType[] getGenericsWithoutArray(ClassNode type) {
        if (type.isArray()) {
            return StaticTypeCheckingSupport.getGenericsWithoutArray(type.getComponentType());
        }
        return type.getGenericsTypes();
    }

    static Map<String, GenericsType> applyGenericsContextToParameterClass(Map<String, GenericsType> spec, ClassNode parameterUsage) {
        GenericsType[] gts = parameterUsage.getGenericsTypes();
        if (gts == null) {
            return Collections.EMPTY_MAP;
        }
        GenericsType[] newGTs = StaticTypeCheckingSupport.applyGenericsContext(spec, gts);
        ClassNode newTarget = parameterUsage.redirect().getPlainNodeReference();
        newTarget.setGenericsTypes(newGTs);
        return GenericsUtils.extractPlaceholders(newTarget);
    }

    private static GenericsType[] applyGenericsContext(Map<String, GenericsType> spec, GenericsType[] gts) {
        if (gts == null) {
            return null;
        }
        GenericsType[] newGTs = new GenericsType[gts.length];
        for (int i = 0; i < gts.length; ++i) {
            GenericsType gt = gts[i];
            newGTs[i] = StaticTypeCheckingSupport.applyGenericsContext(spec, gt);
        }
        return newGTs;
    }

    private static GenericsType applyGenericsContext(Map<String, GenericsType> spec, GenericsType gt) {
        if (gt.isPlaceholder()) {
            String name = gt.getName();
            GenericsType specType = spec.get(name);
            if (specType != null) {
                return specType;
            }
            if (StaticTypeCheckingSupport.hasNonTrivialBounds(gt)) {
                GenericsType newGT = new GenericsType(gt.getType(), StaticTypeCheckingSupport.applyGenericsContext(spec, gt.getUpperBounds()), StaticTypeCheckingSupport.applyGenericsContext(spec, gt.getLowerBound()));
                newGT.setPlaceholder(true);
                return newGT;
            }
            return gt;
        }
        if (gt.isWildcard()) {
            GenericsType newGT = new GenericsType(gt.getType(), StaticTypeCheckingSupport.applyGenericsContext(spec, gt.getUpperBounds()), StaticTypeCheckingSupport.applyGenericsContext(spec, gt.getLowerBound()));
            newGT.setWildcard(true);
            return newGT;
        }
        ClassNode type = gt.getType();
        if (type.getGenericsTypes() == null) {
            return gt;
        }
        ClassNode newType = type.getPlainNodeReference();
        newType.setGenericsPlaceHolder(type.isGenericsPlaceHolder());
        newType.setGenericsTypes(StaticTypeCheckingSupport.applyGenericsContext(spec, type.getGenericsTypes()));
        GenericsType newGT = new GenericsType(newType);
        return newGT;
    }

    private static boolean hasNonTrivialBounds(GenericsType gt) {
        ClassNode[] upperBounds = gt.getUpperBounds();
        return gt.getLowerBound() != null || gt.isWildcard() || upperBounds != null && (upperBounds.length != 1 || upperBounds[0].isGenericsPlaceHolder() || !ClassHelper.OBJECT_TYPE.equals(upperBounds[0]));
    }

    private static ClassNode[] applyGenericsContext(Map<String, GenericsType> spec, ClassNode[] bounds) {
        if (bounds == null) {
            return null;
        }
        ClassNode[] newBounds = new ClassNode[bounds.length];
        for (int i = 0; i < bounds.length; ++i) {
            newBounds[i] = StaticTypeCheckingSupport.applyGenericsContext(spec, bounds[i]);
        }
        return newBounds;
    }

    static ClassNode applyGenericsContext(Map<String, GenericsType> spec, ClassNode bound) {
        if (bound == null) {
            return null;
        }
        if (bound.isArray()) {
            return StaticTypeCheckingSupport.applyGenericsContext(spec, bound.getComponentType()).makeArray();
        }
        if (!bound.isUsingGenerics()) {
            return bound;
        }
        ClassNode newBound = bound.getPlainNodeReference();
        newBound.setGenericsTypes(StaticTypeCheckingSupport.applyGenericsContext(spec, bound.getGenericsTypes()));
        if (bound.isGenericsPlaceHolder()) {
            GenericsType[] gt = newBound.getGenericsTypes();
            boolean hasBounds = StaticTypeCheckingSupport.hasNonTrivialBounds(gt[0]);
            if (hasBounds || !gt[0].isPlaceholder()) {
                return StaticTypeCheckingSupport.getCombinedBoundType(gt[0]);
            }
            String placeHolderName = newBound.getGenericsTypes()[0].getName();
            if (!placeHolderName.equals(newBound.getUnresolvedName())) {
                ClassNode clean = ClassHelper.make(placeHolderName);
                clean.setGenericsTypes(newBound.getGenericsTypes());
                clean.setRedirect(newBound);
                newBound = clean;
            }
            newBound.setGenericsPlaceHolder(true);
        }
        return newBound;
    }

    private static ClassNode getCombinedBoundType(GenericsType genericsType) {
        if (StaticTypeCheckingSupport.hasNonTrivialBounds(genericsType)) {
            if (genericsType.getLowerBound() != null) {
                return genericsType.getLowerBound();
            }
            if (genericsType.getUpperBounds() != null) {
                return genericsType.getUpperBounds()[0];
            }
        }
        return genericsType.getType();
    }

    private static void applyContextGenerics(Map<String, GenericsType> resolvedPlaceholders, Map<String, GenericsType> placeholdersFromContext) {
        if (placeholdersFromContext == null) {
            return;
        }
        for (Map.Entry<String, GenericsType> entry : resolvedPlaceholders.entrySet()) {
            String name;
            GenericsType outer;
            GenericsType gt = entry.getValue();
            if (!gt.isPlaceholder() || (outer = placeholdersFromContext.get(name = gt.getName())) == null) continue;
            entry.setValue(outer);
        }
    }

    private static Map<String, GenericsType> getGenericsParameterMapOfThis(ClassNode cn) {
        if (cn == null) {
            return null;
        }
        Map<String, GenericsType> map = null;
        if (cn.getEnclosingMethod() != null) {
            map = StaticTypeCheckingSupport.extractGenericsParameterMapOfThis(cn.getEnclosingMethod());
        } else if (cn.getOuterClass() != null) {
            map = StaticTypeCheckingSupport.getGenericsParameterMapOfThis(cn.getOuterClass());
        }
        map = StaticTypeCheckingSupport.mergeGenerics(map, cn.getGenericsTypes());
        return map;
    }

    static ClassNode boundUnboundedWildcards(ClassNode type) {
        if (type.isArray()) {
            return StaticTypeCheckingSupport.boundUnboundedWildcards(type.getComponentType()).makeArray();
        }
        ClassNode target = type.redirect();
        if (target == null || type == target || !StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(target)) {
            return type;
        }
        ClassNode newType = type.getPlainNodeReference();
        newType.setGenericsPlaceHolder(type.isGenericsPlaceHolder());
        newType.setGenericsTypes(StaticTypeCheckingSupport.boundUnboundedWildcards(type.getGenericsTypes(), target.getGenericsTypes()));
        return newType;
    }

    private static GenericsType[] boundUnboundedWildcards(GenericsType[] usage, GenericsType[] declaration) {
        GenericsType[] newGts = new GenericsType[usage.length];
        for (int i = 0; i < usage.length; ++i) {
            newGts[i] = StaticTypeCheckingSupport.boundUnboundedWildcard(usage[i], declaration[i]);
        }
        return newGts;
    }

    private static GenericsType boundUnboundedWildcard(GenericsType gt, GenericsType spec) {
        if (StaticTypeCheckingSupport.isUnboundedWildcard(gt)) {
            ClassNode base = ClassHelper.makeWithoutCaching("?");
            GenericsType newGt = new GenericsType(base, spec.getUpperBounds(), spec.getLowerBound());
            newGt.setWildcard(true);
            return newGt;
        }
        return gt;
    }

    private static boolean isUnboundedWildcard(GenericsType gt) {
        if (gt.isWildcard() && gt.getLowerBound() == null) {
            ClassNode[] upperBounds = gt.getUpperBounds();
            return upperBounds == null || upperBounds.length == 0 || upperBounds.length == 1 && ClassHelper.OBJECT_TYPE.equals(upperBounds[0]);
        }
        return false;
    }

    static Map<String, GenericsType> extractGenericsParameterMapOfThis(MethodNode mn) {
        if (mn == null) {
            return null;
        }
        Map<Object, Object> map = mn.isStatic() ? new HashMap() : StaticTypeCheckingSupport.getGenericsParameterMapOfThis(mn.getDeclaringClass());
        return StaticTypeCheckingSupport.mergeGenerics(map, mn.getGenericsTypes());
    }

    private static Map<String, GenericsType> mergeGenerics(Map<String, GenericsType> current, GenericsType[] newGenerics) {
        if (newGenerics == null || newGenerics.length == 0) {
            return current;
        }
        if (current == null) {
            current = new HashMap<String, GenericsType>();
        }
        for (GenericsType gt : newGenerics) {
            String name;
            if (!gt.isPlaceholder() || current.containsKey(name = gt.getName())) continue;
            current.put(name, gt);
        }
        return current;
    }

    public static boolean isGStringOrGStringStringLUB(ClassNode node) {
        return ClassHelper.GSTRING_TYPE.equals(node) || GSTRING_STRING_CLASSNODE.equals(node);
    }

    public static boolean isParameterizedWithGStringOrGStringString(ClassNode node) {
        GenericsType[] genericsTypes;
        if (node.isArray()) {
            return StaticTypeCheckingSupport.isParameterizedWithGStringOrGStringString(node.getComponentType());
        }
        if (node.isUsingGenerics() && (genericsTypes = node.getGenericsTypes()) != null) {
            for (GenericsType genericsType : genericsTypes) {
                if (!StaticTypeCheckingSupport.isGStringOrGStringStringLUB(genericsType.getType())) continue;
                return true;
            }
        }
        return node.getSuperClass() != null && StaticTypeCheckingSupport.isParameterizedWithGStringOrGStringString(node.getUnresolvedSuperClass());
    }

    public static boolean isParameterizedWithString(ClassNode node) {
        GenericsType[] genericsTypes;
        if (node.isArray()) {
            return StaticTypeCheckingSupport.isParameterizedWithString(node.getComponentType());
        }
        if (node.isUsingGenerics() && (genericsTypes = node.getGenericsTypes()) != null) {
            for (GenericsType genericsType : genericsTypes) {
                if (!ClassHelper.STRING_TYPE.equals(genericsType.getType())) continue;
                return true;
            }
        }
        return node.getSuperClass() != null && StaticTypeCheckingSupport.isParameterizedWithString(node.getUnresolvedSuperClass());
    }

    public static boolean missesGenericsTypes(ClassNode cn) {
        if (cn.isArray()) {
            return StaticTypeCheckingSupport.missesGenericsTypes(cn.getComponentType());
        }
        GenericsType[] cnTypes = cn.getGenericsTypes();
        GenericsType[] rnTypes = cn.redirect().getGenericsTypes();
        if (rnTypes != null && cnTypes == null) {
            return true;
        }
        if (cnTypes != null) {
            for (GenericsType genericsType : cnTypes) {
                if (!genericsType.isPlaceholder()) continue;
                return true;
            }
        }
        return false;
    }

    public static Object evaluateExpression(Expression expr, CompilerConfiguration config) {
        String className = "Expression$" + UUID.randomUUID().toString().replace('-', '$');
        ClassNode node = new ClassNode(className, 1, ClassHelper.OBJECT_TYPE);
        ReturnStatement code = new ReturnStatement(expr);
        node.addMethod(new MethodNode("eval", 9, ClassHelper.OBJECT_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, code));
        CompilerConfiguration copyConf = new CompilerConfiguration(config);
        CompilationUnit cu = new CompilationUnit(copyConf);
        cu.addClassNode(node);
        cu.compile(7);
        List classes = cu.getClasses();
        Class aClass = cu.getClassLoader().defineClass(className, ((GroovyClass)classes.get(0)).getBytes());
        try {
            return aClass.getMethod("eval", new Class[0]).invoke(null, new Object[0]);
        }
        catch (IllegalAccessException e) {
            throw new GroovyBugError(e);
        }
        catch (InvocationTargetException e) {
            throw new GroovyBugError(e);
        }
        catch (NoSuchMethodException e) {
            throw new GroovyBugError(e);
        }
    }

    public static Set<ClassNode> collectAllInterfaces(ClassNode node) {
        HashSet<ClassNode> result = new HashSet<ClassNode>();
        StaticTypeCheckingSupport.collectAllInterfaces(node, result);
        return result;
    }

    private static void collectAllInterfaces(ClassNode node, Set<ClassNode> out) {
        if (node == null) {
            return;
        }
        Set<ClassNode> allInterfaces = node.getAllInterfaces();
        out.addAll(allInterfaces);
        StaticTypeCheckingSupport.collectAllInterfaces(node.getSuperClass(), out);
    }

    public static boolean isClassClassNodeWrappingConcreteType(ClassNode classNode) {
        GenericsType[] genericsTypes = classNode.getGenericsTypes();
        return ClassHelper.CLASS_Type.equals(classNode) && classNode.isUsingGenerics() && genericsTypes != null && !genericsTypes[0].isPlaceholder() && !genericsTypes[0].isWildcard();
    }

    public static List<MethodNode> findSetters(ClassNode cn, String setterName, boolean voidOnly) {
        LinkedList<MethodNode> result = null;
        for (MethodNode method : cn.getDeclaredMethods(setterName)) {
            if (!setterName.equals(method.getName()) || voidOnly && ClassHelper.VOID_TYPE != method.getReturnType() || method.getParameters().length != 1) continue;
            if (result == null) {
                result = new LinkedList<MethodNode>();
            }
            result.add(method);
        }
        if (result == null) {
            ClassNode parent = cn.getSuperClass();
            if (parent != null) {
                return StaticTypeCheckingSupport.findSetters(parent, setterName, voidOnly);
            }
            return Collections.emptyList();
        }
        return result;
    }

    public static ClassNode isTraitSelf(VariableExpression vexp) {
        if ("$self".equals(vexp.getName())) {
            ClassNode type;
            Variable accessedVariable = vexp.getAccessedVariable();
            ClassNode classNode = type = accessedVariable != null ? accessedVariable.getType() : null;
            if (accessedVariable instanceof Parameter && Traits.isTrait(type)) {
                return type;
            }
        }
        return null;
    }

    private static class ExtensionMethodCache {
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private Map<String, List<MethodNode>> cachedMethods = null;
        private WeakReference<ClassLoader> origin = new WeakReference<Object>(null);

        private ExtensionMethodCache() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Map<String, List<MethodNode>> getExtensionMethods(ClassLoader loader) {
            this.lock.readLock().lock();
            if (loader != this.origin.get()) {
                this.lock.readLock().unlock();
                this.lock.writeLock().lock();
                try {
                    final LinkedList<ExtensionModule> modules = new LinkedList<ExtensionModule>();
                    ExtensionModuleScanner scanner = new ExtensionModuleScanner(new ExtensionModuleScanner.ExtensionModuleListener(){

                        @Override
                        public void onModule(ExtensionModule module) {
                            boolean skip = false;
                            for (ExtensionModule extensionModule : modules) {
                                if (!extensionModule.getName().equals(module.getName())) continue;
                                skip = true;
                                break;
                            }
                            if (!skip) {
                                modules.add(module);
                            }
                        }
                    }, loader);
                    scanner.scanClasspathModules();
                    this.cachedMethods = ExtensionMethodCache.getDGMMethods(modules);
                    this.origin = new WeakReference<ClassLoader>(loader);
                    this.lock.readLock().lock();
                }
                finally {
                    this.lock.writeLock().unlock();
                }
            }
            try {
                Map<String, List<MethodNode>> map = Collections.unmodifiableMap(this.cachedMethods);
                return map;
            }
            finally {
                this.lock.readLock().unlock();
            }
        }

        private static Map<String, List<MethodNode>> getDGMMethods(List<ExtensionModule> modules) {
            LinkedHashSet<Class> instanceExtClasses = new LinkedHashSet<Class>();
            LinkedHashSet<Class> staticExtClasses = new LinkedHashSet<Class>();
            for (ExtensionModule module : modules) {
                if (!(module instanceof MetaInfExtensionModule)) continue;
                MetaInfExtensionModule extensionModule = (MetaInfExtensionModule)module;
                instanceExtClasses.addAll(extensionModule.getInstanceMethodsExtensionClasses());
                staticExtClasses.addAll(extensionModule.getStaticMethodsExtensionClasses());
            }
            HashMap<String, List<MethodNode>> methods = new HashMap<String, List<MethodNode>>();
            Collections.addAll(instanceExtClasses, DefaultGroovyMethods.DGM_LIKE_CLASSES);
            Collections.addAll(instanceExtClasses, DefaultGroovyMethods.ADDITIONAL_CLASSES);
            staticExtClasses.add(DefaultGroovyStaticMethods.class);
            instanceExtClasses.add(ObjectArrayStaticTypesHelper.class);
            ExtensionMethodCache.scanClassesForDGMMethods(methods, staticExtClasses, true);
            ExtensionMethodCache.scanClassesForDGMMethods(methods, instanceExtClasses, false);
            return methods;
        }

        private static void scanClassesForDGMMethods(Map<String, List<MethodNode>> accumulator, Iterable<Class> allClasses, boolean isStatic) {
            for (Class dgmLikeClass : allClasses) {
                ClassNode cn = ClassHelper.makeWithoutCaching(dgmLikeClass, true);
                for (MethodNode metaMethod : cn.getMethods()) {
                    Parameter[] types = metaMethod.getParameters();
                    if (!metaMethod.isStatic() || !metaMethod.isPublic() || types.length <= 0 || !metaMethod.getAnnotations(Deprecated_TYPE).isEmpty()) continue;
                    Parameter[] parameters = new Parameter[types.length - 1];
                    System.arraycopy(types, 1, parameters, 0, parameters.length);
                    ExtensionMethodNode node = new ExtensionMethodNode(metaMethod, metaMethod.getName(), metaMethod.getModifiers(), metaMethod.getReturnType(), parameters, ClassNode.EMPTY_ARRAY, null, isStatic);
                    node.setGenericsTypes(metaMethod.getGenericsTypes());
                    ClassNode declaringClass = types[0].getType();
                    String declaringClassName = declaringClass.getName();
                    node.setDeclaringClass(declaringClass);
                    List<MethodNode> nodes = accumulator.get(declaringClassName);
                    if (nodes == null) {
                        nodes = new LinkedList<MethodNode>();
                        accumulator.put(declaringClassName, nodes);
                    }
                    nodes.add(node);
                }
            }
        }
    }

    private static class ObjectArrayStaticTypesHelper {
        private ObjectArrayStaticTypesHelper() {
        }

        public static <T> T getAt(T[] arr, int index) {
            return null;
        }

        public static <T, U extends T> void putAt(T[] arr, int index, U object) {
        }
    }
}

