/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.IntRange;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import jline.console.completer.Completer;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Evaluator;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletionCandidate;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletor;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.PackageHelper;

public class ImportCompleter
implements Completer,
GroovyObject {
    protected final Logger log;
    private PackageHelper packageHelper;
    private Groovysh shell;
    private static final Pattern QUALIFIED_CLASS_DOT_PATTERN;
    private static final Pattern PACK_OR_CLASSNAME_PATTERN;
    private static final Pattern PACK_OR_SIMPLE_CLASSNAME_PATTERN;
    private static final Pattern PACK_OR_CLASS_OR_METHODNAME_PATTERN;
    private static final Pattern LOWERCASE_IMPORT_ITEM_PATTERN;
    private final boolean staticImport;
    private final Evaluator interpreter;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ImportCompleter(PackageHelper packageHelper, Evaluator interp, boolean staticImport) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ImportCompleter.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, ImportCompleter.class);
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        PackageHelper packageHelper2 = packageHelper;
        this.packageHelper = (PackageHelper)ScriptBytecodeAdapter.castToType(packageHelper2, PackageHelper.class);
        boolean bl = staticImport;
        this.staticImport = DefaultTypeTransformation.booleanUnbox(bl);
        Evaluator evaluator = interp;
        this.interpreter = (Evaluator)ScriptBytecodeAdapter.castToType(evaluator, Evaluator.class);
        Groovysh groovysh = this.shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
    }

    public int complete(String buffer, int cursor, List<CharSequence> result) {
        Reference<String> currentImportExpression = new Reference<String>(DefaultTypeTransformation.booleanUnbox(buffer) ? buffer.substring(0, cursor) : "");
        if (this.staticImport ? !StringGroovyMethods.matches((CharSequence)currentImportExpression.get(), PACK_OR_CLASS_OR_METHODNAME_PATTERN) : !StringGroovyMethods.matches((CharSequence)currentImportExpression.get(), PACK_OR_SIMPLE_CLASSNAME_PATTERN)) {
            return -1;
        }
        if (currentImportExpression.get().contains("..")) {
            return -1;
        }
        if (currentImportExpression.get().endsWith(".")) {
            if (StringGroovyMethods.matches((CharSequence)currentImportExpression.get(), LOWERCASE_IMPORT_ITEM_PATTERN)) {
                Set<String> classnames = this.packageHelper.getContents(StringGroovyMethods.getAt(currentImportExpression.get(), new IntRange(true, 0, -2)));
                if (DefaultTypeTransformation.booleanUnbox(classnames)) {
                    if (this.staticImport) {
                        public class _complete_closure1
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;

                            public _complete_closure1(Object _outerInstance, Object _thisObject) {
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(String it) {
                                return StringGroovyMethods.plus(it, (CharSequence)".");
                            }

                            public Object call(String it) {
                                return this.doCall(it);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _complete_closure1.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }
                        }
                        result.addAll(DefaultGroovyMethods.collect(classnames, new _complete_closure1(this, this)));
                    } else {
                        public class _complete_closure2
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;

                            public _complete_closure2(Object _outerInstance, Object _thisObject) {
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(String it) {
                                return ImportCompleter.access$0(null, it);
                            }

                            public Object call(String it) {
                                return this.doCall(it);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _complete_closure2.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }
                        }
                        result.addAll(DefaultGroovyMethods.collect(classnames, new _complete_closure2(this, this)));
                    }
                }
                if (!this.staticImport) {
                    result.add("* ");
                }
                return currentImportExpression.get().length();
            }
            if (this.staticImport && StringGroovyMethods.matches((CharSequence)currentImportExpression.get(), QUALIFIED_CLASS_DOT_PATTERN)) {
                Class clazz = (Class)ScriptBytecodeAdapter.asType(this.interpreter.evaluate(ScriptBytecodeAdapter.createList(new Object[]{StringGroovyMethods.getAt(currentImportExpression.get(), new IntRange(true, 0, -2))})), Class.class);
                if (clazz != null) {
                    Collection<ReflectionCompletionCandidate> members = ReflectionCompletor.getPublicFieldsAndMethods(clazz, "");
                    public class _complete_closure3
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;

                        public _complete_closure3(Object _outerInstance, Object _thisObject) {
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(ReflectionCompletionCandidate it) {
                            return StringGroovyMethods.plus(it.getValue().replace("(", "").replace(")", ""), (CharSequence)" ");
                        }

                        public Object call(ReflectionCompletionCandidate it) {
                            return this.doCall(it);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _complete_closure3.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }
                    }
                    result.addAll(DefaultGroovyMethods.collect(members, new _complete_closure3(this, this)));
                }
                result.add("* ");
                return currentImportExpression.get().length();
            }
            return -1;
        }
        Reference<Object> prefix = new Reference<Object>(null);
        String cfr_ignored_0 = prefix.get();
        int lastDot = currentImportExpression.get().lastIndexOf(".");
        if (ScriptBytecodeAdapter.compareEqual(lastDot, -1)) {
            String string = currentImportExpression.get();
            prefix.set(string);
        } else {
            String string = currentImportExpression.get().substring(lastDot + 1);
            prefix.set(string);
        }
        String baseString = currentImportExpression.get().substring(0, Math.max(lastDot, 0));
        if (StringGroovyMethods.matches((CharSequence)currentImportExpression.get(), PACK_OR_CLASSNAME_PATTERN)) {
            Set<String> candidates = this.packageHelper.getContents(baseString);
            if (candidates == null || candidates.size() == 0) {
                public class _complete_closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference currentImportExpression;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;

                    public _complete_closure4(Object _outerInstance, Object _thisObject, Reference currentImportExpression) {
                        super(_outerInstance, _thisObject);
                        Reference reference;
                        this.currentImportExpression = reference = currentImportExpression;
                    }

                    public Object doCall(String it) {
                        return it.startsWith(ShortTypeHandling.castToString(this.currentImportExpression.get()));
                    }

                    public Object call(String it) {
                        return this.doCall(it);
                    }

                    public String getCurrentImportExpression() {
                        return ShortTypeHandling.castToString(this.currentImportExpression.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _complete_closure4.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }
                }
                Collection<Object> standards = DefaultGroovyMethods.findAll((Object[])ScriptBytecodeAdapter.castToType(ResolveVisitor.DEFAULT_IMPORTS, Object[].class), (Closure)new _complete_closure4(this, this, currentImportExpression));
                if (DefaultTypeTransformation.booleanUnbox(standards)) {
                    result.addAll(standards);
                    return 0;
                }
                return -1;
            }
            this.log.debug(prefix.get());
            public class _complete_closure5
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference prefix;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;

                public _complete_closure5(Object _outerInstance, Object _thisObject, Reference prefix) {
                    super(_outerInstance, _thisObject);
                    Reference reference;
                    this.prefix = reference = prefix;
                }

                public Object doCall(String it) {
                    return it.startsWith(ShortTypeHandling.castToString(this.prefix.get()));
                }

                public Object call(String it) {
                    return this.doCall(it);
                }

                public String getPrefix() {
                    return ShortTypeHandling.castToString(this.prefix.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _complete_closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }
            }
            Set<String> matches = DefaultGroovyMethods.findAll(candidates, (Closure)new _complete_closure5(this, this, prefix));
            if (DefaultTypeTransformation.booleanUnbox(matches)) {
                public class _complete_closure6
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;

                    public _complete_closure6(Object _outerInstance, Object _thisObject) {
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(String it) {
                        return ImportCompleter.access$0(null, it);
                    }

                    public Object call(String it) {
                        return this.doCall(it);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _complete_closure6.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }
                }
                result.addAll(DefaultGroovyMethods.collect(matches, new _complete_closure6(this, this)));
                return lastDot <= 0 ? 0 : lastDot + 1;
            }
        } else if (this.staticImport) {
            Class clazz = (Class)ScriptBytecodeAdapter.asType(this.interpreter.evaluate(ScriptBytecodeAdapter.createList(new Object[]{baseString})), Class.class);
            if (clazz != null) {
                Collection<ReflectionCompletionCandidate> members = ReflectionCompletor.getPublicFieldsAndMethods(clazz, prefix.get());
                if (DefaultTypeTransformation.booleanUnbox(members)) {
                    public class _complete_closure7
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;

                        public _complete_closure7(Object _outerInstance, Object _thisObject) {
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(ReflectionCompletionCandidate it) {
                            return StringGroovyMethods.plus(it.getValue().replace("(", "").replace(")", ""), (CharSequence)" ");
                        }

                        public Object call(ReflectionCompletionCandidate it) {
                            return this.doCall(it);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _complete_closure7.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }
                    }
                    result.addAll(DefaultGroovyMethods.collect(members, new _complete_closure7(this, this)));
                    return lastDot <= 0 ? 0 : lastDot + 1;
                }
            }
        }
        return -1;
    }

    private static String addDotOrBlank(String it) {
        CallSite[] callSiteArray = ImportCompleter.$getCallSiteArray();
        if (ScriptBytecodeAdapter.isCase(callSiteArray[1].call((Object)it, 0), ScriptBytecodeAdapter.createRange("A", "Z", true))) {
            return ShortTypeHandling.castToString(callSiteArray[2].call((Object)it, " "));
        }
        return ShortTypeHandling.castToString(callSiteArray[3].call((Object)it, "."));
    }

    public static /* synthetic */ String access$0(ImportCompleter $that, String it) {
        CallSite[] callSiteArray = ImportCompleter.$getCallSiteArray();
        return ImportCompleter.addDotOrBlank(it);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ImportCompleter.class) {
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
        Object object = ScriptBytecodeAdapter.bitwiseNegate("^[a-z_]{1}[a-z0-9_]*(\\.[a-z0-9_]*)*\\.[A-Z][^.]*\\.$");
        QUALIFIED_CLASS_DOT_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
        Object object2 = ScriptBytecodeAdapter.bitwiseNegate("^([a-z_]{1}[a-z0-9_]*(\\.[a-z0-9_]*)*(\\.[A-Z][^.]*)?)?$");
        PACK_OR_CLASSNAME_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object2, Pattern.class);
        Object object3 = ScriptBytecodeAdapter.bitwiseNegate("^([a-z_]{1}[a-z0-9_]*(\\.[a-z0-9_]*)*(\\.[A-Z][^.$_]*)?)?$");
        PACK_OR_SIMPLE_CLASSNAME_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object3, Pattern.class);
        Object object4 = ScriptBytecodeAdapter.bitwiseNegate("^([a-z_]{1}[a-z0-9.]*(\\.[a-z0-9_]*)*(\\.[A-Z][^.$_]*(\\.[a-zA-Z0-9_]*)?)?)?$");
        PACK_OR_CLASS_OR_METHODNAME_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object4, Pattern.class);
        Object object5 = ScriptBytecodeAdapter.bitwiseNegate("^[a-z0-9.]+$");
        LOWERCASE_IMPORT_ITEM_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object5, Pattern.class);
    }

    public PackageHelper getPackageHelper() {
        return this.packageHelper;
    }

    public void setPackageHelper(PackageHelper packageHelper) {
        this.packageHelper = packageHelper;
    }

    public Groovysh getShell() {
        return this.shell;
    }

    public void setShell(Groovysh groovysh) {
        this.shell = groovysh;
    }

    public final boolean getStaticImport() {
        return this.staticImport;
    }

    public final boolean isStaticImport() {
        return this.staticImport;
    }

    public final Evaluator getInterpreter() {
        return this.interpreter;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "getAt";
        stringArray[2] = "plus";
        stringArray[3] = "plus";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        ImportCompleter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ImportCompleter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ImportCompleter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

