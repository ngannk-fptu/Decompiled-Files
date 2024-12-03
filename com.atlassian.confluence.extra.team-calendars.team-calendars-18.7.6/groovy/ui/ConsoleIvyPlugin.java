/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ivy.core.event.IvyListener
 *  org.apache.ivy.core.event.download.PrepareDownloadEvent
 *  org.apache.ivy.core.event.resolve.StartResolveEvent
 */
package groovy.ui;

import groovy.grape.Grape;
import groovy.grape.GrapeIvy;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.ui.Console;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Set;
import org.apache.ivy.core.event.IvyListener;
import org.apache.ivy.core.event.download.PrepareDownloadEvent;
import org.apache.ivy.core.event.resolve.StartResolveEvent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ConsoleIvyPlugin
implements GroovyObject {
    private Console savedConsole;
    private Set<String> resolvedDependencies;
    private Set<String> downloadedArtifacts;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConsoleIvyPlugin() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ConsoleIvyPlugin.$getCallSiteArray();
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.resolvedDependencies = (Set)ScriptBytecodeAdapter.castToType(list, Set.class);
        List list2 = ScriptBytecodeAdapter.createList(new Object[0]);
        this.downloadedArtifacts = (Set)ScriptBytecodeAdapter.castToType(list2, Set.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public Object addListener(Console console) {
        Console console2;
        CallSite[] callSiteArray = ConsoleIvyPlugin.$getCallSiteArray();
        this.savedConsole = console2 = console;
        public class _addListener_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _addListener_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _addListener_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object ivyEvent) {
                CallSite[] callSiteArray = _addListener_closure1.$getCallSiteArray();
                Object object = ivyEvent;
                if (ScriptBytecodeAdapter.isCase(object, StartResolveEvent.class)) {
                    public class _closure2
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure2(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                            Object name = callSiteArray[0].call(it);
                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), name))) {
                                callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), name);
                                return callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{name}, new String[]{"Resolving ", " ..."}));
                            }
                            return null;
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure2.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "toString";
                            stringArray[1] = "contains";
                            stringArray[2] = "resolvedDependencies";
                            stringArray[3] = "leftShift";
                            stringArray[4] = "resolvedDependencies";
                            stringArray[5] = "showMessage";
                            stringArray[6] = "savedConsole";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[7];
                            _closure2.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure2.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure2.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(ivyEvent)), new _closure2(this, this.getThisObject()));
                }
                if (ScriptBytecodeAdapter.isCase(object, PrepareDownloadEvent.class)) {
                    public class _closure3
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure3(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                            Object name = callSiteArray[0].call(it);
                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), name))) {
                                callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), name);
                                return callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{name}, new String[]{"Downloading artifact ", " ..."}));
                            }
                            return null;
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure3.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "toString";
                            stringArray[1] = "contains";
                            stringArray[2] = "downloadedArtifacts";
                            stringArray[3] = "leftShift";
                            stringArray[4] = "downloadedArtifacts";
                            stringArray[5] = "showMessage";
                            stringArray[6] = "savedConsole";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[7];
                            _closure3.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure3.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure3.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[3].call(callSiteArray[4].callGetProperty(ivyEvent), new _closure3(this, this.getThisObject()));
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addListener_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "each";
                stringArray[1] = "dependencies";
                stringArray[2] = "moduleDescriptor";
                stringArray[3] = "each";
                stringArray[4] = "artifacts";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _addListener_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_addListener_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _addListener_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty((GrapeIvy)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGetProperty(Grape.class), GrapeIvy.class))), ScriptBytecodeAdapter.createPojoWrapper((IvyListener)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createMap(new Object[]{"progress", new _addListener_closure1(this, this)}), IvyListener.class), IvyListener.class));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConsoleIvyPlugin.class) {
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

    public Console getSavedConsole() {
        return this.savedConsole;
    }

    public void setSavedConsole(Console console) {
        this.savedConsole = console;
    }

    public Set<String> getResolvedDependencies() {
        return this.resolvedDependencies;
    }

    public void setResolvedDependencies(Set<String> set) {
        this.resolvedDependencies = set;
    }

    public Set<String> getDownloadedArtifacts() {
        return this.downloadedArtifacts;
    }

    public void setDownloadedArtifacts(Set<String> set) {
        this.downloadedArtifacts = set;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "addIvyListener";
        stringArray[1] = "eventManager";
        stringArray[2] = "ivyInstance";
        stringArray[3] = "instance";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        ConsoleIvyPlugin.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConsoleIvyPlugin.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConsoleIvyPlugin.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

