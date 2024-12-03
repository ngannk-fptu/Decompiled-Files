/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import org.mozilla.javascript.AbstractEcmaObjectOperations;
import org.mozilla.javascript.Arguments;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ClassCache;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.DToA;
import org.mozilla.javascript.DefaultErrorReporter;
import org.mozilla.javascript.Delegator;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Evaluator;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.IteratorLikeIterable;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.LazilyLoadedCtor;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeArrayIterator;
import org.mozilla.javascript.NativeBigInt;
import org.mozilla.javascript.NativeBoolean;
import org.mozilla.javascript.NativeCall;
import org.mozilla.javascript.NativeCollectionIterator;
import org.mozilla.javascript.NativeDate;
import org.mozilla.javascript.NativeError;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeGlobal;
import org.mozilla.javascript.NativeIterator;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeJavaMap;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeMap;
import org.mozilla.javascript.NativeMath;
import org.mozilla.javascript.NativeNumber;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativePromise;
import org.mozilla.javascript.NativeScript;
import org.mozilla.javascript.NativeSet;
import org.mozilla.javascript.NativeString;
import org.mozilla.javascript.NativeStringIterator;
import org.mozilla.javascript.NativeSymbol;
import org.mozilla.javascript.NativeWeakMap;
import org.mozilla.javascript.NativeWeakSet;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.RefCallable;
import org.mozilla.javascript.RegExpProxy;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SpecialRef;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.SymbolScriptable;
import org.mozilla.javascript.TokenStream;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.v8dtoa.DoubleConversion;
import org.mozilla.javascript.v8dtoa.FastDtoa;
import org.mozilla.javascript.xml.XMLLib;
import org.mozilla.javascript.xml.XMLObject;

public class ScriptRuntime {
    public static final Class<?> BooleanClass = Kit.classOrNull("java.lang.Boolean");
    public static final Class<?> ByteClass = Kit.classOrNull("java.lang.Byte");
    public static final Class<?> CharacterClass = Kit.classOrNull("java.lang.Character");
    public static final Class<?> ClassClass = Kit.classOrNull("java.lang.Class");
    public static final Class<?> DoubleClass = Kit.classOrNull("java.lang.Double");
    public static final Class<?> FloatClass = Kit.classOrNull("java.lang.Float");
    public static final Class<?> IntegerClass = Kit.classOrNull("java.lang.Integer");
    public static final Class<?> LongClass = Kit.classOrNull("java.lang.Long");
    public static final Class<?> NumberClass = Kit.classOrNull("java.lang.Number");
    public static final Class<?> ObjectClass = Kit.classOrNull("java.lang.Object");
    public static final Class<?> ShortClass = Kit.classOrNull("java.lang.Short");
    public static final Class<?> StringClass = Kit.classOrNull("java.lang.String");
    public static final Class<?> DateClass = Kit.classOrNull("java.util.Date");
    public static final Class<?> BigIntegerClass = Kit.classOrNull("java.math.BigInteger");
    public static final Class<?> ContextClass = Kit.classOrNull("org.mozilla.javascript.Context");
    public static final Class<?> ContextFactoryClass = Kit.classOrNull("org.mozilla.javascript.ContextFactory");
    public static final Class<?> FunctionClass = Kit.classOrNull("org.mozilla.javascript.Function");
    public static final Class<?> ScriptableObjectClass = Kit.classOrNull("org.mozilla.javascript.ScriptableObject");
    public static final Class<Scriptable> ScriptableClass = Scriptable.class;
    private static final Object LIBRARY_SCOPE_KEY = "LIBRARY_SCOPE";
    public static final double NaN = Double.NaN;
    public static final Double NaNobj = Double.NaN;
    public static final double negativeZero = Double.longBitsToDouble(Long.MIN_VALUE);
    public static final Double zeroObj = 0.0;
    public static final Double negativeZeroObj = -0.0;
    private static final String DEFAULT_NS_TAG = "__default_namespace__";
    public static final int ENUMERATE_KEYS = 0;
    public static final int ENUMERATE_VALUES = 1;
    public static final int ENUMERATE_ARRAY = 2;
    public static final int ENUMERATE_KEYS_NO_ITERATOR = 3;
    public static final int ENUMERATE_VALUES_NO_ITERATOR = 4;
    public static final int ENUMERATE_ARRAY_NO_ITERATOR = 5;
    public static final int ENUMERATE_VALUES_IN_ORDER = 6;
    public static final MessageProvider messageProvider = new DefaultMessageProvider();
    public static final Object[] emptyArgs = new Object[0];
    public static final String[] emptyStrings = new String[0];

    protected ScriptRuntime() {
    }

    @Deprecated
    public static BaseFunction typeErrorThrower() {
        return ScriptRuntime.typeErrorThrower(Context.getCurrentContext());
    }

    public static BaseFunction typeErrorThrower(Context cx) {
        if (cx.typeErrorThrower == null) {
            BaseFunction thrower = new BaseFunction(){
                private static final long serialVersionUID = -5891740962154902286L;

                @Override
                public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    throw ScriptRuntime.typeErrorById("msg.op.not.allowed", new Object[0]);
                }

                @Override
                public int getLength() {
                    return 0;
                }
            };
            ScriptRuntime.setFunctionProtoAndParent(thrower, cx.topCallScope);
            thrower.preventExtensions();
            cx.typeErrorThrower = thrower;
        }
        return cx.typeErrorThrower;
    }

    public static boolean isRhinoRuntimeType(Class<?> cl) {
        if (cl.isPrimitive()) {
            return cl != Character.TYPE;
        }
        return cl == StringClass || cl == BooleanClass || NumberClass.isAssignableFrom(cl) || ScriptableClass.isAssignableFrom(cl);
    }

    public static ScriptableObject initSafeStandardObjects(Context cx, ScriptableObject scope, boolean sealed) {
        if (scope == null) {
            scope = new NativeObject();
        } else if (scope instanceof TopLevel) {
            ((TopLevel)scope).clearCache();
        }
        scope.associateValue(LIBRARY_SCOPE_KEY, scope);
        new ClassCache().associate(scope);
        BaseFunction.init(scope, sealed);
        NativeObject.init(scope, sealed);
        Scriptable objectProto = ScriptableObject.getObjectPrototype(scope);
        Scriptable functionProto = ScriptableObject.getClassPrototype(scope, "Function");
        functionProto.setPrototype(objectProto);
        if (scope.getPrototype() == null) {
            scope.setPrototype(objectProto);
        }
        NativeError.init(scope, sealed);
        NativeGlobal.init(cx, scope, sealed);
        NativeArray.init(scope, sealed);
        if (cx.getOptimizationLevel() > 0) {
            NativeArray.setMaximumInitialCapacity(200000);
        }
        NativeString.init(scope, sealed);
        NativeBoolean.init(scope, sealed);
        NativeNumber.init(scope, sealed);
        NativeDate.init(scope, sealed);
        NativeMath.init(scope, sealed);
        NativeJSON.init(scope, sealed);
        NativeWith.init(scope, sealed);
        NativeCall.init(scope, sealed);
        NativeScript.init(scope, sealed);
        NativeIterator.init(cx, scope, sealed);
        NativeArrayIterator.init(scope, sealed);
        NativeStringIterator.init(scope, sealed);
        NativeJavaObject.init(scope, sealed);
        NativeJavaMap.init(scope, sealed);
        boolean withXml = cx.hasFeature(6) && cx.getE4xImplementationFactory() != null;
        new LazilyLoadedCtor(scope, "RegExp", "org.mozilla.javascript.regexp.NativeRegExp", sealed, true);
        new LazilyLoadedCtor(scope, "Continuation", "org.mozilla.javascript.NativeContinuation", sealed, true);
        if (withXml) {
            String xmlImpl = cx.getE4xImplementationFactory().getImplementationClassName();
            new LazilyLoadedCtor(scope, "XML", xmlImpl, sealed, true);
            new LazilyLoadedCtor(scope, "XMLList", xmlImpl, sealed, true);
            new LazilyLoadedCtor(scope, "Namespace", xmlImpl, sealed, true);
            new LazilyLoadedCtor(scope, "QName", xmlImpl, sealed, true);
        }
        if (cx.getLanguageVersion() >= 180 && cx.hasFeature(14) || cx.getLanguageVersion() >= 200) {
            new LazilyLoadedCtor(scope, "ArrayBuffer", "org.mozilla.javascript.typedarrays.NativeArrayBuffer", sealed, true);
            new LazilyLoadedCtor(scope, "Int8Array", "org.mozilla.javascript.typedarrays.NativeInt8Array", sealed, true);
            new LazilyLoadedCtor(scope, "Uint8Array", "org.mozilla.javascript.typedarrays.NativeUint8Array", sealed, true);
            new LazilyLoadedCtor(scope, "Uint8ClampedArray", "org.mozilla.javascript.typedarrays.NativeUint8ClampedArray", sealed, true);
            new LazilyLoadedCtor(scope, "Int16Array", "org.mozilla.javascript.typedarrays.NativeInt16Array", sealed, true);
            new LazilyLoadedCtor(scope, "Uint16Array", "org.mozilla.javascript.typedarrays.NativeUint16Array", sealed, true);
            new LazilyLoadedCtor(scope, "Int32Array", "org.mozilla.javascript.typedarrays.NativeInt32Array", sealed, true);
            new LazilyLoadedCtor(scope, "Uint32Array", "org.mozilla.javascript.typedarrays.NativeUint32Array", sealed, true);
            new LazilyLoadedCtor(scope, "Float32Array", "org.mozilla.javascript.typedarrays.NativeFloat32Array", sealed, true);
            new LazilyLoadedCtor(scope, "Float64Array", "org.mozilla.javascript.typedarrays.NativeFloat64Array", sealed, true);
            new LazilyLoadedCtor(scope, "DataView", "org.mozilla.javascript.typedarrays.NativeDataView", sealed, true);
        }
        if (cx.getLanguageVersion() >= 200) {
            NativeSymbol.init(cx, scope, sealed);
            NativeCollectionIterator.init(scope, "Set Iterator", sealed);
            NativeCollectionIterator.init(scope, "Map Iterator", sealed);
            NativeMap.init(cx, scope, sealed);
            NativePromise.init(cx, scope, sealed);
            NativeSet.init(cx, scope, sealed);
            NativeWeakMap.init(scope, sealed);
            NativeWeakSet.init(scope, sealed);
            NativeBigInt.init(scope, sealed);
        }
        if (scope instanceof TopLevel) {
            ((TopLevel)scope).cacheBuiltins(scope, sealed);
        }
        return scope;
    }

    public static ScriptableObject initStandardObjects(Context cx, ScriptableObject scope, boolean sealed) {
        ScriptableObject s = ScriptRuntime.initSafeStandardObjects(cx, scope, sealed);
        new LazilyLoadedCtor(s, "Packages", "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
        new LazilyLoadedCtor(s, "getClass", "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
        new LazilyLoadedCtor(s, "JavaAdapter", "org.mozilla.javascript.JavaAdapter", sealed, true);
        new LazilyLoadedCtor(s, "JavaImporter", "org.mozilla.javascript.ImporterTopLevel", sealed, true);
        for (String packageName : ScriptRuntime.getTopPackageNames()) {
            new LazilyLoadedCtor(s, packageName, "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
        }
        return s;
    }

    static String[] getTopPackageNames() {
        String[] stringArray;
        if ("Dalvik".equals(System.getProperty("java.vm.name"))) {
            String[] stringArray2 = new String[7];
            stringArray2[0] = "java";
            stringArray2[1] = "javax";
            stringArray2[2] = "org";
            stringArray2[3] = "com";
            stringArray2[4] = "edu";
            stringArray2[5] = "net";
            stringArray = stringArray2;
            stringArray2[6] = "android";
        } else {
            String[] stringArray3 = new String[6];
            stringArray3[0] = "java";
            stringArray3[1] = "javax";
            stringArray3[2] = "org";
            stringArray3[3] = "com";
            stringArray3[4] = "edu";
            stringArray = stringArray3;
            stringArray3[5] = "net";
        }
        return stringArray;
    }

    public static ScriptableObject getLibraryScopeOrNull(Scriptable scope) {
        ScriptableObject libScope = (ScriptableObject)ScriptableObject.getTopScopeValue(scope, LIBRARY_SCOPE_KEY);
        return libScope;
    }

    public static boolean isJSLineTerminator(int c) {
        if ((c & 0xDFD0) != 0) {
            return false;
        }
        return c == 10 || c == 13 || c == 8232 || c == 8233;
    }

    public static boolean isJSWhitespaceOrLineTerminator(int c) {
        return ScriptRuntime.isStrWhiteSpaceChar(c) || ScriptRuntime.isJSLineTerminator(c);
    }

    static boolean isStrWhiteSpaceChar(int c) {
        switch (c) {
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 32: 
            case 160: 
            case 8232: 
            case 8233: 
            case 65279: {
                return true;
            }
        }
        return Character.getType(c) == 12;
    }

    public static Boolean wrapBoolean(boolean b) {
        return b;
    }

    public static Integer wrapInt(int i) {
        return i;
    }

    public static Number wrapNumber(double x) {
        if (Double.isNaN(x)) {
            return NaNobj;
        }
        return x;
    }

    public static boolean toBoolean(Object val) {
        block7: {
            do {
                if (val instanceof Boolean) {
                    return (Boolean)val;
                }
                if (val == null || Undefined.isUndefined(val)) {
                    return false;
                }
                if (val instanceof CharSequence) {
                    return ((CharSequence)val).length() != 0;
                }
                if (val instanceof BigInteger) {
                    return !((BigInteger)val).equals(BigInteger.ZERO);
                }
                if (val instanceof Number) {
                    double d = ((Number)val).doubleValue();
                    return !Double.isNaN(d) && d != 0.0;
                }
                if (!(val instanceof Scriptable)) break block7;
                if (val instanceof ScriptableObject && ((ScriptableObject)val).avoidObjectDetection()) {
                    return false;
                }
                if (!Context.getContext().isVersionECMA1()) continue;
                return true;
            } while (!((val = ((Scriptable)val).getDefaultValue(BooleanClass)) instanceof Scriptable) || ScriptRuntime.isSymbol(val));
            throw ScriptRuntime.errorWithClassName("msg.primitive.expected", val);
        }
        ScriptRuntime.warnAboutNonJSObject(val);
        return true;
    }

    public static double toNumber(Object val) {
        block9: {
            do {
                if (val instanceof BigInteger) {
                    throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
                }
                if (val instanceof Number) {
                    return ((Number)val).doubleValue();
                }
                if (val == null) {
                    return 0.0;
                }
                if (Undefined.isUndefined(val)) {
                    return Double.NaN;
                }
                if (val instanceof String) {
                    return ScriptRuntime.toNumber((String)val);
                }
                if (val instanceof CharSequence) {
                    return ScriptRuntime.toNumber(val.toString());
                }
                if (val instanceof Boolean) {
                    return (Boolean)val != false ? 1.0 : 0.0;
                }
                if (val instanceof Symbol) {
                    throw ScriptRuntime.typeErrorById("msg.not.a.number", new Object[0]);
                }
                if (!(val instanceof Scriptable)) break block9;
            } while (!((val = ((Scriptable)val).getDefaultValue(NumberClass)) instanceof Scriptable) || ScriptRuntime.isSymbol(val));
            throw ScriptRuntime.errorWithClassName("msg.primitive.expected", val);
        }
        ScriptRuntime.warnAboutNonJSObject(val);
        return Double.NaN;
    }

    public static double toNumber(Object[] args, int index) {
        return index < args.length ? ScriptRuntime.toNumber(args[index]) : Double.NaN;
    }

    static double stringPrefixToNumber(String s, int start, int radix) {
        return ScriptRuntime.stringToNumber(s, start, s.length() - 1, radix, true);
    }

    static double stringToNumber(String s, int start, int end, int radix) {
        return ScriptRuntime.stringToNumber(s, start, end, radix, false);
    }

    private static double stringToNumber(String source, int sourceStart, int sourceEnd, int radix, boolean isPrefix) {
        int end;
        char digitMax = '9';
        char lowerCaseBound = 'a';
        char upperCaseBound = 'A';
        if (radix < 10) {
            digitMax = (char)(48 + radix - 1);
        }
        if (radix > 10) {
            lowerCaseBound = (char)(97 + radix - 10);
            upperCaseBound = (char)(65 + radix - 10);
        }
        double sum = 0.0;
        for (end = sourceStart; end <= sourceEnd; ++end) {
            int newDigit;
            char c = source.charAt(end);
            if ('0' <= c && c <= digitMax) {
                newDigit = c - 48;
            } else if ('a' <= c && c < lowerCaseBound) {
                newDigit = c - 97 + 10;
            } else if ('A' <= c && c < upperCaseBound) {
                newDigit = c - 65 + 10;
            } else {
                if (isPrefix) break;
                return Double.NaN;
            }
            sum = sum * (double)radix + (double)newDigit;
        }
        if (sourceStart == end) {
            return Double.NaN;
        }
        if (sum > 9.007199254740991E15) {
            if (radix == 10) {
                try {
                    return Double.parseDouble(source.substring(sourceStart, end));
                }
                catch (NumberFormatException nfe) {
                    return Double.NaN;
                }
            }
            if (radix == 2 || radix == 4 || radix == 8 || radix == 16 || radix == 32) {
                int bitShiftInChar = 1;
                int digit = 0;
                boolean SKIP_LEADING_ZEROS = false;
                boolean FIRST_EXACT_53_BITS = true;
                int AFTER_BIT_53 = 2;
                int ZEROS_AFTER_54 = 3;
                int MIXED_AFTER_54 = 4;
                int state = 0;
                int exactBitsLimit = 53;
                double factor = 0.0;
                boolean bit53 = false;
                boolean bit54 = false;
                int pos = sourceStart;
                while (true) {
                    if (bitShiftInChar == 1) {
                        if (pos == end) break;
                        digit = 48 <= (digit = (int)source.charAt(pos++)) && digit <= 57 ? (digit -= 48) : (97 <= digit && digit <= 122 ? (digit -= 87) : (digit -= 55));
                        bitShiftInChar = radix;
                    }
                    boolean bit = (digit & (bitShiftInChar >>= 1)) != 0;
                    switch (state) {
                        case 0: {
                            if (!bit) break;
                            --exactBitsLimit;
                            sum = 1.0;
                            state = 1;
                            break;
                        }
                        case 1: {
                            sum *= 2.0;
                            if (bit) {
                                sum += 1.0;
                            }
                            if (--exactBitsLimit != 0) break;
                            bit53 = bit;
                            state = 2;
                            break;
                        }
                        case 2: {
                            bit54 = bit;
                            factor = 2.0;
                            state = 3;
                            break;
                        }
                        case 3: {
                            if (bit) {
                                state = 4;
                            }
                        }
                        case 4: {
                            factor *= 2.0;
                        }
                    }
                }
                switch (state) {
                    case 0: {
                        sum = 0.0;
                        break;
                    }
                    case 1: 
                    case 2: {
                        break;
                    }
                    case 3: {
                        if (bit54 & bit53) {
                            sum += 1.0;
                        }
                        sum *= factor;
                        break;
                    }
                    case 4: {
                        if (bit54) {
                            sum += 1.0;
                        }
                        sum *= factor;
                    }
                }
            }
        }
        return sum;
    }

    public static double toNumber(String s) {
        char radixC;
        boolean oldParsingMode;
        char endChar;
        char startChar;
        int len = s.length();
        int start = 0;
        while (true) {
            if (start == len) {
                return 0.0;
            }
            startChar = s.charAt(start);
            if (!ScriptRuntime.isStrWhiteSpaceChar(startChar)) break;
            ++start;
        }
        int end = len - 1;
        while (ScriptRuntime.isStrWhiteSpaceChar(endChar = s.charAt(end))) {
            --end;
        }
        Context cx = Context.getCurrentContext();
        boolean bl = oldParsingMode = cx == null || cx.getLanguageVersion() < 200;
        if (startChar == '0') {
            if (start + 2 <= end) {
                radixC = s.charAt(start + 1);
                int radix = -1;
                if (radixC == 'x' || radixC == 'X') {
                    radix = 16;
                } else if (!(oldParsingMode || radixC != 'o' && radixC != 'O')) {
                    radix = 8;
                } else if (!(oldParsingMode || radixC != 'b' && radixC != 'B')) {
                    radix = 2;
                }
                if (radix != -1) {
                    if (oldParsingMode) {
                        return ScriptRuntime.stringPrefixToNumber(s, start + 2, radix);
                    }
                    return ScriptRuntime.stringToNumber(s, start + 2, end, radix);
                }
            }
        } else if (!(!oldParsingMode || startChar != '+' && startChar != '-' || start + 3 > end || s.charAt(start + 1) != '0' || (radixC = s.charAt(start + 2)) != 'x' && radixC != 'X')) {
            double val = ScriptRuntime.stringPrefixToNumber(s, start + 3, 16);
            return startChar == '-' ? -val : val;
        }
        if (endChar == 'y') {
            if (startChar == '+' || startChar == '-') {
                ++start;
            }
            if (start + 7 == end && s.regionMatches(start, "Infinity", 0, 8)) {
                return startChar == '-' ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            return Double.NaN;
        }
        String sub = s.substring(start, end + 1);
        for (int i = sub.length() - 1; i >= 0; --i) {
            char c = sub.charAt(i);
            if ('0' <= c && c <= '9' || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') continue;
            return Double.NaN;
        }
        try {
            return Double.parseDouble(sub);
        }
        catch (NumberFormatException ex) {
            return Double.NaN;
        }
    }

    public static BigInteger toBigInt(Object val) {
        block13: {
            do {
                if (val instanceof BigInteger) {
                    return (BigInteger)val;
                }
                if (val instanceof BigDecimal) {
                    return ((BigDecimal)val).toBigInteger();
                }
                if (val instanceof Number) {
                    if (val instanceof Long) {
                        return BigInteger.valueOf((Long)val);
                    }
                    double d = ((Number)val).doubleValue();
                    if (Double.isNaN(d) || Double.isInfinite(d)) {
                        throw ScriptRuntime.rangeErrorById("msg.cant.convert.to.bigint.isnt.integer", ScriptRuntime.toString(val));
                    }
                    BigDecimal bd = new BigDecimal(d, MathContext.UNLIMITED);
                    try {
                        return bd.toBigIntegerExact();
                    }
                    catch (ArithmeticException e) {
                        throw ScriptRuntime.rangeErrorById("msg.cant.convert.to.bigint.isnt.integer", ScriptRuntime.toString(val));
                    }
                }
                if (val == null || Undefined.isUndefined(val)) {
                    throw ScriptRuntime.typeErrorById("msg.cant.convert.to.bigint", ScriptRuntime.toString(val));
                }
                if (val instanceof String) {
                    return ScriptRuntime.toBigInt((String)val);
                }
                if (val instanceof CharSequence) {
                    return ScriptRuntime.toBigInt(val.toString());
                }
                if (val instanceof Boolean) {
                    return (Boolean)val != false ? BigInteger.ONE : BigInteger.ZERO;
                }
                if (val instanceof Symbol) {
                    throw ScriptRuntime.typeErrorById("msg.cant.convert.to.bigint", ScriptRuntime.toString(val));
                }
                if (!(val instanceof Scriptable)) break block13;
            } while (!((val = ((Scriptable)val).getDefaultValue(BigIntegerClass)) instanceof Scriptable) || ScriptRuntime.isSymbol(val));
            throw ScriptRuntime.errorWithClassName("msg.primitive.expected", val);
        }
        ScriptRuntime.warnAboutNonJSObject(val);
        return BigInteger.ZERO;
    }

    public static BigInteger toBigInt(String s) {
        char startChar;
        int len = s.length();
        int start = 0;
        while (true) {
            if (start == len) {
                return BigInteger.ZERO;
            }
            startChar = s.charAt(start);
            if (!ScriptRuntime.isStrWhiteSpaceChar(startChar)) break;
            ++start;
        }
        int end = len - 1;
        while (ScriptRuntime.isStrWhiteSpaceChar(s.charAt(end))) {
            --end;
        }
        if (startChar == '0' && start + 2 <= end) {
            char radixC = s.charAt(start + 1);
            int radix = -1;
            if (radixC == 'x' || radixC == 'X') {
                radix = 16;
            } else if (radixC == 'o' || radixC == 'O') {
                radix = 8;
            } else if (radixC == 'b' || radixC == 'B') {
                radix = 2;
            }
            if (radix != -1) {
                try {
                    return new BigInteger(s.substring(start + 2, end + 1), radix);
                }
                catch (NumberFormatException ex) {
                    throw ScriptRuntime.syntaxErrorById("msg.bigint.bad.form", new Object[0]);
                }
            }
        }
        String sub = s.substring(start, end + 1);
        for (int i = sub.length() - 1; i >= 0; --i) {
            char c = sub.charAt(i);
            if (i == 0 && (c == '+' || c == '-') || '0' <= c && c <= '9') continue;
            throw ScriptRuntime.syntaxErrorById("msg.bigint.bad.form", new Object[0]);
        }
        try {
            return new BigInteger(sub);
        }
        catch (NumberFormatException ex) {
            throw ScriptRuntime.syntaxErrorById("msg.bigint.bad.form", new Object[0]);
        }
    }

    public static Number toNumeric(Object val) {
        if (val instanceof Number) {
            return (Number)val;
        }
        return ScriptRuntime.toNumber(val);
    }

    public static int toIndex(Object val) {
        if (Undefined.isUndefined(val)) {
            return 0;
        }
        double integerIndex = ScriptRuntime.toInteger(val);
        if (integerIndex < 0.0) {
            throw ScriptRuntime.rangeError("index out of range");
        }
        double index = Math.min(integerIndex, 9.007199254740991E15);
        if (integerIndex != index) {
            throw ScriptRuntime.rangeError("index out of range");
        }
        return (int)index;
    }

    public static Object[] padArguments(Object[] args, int count) {
        if (count < args.length) {
            return args;
        }
        Object[] result = new Object[count];
        System.arraycopy(args, 0, result, 0, args.length);
        if (args.length < count) {
            Arrays.fill(result, args.length, count, Undefined.instance);
        }
        return result;
    }

    public static String escapeString(String s) {
        return ScriptRuntime.escapeString(s, '\"');
    }

    public static String escapeString(String s, char escapeQuote) {
        if (escapeQuote != '\"' && escapeQuote != '\'') {
            Kit.codeBug();
        }
        StringBuilder sb = null;
        int L = s.length();
        for (int i = 0; i != L; ++i) {
            int hexSize;
            char c = s.charAt(i);
            if (' ' <= c && c <= '~' && c != escapeQuote && c != '\\') {
                if (sb == null) continue;
                sb.append(c);
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder(L + 3);
                sb.append(s);
                sb.setLength(i);
            }
            int escape = -1;
            switch (c) {
                case '\b': {
                    escape = 98;
                    break;
                }
                case '\f': {
                    escape = 102;
                    break;
                }
                case '\n': {
                    escape = 110;
                    break;
                }
                case '\r': {
                    escape = 114;
                    break;
                }
                case '\t': {
                    escape = 116;
                    break;
                }
                case '\u000b': {
                    escape = 118;
                    break;
                }
                case ' ': {
                    escape = 32;
                    break;
                }
                case '\\': {
                    escape = 92;
                }
            }
            if (escape >= 0) {
                sb.append('\\');
                sb.append((char)escape);
                continue;
            }
            if (c == escapeQuote) {
                sb.append('\\');
                sb.append(escapeQuote);
                continue;
            }
            if (c < '\u0100') {
                sb.append("\\x");
                hexSize = 2;
            } else {
                sb.append("\\u");
                hexSize = 4;
            }
            for (int shift = (hexSize - 1) * 4; shift >= 0; shift -= 4) {
                int digit = 0xF & c >> shift;
                int hc = digit < 10 ? 48 + digit : 87 + digit;
                sb.append((char)hc);
            }
        }
        return sb == null ? s : sb.toString();
    }

    static boolean isValidIdentifierName(String s, Context cx, boolean isStrict) {
        int L = s.length();
        if (L == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i != L; ++i) {
            if (Character.isJavaIdentifierPart(s.charAt(i))) continue;
            return false;
        }
        return !TokenStream.isKeyword(s, cx.getLanguageVersion(), isStrict);
    }

    public static CharSequence toCharSequence(Object val) {
        if (val instanceof NativeString) {
            return ((NativeString)val).toCharSequence();
        }
        return val instanceof CharSequence ? (CharSequence)val : ScriptRuntime.toString(val);
    }

    public static String toString(Object val) {
        block8: {
            do {
                if (val == null) {
                    return "null";
                }
                if (Undefined.isUndefined(val)) {
                    return "undefined";
                }
                if (val instanceof String) {
                    return (String)val;
                }
                if (val instanceof CharSequence) {
                    return val.toString();
                }
                if (val instanceof BigInteger) {
                    return val.toString();
                }
                if (val instanceof Number) {
                    return ScriptRuntime.numberToString(((Number)val).doubleValue(), 10);
                }
                if (val instanceof Symbol) {
                    throw ScriptRuntime.typeErrorById("msg.not.a.string", new Object[0]);
                }
                if (!(val instanceof Scriptable)) break block8;
            } while (!((val = ((Scriptable)val).getDefaultValue(StringClass)) instanceof Scriptable) || ScriptRuntime.isSymbol(val));
            throw ScriptRuntime.errorWithClassName("msg.primitive.expected", val);
        }
        return val.toString();
    }

    static String defaultObjectToString(Scriptable obj) {
        if (obj == null) {
            return "[object Null]";
        }
        if (Undefined.isUndefined(obj)) {
            return "[object Undefined]";
        }
        return "[object " + obj.getClassName() + ']';
    }

    public static String toString(Object[] args, int index) {
        return index < args.length ? ScriptRuntime.toString(args[index]) : "undefined";
    }

    public static String toString(double val) {
        return ScriptRuntime.numberToString(val, 10);
    }

    public static String numberToString(double d, int base) {
        if (base < 2 || base > 36) {
            throw Context.reportRuntimeErrorById("msg.bad.radix", Integer.toString(base));
        }
        if (Double.isNaN(d)) {
            return "NaN";
        }
        if (d == Double.POSITIVE_INFINITY) {
            return "Infinity";
        }
        if (d == Double.NEGATIVE_INFINITY) {
            return "-Infinity";
        }
        if (d == 0.0) {
            return "0";
        }
        if (base != 10) {
            return DToA.JS_dtobasestr(base, d);
        }
        String result = FastDtoa.numberToString(d);
        if (result != null) {
            return result;
        }
        StringBuilder buffer = new StringBuilder();
        DToA.JS_dtostr(buffer, 0, 0, d);
        return buffer.toString();
    }

    public static String bigIntToString(BigInteger n, int base) {
        if (base < 2 || base > 36) {
            throw ScriptRuntime.rangeErrorById("msg.bad.radix", Integer.toString(base));
        }
        return n.toString(base);
    }

    static String uneval(Context cx, Scriptable scope, Object value) {
        if (value == null) {
            return "null";
        }
        if (Undefined.isUndefined(value)) {
            return "undefined";
        }
        if (value instanceof CharSequence) {
            String escaped = ScriptRuntime.escapeString(value.toString());
            StringBuilder sb = new StringBuilder(escaped.length() + 2);
            sb.append('\"');
            sb.append(escaped);
            sb.append('\"');
            return sb.toString();
        }
        if (value instanceof Number) {
            double d = ((Number)value).doubleValue();
            if (d == 0.0 && 1.0 / d < 0.0) {
                return "-0";
            }
            return ScriptRuntime.toString(d);
        }
        if (value instanceof Boolean) {
            return ScriptRuntime.toString(value);
        }
        if (value instanceof Scriptable) {
            Object v;
            Scriptable obj = (Scriptable)value;
            if (ScriptableObject.hasProperty(obj, "toSource") && (v = ScriptableObject.getProperty(obj, "toSource")) instanceof Function) {
                Function f = (Function)v;
                return ScriptRuntime.toString(f.call(cx, scope, obj, emptyArgs));
            }
            return ScriptRuntime.toString(value);
        }
        ScriptRuntime.warnAboutNonJSObject(value);
        return value.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String defaultObjectToSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        boolean iterating;
        boolean toplevel;
        if (cx.iterating == null) {
            toplevel = true;
            iterating = false;
            cx.iterating = new ObjToIntMap(31);
        } else {
            toplevel = false;
            iterating = cx.iterating.has(thisObj);
        }
        StringBuilder result = new StringBuilder(128);
        if (toplevel) {
            result.append("(");
        }
        result.append('{');
        try {
            if (!iterating) {
                cx.iterating.intern(thisObj);
                Object[] ids = thisObj.getIds();
                for (int i = 0; i < ids.length; ++i) {
                    Object value;
                    Object id = ids[i];
                    if (id instanceof Integer) {
                        int intId = (Integer)id;
                        value = thisObj.get(intId, thisObj);
                        if (value == Scriptable.NOT_FOUND) continue;
                        if (i > 0) {
                            result.append(", ");
                        }
                        result.append(intId);
                    } else {
                        String strId = (String)id;
                        value = thisObj.get(strId, thisObj);
                        if (value == Scriptable.NOT_FOUND) continue;
                        if (i > 0) {
                            result.append(", ");
                        }
                        if (ScriptRuntime.isValidIdentifierName(strId, cx, cx.isStrictMode())) {
                            result.append(strId);
                        } else {
                            result.append('\'');
                            result.append(ScriptRuntime.escapeString(strId, '\''));
                            result.append('\'');
                        }
                    }
                    result.append(':');
                    result.append(ScriptRuntime.uneval(cx, scope, value));
                }
            }
        }
        finally {
            if (toplevel) {
                cx.iterating = null;
            }
        }
        result.append('}');
        if (toplevel) {
            result.append(')');
        }
        return result.toString();
    }

    public static Scriptable toObject(Scriptable scope, Object val) {
        if (val instanceof Scriptable) {
            return (Scriptable)val;
        }
        return ScriptRuntime.toObject(Context.getContext(), scope, val);
    }

    @Deprecated
    public static Scriptable toObjectOrNull(Context cx, Object obj) {
        if (obj instanceof Scriptable) {
            return (Scriptable)obj;
        }
        if (obj != null && !Undefined.isUndefined(obj)) {
            return ScriptRuntime.toObject(cx, ScriptRuntime.getTopCallScope(cx), obj);
        }
        return null;
    }

    public static Scriptable toObjectOrNull(Context cx, Object obj, Scriptable scope) {
        if (obj instanceof Scriptable) {
            return (Scriptable)obj;
        }
        if (obj != null && !Undefined.isUndefined(obj)) {
            return ScriptRuntime.toObject(cx, scope, obj);
        }
        return null;
    }

    @Deprecated
    public static Scriptable toObject(Scriptable scope, Object val, Class<?> staticClass) {
        if (val instanceof Scriptable) {
            return (Scriptable)val;
        }
        return ScriptRuntime.toObject(Context.getContext(), scope, val);
    }

    public static Scriptable toObject(Context cx, Scriptable scope, Object val) {
        if (val == null) {
            throw ScriptRuntime.typeErrorById("msg.null.to.object", new Object[0]);
        }
        if (Undefined.isUndefined(val)) {
            throw ScriptRuntime.typeErrorById("msg.undef.to.object", new Object[0]);
        }
        if (ScriptRuntime.isSymbol(val)) {
            NativeSymbol result = new NativeSymbol((NativeSymbol)val);
            ScriptRuntime.setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.Symbol);
            return result;
        }
        if (val instanceof Scriptable) {
            return (Scriptable)val;
        }
        if (val instanceof CharSequence) {
            NativeString result = new NativeString((CharSequence)val);
            ScriptRuntime.setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.String);
            return result;
        }
        if (cx.getLanguageVersion() >= 200 && val instanceof BigInteger) {
            NativeBigInt result = new NativeBigInt((BigInteger)val);
            ScriptRuntime.setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.BigInt);
            return result;
        }
        if (val instanceof Number) {
            NativeNumber result = new NativeNumber(((Number)val).doubleValue());
            ScriptRuntime.setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.Number);
            return result;
        }
        if (val instanceof Boolean) {
            NativeBoolean result = new NativeBoolean((Boolean)val);
            ScriptRuntime.setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.Boolean);
            return result;
        }
        Object wrapped = cx.getWrapFactory().wrap(cx, scope, val, null);
        if (wrapped instanceof Scriptable) {
            return (Scriptable)wrapped;
        }
        throw ScriptRuntime.errorWithClassName("msg.invalid.type", val);
    }

    @Deprecated
    public static Scriptable toObject(Context cx, Scriptable scope, Object val, Class<?> staticClass) {
        return ScriptRuntime.toObject(cx, scope, val);
    }

    @Deprecated
    public static Object call(Context cx, Object fun, Object thisArg, Object[] args, Scriptable scope) {
        if (!(fun instanceof Function)) {
            throw ScriptRuntime.notFunctionError(ScriptRuntime.toString(fun));
        }
        Function function = (Function)fun;
        Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, thisArg, scope);
        if (thisObj == null) {
            throw ScriptRuntime.undefCallError(null, "function");
        }
        return function.call(cx, scope, thisObj, args);
    }

    public static Scriptable newObject(Context cx, Scriptable scope, String constructorName, Object[] args) {
        scope = ScriptableObject.getTopLevelScope(scope);
        Function ctor = ScriptRuntime.getExistingCtor(cx, scope, constructorName);
        if (args == null) {
            args = emptyArgs;
        }
        return ctor.construct(cx, scope, args);
    }

    public static Scriptable newBuiltinObject(Context cx, Scriptable scope, TopLevel.Builtins type, Object[] args) {
        scope = ScriptableObject.getTopLevelScope(scope);
        Function ctor = TopLevel.getBuiltinCtor(cx, scope, type);
        if (args == null) {
            args = emptyArgs;
        }
        return ctor.construct(cx, scope, args);
    }

    static Scriptable newNativeError(Context cx, Scriptable scope, TopLevel.NativeErrors type, Object[] args) {
        scope = ScriptableObject.getTopLevelScope(scope);
        Function ctor = TopLevel.getNativeErrorCtor(cx, scope, type);
        if (args == null) {
            args = emptyArgs;
        }
        return ctor.construct(cx, scope, args);
    }

    public static double toInteger(Object val) {
        return ScriptRuntime.toInteger(ScriptRuntime.toNumber(val));
    }

    public static double toInteger(double d) {
        if (Double.isNaN(d)) {
            return 0.0;
        }
        if (d == 0.0 || Double.isInfinite(d)) {
            return d;
        }
        if (d > 0.0) {
            return Math.floor(d);
        }
        return Math.ceil(d);
    }

    public static double toInteger(Object[] args, int index) {
        return index < args.length ? ScriptRuntime.toInteger(args[index]) : 0.0;
    }

    public static long toLength(Object[] args, int index) {
        double len = ScriptRuntime.toInteger(args, index);
        if (len <= 0.0) {
            return 0L;
        }
        return (long)Math.min(len, 9.007199254740991E15);
    }

    public static int toInt32(Object val) {
        if (val instanceof Integer) {
            return (Integer)val;
        }
        return ScriptRuntime.toInt32(ScriptRuntime.toNumber(val));
    }

    public static int toInt32(Object[] args, int index) {
        return index < args.length ? ScriptRuntime.toInt32(args[index]) : 0;
    }

    public static int toInt32(double d) {
        return DoubleConversion.doubleToInt32(d);
    }

    public static long toUint32(double d) {
        return (long)DoubleConversion.doubleToInt32(d) & 0xFFFFFFFFL;
    }

    public static long toUint32(Object val) {
        return ScriptRuntime.toUint32(ScriptRuntime.toNumber(val));
    }

    public static char toUint16(Object val) {
        double d = ScriptRuntime.toNumber(val);
        return (char)DoubleConversion.doubleToInt32(d);
    }

    public static Object setDefaultNamespace(Object namespace, Context cx) {
        Scriptable scope = cx.currentActivationCall;
        if (scope == null) {
            scope = ScriptRuntime.getTopCallScope(cx);
        }
        XMLLib xmlLib = ScriptRuntime.currentXMLLib(cx);
        Object ns = xmlLib.toDefaultXmlNamespace(cx, namespace);
        if (!scope.has(DEFAULT_NS_TAG, scope)) {
            ScriptableObject.defineProperty(scope, DEFAULT_NS_TAG, ns, 6);
        } else {
            scope.put(DEFAULT_NS_TAG, scope, ns);
        }
        return Undefined.instance;
    }

    public static Object searchDefaultNamespace(Context cx) {
        Object nsObject;
        Scriptable scope = cx.currentActivationCall;
        if (scope == null) {
            scope = ScriptRuntime.getTopCallScope(cx);
        }
        while (true) {
            Scriptable parent;
            if ((parent = scope.getParentScope()) == null) {
                nsObject = ScriptableObject.getProperty(scope, DEFAULT_NS_TAG);
                if (nsObject != Scriptable.NOT_FOUND) break;
                return null;
            }
            nsObject = scope.get(DEFAULT_NS_TAG, scope);
            if (nsObject != Scriptable.NOT_FOUND) break;
            scope = parent;
        }
        return nsObject;
    }

    public static Object getTopLevelProp(Scriptable scope, String id) {
        scope = ScriptableObject.getTopLevelScope(scope);
        return ScriptableObject.getProperty(scope, id);
    }

    static Function getExistingCtor(Context cx, Scriptable scope, String constructorName) {
        Object ctorVal = ScriptableObject.getProperty(scope, constructorName);
        if (ctorVal instanceof Function) {
            return (Function)ctorVal;
        }
        if (ctorVal == Scriptable.NOT_FOUND) {
            throw Context.reportRuntimeErrorById("msg.ctor.not.found", constructorName);
        }
        throw Context.reportRuntimeErrorById("msg.not.ctor", constructorName);
    }

    public static long indexFromString(String str) {
        int MAX_VALUE_LENGTH = 10;
        int len = str.length();
        if (len > 0) {
            int i = 0;
            boolean negate = false;
            int c = str.charAt(0);
            if (c == 45 && len > 1) {
                c = str.charAt(1);
                if (c == 48) {
                    return -1L;
                }
                i = 1;
                negate = true;
            }
            if (0 <= (c -= 48) && c <= 9 && len <= (negate ? 11 : 10)) {
                int index = -c;
                int oldIndex = 0;
                ++i;
                if (index != 0) {
                    while (i != len && 0 <= (c = str.charAt(i) - 48) && c <= 9) {
                        oldIndex = index;
                        index = 10 * index - c;
                        ++i;
                    }
                }
                if (i == len && (oldIndex > -214748364 || oldIndex == -214748364 && c <= (negate ? 8 : 7))) {
                    return 0xFFFFFFFFL & (long)(negate ? index : -index);
                }
            }
        }
        return -1L;
    }

    public static long testUint32String(String str) {
        int MAX_VALUE_LENGTH = 10;
        int len = str.length();
        if (1 <= len && len <= 10) {
            int c = str.charAt(0);
            if ((c -= 48) == 0) {
                return len == 1 ? 0L : -1L;
            }
            if (1 <= c && c <= 9) {
                long v = c;
                for (int i = 1; i != len; ++i) {
                    c = str.charAt(i) - 48;
                    if (0 > c || c > 9) {
                        return -1L;
                    }
                    v = 10L * v + (long)c;
                }
                if (v >>> 32 == 0L) {
                    return v;
                }
            }
        }
        return -1L;
    }

    static Object getIndexObject(String s) {
        long indexTest = ScriptRuntime.indexFromString(s);
        if (indexTest >= 0L) {
            return (int)indexTest;
        }
        return s;
    }

    static Object getIndexObject(double d) {
        int i = (int)d;
        if ((double)i == d) {
            return i;
        }
        return ScriptRuntime.toString(d);
    }

    static StringIdOrIndex toStringIdOrIndex(Context cx, Object id) {
        if (id instanceof Number) {
            double d = ((Number)id).doubleValue();
            int index = (int)d;
            if ((double)index == d) {
                return new StringIdOrIndex(index);
            }
            return new StringIdOrIndex(ScriptRuntime.toString(id));
        }
        String s = id instanceof String ? (String)id : ScriptRuntime.toString(id);
        long indexTest = ScriptRuntime.indexFromString(s);
        if (indexTest >= 0L) {
            return new StringIdOrIndex((int)indexTest);
        }
        return new StringIdOrIndex(s);
    }

    @Deprecated
    public static Object getObjectElem(Object obj, Object elem, Context cx) {
        return ScriptRuntime.getObjectElem(obj, elem, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object getObjectElem(Object obj, Object elem, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefReadError(obj, elem);
        }
        return ScriptRuntime.getObjectElem(sobj, elem, cx);
    }

    public static Object getObjectElem(Scriptable obj, Object elem, Context cx) {
        Object result;
        if (obj instanceof XMLObject) {
            result = ((XMLObject)obj).get(cx, elem);
        } else if (ScriptRuntime.isSymbol(elem)) {
            result = ScriptableObject.getProperty(obj, (Symbol)elem);
        } else {
            StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, elem);
            if (s.stringId == null) {
                int index = s.index;
                result = ScriptableObject.getProperty(obj, index);
            } else {
                result = ScriptableObject.getProperty(obj, s.stringId);
            }
        }
        if (result == Scriptable.NOT_FOUND) {
            result = Undefined.instance;
        }
        return result;
    }

    @Deprecated
    public static Object getObjectProp(Object obj, String property, Context cx) {
        return ScriptRuntime.getObjectProp(obj, property, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object getObjectProp(Object obj, String property, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefReadError(obj, property);
        }
        return ScriptRuntime.getObjectProp(sobj, property, cx);
    }

    public static Object getObjectProp(Scriptable obj, String property, Context cx) {
        Object result = ScriptableObject.getProperty(obj, property);
        if (result == Scriptable.NOT_FOUND) {
            if (cx.hasFeature(11)) {
                Context.reportWarning(ScriptRuntime.getMessageById("msg.ref.undefined.prop", property));
            }
            result = Undefined.instance;
        }
        return result;
    }

    @Deprecated
    public static Object getObjectPropNoWarn(Object obj, String property, Context cx) {
        return ScriptRuntime.getObjectPropNoWarn(obj, property, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object getObjectPropNoWarn(Object obj, String property, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefReadError(obj, property);
        }
        Object result = ScriptableObject.getProperty(sobj, property);
        if (result == Scriptable.NOT_FOUND) {
            return Undefined.instance;
        }
        return result;
    }

    @Deprecated
    public static Object getObjectIndex(Object obj, double dblIndex, Context cx) {
        return ScriptRuntime.getObjectIndex(obj, dblIndex, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object getObjectIndex(Object obj, double dblIndex, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefReadError(obj, ScriptRuntime.toString(dblIndex));
        }
        int index = (int)dblIndex;
        if ((double)index == dblIndex) {
            return ScriptRuntime.getObjectIndex(sobj, index, cx);
        }
        String s = ScriptRuntime.toString(dblIndex);
        return ScriptRuntime.getObjectProp(sobj, s, cx);
    }

    public static Object getObjectIndex(Scriptable obj, int index, Context cx) {
        Object result = ScriptableObject.getProperty(obj, index);
        if (result == Scriptable.NOT_FOUND) {
            result = Undefined.instance;
        }
        return result;
    }

    @Deprecated
    public static Object setObjectElem(Object obj, Object elem, Object value, Context cx) {
        return ScriptRuntime.setObjectElem(obj, elem, value, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object setObjectElem(Object obj, Object elem, Object value, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefWriteError(obj, elem, value);
        }
        return ScriptRuntime.setObjectElem(sobj, elem, value, cx);
    }

    public static Object setObjectElem(Scriptable obj, Object elem, Object value, Context cx) {
        if (obj instanceof XMLObject) {
            ((XMLObject)obj).put(cx, elem, value);
        } else if (ScriptRuntime.isSymbol(elem)) {
            ScriptableObject.putProperty(obj, (Symbol)elem, value);
        } else {
            StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, elem);
            if (s.stringId == null) {
                ScriptableObject.putProperty(obj, s.index, value);
            } else {
                ScriptableObject.putProperty(obj, s.stringId, value);
            }
        }
        return value;
    }

    @Deprecated
    public static Object setObjectProp(Object obj, String property, Object value, Context cx) {
        return ScriptRuntime.setObjectProp(obj, property, value, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object setObjectProp(Object obj, String property, Object value, Context cx, Scriptable scope) {
        if (!(obj instanceof Scriptable) && cx.isStrictMode() && cx.getLanguageVersion() >= 180) {
            throw ScriptRuntime.undefWriteError(obj, property, value);
        }
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefWriteError(obj, property, value);
        }
        return ScriptRuntime.setObjectProp(sobj, property, value, cx);
    }

    public static Object setObjectProp(Scriptable obj, String property, Object value, Context cx) {
        ScriptableObject.putProperty(obj, property, value);
        return value;
    }

    @Deprecated
    public static Object setObjectIndex(Object obj, double dblIndex, Object value, Context cx) {
        return ScriptRuntime.setObjectIndex(obj, dblIndex, value, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object setObjectIndex(Object obj, double dblIndex, Object value, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.undefWriteError(obj, String.valueOf(dblIndex), value);
        }
        int index = (int)dblIndex;
        if ((double)index == dblIndex) {
            return ScriptRuntime.setObjectIndex(sobj, index, value, cx);
        }
        String s = ScriptRuntime.toString(dblIndex);
        return ScriptRuntime.setObjectProp(sobj, s, value, cx);
    }

    public static Object setObjectIndex(Scriptable obj, int index, Object value, Context cx) {
        ScriptableObject.putProperty(obj, index, value);
        return value;
    }

    public static boolean deleteObjectElem(Scriptable target, Object elem, Context cx) {
        if (ScriptRuntime.isSymbol(elem)) {
            SymbolScriptable so = ScriptableObject.ensureSymbolScriptable(target);
            Symbol s = (Symbol)elem;
            so.delete(s);
            return !so.has(s, target);
        }
        StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, elem);
        if (s.stringId == null) {
            target.delete(s.index);
            return !target.has(s.index, target);
        }
        target.delete(s.stringId);
        return !target.has(s.stringId, target);
    }

    public static boolean hasObjectElem(Scriptable target, Object elem, Context cx) {
        boolean result;
        if (ScriptRuntime.isSymbol(elem)) {
            result = ScriptableObject.hasProperty(target, (Symbol)elem);
        } else {
            StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, elem);
            result = s.stringId == null ? ScriptableObject.hasProperty(target, s.index) : ScriptableObject.hasProperty(target, s.stringId);
        }
        return result;
    }

    public static Object refGet(Ref ref, Context cx) {
        return ref.get(cx);
    }

    @Deprecated
    public static Object refSet(Ref ref, Object value, Context cx) {
        return ScriptRuntime.refSet(ref, value, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Object refSet(Ref ref, Object value, Context cx, Scriptable scope) {
        return ref.set(cx, scope, value);
    }

    public static Object refDel(Ref ref, Context cx) {
        return ScriptRuntime.wrapBoolean(ref.delete(cx));
    }

    static boolean isSpecialProperty(String s) {
        return s.equals("__proto__") || s.equals("__parent__");
    }

    @Deprecated
    public static Ref specialRef(Object obj, String specialProperty, Context cx) {
        return ScriptRuntime.specialRef(obj, specialProperty, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Ref specialRef(Object obj, String specialProperty, Context cx, Scriptable scope) {
        return SpecialRef.createSpecial(cx, scope, obj, specialProperty);
    }

    @Deprecated
    public static Object delete(Object obj, Object id, Context cx) {
        return ScriptRuntime.delete(obj, id, cx, false);
    }

    @Deprecated
    public static Object delete(Object obj, Object id, Context cx, boolean isName) {
        return ScriptRuntime.delete(obj, id, cx, ScriptRuntime.getTopCallScope(cx), isName);
    }

    public static Object delete(Object obj, Object id, Context cx, Scriptable scope, boolean isName) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            if (isName) {
                return Boolean.TRUE;
            }
            throw ScriptRuntime.undefDeleteError(obj, id);
        }
        boolean result = ScriptRuntime.deleteObjectElem(sobj, id, cx);
        return ScriptRuntime.wrapBoolean(result);
    }

    public static Object name(Context cx, Scriptable scope, String name) {
        Scriptable parent = scope.getParentScope();
        if (parent == null) {
            Object result = ScriptRuntime.topScopeName(cx, scope, name);
            if (result == Scriptable.NOT_FOUND) {
                throw ScriptRuntime.notFoundError(scope, name);
            }
            return result;
        }
        return ScriptRuntime.nameOrFunction(cx, scope, parent, name, false);
    }

    private static Object nameOrFunction(Context cx, Scriptable scope, Scriptable parentScope, String name, boolean asFunctionCall) {
        Object result;
        Scriptable thisObj;
        block17: {
            thisObj = scope;
            XMLObject firstXMLObject = null;
            do {
                if (scope instanceof NativeWith) {
                    Scriptable withObj = scope.getPrototype();
                    if (withObj instanceof XMLObject) {
                        XMLObject xmlObj = (XMLObject)withObj;
                        if (xmlObj.has(name, (Scriptable)xmlObj)) {
                            thisObj = xmlObj;
                            result = xmlObj.get(name, (Scriptable)xmlObj);
                            break block17;
                        }
                        if (firstXMLObject == null) {
                            firstXMLObject = xmlObj;
                        }
                    } else {
                        result = ScriptableObject.getProperty(withObj, name);
                        if (result != Scriptable.NOT_FOUND) {
                            thisObj = withObj;
                            break block17;
                        }
                    }
                } else if (scope instanceof NativeCall) {
                    result = scope.get(name, scope);
                    if (result != Scriptable.NOT_FOUND) {
                        if (asFunctionCall) {
                            thisObj = ScriptableObject.getTopLevelScope(parentScope);
                        }
                        break block17;
                    }
                } else {
                    result = ScriptableObject.getProperty(scope, name);
                    if (result != Scriptable.NOT_FOUND) {
                        thisObj = scope;
                        break block17;
                    }
                }
                scope = parentScope;
            } while ((parentScope = parentScope.getParentScope()) != null);
            result = ScriptRuntime.topScopeName(cx, scope, name);
            if (result == Scriptable.NOT_FOUND) {
                if (firstXMLObject == null || asFunctionCall) {
                    throw ScriptRuntime.notFoundError(scope, name);
                }
                result = firstXMLObject.get(name, (Scriptable)firstXMLObject);
            }
            thisObj = scope;
        }
        if (asFunctionCall) {
            if (!(result instanceof Callable)) {
                throw ScriptRuntime.notFunctionError(result, name);
            }
            ScriptRuntime.storeScriptable(cx, thisObj);
        }
        return result;
    }

    private static Object topScopeName(Context cx, Scriptable scope, String name) {
        if (cx.useDynamicScope) {
            scope = ScriptRuntime.checkDynamicScope(cx.topCallScope, scope);
        }
        return ScriptableObject.getProperty(scope, name);
    }

    public static Scriptable bind(Context cx, Scriptable scope, String id) {
        XMLObject firstXMLObject;
        block11: {
            firstXMLObject = null;
            Scriptable parent = scope.getParentScope();
            if (parent != null) {
                while (scope instanceof NativeWith) {
                    Scriptable withObj = scope.getPrototype();
                    if (withObj instanceof XMLObject) {
                        XMLObject xmlObject = (XMLObject)withObj;
                        if (xmlObject.has(cx, id)) {
                            return xmlObject;
                        }
                        if (firstXMLObject == null) {
                            firstXMLObject = xmlObject;
                        }
                    } else if (ScriptableObject.hasProperty(withObj, id)) {
                        return withObj;
                    }
                    scope = parent;
                    if ((parent = parent.getParentScope()) != null) continue;
                    break block11;
                }
                do {
                    if (ScriptableObject.hasProperty(scope, id)) {
                        return scope;
                    }
                    scope = parent;
                } while ((parent = parent.getParentScope()) != null);
            }
        }
        if (cx.useDynamicScope) {
            scope = ScriptRuntime.checkDynamicScope(cx.topCallScope, scope);
        }
        if (ScriptableObject.hasProperty(scope, id)) {
            return scope;
        }
        return firstXMLObject;
    }

    public static Object setName(Scriptable bound, Object value, Context cx, Scriptable scope, String id) {
        if (bound != null) {
            ScriptableObject.putProperty(bound, id, value);
        } else {
            if (cx.hasFeature(11) || cx.hasFeature(8)) {
                Context.reportWarning(ScriptRuntime.getMessageById("msg.assn.create.strict", id));
            }
            bound = ScriptableObject.getTopLevelScope(scope);
            if (cx.useDynamicScope) {
                bound = ScriptRuntime.checkDynamicScope(cx.topCallScope, bound);
            }
            bound.put(id, bound, value);
        }
        return value;
    }

    public static Object strictSetName(Scriptable bound, Object value, Context cx, Scriptable scope, String id) {
        if (bound != null) {
            ScriptableObject.putProperty(bound, id, value);
            return value;
        }
        String msg = "Assignment to undefined \"" + id + "\" in strict mode";
        throw ScriptRuntime.constructError("ReferenceError", msg);
    }

    public static Object setConst(Scriptable bound, Object value, Context cx, String id) {
        if (bound instanceof XMLObject) {
            bound.put(id, bound, value);
        } else {
            ScriptableObject.putConstProperty(bound, id, value);
        }
        return value;
    }

    public static Scriptable toIterator(Context cx, Scriptable scope, Scriptable obj, boolean keyOnly) {
        if (ScriptableObject.hasProperty(obj, "__iterator__")) {
            Object v = ScriptableObject.getProperty(obj, "__iterator__");
            if (!(v instanceof Callable)) {
                throw ScriptRuntime.typeErrorById("msg.invalid.iterator", new Object[0]);
            }
            Callable f = (Callable)v;
            Object[] args = new Object[]{keyOnly ? Boolean.TRUE : Boolean.FALSE};
            v = f.call(cx, scope, obj, args);
            if (!(v instanceof Scriptable)) {
                throw ScriptRuntime.typeErrorById("msg.iterator.primitive", new Object[0]);
            }
            return (Scriptable)v;
        }
        return null;
    }

    @Deprecated
    public static Object enumInit(Object value, Context cx, boolean enumValues) {
        return ScriptRuntime.enumInit(value, cx, enumValues ? 1 : 0);
    }

    @Deprecated
    public static Object enumInit(Object value, Context cx, int enumType) {
        return ScriptRuntime.enumInit(value, cx, ScriptRuntime.getTopCallScope(cx), enumType);
    }

    public static Object enumInit(Object value, Context cx, Scriptable scope, int enumType) {
        IdEnumeration x = new IdEnumeration();
        x.obj = ScriptRuntime.toObjectOrNull(cx, value, scope);
        if (enumType == 6) {
            x.enumType = enumType;
            x.iterator = null;
            return ScriptRuntime.enumInitInOrder(cx, x);
        }
        if (x.obj == null) {
            return x;
        }
        x.enumType = enumType;
        x.iterator = null;
        if (enumType != 3 && enumType != 4 && enumType != 5) {
            x.iterator = ScriptRuntime.toIterator(cx, x.obj.getParentScope(), x.obj, enumType == 0);
        }
        if (x.iterator == null) {
            ScriptRuntime.enumChangeObject(x);
        }
        return x;
    }

    private static Object enumInitInOrder(Context cx, IdEnumeration x) {
        Object[] args;
        if (!(x.obj instanceof SymbolScriptable) || !ScriptableObject.hasProperty(x.obj, SymbolKey.ITERATOR)) {
            throw ScriptRuntime.typeErrorById("msg.not.iterable", ScriptRuntime.toString(x.obj));
        }
        Object iterator = ScriptableObject.getProperty(x.obj, SymbolKey.ITERATOR);
        if (!(iterator instanceof Callable)) {
            throw ScriptRuntime.typeErrorById("msg.not.iterable", ScriptRuntime.toString(x.obj));
        }
        Callable f = (Callable)iterator;
        Scriptable scope = x.obj.getParentScope();
        Object v = f.call(cx, scope, x.obj, args = new Object[0]);
        if (!(v instanceof Scriptable)) {
            throw ScriptRuntime.typeErrorById("msg.not.iterable", ScriptRuntime.toString(x.obj));
        }
        x.iterator = (Scriptable)v;
        return x;
    }

    public static void setEnumNumbers(Object enumObj, boolean enumNumbers) {
        ((IdEnumeration)enumObj).enumNumbers = enumNumbers;
    }

    public static Boolean enumNext(Object enumObj) {
        block10: {
            int intId;
            IdEnumeration x = (IdEnumeration)enumObj;
            if (x.iterator != null) {
                if (x.enumType == 6) {
                    return ScriptRuntime.enumNextInOrder(x);
                }
                Object v = ScriptableObject.getProperty(x.iterator, "next");
                if (!(v instanceof Callable)) {
                    return Boolean.FALSE;
                }
                Callable f = (Callable)v;
                Context cx = Context.getContext();
                try {
                    x.currentId = f.call(cx, x.iterator.getParentScope(), x.iterator, emptyArgs);
                    return Boolean.TRUE;
                }
                catch (JavaScriptException e) {
                    if (e.getValue() instanceof NativeIterator.StopIteration) {
                        return Boolean.FALSE;
                    }
                    throw e;
                }
            }
            while (true) {
                if (x.obj == null) {
                    return Boolean.FALSE;
                }
                if (x.index == x.ids.length) {
                    x.obj = x.obj.getPrototype();
                    ScriptRuntime.enumChangeObject(x);
                    continue;
                }
                Object id = x.ids[x.index++];
                if (x.used != null && x.used.has(id) || id instanceof Symbol) continue;
                if (id instanceof String) {
                    String strId = (String)id;
                    if (!x.obj.has(strId, x.obj)) continue;
                    x.currentId = strId;
                    break block10;
                }
                intId = ((Number)id).intValue();
                if (x.obj.has(intId, x.obj)) break;
            }
            x.currentId = x.enumNumbers ? Integer.valueOf(intId) : String.valueOf(intId);
        }
        return Boolean.TRUE;
    }

    private static Boolean enumNextInOrder(IdEnumeration enumObj) {
        Object r;
        Scriptable scope;
        Object v = ScriptableObject.getProperty(enumObj.iterator, "next");
        if (!(v instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(enumObj.iterator, "next");
        }
        Callable f = (Callable)v;
        Context cx = Context.getContext();
        Scriptable iteratorResult = ScriptRuntime.toObject(cx, scope = enumObj.iterator.getParentScope(), r = f.call(cx, scope, enumObj.iterator, emptyArgs));
        Object done = ScriptableObject.getProperty(iteratorResult, "done");
        if (done != Scriptable.NOT_FOUND && ScriptRuntime.toBoolean(done)) {
            return Boolean.FALSE;
        }
        enumObj.currentId = ScriptableObject.getProperty(iteratorResult, "value");
        return Boolean.TRUE;
    }

    public static Object enumId(Object enumObj, Context cx) {
        IdEnumeration x = (IdEnumeration)enumObj;
        if (x.iterator != null) {
            return x.currentId;
        }
        switch (x.enumType) {
            case 0: 
            case 3: {
                return x.currentId;
            }
            case 1: 
            case 4: {
                return ScriptRuntime.enumValue(enumObj, cx);
            }
            case 2: 
            case 5: {
                Object[] elements = new Object[]{x.currentId, ScriptRuntime.enumValue(enumObj, cx)};
                return cx.newArray(ScriptableObject.getTopLevelScope(x.obj), elements);
            }
        }
        throw Kit.codeBug();
    }

    public static Object enumValue(Object enumObj, Context cx) {
        Object result;
        IdEnumeration x = (IdEnumeration)enumObj;
        if (ScriptRuntime.isSymbol(x.currentId)) {
            SymbolScriptable so = ScriptableObject.ensureSymbolScriptable(x.obj);
            result = so.get((Symbol)x.currentId, x.obj);
        } else {
            StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, x.currentId);
            result = s.stringId == null ? x.obj.get(s.index, x.obj) : x.obj.get(s.stringId, x.obj);
        }
        return result;
    }

    private static void enumChangeObject(IdEnumeration x) {
        Object[] ids = null;
        while (x.obj != null && (ids = x.obj.getIds()).length == 0) {
            x.obj = x.obj.getPrototype();
        }
        if (x.obj != null && x.ids != null) {
            Object[] previous = x.ids;
            int L = previous.length;
            if (x.used == null) {
                x.used = new ObjToIntMap(L);
            }
            for (int i = 0; i != L; ++i) {
                x.used.intern(previous[i]);
            }
        }
        x.ids = ids;
        x.index = 0;
    }

    public static boolean loadFromIterable(Context cx, Scriptable scope, Object arg1, BiConsumer<Object, Object> setter) {
        if (arg1 == null || Undefined.isUndefined(arg1)) {
            return false;
        }
        Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
        if (Undefined.isUndefined(ito)) {
            return false;
        }
        try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, ito);){
            for (Object val : it) {
                Object finalVal;
                Scriptable sVal = ScriptableObject.ensureScriptable(val);
                if (sVal instanceof Symbol) {
                    throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(sVal));
                }
                Object finalKey = sVal.get(0, sVal);
                if (finalKey == Scriptable.NOT_FOUND) {
                    finalKey = Undefined.instance;
                }
                if ((finalVal = sVal.get(1, sVal)) == Scriptable.NOT_FOUND) {
                    finalVal = Undefined.instance;
                }
                setter.accept(finalKey, finalVal);
            }
        }
        return true;
    }

    public static Callable getNameFunctionAndThis(String name, Context cx, Scriptable scope) {
        Scriptable parent = scope.getParentScope();
        if (parent == null) {
            Object result = ScriptRuntime.topScopeName(cx, scope, name);
            if (!(result instanceof Callable)) {
                if (result == Scriptable.NOT_FOUND) {
                    throw ScriptRuntime.notFoundError(scope, name);
                }
                throw ScriptRuntime.notFunctionError(result, name);
            }
            Scriptable thisObj = scope;
            ScriptRuntime.storeScriptable(cx, thisObj);
            return (Callable)result;
        }
        return (Callable)ScriptRuntime.nameOrFunction(cx, scope, parent, name, true);
    }

    @Deprecated
    public static Callable getElemFunctionAndThis(Object obj, Object elem, Context cx) {
        return ScriptRuntime.getElemFunctionAndThis(obj, elem, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Callable getElemFunctionAndThis(Object obj, Object elem, Context cx, Scriptable scope) {
        Object value;
        Scriptable thisObj;
        if (ScriptRuntime.isSymbol(elem)) {
            thisObj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
            if (thisObj == null) {
                throw ScriptRuntime.undefCallError(obj, String.valueOf(elem));
            }
            value = ScriptableObject.getProperty(thisObj, (Symbol)elem);
        } else {
            StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, elem);
            if (s.stringId != null) {
                return ScriptRuntime.getPropFunctionAndThis(obj, s.stringId, cx, scope);
            }
            thisObj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
            if (thisObj == null) {
                throw ScriptRuntime.undefCallError(obj, String.valueOf(elem));
            }
            value = ScriptableObject.getProperty(thisObj, s.index);
        }
        if (!(value instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(value, elem);
        }
        ScriptRuntime.storeScriptable(cx, thisObj);
        return (Callable)value;
    }

    @Deprecated
    public static Callable getPropFunctionAndThis(Object obj, String property, Context cx) {
        return ScriptRuntime.getPropFunctionAndThis(obj, property, cx, ScriptRuntime.getTopCallScope(cx));
    }

    public static Callable getPropFunctionAndThis(Object obj, String property, Context cx, Scriptable scope) {
        Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        return ScriptRuntime.getPropFunctionAndThisHelper(obj, property, cx, thisObj);
    }

    private static Callable getPropFunctionAndThisHelper(Object obj, String property, Context cx, Scriptable thisObj) {
        Object noSuchMethod;
        if (thisObj == null) {
            throw ScriptRuntime.undefCallError(obj, property);
        }
        Object value = ScriptableObject.getProperty(thisObj, property);
        if (!(value instanceof Callable) && (noSuchMethod = ScriptableObject.getProperty(thisObj, "__noSuchMethod__")) instanceof Callable) {
            value = new NoSuchMethodShim((Callable)noSuchMethod, property);
        }
        if (!(value instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(thisObj, value, property);
        }
        ScriptRuntime.storeScriptable(cx, thisObj);
        return (Callable)value;
    }

    public static Callable getValueFunctionAndThis(Object value, Context cx) {
        if (!(value instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(value);
        }
        Callable f = (Callable)value;
        Scriptable thisObj = null;
        if (f instanceof Scriptable) {
            thisObj = ((Scriptable)((Object)f)).getParentScope();
        }
        if (thisObj == null) {
            if (cx.topCallScope == null) {
                throw new IllegalStateException();
            }
            thisObj = cx.topCallScope;
        }
        if (thisObj.getParentScope() != null && !(thisObj instanceof NativeWith) && thisObj instanceof NativeCall) {
            thisObj = ScriptableObject.getTopLevelScope(thisObj);
        }
        ScriptRuntime.storeScriptable(cx, thisObj);
        return f;
    }

    public static Object callIterator(Object obj, Context cx, Scriptable scope) {
        Callable getIterator = ScriptRuntime.getElemFunctionAndThis(obj, SymbolKey.ITERATOR, cx, scope);
        Scriptable iterable = ScriptRuntime.lastStoredScriptable(cx);
        return getIterator.call(cx, scope, iterable, emptyArgs);
    }

    public static boolean isIteratorDone(Context cx, Object result) {
        if (!(result instanceof Scriptable)) {
            return false;
        }
        Object prop = ScriptRuntime.getObjectProp((Scriptable)result, "done", cx);
        return ScriptRuntime.toBoolean(prop);
    }

    public static Ref callRef(Callable function, Scriptable thisObj, Object[] args, Context cx) {
        if (function instanceof RefCallable) {
            RefCallable rfunction = (RefCallable)function;
            Ref ref = rfunction.refCall(cx, thisObj, args);
            if (ref == null) {
                throw new IllegalStateException(rfunction.getClass().getName() + ".refCall() returned null");
            }
            return ref;
        }
        String msg = ScriptRuntime.getMessageById("msg.no.ref.from.function", ScriptRuntime.toString(function));
        throw ScriptRuntime.constructError("ReferenceError", msg);
    }

    public static Scriptable newObject(Object fun, Context cx, Scriptable scope, Object[] args) {
        if (!(fun instanceof Function)) {
            throw ScriptRuntime.notFunctionError(fun);
        }
        Function function = (Function)fun;
        return function.construct(cx, scope, args);
    }

    public static Object callSpecial(Context cx, Callable fun, Scriptable thisObj, Object[] args, Scriptable scope, Scriptable callerThis, int callType, String filename, int lineNumber) {
        if (callType == 1) {
            if (thisObj.getParentScope() == null && NativeGlobal.isEvalFunction(fun)) {
                return ScriptRuntime.evalSpecial(cx, scope, callerThis, args, filename, lineNumber);
            }
        } else if (callType == 2) {
            if (NativeWith.isWithFunction(fun)) {
                throw Context.reportRuntimeErrorById("msg.only.from.new", "With");
            }
        } else {
            throw Kit.codeBug();
        }
        return fun.call(cx, scope, thisObj, args);
    }

    public static Object newSpecial(Context cx, Object fun, Object[] args, Scriptable scope, int callType) {
        if (callType == 1) {
            if (NativeGlobal.isEvalFunction(fun)) {
                throw ScriptRuntime.typeErrorById("msg.not.ctor", "eval");
            }
        } else if (callType == 2) {
            if (NativeWith.isWithFunction(fun)) {
                return NativeWith.newWithSpecial(cx, scope, args);
            }
        } else {
            throw Kit.codeBug();
        }
        return ScriptRuntime.newObject(fun, cx, scope, args);
    }

    public static Object applyOrCall(boolean isApply, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Object[] callArgs;
        int L = args.length;
        Callable function = ScriptRuntime.getCallable(thisObj);
        Scriptable callThis = null;
        if (L != 0) {
            if (cx.hasFeature(15)) {
                callThis = ScriptRuntime.toObjectOrNull(cx, args[0], scope);
            } else {
                Scriptable scriptable = callThis = args[0] == Undefined.instance ? Undefined.SCRIPTABLE_UNDEFINED : ScriptRuntime.toObjectOrNull(cx, args[0], scope);
            }
        }
        if (callThis == null && cx.hasFeature(15)) {
            callThis = ScriptRuntime.getTopCallScope(cx);
        }
        if (isApply) {
            callArgs = L <= 1 ? emptyArgs : ScriptRuntime.getApplyArguments(cx, args[1]);
        } else if (L <= 1) {
            callArgs = emptyArgs;
        } else {
            callArgs = new Object[L - 1];
            System.arraycopy(args, 1, callArgs, 0, L - 1);
        }
        return function.call(cx, scope, callThis, callArgs);
    }

    private static boolean isArrayLike(Scriptable obj) {
        return obj != null && (obj instanceof NativeArray || obj instanceof Arguments || ScriptableObject.hasProperty(obj, "length"));
    }

    static Object[] getApplyArguments(Context cx, Object arg1) {
        if (arg1 == null || Undefined.isUndefined(arg1)) {
            return emptyArgs;
        }
        if (arg1 instanceof Scriptable && ScriptRuntime.isArrayLike((Scriptable)arg1)) {
            return cx.getElements((Scriptable)arg1);
        }
        if (arg1 instanceof ScriptableObject) {
            return emptyArgs;
        }
        throw ScriptRuntime.typeErrorById("msg.arg.isnt.array", new Object[0]);
    }

    static Callable getCallable(Scriptable thisObj) {
        Callable function;
        if (thisObj instanceof Callable) {
            function = (Callable)((Object)thisObj);
        } else {
            if (thisObj == null) {
                throw ScriptRuntime.notFunctionError(null, null);
            }
            Object value = thisObj.getDefaultValue(FunctionClass);
            if (!(value instanceof Callable)) {
                throw ScriptRuntime.notFunctionError(value, thisObj);
            }
            function = (Callable)value;
        }
        return function;
    }

    public static Object evalSpecial(Context cx, Scriptable scope, Object thisArg, Object[] args, String filename, int lineNumber) {
        if (args.length < 1) {
            return Undefined.instance;
        }
        Object x = args[0];
        if (!(x instanceof CharSequence)) {
            if (cx.hasFeature(11) || cx.hasFeature(9)) {
                throw Context.reportRuntimeErrorById("msg.eval.nonstring.strict", new Object[0]);
            }
            String message = ScriptRuntime.getMessageById("msg.eval.nonstring", new Object[0]);
            Context.reportWarning(message);
            return x;
        }
        if (filename == null) {
            int[] linep = new int[1];
            filename = Context.getSourcePositionFromStack(linep);
            if (filename != null) {
                lineNumber = linep[0];
            } else {
                filename = "";
            }
        }
        String sourceName = ScriptRuntime.makeUrlForGeneratedScript(true, filename, lineNumber);
        ErrorReporter reporter = DefaultErrorReporter.forEval(cx.getErrorReporter());
        Evaluator evaluator = Context.createInterpreter();
        if (evaluator == null) {
            throw new JavaScriptException("Interpreter not present", filename, lineNumber);
        }
        Script script = cx.compileString(x.toString(), evaluator, reporter, sourceName, 1, null);
        evaluator.setEvalScriptFlag(script);
        Callable c = (Callable)((Object)script);
        return c.call(cx, scope, (Scriptable)thisArg, emptyArgs);
    }

    public static String typeof(Object value) {
        if (value == null) {
            return "object";
        }
        if (value == Undefined.instance) {
            return "undefined";
        }
        if (value instanceof Delegator) {
            return ScriptRuntime.typeof(((Delegator)value).getDelegee());
        }
        if (value instanceof ScriptableObject) {
            return ((ScriptableObject)value).getTypeOf();
        }
        if (value instanceof Scriptable) {
            return value instanceof Callable ? "function" : "object";
        }
        if (value instanceof CharSequence) {
            return "string";
        }
        if (value instanceof BigInteger) {
            return "bigint";
        }
        if (value instanceof Number) {
            return "number";
        }
        if (value instanceof Boolean) {
            return "boolean";
        }
        throw ScriptRuntime.errorWithClassName("msg.invalid.type", value);
    }

    public static String typeofName(Scriptable scope, String id) {
        Context cx = Context.getContext();
        Scriptable val = ScriptRuntime.bind(cx, scope, id);
        if (val == null) {
            return "undefined";
        }
        return ScriptRuntime.typeof(ScriptRuntime.getObjectProp(val, id, cx));
    }

    public static boolean isObject(Object value) {
        if (value == null) {
            return false;
        }
        if (Undefined.isUndefined(value)) {
            return false;
        }
        if (value instanceof ScriptableObject) {
            String type = ((ScriptableObject)value).getTypeOf();
            return "object".equals(type) || "function".equals(type);
        }
        if (value instanceof Scriptable) {
            return !(value instanceof Callable);
        }
        return false;
    }

    public static Object add(Object val1, Object val2, Context cx) {
        Object test;
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).add((BigInteger)val2);
        }
        if (val1 instanceof Number && val2 instanceof BigInteger || val1 instanceof BigInteger && val2 instanceof Number) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        if (val1 instanceof Number && val2 instanceof Number) {
            return ScriptRuntime.wrapNumber(((Number)val1).doubleValue() + ((Number)val2).doubleValue());
        }
        if (val1 instanceof CharSequence && val2 instanceof CharSequence) {
            return new ConsString((CharSequence)val1, (CharSequence)val2);
        }
        if (val1 instanceof XMLObject && (test = ((XMLObject)val1).addValues(cx, true, val2)) != Scriptable.NOT_FOUND) {
            return test;
        }
        if (val2 instanceof XMLObject && (test = ((XMLObject)val2).addValues(cx, false, val1)) != Scriptable.NOT_FOUND) {
            return test;
        }
        if (val1 instanceof Symbol || val2 instanceof Symbol) {
            throw ScriptRuntime.typeErrorById("msg.not.a.number", new Object[0]);
        }
        if (val1 instanceof Scriptable) {
            val1 = ((Scriptable)val1).getDefaultValue(null);
        }
        if (val2 instanceof Scriptable) {
            val2 = ((Scriptable)val2).getDefaultValue(null);
        }
        if (!(val1 instanceof CharSequence) && !(val2 instanceof CharSequence)) {
            Number num2;
            Number num1 = val1 instanceof Number ? (Number)((Number)val1) : (Number)ScriptRuntime.toNumeric(val1);
            Number number = num2 = val2 instanceof Number ? (Number)((Number)val2) : (Number)ScriptRuntime.toNumeric(val2);
            if (num1 instanceof BigInteger && num2 instanceof BigInteger) {
                return ((BigInteger)num1).add((BigInteger)num2);
            }
            if (num1 instanceof BigInteger || num2 instanceof BigInteger) {
                throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
            }
            return num1.doubleValue() + num2.doubleValue();
        }
        return new ConsString(ScriptRuntime.toCharSequence(val1), ScriptRuntime.toCharSequence(val2));
    }

    @Deprecated
    public static CharSequence add(CharSequence val1, Object val2) {
        return new ConsString(val1, ScriptRuntime.toCharSequence(val2));
    }

    @Deprecated
    public static CharSequence add(Object val1, CharSequence val2) {
        return new ConsString(ScriptRuntime.toCharSequence(val1), val2);
    }

    public static Number subtract(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).subtract((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        return val1.doubleValue() - val2.doubleValue();
    }

    public static Number multiply(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).multiply((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        return val1.doubleValue() * val2.doubleValue();
    }

    public static Number divide(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            if (val2.equals(BigInteger.ZERO)) {
                throw ScriptRuntime.rangeErrorById("msg.division.zero", new Object[0]);
            }
            return ((BigInteger)val1).divide((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        return val1.doubleValue() / val2.doubleValue();
    }

    public static Number remainder(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            if (val2.equals(BigInteger.ZERO)) {
                throw ScriptRuntime.rangeErrorById("msg.division.zero", new Object[0]);
            }
            return ((BigInteger)val1).remainder((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        return val1.doubleValue() % val2.doubleValue();
    }

    public static Number exponentiate(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            if (((BigInteger)val2).signum() == -1) {
                throw ScriptRuntime.rangeErrorById("msg.bigint.negative.exponent", new Object[0]);
            }
            try {
                int intVal2 = ((BigInteger)val2).intValueExact();
                return ((BigInteger)val1).pow(intVal2);
            }
            catch (ArithmeticException e) {
                throw ScriptRuntime.rangeErrorById("msg.bigint.out.of.range.arithmetic", new Object[0]);
            }
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        return Math.pow(val1.doubleValue(), val2.doubleValue());
    }

    public static Number bitwiseAND(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).and((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        int result = ScriptRuntime.toInt32(val1.doubleValue()) & ScriptRuntime.toInt32(val2.doubleValue());
        return (double)result;
    }

    public static Number bitwiseOR(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).or((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        int result = ScriptRuntime.toInt32(val1.doubleValue()) | ScriptRuntime.toInt32(val2.doubleValue());
        return (double)result;
    }

    public static Number bitwiseXOR(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ((BigInteger)val1).xor((BigInteger)val2);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        int result = ScriptRuntime.toInt32(val1.doubleValue()) ^ ScriptRuntime.toInt32(val2.doubleValue());
        return (double)result;
    }

    public static Number leftShift(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            try {
                int intVal2 = ((BigInteger)val2).intValueExact();
                return ((BigInteger)val1).shiftLeft(intVal2);
            }
            catch (ArithmeticException e) {
                throw ScriptRuntime.rangeErrorById("msg.bigint.out.of.range.arithmetic", new Object[0]);
            }
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        int result = ScriptRuntime.toInt32(val1.doubleValue()) << ScriptRuntime.toInt32(val2.doubleValue());
        return (double)result;
    }

    public static Number signedRightShift(Number val1, Number val2) {
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            try {
                int intVal2 = ((BigInteger)val2).intValueExact();
                return ((BigInteger)val1).shiftRight(intVal2);
            }
            catch (ArithmeticException e) {
                throw ScriptRuntime.rangeErrorById("msg.bigint.out.of.range.arithmetic", new Object[0]);
            }
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
        }
        int result = ScriptRuntime.toInt32(val1.doubleValue()) >> ScriptRuntime.toInt32(val2.doubleValue());
        return (double)result;
    }

    public static Number bitwiseNOT(Number val) {
        if (val instanceof BigInteger) {
            return ((BigInteger)val).not();
        }
        int result = ~ScriptRuntime.toInt32(val.doubleValue());
        return (double)result;
    }

    @Deprecated
    public static Object nameIncrDecr(Scriptable scopeChain, String id, int incrDecrMask) {
        return ScriptRuntime.nameIncrDecr(scopeChain, id, Context.getContext(), incrDecrMask);
    }

    public static Object nameIncrDecr(Scriptable scopeChain, String id, Context cx, int incrDecrMask) {
        Object value;
        Scriptable target;
        block4: {
            do {
                if (cx.useDynamicScope && scopeChain.getParentScope() == null) {
                    scopeChain = ScriptRuntime.checkDynamicScope(cx.topCallScope, scopeChain);
                }
                target = scopeChain;
                while (!(target instanceof NativeWith) || !(target.getPrototype() instanceof XMLObject)) {
                    value = target.get(id, scopeChain);
                    if (value == Scriptable.NOT_FOUND) {
                        if ((target = target.getPrototype()) != null) continue;
                    }
                    break block4;
                }
            } while ((scopeChain = scopeChain.getParentScope()) != null);
            throw ScriptRuntime.notFoundError(null, id);
        }
        return ScriptRuntime.doScriptableIncrDecr(target, id, scopeChain, value, incrDecrMask);
    }

    @Deprecated
    public static Object propIncrDecr(Object obj, String id, Context cx, int incrDecrMask) {
        return ScriptRuntime.propIncrDecr(obj, id, cx, ScriptRuntime.getTopCallScope(cx), incrDecrMask);
    }

    public static Object propIncrDecr(Object obj, String id, Context cx, Scriptable scope, int incrDecrMask) {
        Object value;
        Scriptable start = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (start == null) {
            throw ScriptRuntime.undefReadError(obj, id);
        }
        Scriptable target = start;
        while ((value = target.get(id, start)) == Scriptable.NOT_FOUND) {
            if ((target = target.getPrototype()) != null) continue;
            start.put(id, start, (Object)NaNobj);
            return NaNobj;
        }
        return ScriptRuntime.doScriptableIncrDecr(target, id, start, value, incrDecrMask);
    }

    private static Object doScriptableIncrDecr(Scriptable target, String id, Scriptable protoChainStart, Object value, int incrDecrMask) {
        boolean post = (incrDecrMask & 2) != 0;
        Number number = value instanceof Number ? (Number)((Number)value) : (Number)ScriptRuntime.toNumeric(value);
        Number result = number instanceof BigInteger ? ((incrDecrMask & 1) == 0 ? ((BigInteger)number).add(BigInteger.ONE) : ((BigInteger)number).subtract(BigInteger.ONE)) : ((incrDecrMask & 1) == 0 ? Double.valueOf(number.doubleValue() + 1.0) : Double.valueOf(number.doubleValue() - 1.0));
        target.put(id, protoChainStart, (Object)result);
        if (post) {
            return number;
        }
        return result;
    }

    @Deprecated
    public static Object elemIncrDecr(Object obj, Object index, Context cx, int incrDecrMask) {
        return ScriptRuntime.elemIncrDecr(obj, index, cx, ScriptRuntime.getTopCallScope(cx), incrDecrMask);
    }

    public static Object elemIncrDecr(Object obj, Object index, Context cx, Scriptable scope, int incrDecrMask) {
        Object value = ScriptRuntime.getObjectElem(obj, index, cx, scope);
        boolean post = (incrDecrMask & 2) != 0;
        Number number = value instanceof Number ? (Number)((Number)value) : (Number)ScriptRuntime.toNumeric(value);
        Number result = number instanceof BigInteger ? ((incrDecrMask & 1) == 0 ? ((BigInteger)number).add(BigInteger.ONE) : ((BigInteger)number).subtract(BigInteger.ONE)) : ((incrDecrMask & 1) == 0 ? Double.valueOf(number.doubleValue() + 1.0) : Double.valueOf(number.doubleValue() - 1.0));
        ScriptRuntime.setObjectElem(obj, index, result, cx, scope);
        if (post) {
            return number;
        }
        return result;
    }

    @Deprecated
    public static Object refIncrDecr(Ref ref, Context cx, int incrDecrMask) {
        return ScriptRuntime.refIncrDecr(ref, cx, ScriptRuntime.getTopCallScope(cx), incrDecrMask);
    }

    public static Object refIncrDecr(Ref ref, Context cx, Scriptable scope, int incrDecrMask) {
        Object value = ref.get(cx);
        boolean post = (incrDecrMask & 2) != 0;
        Number number = value instanceof Number ? (Number)((Number)value) : (Number)ScriptRuntime.toNumeric(value);
        Number result = number instanceof BigInteger ? ((incrDecrMask & 1) == 0 ? ((BigInteger)number).add(BigInteger.ONE) : ((BigInteger)number).subtract(BigInteger.ONE)) : ((incrDecrMask & 1) == 0 ? Double.valueOf(number.doubleValue() + 1.0) : Double.valueOf(number.doubleValue() - 1.0));
        ref.set(cx, scope, result);
        if (post) {
            return number;
        }
        return result;
    }

    public static Number negate(Number val) {
        if (val instanceof BigInteger) {
            return ((BigInteger)val).negate();
        }
        return -val.doubleValue();
    }

    public static Object toPrimitive(Object val) {
        return ScriptRuntime.toPrimitive(val, null);
    }

    public static Object toPrimitive(Object val, Class<?> typeHint) {
        if (!(val instanceof Scriptable)) {
            return val;
        }
        Scriptable s = (Scriptable)val;
        Object result = s.getDefaultValue(typeHint);
        if (result instanceof Scriptable && !ScriptRuntime.isSymbol(result)) {
            throw ScriptRuntime.typeErrorById("msg.bad.default.value", new Object[0]);
        }
        return result;
    }

    public static boolean eq(Object x, Object y) {
        if (x == null || Undefined.isUndefined(x)) {
            Object test;
            if (y == null || Undefined.isUndefined(y)) {
                return true;
            }
            if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(x)) != Scriptable.NOT_FOUND) {
                return (Boolean)test;
            }
            return false;
        }
        if (x instanceof BigInteger) {
            return ScriptRuntime.eqBigInt((BigInteger)x, y);
        }
        if (x instanceof Number) {
            return ScriptRuntime.eqNumber(((Number)x).doubleValue(), y);
        }
        if (x == y) {
            return true;
        }
        if (x instanceof CharSequence) {
            return ScriptRuntime.eqString((CharSequence)x, y);
        }
        if (x instanceof Boolean) {
            Object test;
            boolean b = (Boolean)x;
            if (y instanceof Boolean) {
                return b == (Boolean)y;
            }
            if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(x)) != Scriptable.NOT_FOUND) {
                return (Boolean)test;
            }
            return ScriptRuntime.eqNumber(b ? 1.0 : 0.0, y);
        }
        if (x instanceof Scriptable) {
            if (x instanceof Delegator) {
                x = ((Delegator)x).getDelegee();
                if (y instanceof Delegator) {
                    return ScriptRuntime.eq(x, ((Delegator)y).getDelegee());
                }
                if (x == y) {
                    return true;
                }
            }
            if (y instanceof Delegator && ((Delegator)y).getDelegee() == x) {
                return true;
            }
            if (y instanceof Scriptable) {
                Object test;
                if (x instanceof ScriptableObject && (test = ((ScriptableObject)x).equivalentValues(y)) != Scriptable.NOT_FOUND) {
                    return (Boolean)test;
                }
                if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(x)) != Scriptable.NOT_FOUND) {
                    return (Boolean)test;
                }
                if (x instanceof Wrapper && y instanceof Wrapper) {
                    Object unwrappedY;
                    Object unwrappedX = ((Wrapper)x).unwrap();
                    return unwrappedX == (unwrappedY = ((Wrapper)y).unwrap()) || ScriptRuntime.isPrimitive(unwrappedX) && ScriptRuntime.isPrimitive(unwrappedY) && ScriptRuntime.eq(unwrappedX, unwrappedY);
                }
                return false;
            }
            if (y instanceof Boolean) {
                Object test;
                if (x instanceof ScriptableObject && (test = ((ScriptableObject)x).equivalentValues(y)) != Scriptable.NOT_FOUND) {
                    return (Boolean)test;
                }
                double d = (Boolean)y != false ? 1.0 : 0.0;
                return ScriptRuntime.eqNumber(d, x);
            }
            if (y instanceof BigInteger) {
                return ScriptRuntime.eqBigInt((BigInteger)y, x);
            }
            if (y instanceof Number) {
                return ScriptRuntime.eqNumber(((Number)y).doubleValue(), x);
            }
            if (y instanceof CharSequence) {
                return ScriptRuntime.eqString((CharSequence)y, x);
            }
            return false;
        }
        ScriptRuntime.warnAboutNonJSObject(x);
        return x == y;
    }

    public static boolean same(Object x, Object y) {
        if (!ScriptRuntime.typeof(x).equals(ScriptRuntime.typeof(y))) {
            return false;
        }
        if (x instanceof Number) {
            if (ScriptRuntime.isNaN(x) && ScriptRuntime.isNaN(y)) {
                return true;
            }
            return x.equals(y);
        }
        return ScriptRuntime.eq(x, y);
    }

    public static boolean sameZero(Object x, Object y) {
        if (!ScriptRuntime.typeof(x).equals(ScriptRuntime.typeof(y))) {
            return false;
        }
        if (x instanceof BigInteger) {
            return x.equals(y);
        }
        if (x instanceof Number) {
            if (ScriptRuntime.isNaN(x) && ScriptRuntime.isNaN(y)) {
                return true;
            }
            double dx = ((Number)x).doubleValue();
            if (y instanceof Number) {
                double dy = ((Number)y).doubleValue();
                if (dx == negativeZero && dy == 0.0 || dx == 0.0 && dy == negativeZero) {
                    return true;
                }
            }
            return ScriptRuntime.eqNumber(dx, y);
        }
        return ScriptRuntime.eq(x, y);
    }

    public static boolean isNaN(Object n) {
        if (n instanceof Double) {
            return ((Double)n).isNaN();
        }
        if (n instanceof Float) {
            return ((Float)n).isNaN();
        }
        return false;
    }

    public static boolean isPrimitive(Object obj) {
        return obj == null || Undefined.isUndefined(obj) || obj instanceof Number || obj instanceof String || obj instanceof Boolean;
    }

    static boolean eqNumber(double x, Object y) {
        while (true) {
            Number xval;
            Object test;
            if (y == null || Undefined.isUndefined(y)) {
                return false;
            }
            if (y instanceof BigInteger) {
                return ScriptRuntime.eqBigInt((BigInteger)y, x);
            }
            if (y instanceof Number) {
                return x == ((Number)y).doubleValue();
            }
            if (y instanceof CharSequence) {
                return x == ScriptRuntime.toNumber(y);
            }
            if (y instanceof Boolean) {
                return x == ((Boolean)y != false ? 1.0 : 0.0);
            }
            if (ScriptRuntime.isSymbol(y)) {
                return false;
            }
            if (!(y instanceof Scriptable)) break;
            if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(xval = ScriptRuntime.wrapNumber(x))) != Scriptable.NOT_FOUND) {
                return (Boolean)test;
            }
            y = ScriptRuntime.toPrimitive(y);
        }
        ScriptRuntime.warnAboutNonJSObject(y);
        return false;
    }

    static boolean eqBigInt(BigInteger x, Object y) {
        while (true) {
            Object test;
            BigInteger biy;
            if (y == null || Undefined.isUndefined(y)) {
                return false;
            }
            if (y instanceof BigInteger) {
                return x.equals(y);
            }
            if (y instanceof Number) {
                return ScriptRuntime.eqBigInt(x, ((Number)y).doubleValue());
            }
            if (y instanceof CharSequence) {
                try {
                    biy = ScriptRuntime.toBigInt(y);
                }
                catch (EcmaError e) {
                    return false;
                }
                return x.equals(biy);
            }
            if (y instanceof Boolean) {
                biy = (Boolean)y != false ? BigInteger.ONE : BigInteger.ZERO;
                return x.equals(biy);
            }
            if (ScriptRuntime.isSymbol(y)) {
                return false;
            }
            if (!(y instanceof Scriptable)) break;
            if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(x)) != Scriptable.NOT_FOUND) {
                return (Boolean)test;
            }
            y = ScriptRuntime.toPrimitive(y);
        }
        ScriptRuntime.warnAboutNonJSObject(y);
        return false;
    }

    private static boolean eqBigInt(BigInteger x, double y) {
        if (Double.isNaN(y) || Double.isInfinite(y)) {
            return false;
        }
        double d = Math.ceil(y);
        if (d != y) {
            return false;
        }
        BigDecimal bdx = new BigDecimal(x);
        BigDecimal bdy = new BigDecimal(d, MathContext.UNLIMITED);
        return bdx.compareTo(bdy) == 0;
    }

    private static boolean eqString(CharSequence x, Object y) {
        while (true) {
            Object test;
            if (y == null || Undefined.isUndefined(y)) {
                return false;
            }
            if (y instanceof CharSequence) {
                CharSequence c = (CharSequence)y;
                return x.length() == c.length() && x.toString().equals(c.toString());
            }
            if (y instanceof BigInteger) {
                BigInteger bix;
                try {
                    bix = ScriptRuntime.toBigInt(x);
                }
                catch (EcmaError e) {
                    return false;
                }
                return bix.equals(y);
            }
            if (y instanceof Number) {
                return ScriptRuntime.toNumber(x.toString()) == ((Number)y).doubleValue();
            }
            if (y instanceof Boolean) {
                return ScriptRuntime.toNumber(x.toString()) == ((Boolean)y != false ? 1.0 : 0.0);
            }
            if (ScriptRuntime.isSymbol(y)) {
                return false;
            }
            if (!(y instanceof Scriptable)) break;
            if (y instanceof ScriptableObject && (test = ((ScriptableObject)y).equivalentValues(x.toString())) != Scriptable.NOT_FOUND) {
                return (Boolean)test;
            }
            y = ScriptRuntime.toPrimitive(y);
        }
        ScriptRuntime.warnAboutNonJSObject(y);
        return false;
    }

    public static boolean shallowEq(Object x, Object y) {
        if (x == y) {
            if (!(x instanceof Number)) {
                return true;
            }
            double d = ((Number)x).doubleValue();
            return !Double.isNaN(d);
        }
        if (x == null || x == Undefined.instance || x == Undefined.SCRIPTABLE_UNDEFINED) {
            return x == Undefined.instance && y == Undefined.SCRIPTABLE_UNDEFINED || x == Undefined.SCRIPTABLE_UNDEFINED && y == Undefined.instance;
        }
        if (x instanceof BigInteger) {
            if (y instanceof BigInteger) {
                return x.equals(y);
            }
        } else if (x instanceof Number && !(x instanceof BigInteger)) {
            if (y instanceof Number && !(y instanceof BigInteger)) {
                return ((Number)x).doubleValue() == ((Number)y).doubleValue();
            }
        } else if (x instanceof CharSequence) {
            if (y instanceof CharSequence) {
                return x.toString().equals(y.toString());
            }
        } else if (x instanceof Boolean) {
            if (y instanceof Boolean) {
                return x.equals(y);
            }
        } else if (x instanceof Scriptable) {
            if (x instanceof Wrapper && y instanceof Wrapper) {
                return ((Wrapper)x).unwrap() == ((Wrapper)y).unwrap();
            }
            if (x instanceof Delegator) {
                x = ((Delegator)x).getDelegee();
                if (y instanceof Delegator) {
                    return ScriptRuntime.shallowEq(x, ((Delegator)y).getDelegee());
                }
                if (x == y) {
                    return true;
                }
            }
            if (y instanceof Delegator && ((Delegator)y).getDelegee() == x) {
                return true;
            }
        } else {
            ScriptRuntime.warnAboutNonJSObject(x);
            return x == y;
        }
        return false;
    }

    public static boolean instanceOf(Object a, Object b, Context cx) {
        if (!(b instanceof Scriptable)) {
            throw ScriptRuntime.typeErrorById("msg.instanceof.not.object", new Object[0]);
        }
        if (!(a instanceof Scriptable)) {
            return false;
        }
        return ((Scriptable)b).hasInstance((Scriptable)a);
    }

    public static boolean jsDelegatesTo(Scriptable lhs, Scriptable rhs) {
        for (Scriptable proto = lhs.getPrototype(); proto != null; proto = proto.getPrototype()) {
            if (!proto.equals(rhs)) continue;
            return true;
        }
        return false;
    }

    public static boolean in(Object a, Object b, Context cx) {
        if (!(b instanceof Scriptable)) {
            throw ScriptRuntime.typeErrorById("msg.in.not.object", new Object[0]);
        }
        return ScriptRuntime.hasObjectElem((Scriptable)b, a, cx);
    }

    public static boolean compare(Object val1, Object val2, int op) {
        assert (op == 17 || op == 15 || op == 16 || op == 14);
        if (val1 instanceof Number && val2 instanceof Number) {
            return ScriptRuntime.compare((Number)val1, (Number)val2, op);
        }
        if (val1 instanceof Symbol || val2 instanceof Symbol) {
            throw ScriptRuntime.typeErrorById("msg.compare.symbol", new Object[0]);
        }
        if (val1 instanceof Scriptable) {
            val1 = ((Scriptable)val1).getDefaultValue(NumberClass);
        }
        if (val2 instanceof Scriptable) {
            val2 = ((Scriptable)val2).getDefaultValue(NumberClass);
        }
        if (val1 instanceof CharSequence && val2 instanceof CharSequence) {
            return ScriptRuntime.compareTo(val1.toString(), val2.toString(), op);
        }
        return ScriptRuntime.compare(ScriptRuntime.toNumeric(val1), ScriptRuntime.toNumeric(val2), op);
    }

    public static boolean compare(Number val1, Number val2, int op) {
        assert (op == 17 || op == 15 || op == 16 || op == 14);
        if (val1 instanceof BigInteger && val2 instanceof BigInteger) {
            return ScriptRuntime.compareTo((BigInteger)val1, (BigInteger)val2, op);
        }
        if (val1 instanceof BigInteger || val2 instanceof BigInteger) {
            BigDecimal bd2;
            BigDecimal bd1;
            if (val1 instanceof BigInteger) {
                bd1 = new BigDecimal((BigInteger)val1);
            } else {
                double d = val1.doubleValue();
                if (Double.isNaN(d)) {
                    return false;
                }
                if (d == Double.POSITIVE_INFINITY) {
                    return op == 17 || op == 16;
                }
                if (d == Double.NEGATIVE_INFINITY) {
                    return op == 15 || op == 14;
                }
                bd1 = new BigDecimal(d, MathContext.UNLIMITED);
            }
            if (val2 instanceof BigInteger) {
                bd2 = new BigDecimal((BigInteger)val2);
            } else {
                double d = val2.doubleValue();
                if (Double.isNaN(d)) {
                    return false;
                }
                if (d == Double.POSITIVE_INFINITY) {
                    return op == 15 || op == 14;
                }
                if (d == Double.NEGATIVE_INFINITY) {
                    return op == 17 || op == 16;
                }
                bd2 = new BigDecimal(d, MathContext.UNLIMITED);
            }
            return ScriptRuntime.compareTo(bd1, bd2, op);
        }
        return ScriptRuntime.compareTo(val1.doubleValue(), val2.doubleValue(), op);
    }

    private static <T> boolean compareTo(Comparable<T> val1, T val2, int op) {
        switch (op) {
            case 17: {
                return val1.compareTo(val2) >= 0;
            }
            case 15: {
                return val1.compareTo(val2) <= 0;
            }
            case 16: {
                return val1.compareTo(val2) > 0;
            }
            case 14: {
                return val1.compareTo(val2) < 0;
            }
        }
        throw Kit.codeBug();
    }

    private static <T> boolean compareTo(double d1, double d2, int op) {
        switch (op) {
            case 17: {
                return d1 >= d2;
            }
            case 15: {
                return d1 <= d2;
            }
            case 16: {
                return d1 > d2;
            }
            case 14: {
                return d1 < d2;
            }
        }
        throw Kit.codeBug();
    }

    public static ScriptableObject getGlobal(Context cx) {
        String GLOBAL_CLASS = "org.mozilla.javascript.tools.shell.Global";
        Class<?> globalClass = Kit.classOrNull("org.mozilla.javascript.tools.shell.Global");
        if (globalClass != null) {
            try {
                Class[] parm = new Class[]{ContextClass};
                Constructor<?> globalClassCtor = globalClass.getConstructor(parm);
                Object[] arg = new Object[]{cx};
                return (ScriptableObject)globalClassCtor.newInstance(arg);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return new ImporterTopLevel(cx);
    }

    public static boolean hasTopCall(Context cx) {
        return cx.topCallScope != null;
    }

    public static Scriptable getTopCallScope(Context cx) {
        Scriptable scope = cx.topCallScope;
        if (scope == null) {
            throw new IllegalStateException();
        }
        return scope;
    }

    @Deprecated
    public static Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return ScriptRuntime.doTopCall(callable, cx, scope, thisObj, args, cx.isTopLevelStrict);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, boolean isTopLevelStrict) {
        Object result;
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        if (cx.topCallScope != null) {
            throw new IllegalStateException();
        }
        cx.topCallScope = ScriptableObject.getTopLevelScope(scope);
        cx.useDynamicScope = cx.hasFeature(7);
        boolean previousTopLevelStrict = cx.isTopLevelStrict;
        cx.isTopLevelStrict = isTopLevelStrict;
        ContextFactory f = cx.getFactory();
        try {
            result = f.doTopCall(callable, cx, scope, thisObj, args);
        }
        finally {
            cx.topCallScope = null;
            cx.cachedXMLLib = null;
            cx.isTopLevelStrict = previousTopLevelStrict;
            if (cx.currentActivationCall != null) {
                throw new IllegalStateException();
            }
        }
        return result;
    }

    static Scriptable checkDynamicScope(Scriptable possibleDynamicScope, Scriptable staticTopScope) {
        if (possibleDynamicScope == staticTopScope) {
            return possibleDynamicScope;
        }
        Scriptable proto = possibleDynamicScope;
        do {
            if ((proto = proto.getPrototype()) != staticTopScope) continue;
            return possibleDynamicScope;
        } while (proto != null);
        return staticTopScope;
    }

    public static void addInstructionCount(Context cx, int instructionsToAdd) {
        cx.instructionCount += instructionsToAdd;
        if (cx.instructionCount > cx.instructionThreshold) {
            cx.observeInstructionCount(cx.instructionCount);
            cx.instructionCount = 0;
        }
    }

    public static void initScript(NativeFunction funObj, Scriptable thisObj, Context cx, Scriptable scope, boolean evalScript) {
        if (cx.topCallScope == null) {
            throw new IllegalStateException();
        }
        int varCount = funObj.getParamAndVarCount();
        if (varCount != 0) {
            Scriptable varScope = scope;
            while (varScope instanceof NativeWith) {
                varScope = varScope.getParentScope();
            }
            int i = varCount;
            while (i-- != 0) {
                String name = funObj.getParamOrVarName(i);
                boolean isConst = funObj.getParamOrVarConst(i);
                if (!ScriptableObject.hasProperty(scope, name)) {
                    if (isConst) {
                        ScriptableObject.defineConstProperty(varScope, name);
                        continue;
                    }
                    if (!evalScript) {
                        if (funObj instanceof InterpretedFunction && !((InterpretedFunction)funObj).hasFunctionNamed(name)) continue;
                        ScriptableObject.defineProperty(varScope, name, Undefined.instance, 4);
                        continue;
                    }
                    varScope.put(name, varScope, Undefined.instance);
                    continue;
                }
                ScriptableObject.redefineProperty(scope, name, isConst);
            }
        }
    }

    @Deprecated
    public static Scriptable createFunctionActivation(NativeFunction funObj, Scriptable scope, Object[] args) {
        return ScriptRuntime.createFunctionActivation(funObj, scope, args, false);
    }

    public static Scriptable createFunctionActivation(NativeFunction funObj, Scriptable scope, Object[] args, boolean isStrict) {
        return new NativeCall(funObj, scope, args, false, isStrict);
    }

    public static Scriptable createArrowFunctionActivation(NativeFunction funObj, Scriptable scope, Object[] args, boolean isStrict) {
        return new NativeCall(funObj, scope, args, true, isStrict);
    }

    public static void enterActivationFunction(Context cx, Scriptable scope) {
        if (cx.topCallScope == null) {
            throw new IllegalStateException();
        }
        NativeCall call = (NativeCall)scope;
        call.parentActivationCall = cx.currentActivationCall;
        cx.currentActivationCall = call;
        call.defineAttributesForArguments();
    }

    public static void exitActivationFunction(Context cx) {
        NativeCall call = cx.currentActivationCall;
        cx.currentActivationCall = call.parentActivationCall;
        call.parentActivationCall = null;
    }

    static NativeCall findFunctionActivation(Context cx, Function f) {
        NativeCall call = cx.currentActivationCall;
        while (call != null) {
            if (call.function == f) {
                return call;
            }
            call = call.parentActivationCall;
        }
        return null;
    }

    public static Scriptable newCatchScope(Throwable t, Scriptable lastCatchScope, String exceptionName, Context cx, Scriptable scope) {
        Object obj;
        boolean cacheObj;
        if (t instanceof JavaScriptException) {
            cacheObj = false;
            obj = ((JavaScriptException)t).getValue();
        } else {
            cacheObj = true;
            if (lastCatchScope != null) {
                NativeObject last = (NativeObject)lastCatchScope;
                obj = last.getAssociatedValue(t);
                if (obj == null) {
                    Kit.codeBug();
                }
            } else {
                Object wrap;
                int line;
                Object[] args;
                Scriptable errorObject;
                String errorMsg;
                TopLevel.NativeErrors type;
                RhinoException re;
                RhinoException ee;
                Throwable javaException = null;
                if (t instanceof EcmaError) {
                    re = ee = (EcmaError)t;
                    type = TopLevel.NativeErrors.valueOf(((EcmaError)ee).getName());
                    errorMsg = ((EcmaError)ee).getErrorMessage();
                } else if (t instanceof WrappedException) {
                    WrappedException we = (WrappedException)t;
                    re = we;
                    javaException = we.getWrappedException();
                    if (!ScriptRuntime.isVisible(cx, javaException)) {
                        type = TopLevel.NativeErrors.InternalError;
                        errorMsg = javaException.getMessage();
                    } else {
                        type = TopLevel.NativeErrors.JavaException;
                        errorMsg = javaException.getClass().getName() + ": " + javaException.getMessage();
                    }
                } else if (t instanceof EvaluatorException) {
                    re = ee = (EvaluatorException)t;
                    type = TopLevel.NativeErrors.InternalError;
                    errorMsg = ee.getMessage();
                } else if (cx.hasFeature(13)) {
                    re = new WrappedException(t);
                    type = TopLevel.NativeErrors.JavaException;
                    errorMsg = t.toString();
                } else {
                    throw Kit.codeBug();
                }
                String sourceUri = re.sourceName();
                if (sourceUri == null) {
                    sourceUri = "";
                }
                if ((errorObject = ScriptRuntime.newNativeError(cx, scope, type, args = (line = re.lineNumber()) > 0 ? new Object[]{errorMsg, sourceUri, line} : new Object[]{errorMsg, sourceUri})) instanceof NativeError) {
                    ((NativeError)errorObject).setStackProvider(re);
                }
                if (javaException != null && ScriptRuntime.isVisible(cx, javaException)) {
                    wrap = cx.getWrapFactory().wrap(cx, scope, javaException, null);
                    ScriptableObject.defineProperty(errorObject, "javaException", wrap, 7);
                }
                if (ScriptRuntime.isVisible(cx, re)) {
                    wrap = cx.getWrapFactory().wrap(cx, scope, re, null);
                    ScriptableObject.defineProperty(errorObject, "rhinoException", wrap, 7);
                }
                obj = errorObject;
            }
        }
        NativeObject catchScopeObject = new NativeObject();
        catchScopeObject.defineProperty(exceptionName, obj, 4);
        if (ScriptRuntime.isVisible(cx, t)) {
            catchScopeObject.defineProperty("__exception__", Context.javaToJS(t, scope), 6);
        }
        if (cacheObj) {
            catchScopeObject.associateValue(t, obj);
        }
        return catchScopeObject;
    }

    public static Scriptable wrapException(Throwable t, Scriptable scope, Context cx) {
        Object wrap;
        int line;
        String errorMsg;
        String errorName;
        RhinoException re;
        RhinoException ee;
        Throwable javaException = null;
        if (t instanceof EcmaError) {
            re = ee = (EcmaError)t;
            errorName = ((EcmaError)ee).getName();
            errorMsg = ((EcmaError)ee).getErrorMessage();
        } else if (t instanceof WrappedException) {
            WrappedException we = (WrappedException)t;
            re = we;
            javaException = we.getWrappedException();
            errorName = "JavaException";
            errorMsg = javaException.getClass().getName() + ": " + javaException.getMessage();
        } else if (t instanceof EvaluatorException) {
            re = ee = (EvaluatorException)t;
            errorName = "InternalError";
            errorMsg = ee.getMessage();
        } else if (cx.hasFeature(13)) {
            re = new WrappedException(t);
            errorName = "JavaException";
            errorMsg = t.toString();
        } else {
            throw Kit.codeBug();
        }
        String sourceUri = re.sourceName();
        if (sourceUri == null) {
            sourceUri = "";
        }
        Object[] args = (line = re.lineNumber()) > 0 ? new Object[]{errorMsg, sourceUri, line} : new Object[]{errorMsg, sourceUri};
        Scriptable errorObject = cx.newObject(scope, errorName, args);
        ScriptableObject.putProperty(errorObject, "name", (Object)errorName);
        if (errorObject instanceof NativeError) {
            ((NativeError)errorObject).setStackProvider(re);
        }
        if (javaException != null && ScriptRuntime.isVisible(cx, javaException)) {
            wrap = cx.getWrapFactory().wrap(cx, scope, javaException, null);
            ScriptableObject.defineProperty(errorObject, "javaException", wrap, 7);
        }
        if (ScriptRuntime.isVisible(cx, re)) {
            wrap = cx.getWrapFactory().wrap(cx, scope, re, null);
            ScriptableObject.defineProperty(errorObject, "rhinoException", wrap, 7);
        }
        return errorObject;
    }

    private static boolean isVisible(Context cx, Object obj) {
        ClassShutter shutter = cx.getClassShutter();
        return shutter == null || shutter.visibleToScripts(obj.getClass().getName());
    }

    public static Scriptable enterWith(Object obj, Context cx, Scriptable scope) {
        Scriptable sobj = ScriptRuntime.toObjectOrNull(cx, obj, scope);
        if (sobj == null) {
            throw ScriptRuntime.typeErrorById("msg.undef.with", ScriptRuntime.toString(obj));
        }
        if (sobj instanceof XMLObject) {
            XMLObject xmlObject = (XMLObject)sobj;
            return xmlObject.enterWith(scope);
        }
        return new NativeWith(scope, sobj);
    }

    public static Scriptable leaveWith(Scriptable scope) {
        NativeWith nw = (NativeWith)scope;
        return nw.getParentScope();
    }

    public static Scriptable enterDotQuery(Object value, Scriptable scope) {
        if (!(value instanceof XMLObject)) {
            throw ScriptRuntime.notXmlError(value);
        }
        XMLObject object = (XMLObject)value;
        return object.enterDotQuery(scope);
    }

    public static Object updateDotQuery(boolean value, Scriptable scope) {
        NativeWith nw = (NativeWith)scope;
        return nw.updateDotQuery(value);
    }

    public static Scriptable leaveDotQuery(Scriptable scope) {
        NativeWith nw = (NativeWith)scope;
        return nw.getParentScope();
    }

    public static void setFunctionProtoAndParent(BaseFunction fn, Scriptable scope) {
        ScriptRuntime.setFunctionProtoAndParent(fn, scope, false);
    }

    public static void setFunctionProtoAndParent(BaseFunction fn, Scriptable scope, boolean es6GeneratorFunction) {
        fn.setParentScope(scope);
        if (es6GeneratorFunction) {
            fn.setPrototype(ScriptableObject.getGeneratorFunctionPrototype(scope));
        } else {
            fn.setPrototype(ScriptableObject.getFunctionPrototype(scope));
        }
    }

    public static void setObjectProtoAndParent(ScriptableObject object, Scriptable scope) {
        scope = ScriptableObject.getTopLevelScope(scope);
        object.setParentScope(scope);
        Scriptable proto = ScriptableObject.getClassPrototype(scope, object.getClassName());
        object.setPrototype(proto);
    }

    public static void setBuiltinProtoAndParent(ScriptableObject object, Scriptable scope, TopLevel.Builtins type) {
        scope = ScriptableObject.getTopLevelScope(scope);
        object.setParentScope(scope);
        object.setPrototype(TopLevel.getBuiltinPrototype(scope, type));
    }

    public static void initFunction(Context cx, Scriptable scope, NativeFunction function, int type, boolean fromEvalCode) {
        if (type == 1) {
            String name = function.getFunctionName();
            if (name != null && name.length() != 0) {
                if (!fromEvalCode) {
                    ScriptableObject.defineProperty(scope, name, function, 4);
                } else {
                    scope.put(name, scope, (Object)function);
                }
            }
        } else if (type == 3) {
            String name = function.getFunctionName();
            if (name != null && name.length() != 0) {
                while (scope instanceof NativeWith) {
                    scope = scope.getParentScope();
                }
                scope.put(name, scope, (Object)function);
            }
        } else {
            throw Kit.codeBug();
        }
    }

    public static Scriptable newArrayLiteral(Object[] objects, int[] skipIndices, Context cx, Scriptable scope) {
        int length;
        int SKIP_DENSITY = 2;
        int count = objects.length;
        int skipCount = 0;
        if (skipIndices != null) {
            skipCount = skipIndices.length;
        }
        if ((length = count + skipCount) > 1 && skipCount * 2 < length) {
            Object[] sparse;
            if (skipCount == 0) {
                sparse = objects;
            } else {
                sparse = new Object[length];
                int skip = 0;
                int j = 0;
                for (int i = 0; i != length; ++i) {
                    if (skip != skipCount && skipIndices[skip] == i) {
                        sparse[i] = Scriptable.NOT_FOUND;
                        ++skip;
                        continue;
                    }
                    sparse[i] = objects[j];
                    ++j;
                }
            }
            return cx.newArray(scope, sparse);
        }
        Scriptable array = cx.newArray(scope, length);
        int skip = 0;
        int j = 0;
        for (int i = 0; i != length; ++i) {
            if (skip != skipCount && skipIndices[skip] == i) {
                ++skip;
                continue;
            }
            array.put(i, array, objects[j]);
            ++j;
        }
        return array;
    }

    @Deprecated
    public static Scriptable newObjectLiteral(Object[] propertyIds, Object[] propertyValues, Context cx, Scriptable scope) {
        return ScriptRuntime.newObjectLiteral(propertyIds, propertyValues, null, cx, scope);
    }

    public static Scriptable newObjectLiteral(Object[] propertyIds, Object[] propertyValues, int[] getterSetters, Context cx, Scriptable scope) {
        Scriptable object = cx.newObject(scope);
        int end = propertyIds == null ? 0 : propertyIds.length;
        for (int i = 0; i != end; ++i) {
            Object id = propertyIds[i];
            int getterSetter = getterSetters == null ? 0 : getterSetters[i];
            Object value = propertyValues[i];
            if (getterSetter == 0) {
                if (id instanceof Symbol) {
                    Symbol sym = (Symbol)id;
                    SymbolScriptable so = (SymbolScriptable)((Object)object);
                    so.put(sym, object, value);
                    continue;
                }
                if (id instanceof Integer) {
                    int index = (Integer)id;
                    object.put(index, object, value);
                    continue;
                }
                String stringId = ScriptRuntime.toString(id);
                if (ScriptRuntime.isSpecialProperty(stringId)) {
                    Ref ref = ScriptRuntime.specialRef(object, stringId, cx, scope);
                    ref.set(cx, scope, value);
                    continue;
                }
                object.put(stringId, object, value);
                continue;
            }
            ScriptableObject so = (ScriptableObject)object;
            Callable getterOrSetter = (Callable)value;
            boolean isSetter = getterSetter == 1;
            String key = id instanceof String ? (String)id : null;
            int index = key == null ? (Integer)id : 0;
            so.setGetterOrSetter(key, index, getterOrSetter, isSetter);
        }
        return object;
    }

    public static boolean isArrayObject(Object obj) {
        return obj instanceof NativeArray || obj instanceof Arguments;
    }

    public static Object[] getArrayElements(Scriptable object) {
        Context cx = Context.getContext();
        long longLen = NativeArray.getLengthProperty(cx, object);
        if (longLen > Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        int len = (int)longLen;
        if (len == 0) {
            return emptyArgs;
        }
        Object[] result = new Object[len];
        for (int i = 0; i < len; ++i) {
            Object elem = ScriptableObject.getProperty(object, i);
            result[i] = elem == Scriptable.NOT_FOUND ? Undefined.instance : elem;
        }
        return result;
    }

    static void checkDeprecated(Context cx, String name) {
        int version = cx.getLanguageVersion();
        if (version >= 140 || version == 0) {
            String msg = ScriptRuntime.getMessageById("msg.deprec.ctor", name);
            if (version == 0) {
                Context.reportWarning(msg);
            } else {
                throw Context.reportRuntimeError(msg);
            }
        }
    }

    @Deprecated
    public static String getMessage0(String messageId) {
        return ScriptRuntime.getMessage(messageId, null);
    }

    @Deprecated
    public static String getMessage1(String messageId, Object arg1) {
        Object[] arguments = new Object[]{arg1};
        return ScriptRuntime.getMessage(messageId, arguments);
    }

    @Deprecated
    public static String getMessage2(String messageId, Object arg1, Object arg2) {
        Object[] arguments = new Object[]{arg1, arg2};
        return ScriptRuntime.getMessage(messageId, arguments);
    }

    @Deprecated
    public static String getMessage3(String messageId, Object arg1, Object arg2, Object arg3) {
        Object[] arguments = new Object[]{arg1, arg2, arg3};
        return ScriptRuntime.getMessage(messageId, arguments);
    }

    @Deprecated
    public static String getMessage4(String messageId, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] arguments = new Object[]{arg1, arg2, arg3, arg4};
        return ScriptRuntime.getMessage(messageId, arguments);
    }

    @Deprecated
    public static String getMessage(String messageId, Object[] arguments) {
        return messageProvider.getMessage(messageId, arguments);
    }

    public static String getMessageById(String messageId, Object ... args) {
        return messageProvider.getMessage(messageId, args);
    }

    public static EcmaError constructError(String error, String message) {
        int[] linep = new int[1];
        String filename = Context.getSourcePositionFromStack(linep);
        return ScriptRuntime.constructError(error, message, filename, linep[0], null, 0);
    }

    public static EcmaError constructError(String error, String message, int lineNumberDelta) {
        int[] linep = new int[1];
        String filename = Context.getSourcePositionFromStack(linep);
        if (linep[0] != 0) {
            linep[0] = linep[0] + lineNumberDelta;
        }
        return ScriptRuntime.constructError(error, message, filename, linep[0], null, 0);
    }

    public static EcmaError constructError(String error, String message, String sourceName, int lineNumber, String lineSource, int columnNumber) {
        return new EcmaError(error, message, sourceName, lineNumber, lineSource, columnNumber);
    }

    public static EcmaError rangeError(String message) {
        return ScriptRuntime.constructError("RangeError", message);
    }

    public static EcmaError rangeErrorById(String messageId, Object ... args) {
        String msg = ScriptRuntime.getMessageById(messageId, args);
        return ScriptRuntime.rangeError(msg);
    }

    public static EcmaError typeError(String message) {
        return ScriptRuntime.constructError("TypeError", message);
    }

    public static EcmaError typeErrorById(String messageId, Object ... args) {
        String msg = ScriptRuntime.getMessageById(messageId, args);
        return ScriptRuntime.typeError(msg);
    }

    @Deprecated
    public static EcmaError typeError0(String messageId) {
        String msg = ScriptRuntime.getMessage0(messageId);
        return ScriptRuntime.typeError(msg);
    }

    @Deprecated
    public static EcmaError typeError1(String messageId, Object arg1) {
        String msg = ScriptRuntime.getMessage1(messageId, arg1);
        return ScriptRuntime.typeError(msg);
    }

    @Deprecated
    public static EcmaError typeError2(String messageId, Object arg1, Object arg2) {
        String msg = ScriptRuntime.getMessage2(messageId, arg1, arg2);
        return ScriptRuntime.typeError(msg);
    }

    @Deprecated
    public static EcmaError typeError3(String messageId, String arg1, String arg2, String arg3) {
        String msg = ScriptRuntime.getMessage3(messageId, arg1, arg2, arg3);
        return ScriptRuntime.typeError(msg);
    }

    public static RuntimeException undefReadError(Object object, Object id) {
        return ScriptRuntime.typeErrorById("msg.undef.prop.read", ScriptRuntime.toString(object), ScriptRuntime.toString(id));
    }

    public static RuntimeException undefCallError(Object object, Object id) {
        return ScriptRuntime.typeErrorById("msg.undef.method.call", ScriptRuntime.toString(object), ScriptRuntime.toString(id));
    }

    public static RuntimeException undefWriteError(Object object, Object id, Object value) {
        return ScriptRuntime.typeErrorById("msg.undef.prop.write", ScriptRuntime.toString(object), ScriptRuntime.toString(id), ScriptRuntime.toString(value));
    }

    private static RuntimeException undefDeleteError(Object object, Object id) {
        throw ScriptRuntime.typeErrorById("msg.undef.prop.delete", ScriptRuntime.toString(object), ScriptRuntime.toString(id));
    }

    public static RuntimeException notFoundError(Scriptable object, String property) {
        String msg = ScriptRuntime.getMessageById("msg.is.not.defined", property);
        throw ScriptRuntime.constructError("ReferenceError", msg);
    }

    public static RuntimeException notFunctionError(Object value) {
        return ScriptRuntime.notFunctionError(value, value);
    }

    public static RuntimeException notFunctionError(Object value, Object messageHelper) {
        String msg;
        String string = msg = messageHelper == null ? "null" : messageHelper.toString();
        if (value == Scriptable.NOT_FOUND) {
            return ScriptRuntime.typeErrorById("msg.function.not.found", msg);
        }
        return ScriptRuntime.typeErrorById("msg.isnt.function", msg, ScriptRuntime.typeof(value));
    }

    public static RuntimeException notFunctionError(Object obj, Object value, String propertyName) {
        int paren;
        int curly;
        String objString = ScriptRuntime.toString(obj);
        if (obj instanceof NativeFunction && (curly = objString.indexOf(123, paren = objString.indexOf(41))) > -1) {
            objString = objString.substring(0, curly + 1) + "...}";
        }
        if (value == Scriptable.NOT_FOUND) {
            return ScriptRuntime.typeErrorById("msg.function.not.found.in", propertyName, objString);
        }
        return ScriptRuntime.typeErrorById("msg.isnt.function.in", propertyName, objString, ScriptRuntime.typeof(value));
    }

    private static RuntimeException notXmlError(Object value) {
        throw ScriptRuntime.typeErrorById("msg.isnt.xml.object", ScriptRuntime.toString(value));
    }

    public static EcmaError syntaxError(String message) {
        return ScriptRuntime.constructError("SyntaxError", message);
    }

    public static EcmaError syntaxErrorById(String messageId, Object ... args) {
        String msg = ScriptRuntime.getMessageById(messageId, args);
        return ScriptRuntime.syntaxError(msg);
    }

    private static void warnAboutNonJSObject(Object nonJSObject) {
        String omitParam = ScriptRuntime.getMessageById("params.omit.non.js.object.warning", new Object[0]);
        if (!"true".equals(omitParam)) {
            String message = ScriptRuntime.getMessageById("msg.non.js.object.warning", nonJSObject, nonJSObject.getClass().getName());
            Context.reportWarning(message);
            System.err.println(message);
        }
    }

    public static RegExpProxy getRegExpProxy(Context cx) {
        return cx.getRegExpProxy();
    }

    public static void setRegExpProxy(Context cx, RegExpProxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException();
        }
        cx.regExpProxy = proxy;
    }

    public static RegExpProxy checkRegExpProxy(Context cx) {
        RegExpProxy result = ScriptRuntime.getRegExpProxy(cx);
        if (result == null) {
            throw Context.reportRuntimeErrorById("msg.no.regexp", new Object[0]);
        }
        return result;
    }

    public static Scriptable wrapRegExp(Context cx, Scriptable scope, Object compiled) {
        return cx.getRegExpProxy().wrapRegExp(cx, scope, compiled);
    }

    public static Scriptable getTemplateLiteralCallSite(Context cx, Scriptable scope, Object[] strings, int index) {
        Object callsite = strings[index];
        if (callsite instanceof Scriptable) {
            return (Scriptable)callsite;
        }
        assert (callsite instanceof String[]);
        String[] vals = (String[])callsite;
        assert ((vals.length & 1) == 0);
        ScriptableObject siteObj = (ScriptableObject)cx.newArray(scope, vals.length >>> 1);
        ScriptableObject rawObj = (ScriptableObject)cx.newArray(scope, vals.length >>> 1);
        siteObj.put("raw", (Scriptable)siteObj, (Object)rawObj);
        siteObj.setAttributes("raw", 2);
        int n = vals.length;
        for (int i = 0; i < n; i += 2) {
            int idx = i >>> 1;
            siteObj.put(idx, (Scriptable)siteObj, vals[i] == null ? Undefined.instance : vals[i]);
            rawObj.put(idx, (Scriptable)rawObj, (Object)vals[i + 1]);
        }
        AbstractEcmaObjectOperations.setIntegrityLevel(cx, rawObj, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.FROZEN);
        AbstractEcmaObjectOperations.setIntegrityLevel(cx, siteObj, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.FROZEN);
        strings[index] = siteObj;
        return siteObj;
    }

    private static XMLLib currentXMLLib(Context cx) {
        if (cx.topCallScope == null) {
            throw new IllegalStateException();
        }
        XMLLib xmlLib = cx.cachedXMLLib;
        if (xmlLib == null) {
            xmlLib = XMLLib.extractFromScope(cx.topCallScope);
            if (xmlLib == null) {
                throw new IllegalStateException();
            }
            cx.cachedXMLLib = xmlLib;
        }
        return xmlLib;
    }

    public static String escapeAttributeValue(Object value, Context cx) {
        XMLLib xmlLib = ScriptRuntime.currentXMLLib(cx);
        return xmlLib.escapeAttributeValue(value);
    }

    public static String escapeTextValue(Object value, Context cx) {
        XMLLib xmlLib = ScriptRuntime.currentXMLLib(cx);
        return xmlLib.escapeTextValue(value);
    }

    public static Ref memberRef(Object obj, Object elem, Context cx, int memberTypeFlags) {
        if (!(obj instanceof XMLObject)) {
            throw ScriptRuntime.notXmlError(obj);
        }
        XMLObject xmlObject = (XMLObject)obj;
        return xmlObject.memberRef(cx, elem, memberTypeFlags);
    }

    public static Ref memberRef(Object obj, Object namespace, Object elem, Context cx, int memberTypeFlags) {
        if (!(obj instanceof XMLObject)) {
            throw ScriptRuntime.notXmlError(obj);
        }
        XMLObject xmlObject = (XMLObject)obj;
        return xmlObject.memberRef(cx, namespace, elem, memberTypeFlags);
    }

    public static Ref nameRef(Object name, Context cx, Scriptable scope, int memberTypeFlags) {
        XMLLib xmlLib = ScriptRuntime.currentXMLLib(cx);
        return xmlLib.nameRef(cx, name, scope, memberTypeFlags);
    }

    public static Ref nameRef(Object namespace, Object name, Context cx, Scriptable scope, int memberTypeFlags) {
        XMLLib xmlLib = ScriptRuntime.currentXMLLib(cx);
        return xmlLib.nameRef(cx, namespace, name, scope, memberTypeFlags);
    }

    public static void storeUint32Result(Context cx, long value) {
        if (value >>> 32 != 0L) {
            throw new IllegalArgumentException();
        }
        cx.scratchUint32 = value;
    }

    public static long lastUint32Result(Context cx) {
        long value = cx.scratchUint32;
        if (value >>> 32 != 0L) {
            throw new IllegalStateException();
        }
        return value;
    }

    private static void storeScriptable(Context cx, Scriptable value) {
        if (cx.scratchScriptable != null) {
            throw new IllegalStateException();
        }
        cx.scratchScriptable = value;
    }

    public static Scriptable lastStoredScriptable(Context cx) {
        Scriptable result = cx.scratchScriptable;
        cx.scratchScriptable = null;
        return result;
    }

    static String makeUrlForGeneratedScript(boolean isEval, String masterScriptUrl, int masterScriptLine) {
        if (isEval) {
            return masterScriptUrl + '#' + masterScriptLine + "(eval)";
        }
        return masterScriptUrl + '#' + masterScriptLine + "(Function)";
    }

    static boolean isGeneratedScript(String sourceUrl) {
        return sourceUrl.indexOf("(eval)") >= 0 || sourceUrl.indexOf("(Function)") >= 0;
    }

    static boolean isSymbol(Object obj) {
        return obj instanceof NativeSymbol && ((NativeSymbol)obj).isSymbol() || obj instanceof SymbolKey;
    }

    private static RuntimeException errorWithClassName(String msg, Object val) {
        return Context.reportRuntimeErrorById(msg, val.getClass().getName());
    }

    public static JavaScriptException throwError(Context cx, Scriptable scope, String message) {
        int[] linep = new int[]{0};
        String filename = Context.getSourcePositionFromStack(linep);
        Scriptable error = ScriptRuntime.newBuiltinObject(cx, scope, TopLevel.Builtins.Error, new Object[]{message, filename, linep[0]});
        return new JavaScriptException(error, filename, linep[0]);
    }

    public static JavaScriptException throwCustomError(Context cx, Scriptable scope, String constructorName, String message) {
        int[] linep = new int[]{0};
        String filename = Context.getSourcePositionFromStack(linep);
        Scriptable error = cx.newObject(scope, constructorName, new Object[]{message, filename, linep[0]});
        return new JavaScriptException(error, filename, linep[0]);
    }

    private static class DefaultMessageProvider
    implements MessageProvider {
        private DefaultMessageProvider() {
        }

        @Override
        public String getMessage(String messageId, Object[] arguments) {
            String formatString;
            String defaultResource = "org.mozilla.javascript.resources.Messages";
            Context cx = Context.getCurrentContext();
            Locale locale = cx != null ? cx.getLocale() : Locale.getDefault();
            ResourceBundle rb = ResourceBundle.getBundle("org.mozilla.javascript.resources.Messages", locale);
            try {
                formatString = rb.getString(messageId);
            }
            catch (MissingResourceException mre) {
                throw new RuntimeException("no message resource found for message property " + messageId);
            }
            MessageFormat formatter = new MessageFormat(formatString);
            return formatter.format(arguments);
        }
    }

    public static interface MessageProvider {
        public String getMessage(String var1, Object[] var2);
    }

    private static class IdEnumeration
    implements Serializable {
        private static final long serialVersionUID = 1L;
        Scriptable obj;
        Object[] ids;
        ObjToIntMap used;
        Object currentId;
        int index;
        int enumType;
        boolean enumNumbers;
        Scriptable iterator;

        private IdEnumeration() {
        }
    }

    static final class StringIdOrIndex {
        final String stringId;
        final int index;

        StringIdOrIndex(String stringId) {
            this.stringId = stringId;
            this.index = -1;
        }

        StringIdOrIndex(int index) {
            this.stringId = null;
            this.index = index;
        }
    }

    static class NoSuchMethodShim
    implements Callable {
        String methodName;
        Callable noSuchMethodMethod;

        NoSuchMethodShim(Callable noSuchMethodMethod, String methodName) {
            this.noSuchMethodMethod = noSuchMethodMethod;
            this.methodName = methodName;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            Object[] nestedArgs = new Object[]{this.methodName, ScriptRuntime.newArrayLiteral(args, null, cx, scope)};
            return this.noSuchMethodMethod.call(cx, scope, thisObj, nestedArgs);
        }
    }
}

