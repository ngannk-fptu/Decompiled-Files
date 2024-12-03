/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Assert
 */
package groovy.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import junit.framework.Assert;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;

public class StringTestUtil
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StringTestUtil() {
        MetaClass metaClass;
        CallSite[] callSiteArray = StringTestUtil.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void assertMultilineStringsEqual(String a, String b) {
        CallSite[] callSiteArray = StringTestUtil.$getCallSiteArray();
        Object aLines = callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].call(a), "\r", ""), "\n");
        Object bLines = callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call(b), "\r", ""), "\n");
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[6];
            Object object = aLines;
            valueRecorder.record(object, 8);
            Object object2 = callSite.call(object);
            valueRecorder.record(object2, 15);
            CallSite callSite2 = callSiteArray[7];
            Object object3 = bLines;
            valueRecorder.record(object3, 25);
            Object object4 = callSite2.call(object3);
            valueRecorder.record(object4, 32);
            boolean bl = ScriptBytecodeAdapter.compareEqual(object2, object4);
            valueRecorder.record(bl, 22);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert aLines.size() == bLines.size()", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Object i = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[8].call(ScriptBytecodeAdapter.createRange(0, callSiteArray[9].call(aLines), false)), Iterator.class);
        while (iterator.hasNext()) {
            i = iterator.next();
            callSiteArray[10].call(Assert.class, callSiteArray[11].call(callSiteArray[12].call(aLines, (Object)i)), callSiteArray[13].call(callSiteArray[14].call(bLines, (Object)i)));
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StringTestUtil.class) {
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
        stringArray[0] = "split";
        stringArray[1] = "replaceAll";
        stringArray[2] = "trim";
        stringArray[3] = "split";
        stringArray[4] = "replaceAll";
        stringArray[5] = "trim";
        stringArray[6] = "size";
        stringArray[7] = "size";
        stringArray[8] = "iterator";
        stringArray[9] = "size";
        stringArray[10] = "assertEquals";
        stringArray[11] = "trim";
        stringArray[12] = "getAt";
        stringArray[13] = "trim";
        stringArray[14] = "getAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        StringTestUtil.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StringTestUtil.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StringTestUtil.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

