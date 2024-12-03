/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.text.markup.BaseTemplate;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class TagLibAdapter
implements GroovyObject {
    private final BaseTemplate template;
    private final List<Object> tagLibs;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TagLibAdapter(BaseTemplate tpl) {
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = TagLibAdapter.$getCallSiteArray();
        this.tagLibs = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        BaseTemplate baseTemplate = tpl;
        this.template = (BaseTemplate)ScriptBytecodeAdapter.castToType(baseTemplate, BaseTemplate.class);
    }

    public void registerTagLib(Class tagLibClass) {
        CallSite[] callSiteArray = TagLibAdapter.$getCallSiteArray();
        callSiteArray[0].call(this.tagLibs, callSiteArray[1].call(tagLibClass));
    }

    public void registerTagLib(Object tagLib) {
        CallSite[] callSiteArray = TagLibAdapter.$getCallSiteArray();
        callSiteArray[2].call(this.tagLibs, tagLib);
    }

    public Object methodMissing(String name, Object args) {
        CallSite[] callSiteArray = TagLibAdapter.$getCallSiteArray();
        Object tagLib = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(this.tagLibs), Iterator.class);
        while (iterator.hasNext()) {
            tagLib = iterator.next();
            Object p = ScriptBytecodeAdapter.getProperty(TagLibAdapter.class, tagLib, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            if (!(p instanceof Closure)) continue;
            Object clone = callSiteArray[4].call(p, this.template, this.template, this.template);
            return callSiteArray[5].call(clone, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }
        throw (Throwable)callSiteArray[6].callConstructor(MissingMethodException.class, name, TagLibAdapter.class, args);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TagLibAdapter.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "add";
        stringArray[1] = "newInstance";
        stringArray[2] = "add";
        stringArray[3] = "iterator";
        stringArray[4] = "rehydrate";
        stringArray[5] = "call";
        stringArray[6] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[7];
        TagLibAdapter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TagLibAdapter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TagLibAdapter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

