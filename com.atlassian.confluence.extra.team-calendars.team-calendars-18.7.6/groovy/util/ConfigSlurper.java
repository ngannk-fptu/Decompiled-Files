/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;
import groovy.lang.Reference;
import groovy.lang.Script;
import groovy.util.ConfigBinding;
import groovy.util.ConfigObject;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ConfigSlurper
implements GroovyObject {
    private static final Object ENVIRONMENTS_METHOD;
    private GroovyClassLoader classLoader;
    private Map bindingVars;
    private final Map<String, String> conditionValues;
    private final Stack<Map<String, ConfigObject>> conditionalBlocks;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConfigSlurper() {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        this("");
    }

    public ConfigSlurper(String env) {
        MetaClass metaClass;
        Map map;
        Map map2;
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(GroovyClassLoader.class);
        this.classLoader = (GroovyClassLoader)ScriptBytecodeAdapter.castToType(object, GroovyClassLoader.class);
        this.bindingVars = map2 = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.conditionValues = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object2 = callSiteArray[1].callConstructor(Stack.class);
        this.conditionalBlocks = (Stack)ScriptBytecodeAdapter.castToType(object2, Stack.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        String string = env;
        callSiteArray[2].call(this.conditionValues, ENVIRONMENTS_METHOD, string);
    }

    public void registerConditionalBlock(String blockName, String blockValue) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(blockName)) {
            if (!DefaultTypeTransformation.booleanUnbox(blockValue)) {
                callSiteArray[3].call(this.conditionValues, blockName);
            } else {
                String string = blockValue;
                callSiteArray[4].call(this.conditionValues, blockName, string);
            }
        }
    }

    public Map<String, String> getConditionalBlockValues() {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return (Map)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(Collections.class, this.conditionValues), Map.class);
    }

    public String getEnvironment() {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[6].call(this.conditionValues, ENVIRONMENTS_METHOD));
    }

    public void setEnvironment(String environment) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        String string = environment;
        callSiteArray[7].call(this.conditionValues, ENVIRONMENTS_METHOD, string);
    }

    public void setBinding(Map vars) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        Map map = vars;
        this.bindingVars = (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    public ConfigObject parse(Properties properties) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        ConfigObject config = (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(ConfigObject.class), ConfigObject.class);
        Object key = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[9].call(callSiteArray[10].call(properties)), Iterator.class);
        while (iterator.hasNext()) {
            key = iterator.next();
            Object tokens = callSiteArray[11].call(key, "\\.");
            Object current = config;
            Reference<Object> last = new Reference<Object>(null);
            last.get();
            Object lastToken = null;
            Boolean foundBase = false;
            Object token = null;
            Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(tokens), Iterator.class);
            while (iterator2.hasNext()) {
                Object object;
                token = iterator2.next();
                if (DefaultTypeTransformation.booleanUnbox(foundBase)) {
                    callSiteArray[13].call(lastToken, callSiteArray[14].call((Object)".", (Object)token));
                    Object var13_13 = last.get();
                    current = var13_13;
                    continue;
                }
                ConfigObject configObject = current;
                last.set(configObject);
                Object var15_15 = token;
                lastToken = var15_15;
                current = object = ScriptBytecodeAdapter.getProperty(ConfigSlurper.class, current, ShortTypeHandling.castToString(new GStringImpl(new Object[]{token}, new String[]{"", ""})));
                if (!(!(current instanceof ConfigObject))) continue;
                boolean bl = true;
                foundBase = bl;
            }
            if (current instanceof ConfigObject) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(last.get(), lastToken))) {
                    Object flattened = callSiteArray[16].call(last.get());
                    callSiteArray[17].call(last.get());
                    public class _parse_closure1
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference last;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _parse_closure1(Object _outerInstance, Object _thisObject, Reference last) {
                            Reference reference;
                            CallSite[] callSiteArray = _parse_closure1.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.last = reference = last;
                        }

                        public Object doCall(Object k2, Object v2) {
                            CallSite[] callSiteArray = _parse_closure1.$getCallSiteArray();
                            Object object = v2;
                            callSiteArray[0].call(this.last.get(), k2, object);
                            return object;
                        }

                        public Object call(Object k2, Object v2) {
                            CallSite[] callSiteArray = _parse_closure1.$getCallSiteArray();
                            return callSiteArray[1].callCurrent(this, k2, v2);
                        }

                        public Object getLast() {
                            CallSite[] callSiteArray = _parse_closure1.$getCallSiteArray();
                            return this.last.get();
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _parse_closure1.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "putAt";
                            stringArray[1] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[2];
                            _parse_closure1.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_parse_closure1.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _parse_closure1.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[18].call(flattened, new _parse_closure1(this, this, last));
                    Object object = callSiteArray[19].call((Object)properties, (Object)key);
                    callSiteArray[20].call(last.get(), lastToken, object);
                } else {
                    Object object = callSiteArray[21].call((Object)properties, (Object)key);
                    callSiteArray[22].call(last.get(), lastToken, object);
                }
            }
            ConfigObject configObject = config;
            current = configObject;
        }
        return config;
    }

    public ConfigObject parse(String script) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[23].callCurrent((GroovyObject)this, callSiteArray[24].call((Object)this.classLoader, script)), ConfigObject.class);
    }

    public ConfigObject parse(Class scriptClass) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[25].callCurrent((GroovyObject)this, callSiteArray[26].call(scriptClass)), ConfigObject.class);
    }

    public ConfigObject parse(Script script) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[27].callCurrent(this, script, null), ConfigObject.class);
    }

    public ConfigObject parse(URL scriptLocation) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        return (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[28].callCurrent(this, callSiteArray[29].call(callSiteArray[30].call((Object)this.classLoader, callSiteArray[31].callGetProperty(scriptLocation))), scriptLocation), ConfigObject.class);
    }

    public ConfigObject parse(Script script, URL location) {
        CallSite[] callSiteArray = ConfigSlurper.$getCallSiteArray();
        Reference<Stack> currentConditionalBlock = new Reference<Stack>((Stack)ScriptBytecodeAdapter.castToType(callSiteArray[32].callConstructor(Stack.class), Stack.class));
        Reference<ConfigObject> config = new Reference<ConfigObject>(DefaultTypeTransformation.booleanUnbox(location) ? (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[33].callConstructor(ConfigObject.class, location), ConfigObject.class) : (ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[34].callConstructor(ConfigObject.class), ConfigObject.class));
        callSiteArray[35].call(callSiteArray[36].callGetProperty(GroovySystem.class), callSiteArray[37].callGroovyObjectGetProperty(script));
        Reference<Object> mc = new Reference<Object>(callSiteArray[38].callGetProperty(callSiteArray[39].callGroovyObjectGetProperty(script)));
        Reference<String> prefix = new Reference<String>("");
        Reference<LinkedList> stack = new Reference<LinkedList>((LinkedList)ScriptBytecodeAdapter.castToType(callSiteArray[40].callConstructor(LinkedList.class), LinkedList.class));
        callSiteArray[41].call((Object)stack.get(), ScriptBytecodeAdapter.createMap(new Object[]{"config", config.get(), "scope", ScriptBytecodeAdapter.createMap(new Object[0])}));
        public class _parse_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference stack;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parse_closure2(Object _outerInstance, Object _thisObject, Reference stack) {
                Reference reference;
                CallSite[] callSiteArray = _parse_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.stack = reference = stack;
            }

            public Object doCall(Object co) {
                CallSite[] callSiteArray = _parse_closure2.$getCallSiteArray();
                return callSiteArray[0].call(this.stack.get(), ScriptBytecodeAdapter.createMap(new Object[]{"config", co, "scope", callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(this.stack.get())))}));
            }

            public LinkedList getStack() {
                CallSite[] callSiteArray = _parse_closure2.$getCallSiteArray();
                return (LinkedList)ScriptBytecodeAdapter.castToType(this.stack.get(), LinkedList.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parse_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "clone";
                stringArray[2] = "scope";
                stringArray[3] = "last";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _parse_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parse_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parse_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<_parse_closure2> pushStack = new Reference<_parse_closure2>(new _parse_closure2(this, this, stack));
        public class _parse_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference stack;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parse_closure3(Object _outerInstance, Object _thisObject, Reference stack) {
                Reference reference;
                CallSite[] callSiteArray = _parse_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.stack = reference = stack;
            }

            public Object doCall(Object name, Object co) {
                CallSite[] callSiteArray = _parse_closure3.$getCallSiteArray();
                Object current = callSiteArray[0].callGetProperty(this.stack.get());
                Object object = co;
                callSiteArray[1].call(callSiteArray[2].callGetProperty(current), name, object);
                Object object2 = co;
                callSiteArray[3].call(callSiteArray[4].callGetProperty(current), name, object2);
                return object2;
            }

            public Object call(Object name, Object co) {
                CallSite[] callSiteArray = _parse_closure3.$getCallSiteArray();
                return callSiteArray[5].callCurrent(this, name, co);
            }

            public LinkedList getStack() {
                CallSite[] callSiteArray = _parse_closure3.$getCallSiteArray();
                return (LinkedList)ScriptBytecodeAdapter.castToType(this.stack.get(), LinkedList.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parse_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "last";
                stringArray[1] = "putAt";
                stringArray[2] = "config";
                stringArray[3] = "putAt";
                stringArray[4] = "scope";
                stringArray[5] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _parse_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parse_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parse_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<_parse_closure3> assignName = new Reference<_parse_closure3>(new _parse_closure3(this, this, stack));
        public class _parse_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference stack;
            private /* synthetic */ Reference assignName;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parse_closure4(Object _outerInstance, Object _thisObject, Reference stack, Reference assignName) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _parse_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.stack = reference2 = stack;
                this.assignName = reference = assignName;
            }

            public Object doCall(String name) {
                CallSite[] callSiteArray = _parse_closure4.$getCallSiteArray();
                Object current = callSiteArray[0].callGetProperty(this.stack.get());
                Object result = null;
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGetProperty(current), name))) {
                    Object object;
                    result = object = callSiteArray[3].call(callSiteArray[4].callGetProperty(current), name);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(callSiteArray[6].callGetProperty(current), name))) {
                    Object object;
                    result = object = callSiteArray[7].call(callSiteArray[8].callGetProperty(current), name);
                } else {
                    try {
                        Object object;
                        result = object = callSiteArray[9].call(InvokerHelper.class, this.getThisObject(), name);
                    }
                    catch (GroovyRuntimeException e) {
                        Object object;
                        result = object = callSiteArray[10].callConstructor(ConfigObject.class);
                        callSiteArray[11].call(this.assignName.get(), name, result);
                    }
                }
                return result;
            }

            public Object call(String name) {
                CallSite[] callSiteArray = _parse_closure4.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[12].callCurrent((GroovyObject)this, name);
                }
                return this.doCall(name);
            }

            public LinkedList getStack() {
                CallSite[] callSiteArray = _parse_closure4.$getCallSiteArray();
                return (LinkedList)ScriptBytecodeAdapter.castToType(this.stack.get(), LinkedList.class);
            }

            public Object getAssignName() {
                CallSite[] callSiteArray = _parse_closure4.$getCallSiteArray();
                return this.assignName.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parse_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "last";
                stringArray[1] = "get";
                stringArray[2] = "config";
                stringArray[3] = "get";
                stringArray[4] = "config";
                stringArray[5] = "getAt";
                stringArray[6] = "scope";
                stringArray[7] = "getAt";
                stringArray[8] = "scope";
                stringArray[9] = "getProperty";
                stringArray[10] = "<$constructor$>";
                stringArray[11] = "call";
                stringArray[12] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
                _parse_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parse_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parse_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _parse_closure4 _parse_closure42 = new _parse_closure4(this, this, stack, assignName);
        ScriptBytecodeAdapter.setProperty(_parse_closure42, null, mc.get(), "getProperty");
        Reference<ConfigObject> overrides = new Reference<ConfigObject>((ConfigObject)ScriptBytecodeAdapter.castToType(callSiteArray[42].callConstructor(ConfigObject.class), ConfigObject.class));
        public class _parse_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference currentConditionalBlock;
            private /* synthetic */ Reference stack;
            private /* synthetic */ Reference config;
            private /* synthetic */ Reference overrides;
            private /* synthetic */ Reference pushStack;
            private /* synthetic */ Reference assignName;
            private /* synthetic */ Reference prefix;
            private /* synthetic */ Reference mc;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parse_closure5(Object _outerInstance, Object _thisObject, Reference currentConditionalBlock, Reference stack, Reference config, Reference overrides, Reference pushStack, Reference assignName, Reference prefix, Reference mc) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                Reference reference5;
                Reference reference6;
                Reference reference7;
                Reference reference8;
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.currentConditionalBlock = reference8 = currentConditionalBlock;
                this.stack = reference7 = stack;
                this.config = reference6 = config;
                this.overrides = reference5 = overrides;
                this.pushStack = reference4 = pushStack;
                this.assignName = reference3 = assignName;
                this.prefix = reference2 = prefix;
                this.mc = reference = mc;
            }

            public Object doCall(String name, Object args) {
                Object result;
                block15: {
                    CallSite[] callSiteArray;
                    block13: {
                        Object object;
                        Object object2;
                        block16: {
                            block14: {
                                callSiteArray = _parse_closure5.$getCallSiteArray();
                                result = null;
                                if (!(ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(args), 1) && callSiteArray[1].call(args, 0) instanceof Closure)) break block13;
                                if (!ScriptBytecodeAdapter.isCase(name, callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this)))) break block14;
                                try {
                                    callSiteArray[4].call(this.currentConditionalBlock.get(), name);
                                    callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.createMap(new Object[0]));
                                    callSiteArray[7].call(callSiteArray[8].call(args, 0));
                                }
                                catch (Throwable throwable) {
                                    callSiteArray[18].call(this.currentConditionalBlock.get());
                                    Object entry = null;
                                    Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this)))), Iterator.class);
                                    while (iterator.hasNext()) {
                                        entry = iterator.next();
                                        Object c = callSiteArray[23].callGetProperty(callSiteArray[24].callGetProperty(this.stack.get()));
                                        callSiteArray[25].call(ScriptBytecodeAdapter.compareNotEqual(c, this.config.get()) ? c : this.overrides.get(), callSiteArray[26].callGetProperty(entry));
                                    }
                                    throw throwable;
                                }
                                callSiteArray[9].call(this.currentConditionalBlock.get());
                                Object entry = null;
                                Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty(this)))), Iterator.class);
                                while (iterator.hasNext()) {
                                    entry = iterator.next();
                                    Object c = callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(this.stack.get()));
                                    callSiteArray[16].call(ScriptBytecodeAdapter.compareNotEqual(c, this.config.get()) ? c : this.overrides.get(), callSiteArray[17].callGetProperty(entry));
                                }
                                break block15;
                            }
                            if (!ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[27].call(this.currentConditionalBlock.get()), 0)) break block16;
                            String conditionalBlockKey = ShortTypeHandling.castToString(callSiteArray[28].call(this.currentConditionalBlock.get()));
                            if (!ScriptBytecodeAdapter.compareEqual(name, callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this), conditionalBlockKey))) break block15;
                            Object co = callSiteArray[31].callConstructor(ConfigObject.class);
                            Object object3 = co;
                            callSiteArray[32].call(callSiteArray[33].call(callSiteArray[34].callGroovyObjectGetProperty(this)), conditionalBlockKey, object3);
                            callSiteArray[35].call(this.pushStack.get(), co);
                            try {
                                callSiteArray[36].call(this.currentConditionalBlock.get());
                                callSiteArray[37].call(callSiteArray[38].call(args, 0));
                            }
                            catch (Throwable throwable) {
                                callSiteArray[40].call(this.currentConditionalBlock.get(), conditionalBlockKey);
                                throw throwable;
                            }
                            callSiteArray[39].call(this.currentConditionalBlock.get(), conditionalBlockKey);
                            callSiteArray[41].call(this.stack.get());
                            break block15;
                        }
                        Object co = null;
                        co = callSiteArray[42].call(callSiteArray[43].callGetProperty(callSiteArray[44].callGetProperty(this.stack.get())), name) instanceof ConfigObject ? (object2 = callSiteArray[45].call(callSiteArray[46].callGetProperty(callSiteArray[47].callGetProperty(this.stack.get())), name)) : (object = callSiteArray[48].callConstructor(ConfigObject.class));
                        callSiteArray[49].call(this.assignName.get(), name, co);
                        callSiteArray[50].call(this.pushStack.get(), co);
                        callSiteArray[51].call(callSiteArray[52].call(args, 0));
                        callSiteArray[53].call(this.stack.get());
                        break block15;
                    }
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[54].callGetProperty(args), 2) && callSiteArray[55].call(args, 1) instanceof Closure) {
                        try {
                            Object object = callSiteArray[56].call((Object)name, ".");
                            this.prefix.set(object);
                            callSiteArray[57].call(this.assignName.get(), name, callSiteArray[58].call(args, 0));
                            callSiteArray[59].call(callSiteArray[60].call(args, 1));
                        }
                        finally {
                            String string = "";
                            this.prefix.set(string);
                        }
                    } else {
                        MetaMethod mm = (MetaMethod)ScriptBytecodeAdapter.castToType(callSiteArray[61].call(this.mc.get(), name, args), MetaMethod.class);
                        if (DefaultTypeTransformation.booleanUnbox(mm)) {
                            Object object;
                            result = object = callSiteArray[62].call(mm, callSiteArray[63].callGroovyObjectGetProperty(this), args);
                        } else {
                            throw (Throwable)callSiteArray[64].callConstructor(MissingMethodException.class, name, callSiteArray[65].callCurrent(this), args);
                        }
                    }
                }
                return result;
            }

            public Object call(String name, Object args) {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return callSiteArray[66].callCurrent(this, name, args);
            }

            public Stack getCurrentConditionalBlock() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return (Stack)ScriptBytecodeAdapter.castToType(this.currentConditionalBlock.get(), Stack.class);
            }

            public LinkedList getStack() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return (LinkedList)ScriptBytecodeAdapter.castToType(this.stack.get(), LinkedList.class);
            }

            public Object getConfig() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return this.config.get();
            }

            public ConfigObject getOverrides() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return (ConfigObject)ScriptBytecodeAdapter.castToType(this.overrides.get(), ConfigObject.class);
            }

            public Object getPushStack() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return this.pushStack.get();
            }

            public Object getAssignName() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return this.assignName.get();
            }

            public Object getPrefix() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return this.prefix.get();
            }

            public Object getMc() {
                CallSite[] callSiteArray = _parse_closure5.$getCallSiteArray();
                return this.mc.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parse_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "length";
                stringArray[1] = "getAt";
                stringArray[2] = "keySet";
                stringArray[3] = "conditionValues";
                stringArray[4] = "push";
                stringArray[5] = "push";
                stringArray[6] = "conditionalBlocks";
                stringArray[7] = "call";
                stringArray[8] = "getAt";
                stringArray[9] = "pop";
                stringArray[10] = "iterator";
                stringArray[11] = "entrySet";
                stringArray[12] = "pop";
                stringArray[13] = "conditionalBlocks";
                stringArray[14] = "config";
                stringArray[15] = "last";
                stringArray[16] = "merge";
                stringArray[17] = "value";
                stringArray[18] = "pop";
                stringArray[19] = "iterator";
                stringArray[20] = "entrySet";
                stringArray[21] = "pop";
                stringArray[22] = "conditionalBlocks";
                stringArray[23] = "config";
                stringArray[24] = "last";
                stringArray[25] = "merge";
                stringArray[26] = "value";
                stringArray[27] = "size";
                stringArray[28] = "peek";
                stringArray[29] = "getAt";
                stringArray[30] = "conditionValues";
                stringArray[31] = "<$constructor$>";
                stringArray[32] = "putAt";
                stringArray[33] = "peek";
                stringArray[34] = "conditionalBlocks";
                stringArray[35] = "call";
                stringArray[36] = "pop";
                stringArray[37] = "call";
                stringArray[38] = "getAt";
                stringArray[39] = "push";
                stringArray[40] = "push";
                stringArray[41] = "removeLast";
                stringArray[42] = "get";
                stringArray[43] = "config";
                stringArray[44] = "last";
                stringArray[45] = "get";
                stringArray[46] = "config";
                stringArray[47] = "last";
                stringArray[48] = "<$constructor$>";
                stringArray[49] = "call";
                stringArray[50] = "call";
                stringArray[51] = "call";
                stringArray[52] = "getAt";
                stringArray[53] = "removeLast";
                stringArray[54] = "length";
                stringArray[55] = "getAt";
                stringArray[56] = "plus";
                stringArray[57] = "call";
                stringArray[58] = "getAt";
                stringArray[59] = "call";
                stringArray[60] = "getAt";
                stringArray[61] = "getMetaMethod";
                stringArray[62] = "invoke";
                stringArray[63] = "delegate";
                stringArray[64] = "<$constructor$>";
                stringArray[65] = "getClass";
                stringArray[66] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[67];
                _parse_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parse_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parse_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _parse_closure5 _parse_closure52 = new _parse_closure5(this, this, currentConditionalBlock, stack, config, overrides, pushStack, assignName, prefix, mc);
        ScriptBytecodeAdapter.setProperty(_parse_closure52, null, mc.get(), "invokeMethod");
        Object object = mc.get();
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, ConfigSlurper.class, script, "metaClass");
        public class _parse_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference assignName;
            private /* synthetic */ Reference prefix;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parse_closure6(Object _outerInstance, Object _thisObject, Reference assignName, Reference prefix) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _parse_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.assignName = reference2 = assignName;
                this.prefix = reference = prefix;
            }

            public Object doCall(String name, Object value) {
                CallSite[] callSiteArray = _parse_closure6.$getCallSiteArray();
                return callSiteArray[0].call(this.assignName.get(), callSiteArray[1].call(this.prefix.get(), name), value);
            }

            public Object call(String name, Object value) {
                CallSite[] callSiteArray = _parse_closure6.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, name, value);
            }

            public Object getAssignName() {
                CallSite[] callSiteArray = _parse_closure6.$getCallSiteArray();
                return this.assignName.get();
            }

            public Object getPrefix() {
                CallSite[] callSiteArray = _parse_closure6.$getCallSiteArray();
                return this.prefix.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parse_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "call";
                stringArray[1] = "plus";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _parse_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parse_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parse_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _parse_closure6 setProperty = new _parse_closure6(this, this, assignName, prefix);
        Object binding = callSiteArray[43].callConstructor(ConfigBinding.class, setProperty);
        if (DefaultTypeTransformation.booleanUnbox(this.bindingVars)) {
            callSiteArray[44].call(callSiteArray[45].call(binding), this.bindingVars);
        }
        Object object2 = binding;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, ConfigSlurper.class, script, "binding");
        callSiteArray[46].call(script);
        callSiteArray[47].call((Object)config.get(), overrides.get());
        return (ConfigObject)ScriptBytecodeAdapter.castToType(config.get(), ConfigObject.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConfigSlurper.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    static {
        String string = "environments";
        ENVIRONMENTS_METHOD = string;
    }

    public GroovyClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(GroovyClassLoader groovyClassLoader) {
        this.classLoader = groovyClassLoader;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "putAt";
        stringArray[3] = "remove";
        stringArray[4] = "putAt";
        stringArray[5] = "unmodifiableMap";
        stringArray[6] = "getAt";
        stringArray[7] = "putAt";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "iterator";
        stringArray[10] = "keySet";
        stringArray[11] = "split";
        stringArray[12] = "iterator";
        stringArray[13] = "plus";
        stringArray[14] = "plus";
        stringArray[15] = "getAt";
        stringArray[16] = "flatten";
        stringArray[17] = "clear";
        stringArray[18] = "each";
        stringArray[19] = "get";
        stringArray[20] = "putAt";
        stringArray[21] = "get";
        stringArray[22] = "putAt";
        stringArray[23] = "parse";
        stringArray[24] = "parseClass";
        stringArray[25] = "parse";
        stringArray[26] = "newInstance";
        stringArray[27] = "parse";
        stringArray[28] = "parse";
        stringArray[29] = "newInstance";
        stringArray[30] = "parseClass";
        stringArray[31] = "text";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "removeMetaClass";
        stringArray[36] = "metaClassRegistry";
        stringArray[37] = "class";
        stringArray[38] = "metaClass";
        stringArray[39] = "class";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "leftShift";
        stringArray[42] = "<$constructor$>";
        stringArray[43] = "<$constructor$>";
        stringArray[44] = "putAll";
        stringArray[45] = "getVariables";
        stringArray[46] = "run";
        stringArray[47] = "merge";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[48];
        ConfigSlurper.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConfigSlurper.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConfigSlurper.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

