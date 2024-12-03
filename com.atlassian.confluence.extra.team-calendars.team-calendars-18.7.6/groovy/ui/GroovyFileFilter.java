/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class GroovyFileFilter
extends FileFilter
implements GroovyObject {
    private static final Object GROOVY_SOURCE_EXTENSIONS;
    private static final Object GROOVY_SOURCE_EXT_DESC;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GroovyFileFilter() {
        MetaClass metaClass;
        CallSite[] callSiteArray = GroovyFileFilter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean accept(File f) {
        Reference<File> f2 = new Reference<File>(f);
        CallSite[] callSiteArray = GroovyFileFilter.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(f2.get()))) {
            return true;
        }
        public class _accept_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference f;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _accept_closure1(Object _outerInstance, Object _thisObject, Reference f) {
                Reference reference;
                CallSite[] callSiteArray = _accept_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.f = reference = f;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _accept_closure1.$getCallSiteArray();
                return ScriptBytecodeAdapter.compareEqual(it, callSiteArray[0].callCurrent((GroovyObject)this, this.f.get()));
            }

            public File getF() {
                CallSite[] callSiteArray = _accept_closure1.$getCallSiteArray();
                return (File)ScriptBytecodeAdapter.castToType(this.f.get(), File.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _accept_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _accept_closure1.class) {
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
                stringArray[0] = "getExtension";
                return new CallSiteArray(_accept_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _accept_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(GROOVY_SOURCE_EXTENSIONS, new _accept_closure1(this, this, f2)));
    }

    @Override
    public String getDescription() {
        CallSite[] callSiteArray = GroovyFileFilter.$getCallSiteArray();
        return ShortTypeHandling.castToString(new GStringImpl(new Object[]{GROOVY_SOURCE_EXT_DESC}, new String[]{"Groovy Source Files (", ")"}));
    }

    public static String getExtension(Object f) {
        CallSite[] callSiteArray = GroovyFileFilter.$getCallSiteArray();
        Object ext = null;
        Object s = callSiteArray[2].call(f);
        Object i = callSiteArray[3].call(s, ".");
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareGreaterThan(i, 0) && ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[4].call(callSiteArray[5].call(s), 1))) {
                Object object;
                ext = object = callSiteArray[6].call(callSiteArray[7].call(s, i));
            }
        } else if (ScriptBytecodeAdapter.compareGreaterThan(i, 0) && ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[8].call(callSiteArray[9].call(s), 1))) {
            Object object;
            ext = object = callSiteArray[10].call(callSiteArray[11].call(s, i));
        }
        return ShortTypeHandling.castToString(new GStringImpl(new Object[]{ext}, new String[]{"*", ""}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GroovyFileFilter.class) {
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
        Object object;
        List list = ScriptBytecodeAdapter.createList(new Object[]{"*.groovy", "*.gvy", "*.gy", "*.gsh", "*.story", "*.gpp", "*.grunit"});
        GROOVY_SOURCE_EXTENSIONS = list;
        GROOVY_SOURCE_EXT_DESC = object = GroovyFileFilter.$getCallSiteArray()[12].call(GROOVY_SOURCE_EXTENSIONS, ",");
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "isDirectory";
        stringArray[1] = "find";
        stringArray[2] = "getName";
        stringArray[3] = "lastIndexOf";
        stringArray[4] = "minus";
        stringArray[5] = "length";
        stringArray[6] = "toLowerCase";
        stringArray[7] = "substring";
        stringArray[8] = "minus";
        stringArray[9] = "length";
        stringArray[10] = "toLowerCase";
        stringArray[11] = "substring";
        stringArray[12] = "join";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[13];
        GroovyFileFilter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GroovyFileFilter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GroovyFileFilter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

