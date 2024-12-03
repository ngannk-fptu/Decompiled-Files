/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.fusesource.jansi.Ansi$Attribute
 *  org.fusesource.jansi.AnsiRenderer
 *  org.fusesource.jansi.AnsiRenderer$Code
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MissingFieldException;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.NavigablePropertiesCompleter;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletionCandidate;
import org.codehaus.groovy.tools.shell.util.Preferences;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderer;

public class ReflectionCompletor
implements GroovyObject {
    private static final NavigablePropertiesCompleter PROPERTIES_COMPLETER;
    private static final Pattern BEAN_ACCESSOR_PATTERN;
    private final Groovysh shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ReflectionCompletor(Groovysh shell) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
    }

    public int complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        GroovySourceToken currentElementToken = null;
        GroovySourceToken dotToken = null;
        List previousTokens = null;
        if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[0].call(tokens), 2)) {
            throw (Throwable)callSiteArray[1].callConstructor(IllegalArgumentException.class, callSiteArray[2].call((Object)"must be invoked with at least 2 tokens, one of which is dot", ScriptBytecodeAdapter.getPropertySpreadSafe(ReflectionCompletor.class, tokens, "text")));
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[3].callGetProperty(callSiteArray[4].call(tokens)), callSiteArray[5].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[6].callGetProperty(callSiteArray[7].call(tokens)), callSiteArray[8].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[9].callGetProperty(callSiteArray[10].call(tokens)), callSiteArray[11].callGetProperty(GroovyTokenTypes.class))) {
                Object object = callSiteArray[12].call(tokens);
                dotToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object, GroovySourceToken.class);
                Object object2 = callSiteArray[13].call(tokens, ScriptBytecodeAdapter.createRange(0, -2, true));
                previousTokens = (List)ScriptBytecodeAdapter.castToType(object2, List.class);
            } else {
                if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[14].callGetProperty(callSiteArray[15].call(tokens, -2)), callSiteArray[16].callGetProperty(GroovyTokenTypes.class)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[17].callGetProperty(callSiteArray[18].call(tokens, -2)), callSiteArray[19].callGetProperty(GroovyTokenTypes.class)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[20].callGetProperty(callSiteArray[21].call(tokens, -2)), callSiteArray[22].callGetProperty(GroovyTokenTypes.class))) {
                    throw (Throwable)callSiteArray[23].callConstructor(IllegalArgumentException.class, callSiteArray[24].call((Object)"must be invoked with token list with dot at last position or one position before", ScriptBytecodeAdapter.getPropertySpreadSafe(ReflectionCompletor.class, tokens, "text")));
                }
                Object object = callSiteArray[25].call(tokens);
                currentElementToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object, GroovySourceToken.class);
                Object object3 = callSiteArray[26].call(tokens, -2);
                dotToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object3, GroovySourceToken.class);
                Object object4 = callSiteArray[27].call(tokens, ScriptBytecodeAdapter.createRange(0, -3, true));
                previousTokens = (List)ScriptBytecodeAdapter.castToType(object4, List.class);
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[28].callGetProperty(callSiteArray[29].call(tokens)), callSiteArray[30].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[31].callGetProperty(callSiteArray[32].call(tokens)), callSiteArray[33].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[34].callGetProperty(callSiteArray[35].call(tokens)), callSiteArray[36].callGetProperty(GroovyTokenTypes.class))) {
            Object object = callSiteArray[37].call(tokens);
            dotToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object, GroovySourceToken.class);
            Object object5 = callSiteArray[38].call(tokens, ScriptBytecodeAdapter.createRange(0, -2, true));
            previousTokens = (List)ScriptBytecodeAdapter.castToType(object5, List.class);
        } else {
            if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[39].callGetProperty(callSiteArray[40].call(tokens, -2)), callSiteArray[41].callGetProperty(GroovyTokenTypes.class)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[42].callGetProperty(callSiteArray[43].call(tokens, -2)), callSiteArray[44].callGetProperty(GroovyTokenTypes.class)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[45].callGetProperty(callSiteArray[46].call(tokens, -2)), callSiteArray[47].callGetProperty(GroovyTokenTypes.class))) {
                throw (Throwable)callSiteArray[48].callConstructor(IllegalArgumentException.class, callSiteArray[49].call((Object)"must be invoked with token list with dot at last position or one position before", ScriptBytecodeAdapter.getPropertySpreadSafe(ReflectionCompletor.class, tokens, "text")));
            }
            Object object = callSiteArray[50].call(tokens);
            currentElementToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object, GroovySourceToken.class);
            Object object6 = callSiteArray[51].call(tokens, -2);
            dotToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(object6, GroovySourceToken.class);
            Object object7 = callSiteArray[52].call(tokens, ScriptBytecodeAdapter.createRange(0, -3, true));
            previousTokens = (List)ScriptBytecodeAdapter.castToType(object7, List.class);
        }
        Object instanceOrClass = callSiteArray[53].callCurrent((GroovyObject)this, previousTokens);
        if (ScriptBytecodeAdapter.compareEqual(instanceOrClass, null)) {
            return -1;
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[54].callGetProperty(dotToken), callSiteArray[55].callGetProperty(GroovyTokenTypes.class))) {
            Object object;
            instanceOrClass = object = callSiteArray[56].call(instanceOrClass);
            if (ScriptBytecodeAdapter.compareEqual(instanceOrClass, null)) {
                return -1;
            }
        }
        String identifierPrefix = null;
        if (DefaultTypeTransformation.booleanUnbox(currentElementToken)) {
            Object object = callSiteArray[57].callGetProperty(currentElementToken);
            identifierPrefix = ShortTypeHandling.castToString(object);
        } else {
            String string;
            identifierPrefix = string = "";
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[58].callCurrent((GroovyObject)this, ArrayUtil.createArray(instanceOrClass, identifierPrefix, candidates, currentElementToken, dotToken)));
    }

    private int completeInstanceMembers(Object instanceOrClass, String identifierPrefix, List<CharSequence> candidates, GroovySourceToken currentElementToken, GroovySourceToken dotToken) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        Collection myCandidates = (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[59].callStatic(ReflectionCompletor.class, instanceOrClass, identifierPrefix), Collection.class);
        boolean showAllMethods = false;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl = ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[60].call(identifierPrefix), callSiteArray[61].call(Integer.class, callSiteArray[62].call(Preferences.class, callSiteArray[63].callGetProperty(Groovysh.class), "3")));
            showAllMethods = bl;
        } else {
            boolean bl = ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[64].call(identifierPrefix), callSiteArray[65].call(Integer.class, callSiteArray[66].call(Preferences.class, callSiteArray[67].callGetProperty(Groovysh.class), "3")));
            showAllMethods = bl;
        }
        public class _completeInstanceMembers_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _completeInstanceMembers_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure1.$getCallSiteArray();
                return callSiteArray[0].callConstructor(ReflectionCompletionCandidate.class, it);
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure1.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _completeInstanceMembers_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _completeInstanceMembers_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_completeInstanceMembers_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _completeInstanceMembers_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[68].call((Object)myCandidates, callSiteArray[69].call(callSiteArray[70].callStatic(ReflectionCompletor.class, instanceOrClass, identifierPrefix, showAllMethods), new _completeInstanceMembers_closure1(this, this)));
        if (!showAllMethods) {
            callSiteArray[71].callStatic(ReflectionCompletor.class, myCandidates);
        }
        public class _completeInstanceMembers_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _completeInstanceMembers_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure2.$getCallSiteArray();
                return callSiteArray[0].callConstructor(ReflectionCompletionCandidate.class, it, callSiteArray[1].call(callSiteArray[2].callGetProperty(AnsiRenderer.Code.class)));
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _completeInstanceMembers_closure2.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[3].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _completeInstanceMembers_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "name";
                stringArray[2] = "BLUE";
                stringArray[3] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _completeInstanceMembers_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_completeInstanceMembers_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _completeInstanceMembers_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[72].call((Object)myCandidates, callSiteArray[73].call(callSiteArray[74].callStatic(ReflectionCompletor.class, instanceOrClass, identifierPrefix), new _completeInstanceMembers_closure2(this, this)));
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[75].call(myCandidates), 0)) {
            Object object = callSiteArray[76].call(myCandidates);
            myCandidates = (Collection)ScriptBytecodeAdapter.castToType(object, Collection.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[77].call(Boolean.class, callSiteArray[78].call(Preferences.class, callSiteArray[79].callGetProperty(Groovysh.class), "true")))) {
                public class _completeInstanceMembers_closure3
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _completeInstanceMembers_closure3(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _completeInstanceMembers_closure3.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(ReflectionCompletionCandidate it) {
                        CallSite[] callSiteArray = _completeInstanceMembers_closure3.$getCallSiteArray();
                        return callSiteArray[0].call(AnsiRenderer.class, callSiteArray[1].callGroovyObjectGetProperty(it), callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(it), (Object)new String[DefaultTypeTransformation.intUnbox(callSiteArray[4].call(callSiteArray[5].callGroovyObjectGetProperty(it)))]));
                    }

                    public Object call(ReflectionCompletionCandidate it) {
                        CallSite[] callSiteArray = _completeInstanceMembers_closure3.$getCallSiteArray();
                        return callSiteArray[6].callCurrent((GroovyObject)this, it);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _completeInstanceMembers_closure3.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "render";
                        stringArray[1] = "value";
                        stringArray[2] = "toArray";
                        stringArray[3] = "jAnsiCodes";
                        stringArray[4] = "size";
                        stringArray[5] = "jAnsiCodes";
                        stringArray[6] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[7];
                        _completeInstanceMembers_closure3.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_completeInstanceMembers_closure3.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _completeInstanceMembers_closure3.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[80].call(candidates, callSiteArray[81].call((Object)myCandidates, new _completeInstanceMembers_closure3(this, this)));
            } else {
                callSiteArray[82].call(candidates, ScriptBytecodeAdapter.getPropertySpreadSafe(ReflectionCompletor.class, myCandidates, "value"));
            }
            int lastDot = 0;
            if (DefaultTypeTransformation.booleanUnbox(currentElementToken) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[83].callGetProperty(dotToken), callSiteArray[84].callGetProperty(currentElementToken))) {
                Object object2 = callSiteArray[85].call(callSiteArray[86].callGetProperty(currentElementToken), 1);
                lastDot = DefaultTypeTransformation.intUnbox(object2);
            } else {
                Object object3 = callSiteArray[87].call(callSiteArray[88].callGetProperty(dotToken), callSiteArray[89].call(callSiteArray[90].call(callSiteArray[91].call(dotToken)), 1));
                lastDot = DefaultTypeTransformation.intUnbox(object3);
            }
            return lastDot;
        }
        return -1;
    }

    public Object getInvokerClassOrInstance(List<GroovySourceToken> groovySourceTokens) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? !DefaultTypeTransformation.booleanUnbox(groovySourceTokens) || ScriptBytecodeAdapter.compareEqual(callSiteArray[92].callGetProperty(callSiteArray[93].call(groovySourceTokens)), callSiteArray[94].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[95].callGetProperty(callSiteArray[96].call(groovySourceTokens)), callSiteArray[97].callGetProperty(GroovyTokenTypes.class)) : !DefaultTypeTransformation.booleanUnbox(groovySourceTokens) || ScriptBytecodeAdapter.compareEqual(callSiteArray[98].callGetProperty(callSiteArray[99].call(groovySourceTokens)), callSiteArray[100].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[101].callGetProperty(callSiteArray[102].call(groovySourceTokens)), callSiteArray[103].callGetProperty(GroovyTokenTypes.class))) {
            return null;
        }
        List invokerTokens = (List)ScriptBytecodeAdapter.castToType(callSiteArray[104].callStatic(ReflectionCompletor.class, groovySourceTokens), List.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(invokerTokens)) {
                String instanceRefExpression = ShortTypeHandling.castToString(callSiteArray[105].callStatic(ReflectionCompletor.class, invokerTokens));
                Object object = callSiteArray[106].call(instanceRefExpression, "\n", "");
                instanceRefExpression = ShortTypeHandling.castToString(object);
                Object instance = callSiteArray[107].call(callSiteArray[108].callGetProperty(this.shell), callSiteArray[109].call(callSiteArray[110].call((Object)ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[111].call(this.shell)}), ScriptBytecodeAdapter.createList(new Object[]{"true"})), ScriptBytecodeAdapter.createList(new Object[]{instanceRefExpression})));
                Object object2 = instance;
                try {
                    return object2;
                }
                catch (MissingPropertyException e) {
                }
                catch (MissingMethodException e) {
                }
                catch (MissingFieldException e) {
                }
                catch (MultipleCompilationErrorsException e) {
                }
            }
        } else if (DefaultTypeTransformation.booleanUnbox(invokerTokens)) {
            String instanceRefExpression = ShortTypeHandling.castToString(callSiteArray[112].callStatic(ReflectionCompletor.class, invokerTokens));
            Object object = callSiteArray[113].call(instanceRefExpression, "\n", "");
            instanceRefExpression = ShortTypeHandling.castToString(object);
            Object instance = callSiteArray[114].call(callSiteArray[115].callGetProperty(this.shell), callSiteArray[116].call(callSiteArray[117].call((Object)ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[118].call(this.shell)}), ScriptBytecodeAdapter.createList(new Object[]{"true"})), ScriptBytecodeAdapter.createList(new Object[]{instanceRefExpression})));
            Object object3 = instance;
            try {
                return object3;
            }
            catch (MissingPropertyException e) {
            }
            catch (MissingMethodException e) {
            }
            catch (MissingFieldException e) {
            }
            catch (MultipleCompilationErrorsException e) {
            }
        }
        return null;
    }

    public static List<GroovySourceToken> getInvokerTokens(List<GroovySourceToken> groovySourceTokens) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        int validIndex = DefaultTypeTransformation.intUnbox(callSiteArray[119].call(groovySourceTokens));
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? validIndex == 0 : validIndex == 0) {
            return ScriptBytecodeAdapter.createList(new Object[0]);
        }
        Stack expectedOpeners = (Stack)ScriptBytecodeAdapter.castToType(callSiteArray[120].callConstructor(Stack.class), Stack.class);
        GroovySourceToken lastToken = null;
        GroovySourceToken loopToken = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[121].call(callSiteArray[122].call(groovySourceTokens)), Iterator.class);
        while (iterator.hasNext()) {
            GroovySourceToken groovySourceToken;
            loopToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(iterator.next(), GroovySourceToken.class);
            Object object = callSiteArray[123].callGetProperty(loopToken);
            if (!ScriptBytecodeAdapter.isCase(object, callSiteArray[124].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[125].callGetProperty(GroovyTokenTypes.class))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[126].call(expectedOpeners))) break;
                    if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[127].call(expectedOpeners), callSiteArray[128].callGetProperty(GroovyTokenTypes.class))) {
                        return ScriptBytecodeAdapter.createList(new Object[0]);
                    }
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[129].callGetProperty(GroovyTokenTypes.class))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[130].call(expectedOpeners))) break;
                    if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[131].call(expectedOpeners), callSiteArray[132].callGetProperty(GroovyTokenTypes.class))) {
                        return ScriptBytecodeAdapter.createList(new Object[0]);
                    }
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[133].callGetProperty(GroovyTokenTypes.class))) {
                    callSiteArray[134].call((Object)expectedOpeners, callSiteArray[135].callGetProperty(GroovyTokenTypes.class));
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[136].callGetProperty(GroovyTokenTypes.class))) {
                    callSiteArray[137].call((Object)expectedOpeners, callSiteArray[138].callGetProperty(GroovyTokenTypes.class));
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[139].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[140].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[141].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[142].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[143].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[144].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[145].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[146].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[147].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[148].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[149].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[150].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[151].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[152].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[153].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[154].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[155].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[156].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[157].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[158].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[159].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[160].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[161].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[162].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[163].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[164].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[165].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[166].callGetProperty(GroovyTokenTypes.class))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[167].call(expectedOpeners))) {
                        break;
                    }
                } else {
                    if (ScriptBytecodeAdapter.isCase(object, callSiteArray[168].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[169].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[170].callGetProperty(GroovyTokenTypes.class))) break;
                    if (ScriptBytecodeAdapter.isCase(object, callSiteArray[171].callGetProperty(GroovyTokenTypes.class))) {
                        if (DefaultTypeTransformation.booleanUnbox(lastToken)) {
                            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[172].callGetProperty(lastToken), callSiteArray[173].callGetProperty(GroovyTokenTypes.class))) {
                                return ScriptBytecodeAdapter.createList(new Object[0]);
                            }
                            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[174].callGetProperty(lastToken), callSiteArray[175].callGetProperty(GroovyTokenTypes.class))) {
                                return ScriptBytecodeAdapter.createList(new Object[0]);
                            }
                        }
                    } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[176].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[177].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[178].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[179].callGetProperty(GroovyTokenTypes.class))) {
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[180].call(expectedOpeners))) {
                            break;
                        }
                    } else if (!(ScriptBytecodeAdapter.isCase(object, callSiteArray[181].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[182].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[183].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[184].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[185].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[186].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[187].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[188].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[189].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[190].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[191].callGetProperty(GroovyTokenTypes.class)))) {
                        return (List)ScriptBytecodeAdapter.castToType(null, List.class);
                    }
                }
            }
            int n = validIndex;
            validIndex = DefaultTypeTransformation.intUnbox(callSiteArray[192].call(n));
            lastToken = groovySourceToken = loopToken;
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[193].call(groovySourceTokens, ScriptBytecodeAdapter.createRange(validIndex, -1, true)), List.class);
    }

    public static String tokenListToEvalString(List<GroovySourceToken> groovySourceTokens) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        StringBuilder builder = (StringBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[194].callConstructor(StringBuilder.class), StringBuilder.class);
        GroovySourceToken token = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[195].call(groovySourceTokens), Iterator.class);
        while (iterator.hasNext()) {
            token = (GroovySourceToken)ScriptBytecodeAdapter.castToType(iterator.next(), GroovySourceToken.class);
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[196].callGetProperty(token), callSiteArray[197].callGetProperty(GroovyTokenTypes.class))) {
                callSiteArray[198].call(callSiteArray[199].call(callSiteArray[200].call((Object)builder, "'"), callSiteArray[201].callGetProperty(token)), "'");
                continue;
            }
            callSiteArray[202].call((Object)builder, callSiteArray[203].callGetProperty(token));
        }
        return ShortTypeHandling.castToString(callSiteArray[204].call(builder));
    }

    public static boolean acceptName(String name, String prefix) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (!DefaultTypeTransformation.booleanUnbox(prefix) || DefaultTypeTransformation.booleanUnbox(callSiteArray[205].call((Object)name, prefix))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[206].call((Object)name, "$")) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[207].call((Object)name, "_"));
        }
        return (!DefaultTypeTransformation.booleanUnbox(prefix) || DefaultTypeTransformation.booleanUnbox(callSiteArray[208].call((Object)name, prefix))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[209].call((Object)name, "$")) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[210].call((Object)name, "_"));
    }

    public static Collection<String> getMetaclassMethods(Object instance, String prefix, boolean includeMetaClassImplMethods) {
        public class _getMetaclassMethods_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefix;
            private /* synthetic */ Reference rv;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getMetaclassMethods_closure4(Object _outerInstance, Object _thisObject, Reference prefix, Reference rv) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _getMetaclassMethods_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefix = reference2 = prefix;
                this.rv = reference = rv;
            }

            public Object doCall(MetaMethod mmit) {
                CallSite[] callSiteArray = _getMetaclassMethods_closure4.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this, callSiteArray[1].callGetProperty(mmit), this.prefix.get()))) {
                    return callSiteArray[2].call(this.rv.get(), callSiteArray[3].call(callSiteArray[4].call(mmit), ScriptBytecodeAdapter.compareEqual(callSiteArray[5].callGetProperty(callSiteArray[6].callGetProperty(mmit)), 0) ? "()" : "("));
                }
                return null;
            }

            public Object call(MetaMethod mmit) {
                CallSite[] callSiteArray = _getMetaclassMethods_closure4.$getCallSiteArray();
                return callSiteArray[7].callCurrent((GroovyObject)this, mmit);
            }

            public String getPrefix() {
                CallSite[] callSiteArray = _getMetaclassMethods_closure4.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.prefix.get());
            }

            public Set getRv() {
                CallSite[] callSiteArray = _getMetaclassMethods_closure4.$getCallSiteArray();
                return (Set)ScriptBytecodeAdapter.castToType(this.rv.get(), Set.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getMetaclassMethods_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "acceptName";
                stringArray[1] = "name";
                stringArray[2] = "leftShift";
                stringArray[3] = "plus";
                stringArray[4] = "getName";
                stringArray[5] = "length";
                stringArray[6] = "parameterTypes";
                stringArray[7] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _getMetaclassMethods_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getMetaclassMethods_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getMetaclassMethods_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<String> prefix2 = new Reference<String>(prefix);
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        Reference<Set> rv = new Reference<Set>((Set)ScriptBytecodeAdapter.castToType(callSiteArray[211].callConstructor(HashSet.class), Set.class));
        MetaClass metaclass = (MetaClass)ScriptBytecodeAdapter.castToType(callSiteArray[212].call(InvokerHelper.class, instance), MetaClass.class);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (includeMetaClassImplMethods || !(metaclass instanceof MetaClassImpl)) {
                callSiteArray[213].call(callSiteArray[214].callGetProperty(metaclass), new _getMetaclassMethods_closure4(ReflectionCompletor.class, ReflectionCompletor.class, prefix2, rv));
            }
        } else if (includeMetaClassImplMethods || !(metaclass instanceof MetaClassImpl)) {
            callSiteArray[215].call(callSiteArray[216].callGetProperty(metaclass), new _getMetaclassMethods_closure4(ReflectionCompletor.class, ReflectionCompletor.class, prefix2, rv));
        }
        return (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[217].call(rv.get()), Collection.class);
    }

    public static Collection<ReflectionCompletionCandidate> getPublicFieldsAndMethods(Object instance, String prefix) {
        boolean bl;
        boolean bl2;
        boolean bl3;
        boolean bl4;
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        Set rv = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[218].callConstructor(HashSet.class), Set.class);
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[219].call(instance));
        if (ScriptBytecodeAdapter.compareEqual(clazz, null)) {
            return rv;
        }
        int isClass = 0;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl5 = ScriptBytecodeAdapter.compareEqual(clazz, Class.class);
            isClass = bl5 ? 1 : 0;
        } else {
            boolean bl6 = ScriptBytecodeAdapter.compareEqual(clazz, Class.class);
            isClass = bl6 ? 1 : 0;
        }
        if (isClass != 0) {
            Class clazz2;
            clazz = clazz2 = (Class)ScriptBytecodeAdapter.asType(instance, Class.class);
        }
        Class loopclazz = clazz;
        boolean renderBold = false;
        renderBold = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (bl4 = isClass == 0) : (bl3 = isClass == 0);
        boolean showStatic = false;
        showStatic = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (bl2 = isClass != 0 || ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[220].call(prefix), callSiteArray[221].call(Integer.class, callSiteArray[222].call(Preferences.class, callSiteArray[223].callGetProperty(Groovysh.class), "3")))) : (bl = isClass != 0 || ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[224].call(prefix), callSiteArray[225].call(Integer.class, callSiteArray[226].call(Preferences.class, callSiteArray[227].callGetProperty(Groovysh.class), "3"))));
        while (ScriptBytecodeAdapter.compareNotEqual(loopclazz, null) && ScriptBytecodeAdapter.compareNotEqual(loopclazz, Object.class) && ScriptBytecodeAdapter.compareNotEqual(loopclazz, GroovyObject.class)) {
            boolean bl7;
            callSiteArray[228].callStatic(ReflectionCompletor.class, ArrayUtil.createArray(loopclazz, showStatic, isClass == 0, prefix, rv, renderBold));
            renderBold = bl7 = false;
            Object object = callSiteArray[229].callGetProperty(loopclazz);
            loopclazz = ShortTypeHandling.castToClass(object);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[230].call(clazz)) && isClass == 0) {
            String member = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[231].call(ScriptBytecodeAdapter.createList(new Object[]{"length", "clone()"})), Iterator.class);
            while (iterator.hasNext()) {
                member = ShortTypeHandling.castToString(iterator.next());
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[232].call((Object)member, prefix))) continue;
                callSiteArray[233].call((Object)rv, callSiteArray[234].callConstructor(ReflectionCompletionCandidate.class, member, callSiteArray[235].call(callSiteArray[236].callGetProperty(Ansi.Attribute.class))));
            }
        }
        if (isClass == 0) {
            Set candidates = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[237].callConstructor(HashSet.class), Set.class);
            callSiteArray[238].call(PROPERTIES_COMPLETER, instance, prefix, candidates);
            public class _getPublicFieldsAndMethods_closure5
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getPublicFieldsAndMethods_closure5(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getPublicFieldsAndMethods_closure5.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String it) {
                    CallSite[] callSiteArray = _getPublicFieldsAndMethods_closure5.$getCallSiteArray();
                    return callSiteArray[0].callConstructor(ReflectionCompletionCandidate.class, it, callSiteArray[1].call(callSiteArray[2].callGetProperty(AnsiRenderer.Code.class)));
                }

                public Object call(String it) {
                    CallSite[] callSiteArray = _getPublicFieldsAndMethods_closure5.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[3].callCurrent((GroovyObject)this, it);
                    }
                    return this.doCall(it);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getPublicFieldsAndMethods_closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "<$constructor$>";
                    stringArray[1] = "name";
                    stringArray[2] = "MAGENTA";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _getPublicFieldsAndMethods_closure5.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getPublicFieldsAndMethods_closure5.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getPublicFieldsAndMethods_closure5.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[239].call((Object)rv, callSiteArray[240].call((Object)candidates, new _getPublicFieldsAndMethods_closure5(ReflectionCompletor.class, ReflectionCompletor.class)));
        }
        return (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[241].call(rv), Collection.class);
    }

    public static Object removeStandardMethods(Collection<ReflectionCompletionCandidate> candidates) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        String defaultMethod = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[242].call(ScriptBytecodeAdapter.createList(new Object[]{"clone()", "finalize()", "getClass()", "getMetaClass()", "getProperty(", "invokeMethod(", "setMetaClass(", "setProperty(", "equals(", "hashCode()", "toString()", "notify()", "notifyAll()", "wait(", "wait()"})), Iterator.class);
        block0: while (iterator.hasNext()) {
            defaultMethod = ShortTypeHandling.castToString(iterator.next());
            ReflectionCompletionCandidate candidate = null;
            Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[243].call(candidates), Iterator.class);
            while (iterator2.hasNext()) {
                candidate = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(iterator2.next(), ReflectionCompletionCandidate.class);
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[244].call((Object)defaultMethod, callSiteArray[245].callGroovyObjectGetProperty(candidate)))) continue;
                callSiteArray[246].call(candidates, candidate);
                continue block0;
            }
        }
        return null;
    }

    public static List<String> getDefaultMethods(Object instance, String prefix) {
        public class _getDefaultMethods_closure27
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference candidates;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getDefaultMethods_closure27(Object _outerInstance, Object _thisObject, Reference candidates) {
                Reference reference;
                CallSite[] callSiteArray = _getDefaultMethods_closure27.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.candidates = reference = candidates;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getDefaultMethods_closure27.$getCallSiteArray();
                return callSiteArray[0].call(this.candidates.get(), it);
            }

            public List getCandidates() {
                CallSite[] callSiteArray = _getDefaultMethods_closure27.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getDefaultMethods_closure27.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getDefaultMethods_closure27.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "add";
                return new CallSiteArray(_getDefaultMethods_closure27.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getDefaultMethods_closure27.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _getDefaultMethods_closure26
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefix;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getDefaultMethods_closure26(Object _outerInstance, Object _thisObject, Reference prefix) {
                Reference reference;
                CallSite[] callSiteArray = _getDefaultMethods_closure26.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefix = reference = prefix;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getDefaultMethods_closure26.$getCallSiteArray();
                return callSiteArray[0].call(it, this.prefix.get());
            }

            public String getPrefix() {
                CallSite[] callSiteArray = _getDefaultMethods_closure26.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.prefix.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getDefaultMethods_closure26.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getDefaultMethods_closure26.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "startsWith";
                return new CallSiteArray(_getDefaultMethods_closure26.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getDefaultMethods_closure26.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<String> prefix2 = new Reference<String>(prefix);
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        Reference<List> candidates = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        if (instance instanceof Iterable) {
            public class _getDefaultMethods_closure6
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure6(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure6.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure6.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure6.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure6.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure6.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure6.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure7
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure7(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure7.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure7.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure7.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure7.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure7.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure7.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure7.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[247].call(callSiteArray[248].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"any()", "any(", "collect()", "collect(", "combinations()", "count(", "countBy(", "drop(", "dropRight(", "dropWhile(", "each()", "each(", "eachPermutation(", "every()", "every(", "find(", "findResult(", "findResults(", "flatten()", "init()", "inject(", "intersect(", "join(", "max()", "min()", "reverse()", "size()", "sort()", "split(", "take(", "takeRight(", "takeWhile(", "toSet()", "retainAll(", "removeAll(", "unique()", "unique("}), new _getDefaultMethods_closure6(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure7(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
            if (instance instanceof Collection) {
                public class _getDefaultMethods_closure8
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference prefix;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getDefaultMethods_closure8(Object _outerInstance, Object _thisObject, Reference prefix) {
                        Reference reference;
                        CallSite[] callSiteArray = _getDefaultMethods_closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.prefix = reference = prefix;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _getDefaultMethods_closure8.$getCallSiteArray();
                        return callSiteArray[0].call(it, this.prefix.get());
                    }

                    public String getPrefix() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure8.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.prefix.get());
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure8.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getDefaultMethods_closure8.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "startsWith";
                        return new CallSiteArray(_getDefaultMethods_closure8.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getDefaultMethods_closure8.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                public class _getDefaultMethods_closure9
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference candidates;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getDefaultMethods_closure9(Object _outerInstance, Object _thisObject, Reference candidates) {
                        Reference reference;
                        CallSite[] callSiteArray = _getDefaultMethods_closure9.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.candidates = reference = candidates;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _getDefaultMethods_closure9.$getCallSiteArray();
                        return callSiteArray[0].call(this.candidates.get(), it);
                    }

                    public List getCandidates() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure9.$getCallSiteArray();
                        return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure9.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getDefaultMethods_closure9.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "add";
                        return new CallSiteArray(_getDefaultMethods_closure9.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getDefaultMethods_closure9.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[249].call(callSiteArray[250].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"grep("}), new _getDefaultMethods_closure8(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure9(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
            }
            if (instance instanceof List) {
                public class _getDefaultMethods_closure10
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference prefix;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getDefaultMethods_closure10(Object _outerInstance, Object _thisObject, Reference prefix) {
                        Reference reference;
                        CallSite[] callSiteArray = _getDefaultMethods_closure10.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.prefix = reference = prefix;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _getDefaultMethods_closure10.$getCallSiteArray();
                        return callSiteArray[0].call(it, this.prefix.get());
                    }

                    public String getPrefix() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure10.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.prefix.get());
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure10.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getDefaultMethods_closure10.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "startsWith";
                        return new CallSiteArray(_getDefaultMethods_closure10.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getDefaultMethods_closure10.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                public class _getDefaultMethods_closure11
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference candidates;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getDefaultMethods_closure11(Object _outerInstance, Object _thisObject, Reference candidates) {
                        Reference reference;
                        CallSite[] callSiteArray = _getDefaultMethods_closure11.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.candidates = reference = candidates;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _getDefaultMethods_closure11.$getCallSiteArray();
                        return callSiteArray[0].call(this.candidates.get(), it);
                    }

                    public List getCandidates() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure11.$getCallSiteArray();
                        return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _getDefaultMethods_closure11.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getDefaultMethods_closure11.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "add";
                        return new CallSiteArray(_getDefaultMethods_closure11.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getDefaultMethods_closure11.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[251].call(callSiteArray[252].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"collate(", "execute()", "execute(", "pop()", "transpose()"}), new _getDefaultMethods_closure10(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure11(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
            }
        }
        if (instance instanceof Map) {
            public class _getDefaultMethods_closure12
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure12(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure12.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure12.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure12.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure12.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure12.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure12.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure12.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure13
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure13(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure13.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure13.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure13.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure13.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure13.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure13.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure13.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[253].call(callSiteArray[254].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"any(", "collect(", "collectEntries(", "collectMany(", "count(", "drop(", "each(", "every(", "find(", "findAll(", "findResult(", "findResults(", "groupEntriesBy(", "groupBy(", "inject(", "intersect(", "max(", "min(", "sort(", "spread()", "subMap(", "take(", "takeWhile("}), new _getDefaultMethods_closure12(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure13(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof File) {
            public class _getDefaultMethods_closure14
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure14(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure14.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure14.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure14.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure14.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure14.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure14.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure14.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure15
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure15(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure15.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure15.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure15.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure15.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure15.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure15.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure15.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[255].call(callSiteArray[256].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"append(", "createTempDir()", "createTempDir(", "deleteDir()", "directorySize()", "eachByte(", "eachDir(", "eachDirMatch(", "eachDirRecurse(", "eachFile(", "eachFileMatch(", "eachFileRecurse(", "eachLine(", "filterLine(", "getBytes()", "getText()", "getText(", "newInputStream()", "newOutputStream()", "newPrintWriter()", "newPrintWriter(", "newReader()", "newReader(", "newWriter()", "newWriter(", "readBytes()", "readLines(", "setBytes(", "setText(", "size()", "splitEachLine(", "traverse(", "withInputStream(", "withOutputStream(", "withPrintWriter(", "withReader(", "withWriter(", "withWriterAppend(", "write("}), new _getDefaultMethods_closure14(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure15(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof String) {
            public class _getDefaultMethods_closure16
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure16(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure16.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure16.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure16.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure16.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure16.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure16.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure16.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure17
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure17(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure17.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure17.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure17.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure17.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure17.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure17.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure17.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[257].call(callSiteArray[258].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"capitalize()", "center(", "collectReplacements(", "count(", "decodeBase64()", "decodeHex()", "denormalize()", "eachLine(", "eachMatch(", "execute()", "execute(", "find(", "findAll(", "isAllWhitespace()", "isBigDecimal()", "isBigInteger()", "isDouble()", "isFloat()", "isInteger()", "isLong()", "isNumber()", "normalize()", "padLeft(", "padRight(", "readLines()", "reverse()", "size()", "splitEachLine(", "stripIndent(", "stripMargin(", "toBigDecimal()", "toBigInteger()", "toBoolean()", "toCharacter()", "toDouble()", "toFloat()", "toInteger()", "toList()", "toLong()", "toSet()", "toShort()", "toURI()", "toURL()", "tokenize(", "tr("}), new _getDefaultMethods_closure16(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure17(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof URL) {
            public class _getDefaultMethods_closure18
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure18(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure18.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure18.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure18.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure18.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure18.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure18.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure18.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure19
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure19(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure19.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure19.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure19.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure19.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure19.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure19.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure19.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[259].call(callSiteArray[260].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"eachLine(", "filterLine(", "getBytes()", "getBytes(", "getText()", "getText(", "newInputStream()", "newInputStream(", "newReader()", "newReader(", "readLines()", "readLines(", "splitEachLine(", "withInputStream(", "withReader("}), new _getDefaultMethods_closure18(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure19(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof InputStream) {
            public class _getDefaultMethods_closure20
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure20(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure20.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure20.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure20.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure20.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure20.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure20.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure20.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure21
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure21(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure21.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure21.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure21.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure21.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure21.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure21.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure21.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[261].call(callSiteArray[262].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"eachLine(", "filterLine(", "getBytes()", "getText()", "getText(", "newReader()", "newReader(", "readLines()", "readLines(", "splitEachLine(", "withReader(", "withStream("}), new _getDefaultMethods_closure20(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure21(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof OutputStream) {
            public class _getDefaultMethods_closure22
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure22(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure22.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure22.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure22.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure22.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure22.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure22.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure22.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure23
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure23(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure23.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure23.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure23.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure23.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure23.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure23.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure23.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[263].call(callSiteArray[264].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"newPrintWriter()", "newWriter()", "newWriter(", "setBytes(", "withPrintWriter(", "withStream(", "withWriter("}), new _getDefaultMethods_closure22(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure23(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        if (instance instanceof Number) {
            public class _getDefaultMethods_closure24
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure24(Object _outerInstance, Object _thisObject, Reference prefix) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure24.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.prefix = reference = prefix;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure24.$getCallSiteArray();
                    return callSiteArray[0].call(it, this.prefix.get());
                }

                public String getPrefix() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure24.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure24.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure24.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "startsWith";
                    return new CallSiteArray(_getDefaultMethods_closure24.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure24.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            public class _getDefaultMethods_closure25
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference candidates;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDefaultMethods_closure25(Object _outerInstance, Object _thisObject, Reference candidates) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDefaultMethods_closure25.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.candidates = reference = candidates;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDefaultMethods_closure25.$getCallSiteArray();
                    return callSiteArray[0].call(this.candidates.get(), it);
                }

                public List getCandidates() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure25.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.candidates.get(), List.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDefaultMethods_closure25.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDefaultMethods_closure25.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "add";
                    return new CallSiteArray(_getDefaultMethods_closure25.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDefaultMethods_closure25.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[265].call(callSiteArray[266].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"abs()", "downto(", "times(", "power(", "upto("}), new _getDefaultMethods_closure24(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure25(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[267].call(instance));
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual(clazz, null) && ScriptBytecodeAdapter.compareNotEqual(clazz, Class.class) && DefaultTypeTransformation.booleanUnbox(callSiteArray[268].call(clazz))) {
                callSiteArray[269].call(callSiteArray[270].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"any()", "any(", "collect()", "collect(", "count(", "countBy(", "drop(", "dropRight(", "dropWhile(", "each()", "each(", "every()", "every(", "find(", "findResult(", "flatten()", "init()", "inject(", "join(", "max()", "min()", "reverse()", "size()", "sort()", "split(", "take(", "takeRight(", "takeWhile("}), new _getDefaultMethods_closure26(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure27(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual(clazz, null) && ScriptBytecodeAdapter.compareNotEqual(clazz, Class.class) && DefaultTypeTransformation.booleanUnbox(callSiteArray[271].call(clazz))) {
            callSiteArray[272].call(callSiteArray[273].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"any()", "any(", "collect()", "collect(", "count(", "countBy(", "drop(", "dropRight(", "dropWhile(", "each()", "each(", "every()", "every(", "find(", "findResult(", "flatten()", "init()", "inject(", "join(", "max()", "min()", "reverse()", "size()", "sort()", "split(", "take(", "takeRight(", "takeWhile("}), new _getDefaultMethods_closure26(ReflectionCompletor.class, ReflectionCompletor.class, prefix2)), new _getDefaultMethods_closure27(ReflectionCompletor.class, ReflectionCompletor.class, candidates));
        }
        return candidates.get();
    }

    /*
     * WARNING - void declaration
     */
    private static Collection<ReflectionCompletionCandidate> addClassFieldsAndMethods(Class clazz, boolean includeStatic, boolean includeNonStatic, String prefix, Collection<ReflectionCompletionCandidate> rv, boolean renderBold) {
        void var3_3;
        Reference<Class> clazz2 = new Reference<Class>(clazz);
        Reference<Boolean> includeStatic2 = new Reference<Boolean>(includeStatic);
        Reference<Boolean> includeNonStatic2 = new Reference<Boolean>(includeNonStatic);
        Reference<void> prefix2 = new Reference<void>(var3_3);
        Reference<Collection<ReflectionCompletionCandidate>> rv2 = new Reference<Collection<ReflectionCompletionCandidate>>(rv);
        Reference<Boolean> renderBold2 = new Reference<Boolean>(renderBold);
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        Field[] fields = null;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = DefaultTypeTransformation.booleanUnbox(includeStatic2.get()) && !DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get()) ? callSiteArray[274].callGetProperty(clazz2.get()) : callSiteArray[275].call(clazz2.get());
            fields = (Field[])ScriptBytecodeAdapter.castToType(object, Field[].class);
        } else {
            Object object = DefaultTypeTransformation.booleanUnbox(includeStatic2.get()) && !DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get()) ? callSiteArray[276].callGetProperty(clazz2.get()) : callSiteArray[277].call(clazz2.get());
            fields = (Field[])ScriptBytecodeAdapter.castToType(object, Field[].class);
        }
        public class _addClassFieldsAndMethods_closure28
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefix;
            private /* synthetic */ Reference includeStatic;
            private /* synthetic */ Reference includeNonStatic;
            private /* synthetic */ Reference clazz;
            private /* synthetic */ Reference renderBold;
            private /* synthetic */ Reference rv;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _addClassFieldsAndMethods_closure28(Object _outerInstance, Object _thisObject, Reference prefix, Reference includeStatic, Reference includeNonStatic, Reference clazz, Reference renderBold, Reference rv) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                Reference reference5;
                Reference reference6;
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefix = reference6 = prefix;
                this.includeStatic = reference5 = includeStatic;
                this.includeNonStatic = reference4 = includeNonStatic;
                this.clazz = reference3 = clazz;
                this.renderBold = reference2 = renderBold;
                this.rv = reference = rv;
            }

            public Object doCall(Field fit) {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this, callSiteArray[1].callGetProperty(fit), this.prefix.get()))) {
                    int modifiers = DefaultTypeTransformation.intUnbox(callSiteArray[2].call(fit));
                    if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(Modifier.class, modifiers)) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(Modifier.class, modifiers)) ? DefaultTypeTransformation.booleanUnbox(this.includeStatic.get()) : DefaultTypeTransformation.booleanUnbox(this.includeNonStatic.get()))) {
                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(this.clazz.get())) || !(!DefaultTypeTransformation.booleanUnbox(this.includeStatic.get()) && DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[8].call(Modifier.class, modifiers)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[9].callGetProperty(fit), this.clazz.get()))) {
                                ReflectionCompletionCandidate candidate = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(callSiteArray[10].callConstructor(ReflectionCompletionCandidate.class, callSiteArray[11].callGetProperty(fit)), ReflectionCompletionCandidate.class);
                                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(this.renderBold.get())) {
                                    callSiteArray[13].call(callSiteArray[14].callGroovyObjectGetProperty(candidate), callSiteArray[15].call(callSiteArray[16].callGetProperty(Ansi.Attribute.class)));
                                }
                                return callSiteArray[17].call(this.rv.get(), candidate);
                            }
                            return null;
                        }
                        return null;
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call(Modifier.class, modifiers)) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(Modifier.class, modifiers)) ? DefaultTypeTransformation.booleanUnbox(this.includeStatic.get()) : DefaultTypeTransformation.booleanUnbox(this.includeNonStatic.get()))) {
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[20].call(this.clazz.get())) || !(!DefaultTypeTransformation.booleanUnbox(this.includeStatic.get()) && DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(Modifier.class, modifiers)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[24].callGetProperty(fit), this.clazz.get()))) {
                            ReflectionCompletionCandidate candidate = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(callSiteArray[25].callConstructor(ReflectionCompletionCandidate.class, callSiteArray[26].callGetProperty(fit)), ReflectionCompletionCandidate.class);
                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(this.renderBold.get())) {
                                callSiteArray[28].call(callSiteArray[29].callGroovyObjectGetProperty(candidate), callSiteArray[30].call(callSiteArray[31].callGetProperty(Ansi.Attribute.class)));
                            }
                            return callSiteArray[32].call(this.rv.get(), candidate);
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }

            public Object call(Field fit) {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[33].callCurrent((GroovyObject)this, fit);
                }
                return this.doCall(fit);
            }

            public String getPrefix() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.prefix.get());
            }

            public boolean getIncludeStatic() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.includeStatic.get());
            }

            public boolean getIncludeNonStatic() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.includeNonStatic.get());
            }

            public Class getClazz() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return ShortTypeHandling.castToClass(this.clazz.get());
            }

            public boolean getRenderBold() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.renderBold.get());
            }

            public Collection getRv() {
                CallSite[] callSiteArray = _addClassFieldsAndMethods_closure28.$getCallSiteArray();
                return (Collection)ScriptBytecodeAdapter.castToType(this.rv.get(), Collection.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addClassFieldsAndMethods_closure28.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "acceptName";
                stringArray[1] = "name";
                stringArray[2] = "getModifiers";
                stringArray[3] = "isPublic";
                stringArray[4] = "isStatic";
                stringArray[5] = "isEnum";
                stringArray[6] = "isPublic";
                stringArray[7] = "isFinal";
                stringArray[8] = "isStatic";
                stringArray[9] = "type";
                stringArray[10] = "<$constructor$>";
                stringArray[11] = "name";
                stringArray[12] = "isStatic";
                stringArray[13] = "add";
                stringArray[14] = "jAnsiCodes";
                stringArray[15] = "name";
                stringArray[16] = "INTENSITY_BOLD";
                stringArray[17] = "leftShift";
                stringArray[18] = "isPublic";
                stringArray[19] = "isStatic";
                stringArray[20] = "isEnum";
                stringArray[21] = "isPublic";
                stringArray[22] = "isFinal";
                stringArray[23] = "isStatic";
                stringArray[24] = "type";
                stringArray[25] = "<$constructor$>";
                stringArray[26] = "name";
                stringArray[27] = "isStatic";
                stringArray[28] = "add";
                stringArray[29] = "jAnsiCodes";
                stringArray[30] = "name";
                stringArray[31] = "INTENSITY_BOLD";
                stringArray[32] = "leftShift";
                stringArray[33] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[34];
                _addClassFieldsAndMethods_closure28.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_addClassFieldsAndMethods_closure28.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _addClassFieldsAndMethods_closure28.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[278].call((Object)fields, new _addClassFieldsAndMethods_closure28(ReflectionCompletor.class, ReflectionCompletor.class, prefix2, includeStatic2, includeNonStatic2, clazz2, renderBold2, rv2));
        Method[] methods = null;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = DefaultTypeTransformation.booleanUnbox(includeStatic2.get()) && !DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get()) ? callSiteArray[279].callGetProperty(clazz2.get()) : callSiteArray[280].call(clazz2.get());
            methods = (Method[])ScriptBytecodeAdapter.castToType(object, Method[].class);
        } else {
            Object object = DefaultTypeTransformation.booleanUnbox(includeStatic2.get()) && !DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get()) ? callSiteArray[281].callGetProperty(clazz2.get()) : callSiteArray[282].call(clazz2.get());
            methods = (Method[])ScriptBytecodeAdapter.castToType(object, Method[].class);
        }
        Method methIt = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[283].call(methods), Iterator.class);
        while (iterator.hasNext()) {
            methIt = (Method)ScriptBytecodeAdapter.castToType(iterator.next(), Method.class);
            String name = ShortTypeHandling.castToString(callSiteArray[284].call(methIt));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[285].call((Object)name, "super$"))) {
                Object object = callSiteArray[286].call((Object)name, callSiteArray[287].call(callSiteArray[288].call((Object)name, "^super\\$.*\\$")));
                name = ShortTypeHandling.castToString(object);
            }
            int modifiers = DefaultTypeTransformation.intUnbox(callSiteArray[289].call(methIt));
            if (!(DefaultTypeTransformation.booleanUnbox(callSiteArray[290].call(Modifier.class, modifiers)) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[291].call(Modifier.class, modifiers)) ? DefaultTypeTransformation.booleanUnbox(includeStatic2.get()) : DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get())))) continue;
            boolean fieldnameSuggested = false;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[292].call((Object)name, BEAN_ACCESSOR_PATTERN))) {
                String fieldname = ShortTypeHandling.castToString(callSiteArray[293].callStatic(ReflectionCompletor.class, name, callSiteArray[294].callGetProperty(callSiteArray[295].callGetProperty(methIt))));
                if (ScriptBytecodeAdapter.compareNotEqual(fieldname, null) && ScriptBytecodeAdapter.compareNotEqual(fieldname, "metaClass") && ScriptBytecodeAdapter.compareNotEqual(fieldname, "property") && DefaultTypeTransformation.booleanUnbox(callSiteArray[296].callStatic(ReflectionCompletor.class, fieldname, (String)prefix2.get()))) {
                    boolean bl;
                    fieldnameSuggested = bl = true;
                    ReflectionCompletionCandidate fieldCandidate = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(callSiteArray[297].callConstructor(ReflectionCompletionCandidate.class, fieldname), ReflectionCompletionCandidate.class);
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[298].call(rv2.get(), fieldCandidate))) {
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[299].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(renderBold2.get())) {
                            callSiteArray[300].call(callSiteArray[301].callGroovyObjectGetProperty(fieldCandidate), callSiteArray[302].call(callSiteArray[303].callGetProperty(Ansi.Attribute.class)));
                        }
                        callSiteArray[304].call(rv2.get(), fieldCandidate);
                    }
                }
            }
            if (!(!fieldnameSuggested && DefaultTypeTransformation.booleanUnbox(callSiteArray[305].callStatic(ReflectionCompletor.class, name, (String)prefix2.get())))) continue;
            ReflectionCompletionCandidate candidate = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(callSiteArray[306].callConstructor(ReflectionCompletionCandidate.class, callSiteArray[307].call((Object)name, ScriptBytecodeAdapter.compareEqual(callSiteArray[308].callGetProperty(callSiteArray[309].callGetProperty(methIt)), 0) ? "()" : "(")), ReflectionCompletionCandidate.class);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[310].call(Modifier.class, modifiers)) && DefaultTypeTransformation.booleanUnbox(renderBold2.get())) {
                callSiteArray[311].call(callSiteArray[312].callGroovyObjectGetProperty(candidate), callSiteArray[313].call(callSiteArray[314].callGetProperty(Ansi.Attribute.class)));
            }
            callSiteArray[315].call(rv2.get(), candidate);
        }
        Class interface_ = null;
        Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[316].call(callSiteArray[317].call(clazz2.get())), Iterator.class);
        while (iterator2.hasNext()) {
            interface_ = ShortTypeHandling.castToClass(iterator2.next());
            callSiteArray[318].callStatic(ReflectionCompletor.class, ArrayUtil.createArray(interface_, DefaultTypeTransformation.booleanUnbox(includeStatic2.get()), DefaultTypeTransformation.booleanUnbox(includeNonStatic2.get()), (String)prefix2.get(), rv2.get(), false));
        }
        return (Collection)ScriptBytecodeAdapter.castToType(null, Collection.class);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static CharSequence getFieldnameForAccessor(String accessor, int parameterLength) {
        CallSite[] callSiteArray = ReflectionCompletor.$getCallSiteArray();
        String fieldname = null;
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[319].call((Object)accessor, "get"))) {
                if (parameterLength == 0) {
                    Object object = callSiteArray[320].call((Object)accessor, 3);
                    fieldname = ShortTypeHandling.castToString(object);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[321].call((Object)accessor, "set"))) {
                if (parameterLength == 1) {
                    Object object = callSiteArray[322].call((Object)accessor, 3);
                    fieldname = ShortTypeHandling.castToString(object);
                }
            } else {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[323].call((Object)accessor, "is"))) throw (Throwable)callSiteArray[325].callConstructor(IllegalStateException.class, callSiteArray[326].call((Object)"getFieldnameForAccessor called with invalid accessor : ", accessor));
                if (parameterLength == 0) {
                    Object object = callSiteArray[324].call((Object)accessor, 2);
                    fieldname = ShortTypeHandling.castToString(object);
                }
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[327].call((Object)accessor, "get"))) {
            if (parameterLength == 0) {
                Object object = callSiteArray[328].call((Object)accessor, 3);
                fieldname = ShortTypeHandling.castToString(object);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[329].call((Object)accessor, "set"))) {
            if (parameterLength == 1) {
                Object object = callSiteArray[330].call((Object)accessor, 3);
                fieldname = ShortTypeHandling.castToString(object);
            }
        } else {
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[331].call((Object)accessor, "is"))) throw (Throwable)callSiteArray[333].callConstructor(IllegalStateException.class, callSiteArray[334].call((Object)"getFieldnameForAccessor called with invalid accessor : ", accessor));
            if (parameterLength == 0) {
                Object object = callSiteArray[332].call((Object)accessor, 2);
                fieldname = ShortTypeHandling.castToString(object);
            }
        }
        if (!ScriptBytecodeAdapter.compareEqual(fieldname, null)) return (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[335].call(callSiteArray[336].call(callSiteArray[337].call((Object)fieldname, 0)), callSiteArray[338].call((Object)fieldname, 1)), CharSequence.class);
        return (CharSequence)ScriptBytecodeAdapter.castToType(null, CharSequence.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ReflectionCompletor.class) {
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
        Object object = ReflectionCompletor.$getCallSiteArray()[339].callConstructor(NavigablePropertiesCompleter.class);
        PROPERTIES_COMPLETER = (NavigablePropertiesCompleter)ScriptBytecodeAdapter.castToType(object, NavigablePropertiesCompleter.class);
        Object object2 = ScriptBytecodeAdapter.bitwiseNegate("^(get|set|is)[A-Z].*");
        BEAN_ACCESSOR_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object2, Pattern.class);
    }

    public final Groovysh getShell() {
        return this.shell;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "size";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "plus";
        stringArray[3] = "type";
        stringArray[4] = "last";
        stringArray[5] = "DOT";
        stringArray[6] = "type";
        stringArray[7] = "last";
        stringArray[8] = "OPTIONAL_DOT";
        stringArray[9] = "type";
        stringArray[10] = "last";
        stringArray[11] = "SPREAD_DOT";
        stringArray[12] = "last";
        stringArray[13] = "getAt";
        stringArray[14] = "type";
        stringArray[15] = "getAt";
        stringArray[16] = "DOT";
        stringArray[17] = "type";
        stringArray[18] = "getAt";
        stringArray[19] = "OPTIONAL_DOT";
        stringArray[20] = "type";
        stringArray[21] = "getAt";
        stringArray[22] = "SPREAD_DOT";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "plus";
        stringArray[25] = "last";
        stringArray[26] = "getAt";
        stringArray[27] = "getAt";
        stringArray[28] = "type";
        stringArray[29] = "last";
        stringArray[30] = "DOT";
        stringArray[31] = "type";
        stringArray[32] = "last";
        stringArray[33] = "OPTIONAL_DOT";
        stringArray[34] = "type";
        stringArray[35] = "last";
        stringArray[36] = "SPREAD_DOT";
        stringArray[37] = "last";
        stringArray[38] = "getAt";
        stringArray[39] = "type";
        stringArray[40] = "getAt";
        stringArray[41] = "DOT";
        stringArray[42] = "type";
        stringArray[43] = "getAt";
        stringArray[44] = "OPTIONAL_DOT";
        stringArray[45] = "type";
        stringArray[46] = "getAt";
        stringArray[47] = "SPREAD_DOT";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "plus";
        stringArray[50] = "last";
        stringArray[51] = "getAt";
        stringArray[52] = "getAt";
        stringArray[53] = "getInvokerClassOrInstance";
        stringArray[54] = "type";
        stringArray[55] = "SPREAD_DOT";
        stringArray[56] = "find";
        stringArray[57] = "text";
        stringArray[58] = "completeInstanceMembers";
        stringArray[59] = "getPublicFieldsAndMethods";
        stringArray[60] = "length";
        stringArray[61] = "valueOf";
        stringArray[62] = "get";
        stringArray[63] = "METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY";
        stringArray[64] = "length";
        stringArray[65] = "valueOf";
        stringArray[66] = "get";
        stringArray[67] = "METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY";
        stringArray[68] = "addAll";
        stringArray[69] = "collect";
        stringArray[70] = "getMetaclassMethods";
        stringArray[71] = "removeStandardMethods";
        stringArray[72] = "addAll";
        stringArray[73] = "collect";
        stringArray[74] = "getDefaultMethods";
        stringArray[75] = "size";
        stringArray[76] = "sort";
        stringArray[77] = "valueOf";
        stringArray[78] = "get";
        stringArray[79] = "COLORS_PREFERENCE_KEY";
        stringArray[80] = "addAll";
        stringArray[81] = "collect";
        stringArray[82] = "addAll";
        stringArray[83] = "line";
        stringArray[84] = "line";
        stringArray[85] = "minus";
        stringArray[86] = "column";
        stringArray[87] = "plus";
        stringArray[88] = "column";
        stringArray[89] = "minus";
        stringArray[90] = "length";
        stringArray[91] = "getText";
        stringArray[92] = "type";
        stringArray[93] = "last";
        stringArray[94] = "DOT";
        stringArray[95] = "type";
        stringArray[96] = "last";
        stringArray[97] = "OPTIONAL_DOT";
        stringArray[98] = "type";
        stringArray[99] = "last";
        stringArray[100] = "DOT";
        stringArray[101] = "type";
        stringArray[102] = "last";
        stringArray[103] = "OPTIONAL_DOT";
        stringArray[104] = "getInvokerTokens";
        stringArray[105] = "tokenListToEvalString";
        stringArray[106] = "replace";
        stringArray[107] = "evaluate";
        stringArray[108] = "interp";
        stringArray[109] = "plus";
        stringArray[110] = "plus";
        stringArray[111] = "getImportStatements";
        stringArray[112] = "tokenListToEvalString";
        stringArray[113] = "replace";
        stringArray[114] = "evaluate";
        stringArray[115] = "interp";
        stringArray[116] = "plus";
        stringArray[117] = "plus";
        stringArray[118] = "getImportStatements";
        stringArray[119] = "size";
        stringArray[120] = "<$constructor$>";
        stringArray[121] = "iterator";
        stringArray[122] = "reverse";
        stringArray[123] = "type";
        stringArray[124] = "STRING_LITERAL";
        stringArray[125] = "LPAREN";
        stringArray[126] = "empty";
        stringArray[127] = "pop";
        stringArray[128] = "LPAREN";
        stringArray[129] = "LBRACK";
        stringArray[130] = "empty";
        stringArray[131] = "pop";
        stringArray[132] = "LBRACK";
        stringArray[133] = "RBRACK";
        stringArray[134] = "push";
        stringArray[135] = "LBRACK";
        stringArray[136] = "RPAREN";
        stringArray[137] = "push";
        stringArray[138] = "LPAREN";
        stringArray[139] = "COMPARE_TO";
        stringArray[140] = "EQUAL";
        stringArray[141] = "NOT_EQUAL";
        stringArray[142] = "ASSIGN";
        stringArray[143] = "GT";
        stringArray[144] = "LT";
        stringArray[145] = "GE";
        stringArray[146] = "LE";
        stringArray[147] = "PLUS";
        stringArray[148] = "PLUS_ASSIGN";
        stringArray[149] = "MINUS";
        stringArray[150] = "MINUS_ASSIGN";
        stringArray[151] = "STAR";
        stringArray[152] = "STAR_ASSIGN";
        stringArray[153] = "DIV";
        stringArray[154] = "DIV_ASSIGN";
        stringArray[155] = "BOR";
        stringArray[156] = "BOR_ASSIGN";
        stringArray[157] = "BAND";
        stringArray[158] = "BAND_ASSIGN";
        stringArray[159] = "BXOR";
        stringArray[160] = "BXOR_ASSIGN";
        stringArray[161] = "BNOT";
        stringArray[162] = "LOR";
        stringArray[163] = "LAND";
        stringArray[164] = "LNOT";
        stringArray[165] = "LITERAL_in";
        stringArray[166] = "LITERAL_instanceof";
        stringArray[167] = "empty";
        stringArray[168] = "LCURLY";
        stringArray[169] = "SEMI";
        stringArray[170] = "STRING_CTOR_START";
        stringArray[171] = "IDENT";
        stringArray[172] = "type";
        stringArray[173] = "LPAREN";
        stringArray[174] = "type";
        stringArray[175] = "IDENT";
        stringArray[176] = "RANGE_INCLUSIVE";
        stringArray[177] = "RANGE_EXCLUSIVE";
        stringArray[178] = "COLON";
        stringArray[179] = "COMMA";
        stringArray[180] = "empty";
        stringArray[181] = "LITERAL_true";
        stringArray[182] = "LITERAL_false";
        stringArray[183] = "NUM_INT";
        stringArray[184] = "NUM_FLOAT";
        stringArray[185] = "NUM_LONG";
        stringArray[186] = "NUM_DOUBLE";
        stringArray[187] = "NUM_BIG_INT";
        stringArray[188] = "NUM_BIG_DECIMAL";
        stringArray[189] = "MEMBER_POINTER";
        stringArray[190] = "DOT";
        stringArray[191] = "OPTIONAL_DOT";
        stringArray[192] = "previous";
        stringArray[193] = "getAt";
        stringArray[194] = "<$constructor$>";
        stringArray[195] = "iterator";
        stringArray[196] = "type";
        stringArray[197] = "STRING_LITERAL";
        stringArray[198] = "append";
        stringArray[199] = "append";
        stringArray[200] = "append";
        stringArray[201] = "text";
        stringArray[202] = "append";
        stringArray[203] = "text";
        stringArray[204] = "toString";
        stringArray[205] = "startsWith";
        stringArray[206] = "contains";
        stringArray[207] = "startsWith";
        stringArray[208] = "startsWith";
        stringArray[209] = "contains";
        stringArray[210] = "startsWith";
        stringArray[211] = "<$constructor$>";
        stringArray[212] = "getMetaClass";
        stringArray[213] = "each";
        stringArray[214] = "metaMethods";
        stringArray[215] = "each";
        stringArray[216] = "metaMethods";
        stringArray[217] = "sort";
        stringArray[218] = "<$constructor$>";
        stringArray[219] = "getClass";
        stringArray[220] = "length";
        stringArray[221] = "valueOf";
        stringArray[222] = "get";
        stringArray[223] = "METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY";
        stringArray[224] = "length";
        stringArray[225] = "valueOf";
        stringArray[226] = "get";
        stringArray[227] = "METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY";
        stringArray[228] = "addClassFieldsAndMethods";
        stringArray[229] = "superclass";
        stringArray[230] = "isArray";
        stringArray[231] = "iterator";
        stringArray[232] = "startsWith";
        stringArray[233] = "add";
        stringArray[234] = "<$constructor$>";
        stringArray[235] = "name";
        stringArray[236] = "INTENSITY_BOLD";
        stringArray[237] = "<$constructor$>";
        stringArray[238] = "addCompletions";
        stringArray[239] = "addAll";
        stringArray[240] = "collect";
        stringArray[241] = "sort";
        stringArray[242] = "iterator";
        stringArray[243] = "iterator";
        stringArray[244] = "equals";
        stringArray[245] = "value";
        stringArray[246] = "remove";
        stringArray[247] = "each";
        stringArray[248] = "findAll";
        stringArray[249] = "each";
        stringArray[250] = "findAll";
        stringArray[251] = "each";
        stringArray[252] = "findAll";
        stringArray[253] = "each";
        stringArray[254] = "findAll";
        stringArray[255] = "each";
        stringArray[256] = "findAll";
        stringArray[257] = "each";
        stringArray[258] = "findAll";
        stringArray[259] = "each";
        stringArray[260] = "findAll";
        stringArray[261] = "each";
        stringArray[262] = "findAll";
        stringArray[263] = "each";
        stringArray[264] = "findAll";
        stringArray[265] = "each";
        stringArray[266] = "findAll";
        stringArray[267] = "getClass";
        stringArray[268] = "isArray";
        stringArray[269] = "each";
        stringArray[270] = "findAll";
        stringArray[271] = "isArray";
        stringArray[272] = "each";
        stringArray[273] = "findAll";
        stringArray[274] = "fields";
        stringArray[275] = "getDeclaredFields";
        stringArray[276] = "fields";
        stringArray[277] = "getDeclaredFields";
        stringArray[278] = "each";
        stringArray[279] = "methods";
        stringArray[280] = "getDeclaredMethods";
        stringArray[281] = "methods";
        stringArray[282] = "getDeclaredMethods";
        stringArray[283] = "iterator";
        stringArray[284] = "getName";
        stringArray[285] = "startsWith";
        stringArray[286] = "substring";
        stringArray[287] = "length";
        stringArray[288] = "find";
        stringArray[289] = "getModifiers";
        stringArray[290] = "isPublic";
        stringArray[291] = "isStatic";
        stringArray[292] = "matches";
        stringArray[293] = "getFieldnameForAccessor";
        stringArray[294] = "length";
        stringArray[295] = "parameterTypes";
        stringArray[296] = "acceptName";
        stringArray[297] = "<$constructor$>";
        stringArray[298] = "contains";
        stringArray[299] = "isStatic";
        stringArray[300] = "add";
        stringArray[301] = "jAnsiCodes";
        stringArray[302] = "name";
        stringArray[303] = "INTENSITY_BOLD";
        stringArray[304] = "add";
        stringArray[305] = "acceptName";
        stringArray[306] = "<$constructor$>";
        stringArray[307] = "plus";
        stringArray[308] = "length";
        stringArray[309] = "parameterTypes";
        stringArray[310] = "isStatic";
        stringArray[311] = "add";
        stringArray[312] = "jAnsiCodes";
        stringArray[313] = "name";
        stringArray[314] = "INTENSITY_BOLD";
        stringArray[315] = "add";
        stringArray[316] = "iterator";
        stringArray[317] = "getInterfaces";
        stringArray[318] = "addClassFieldsAndMethods";
        stringArray[319] = "startsWith";
        stringArray[320] = "substring";
        stringArray[321] = "startsWith";
        stringArray[322] = "substring";
        stringArray[323] = "startsWith";
        stringArray[324] = "substring";
        stringArray[325] = "<$constructor$>";
        stringArray[326] = "plus";
        stringArray[327] = "startsWith";
        stringArray[328] = "substring";
        stringArray[329] = "startsWith";
        stringArray[330] = "substring";
        stringArray[331] = "startsWith";
        stringArray[332] = "substring";
        stringArray[333] = "<$constructor$>";
        stringArray[334] = "plus";
        stringArray[335] = "plus";
        stringArray[336] = "toLowerCase";
        stringArray[337] = "getAt";
        stringArray[338] = "substring";
        stringArray[339] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[340];
        ReflectionCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ReflectionCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ReflectionCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

