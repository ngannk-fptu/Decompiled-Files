/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.IdentifierCompletor;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletor;

public class ImportsSyntaxCompletor
implements IdentifierCompletor,
GroovyObject {
    private final Groovysh shell;
    private List<String> preimportedClassNames;
    private final Map<String, Collection<String>> cachedImports;
    private static final String STATIC_IMPORT_PATTERN;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ImportsSyntaxCompletor(Groovysh shell) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ImportsSyntaxCompletor.$getCallSiteArray();
        Object object = callSiteArray[0].call(callSiteArray[1].callConstructor(HashMap.class), new _closure1(this, this));
        this.cachedImports = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
    }

    @Override
    public boolean complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = ImportsSyntaxCompletor.$getCallSiteArray();
        String prefix = ShortTypeHandling.castToString(callSiteArray[2].call(callSiteArray[3].call(tokens)));
        boolean foundMatch = DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callCurrent(this, prefix, candidates));
        String importSpec = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(callSiteArray[6].callGetProperty(this.shell)), Iterator.class);
        while (iterator.hasNext()) {
            importSpec = ShortTypeHandling.castToString(iterator.next());
            foundMatch = DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)foundMatch, callSiteArray[8].callCurrent(this, prefix, importSpec, candidates)));
        }
        return foundMatch;
    }

    public boolean findMatchingImportedClassesCached(String prefix, String importSpec, List<String> candidates) {
        Reference<String> prefix2 = new Reference<String>(prefix);
        CallSite[] callSiteArray = ImportsSyntaxCompletor.$getCallSiteArray();
        public class _findMatchingImportedClassesCached_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefix;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _findMatchingImportedClassesCached_closure2(Object _outerInstance, Object _thisObject, Reference prefix) {
                Reference reference;
                CallSite[] callSiteArray = _findMatchingImportedClassesCached_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefix = reference = prefix;
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _findMatchingImportedClassesCached_closure2.$getCallSiteArray();
                return callSiteArray[0].call((Object)it, this.prefix.get());
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _findMatchingImportedClassesCached_closure2.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            public String getPrefix() {
                CallSite[] callSiteArray = _findMatchingImportedClassesCached_closure2.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.prefix.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _findMatchingImportedClassesCached_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "startsWith";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _findMatchingImportedClassesCached_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_findMatchingImportedClassesCached_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _findMatchingImportedClassesCached_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call(candidates, callSiteArray[10].call(callSiteArray[11].call(this.cachedImports, importSpec), new _findMatchingImportedClassesCached_closure2(this, this, prefix2))));
    }

    public boolean findMatchingPreImportedClasses(String prefix, Collection<String> matches) {
        CallSite[] callSiteArray = ImportsSyntaxCompletor.$getCallSiteArray();
        boolean foundMatch = false;
        if (ScriptBytecodeAdapter.compareEqual(this.preimportedClassNames, null)) {
            List list;
            this.preimportedClassNames = list = ScriptBytecodeAdapter.createList(new Object[0]);
            Object packname = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(callSiteArray[13].callGetProperty(ResolveVisitor.class)), Iterator.class);
            while (iterator.hasNext()) {
                packname = iterator.next();
                Set packnames = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[14].call(callSiteArray[15].callGetProperty(this.shell), callSiteArray[16].call(packname, ScriptBytecodeAdapter.createRange(0, -2, true))), Set.class);
                if (!DefaultTypeTransformation.booleanUnbox(packnames)) continue;
                public class _findMatchingPreImportedClasses_closure3
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _findMatchingPreImportedClasses_closure3(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _findMatchingPreImportedClasses_closure3.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(String it) {
                        CallSite[] callSiteArray = _findMatchingPreImportedClasses_closure3.$getCallSiteArray();
                        return ScriptBytecodeAdapter.isCase(callSiteArray[0].call((Object)it, 0), ScriptBytecodeAdapter.createRange("A", "Z", true));
                    }

                    public Object call(String it) {
                        CallSite[] callSiteArray = _findMatchingPreImportedClasses_closure3.$getCallSiteArray();
                        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            return callSiteArray[1].callCurrent((GroovyObject)this, it);
                        }
                        return this.doCall(it);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _findMatchingPreImportedClasses_closure3.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "getAt";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _findMatchingPreImportedClasses_closure3.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_findMatchingPreImportedClasses_closure3.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _findMatchingPreImportedClasses_closure3.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[17].call(this.preimportedClassNames, callSiteArray[18].call((Object)packnames, new _findMatchingPreImportedClasses_closure3(this, this)));
            }
            callSiteArray[19].call(this.preimportedClassNames, "BigInteger");
            callSiteArray[20].call(this.preimportedClassNames, "BigDecimal");
        }
        String preImpClassname = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[21].call(this.preimportedClassNames), Iterator.class);
        while (iterator.hasNext()) {
            boolean bl;
            preImpClassname = ShortTypeHandling.castToString(iterator.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call((Object)preImpClassname, prefix))) continue;
            callSiteArray[23].call(matches, preImpClassname);
            foundMatch = bl = true;
        }
        return foundMatch;
    }

    public void collectImportedSymbols(String importSpec, Collection<String> matches) {
        CallSite[] callSiteArray = ImportsSyntaxCompletor.$getCallSiteArray();
        String asKeyword = " as ";
        int asIndex = DefaultTypeTransformation.intUnbox(callSiteArray[24].call((Object)importSpec, asKeyword));
        if (ScriptBytecodeAdapter.compareGreaterThan(asIndex, -1)) {
            String alias = ShortTypeHandling.castToString(callSiteArray[25].call((Object)importSpec, callSiteArray[26].call((Object)asIndex, callSiteArray[27].call(asKeyword))));
            callSiteArray[28].call(matches, alias);
            return;
        }
        int lastDotIndex = DefaultTypeTransformation.intUnbox(callSiteArray[29].call((Object)importSpec, "."));
        String symbolName = null;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[30].call((Object)importSpec, callSiteArray[31].call((Object)lastDotIndex, 1));
            symbolName = ShortTypeHandling.castToString(object);
        } else {
            Object object = callSiteArray[32].call((Object)importSpec, lastDotIndex + 1);
            symbolName = ShortTypeHandling.castToString(object);
        }
        String staticPrefix = "static ";
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[33].call((Object)importSpec, staticPrefix))) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[34].call((Object)importSpec, STATIC_IMPORT_PATTERN))) {
                String className = ShortTypeHandling.castToString(callSiteArray[35].call(importSpec, callSiteArray[36].call(staticPrefix), lastDotIndex));
                Class clazz = (Class)ScriptBytecodeAdapter.asType(callSiteArray[37].call(callSiteArray[38].callGetProperty(this.shell), ScriptBytecodeAdapter.createList(new Object[]{className})), Class.class);
                if (ScriptBytecodeAdapter.compareNotEqual(clazz, null)) {
                    List clazzSymbols = (List)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.getPropertySpreadSafe(ImportsSyntaxCompletor.class, callSiteArray[39].call(ReflectionCompletor.class, clazz, ""), "value"), List.class);
                    List importedSymbols = null;
                    if (ScriptBytecodeAdapter.compareEqual(symbolName, "*")) {
                        List list;
                        importedSymbols = list = clazzSymbols;
                    } else {
                        Set acceptableMatches = (Set)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createList(new Object[]{symbolName, callSiteArray[40].call((Object)symbolName, "("), callSiteArray[41].call((Object)symbolName, "()")}), Set.class);
                        Object object = callSiteArray[42].call((Object)acceptableMatches, clazzSymbols);
                        importedSymbols = (List)ScriptBytecodeAdapter.castToType(object, List.class);
                    }
                    callSiteArray[43].call(matches, importedSymbols);
                }
            }
        } else if (ScriptBytecodeAdapter.compareEqual(symbolName, "*")) {
            callSiteArray[44].call(matches, callSiteArray[45].call(callSiteArray[46].callGetProperty(this.shell), importSpec));
        } else {
            callSiteArray[47].call(matches, symbolName);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ImportsSyntaxCompletor.class) {
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
        Object object = ScriptBytecodeAdapter.bitwiseNegate("^static ([a-zA-Z_][a-zA-Z_0-9]*\\.)+([a-zA-Z_][a-zA-Z_0-9]*|\\*)$");
        STATIC_IMPORT_PATTERN = ShortTypeHandling.castToString(object);
    }

    public final Groovysh getShell() {
        return this.shell;
    }

    public List<String> getPreimportedClassNames() {
        return this.preimportedClassNames;
    }

    public void setPreimportedClassNames(List<String> list) {
        this.preimportedClassNames = list;
    }

    public final Map<String, Collection<String>> getCachedImports() {
        return this.cachedImports;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "withDefault";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "getText";
        stringArray[3] = "last";
        stringArray[4] = "findMatchingPreImportedClasses";
        stringArray[5] = "iterator";
        stringArray[6] = "imports";
        stringArray[7] = "or";
        stringArray[8] = "findMatchingImportedClassesCached";
        stringArray[9] = "addAll";
        stringArray[10] = "findAll";
        stringArray[11] = "get";
        stringArray[12] = "iterator";
        stringArray[13] = "DEFAULT_IMPORTS";
        stringArray[14] = "getContents";
        stringArray[15] = "packageHelper";
        stringArray[16] = "getAt";
        stringArray[17] = "addAll";
        stringArray[18] = "findAll";
        stringArray[19] = "add";
        stringArray[20] = "add";
        stringArray[21] = "iterator";
        stringArray[22] = "startsWith";
        stringArray[23] = "add";
        stringArray[24] = "indexOf";
        stringArray[25] = "substring";
        stringArray[26] = "plus";
        stringArray[27] = "length";
        stringArray[28] = "leftShift";
        stringArray[29] = "lastIndexOf";
        stringArray[30] = "substring";
        stringArray[31] = "plus";
        stringArray[32] = "substring";
        stringArray[33] = "startsWith";
        stringArray[34] = "matches";
        stringArray[35] = "substring";
        stringArray[36] = "length";
        stringArray[37] = "evaluate";
        stringArray[38] = "interp";
        stringArray[39] = "getPublicFieldsAndMethods";
        stringArray[40] = "plus";
        stringArray[41] = "plus";
        stringArray[42] = "intersect";
        stringArray[43] = "addAll";
        stringArray[44] = "addAll";
        stringArray[45] = "getContents";
        stringArray[46] = "packageHelper";
        stringArray[47] = "leftShift";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[48];
        ImportsSyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ImportsSyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ImportsSyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(String key) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            Collection matchingImports = (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(TreeSet.class), Collection.class);
            callSiteArray[1].callCurrent(this, key, matchingImports);
            return matchingImports;
        }

        public Object call(String key) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                return callSiteArray[2].callCurrent((GroovyObject)this, key);
            }
            return this.doCall(key);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
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
            stringArray[1] = "collectImportedSymbols";
            stringArray[2] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[3];
            _closure1.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure1.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure1.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

