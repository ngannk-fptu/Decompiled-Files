/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.util.Logger;

public class BufferManager
implements GroovyObject {
    protected final Logger log;
    private final List<List<String>> buffers;
    private int selected;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BufferManager() {
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.buffers = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[2].callCurrent(this);
        } else {
            this.reset();
        }
    }

    public void reset() {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        callSiteArray[3].call(this.buffers);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[4].callCurrent((GroovyObject)this, true);
        } else {
            this.create(true);
        }
        callSiteArray[5].call((Object)this.log, "Buffers reset");
    }

    public List<String> current() {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[6];
            List<List<String>> list = this.buffers;
            valueRecorder.record(list, 9);
            valueRecorder.record(list, 9);
            Object object = callSite.call(list);
            valueRecorder.record(object, 17);
            boolean bl = !DefaultTypeTransformation.booleanUnbox(object);
            valueRecorder.record(bl, 8);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert !buffers.isEmpty()", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[7].call(this.buffers, this.selected), List.class);
    }

    /*
     * Unable to fully structure code
     */
    public void select(int index) {
        var2_2 = BufferManager.$getCallSiteArray();
        var3_3 = new ValueRecorder();
        try {
            v0 = index;
            var3_3.record(v0, 8);
            v1 = v0 >= 0;
            var3_3.record(v1, 14);
            if (!v1) ** GOTO lbl-1000
            v2 = index;
            var3_3.record(v2, 22);
            v3 = v2;
            v4 = var2_2[8];
            v5 = this.buffers;
            var3_3.record(v5, 30);
            var3_3.record(v5, 30);
            v6 = v4.call(v5);
            var3_3.record(v6, 38);
            v7 = ScriptBytecodeAdapter.compareLessThan(v3, v6);
            var3_3.record(v7, 28);
            if (v7) {
                v8 = true;
            } else lbl-1000:
            // 2 sources

            {
                v8 = false;
            }
            var3_3.record(v8, 19);
            if (v8) {
                var3_3.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert index >= 0 && index < buffers.size()", var3_3), null);
            }
        }
        catch (Throwable v9) {
            var3_3.clear();
            throw v9;
        }
        this.selected = var4_4 = index;
    }

    public int create(boolean select) {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        callSiteArray[9].call(this.buffers, ScriptBytecodeAdapter.createList(new Object[0]));
        Object i = callSiteArray[10].call(callSiteArray[11].call(this.buffers), 1);
        if (select) {
            callSiteArray[12].callCurrent((GroovyObject)this, i);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[13].callGetProperty(this.log))) {
            callSiteArray[14].call((Object)this.log, new GStringImpl(new Object[]{i}, new String[]{"Created new buffer with index: ", ""}));
        }
        return DefaultTypeTransformation.intUnbox(i);
    }

    /*
     * Unable to fully structure code
     */
    public void delete(int index) {
        var2_2 = BufferManager.$getCallSiteArray();
        var3_3 = new ValueRecorder();
        try {
            v0 = index;
            var3_3.record(v0, 8);
            v1 = v0 >= 0;
            var3_3.record(v1, 14);
            if (!v1) ** GOTO lbl-1000
            v2 = index;
            var3_3.record(v2, 22);
            v3 = v2;
            v4 = var2_2[15];
            v5 = this.buffers;
            var3_3.record(v5, 30);
            var3_3.record(v5, 30);
            v6 = v4.call(v5);
            var3_3.record(v6, 38);
            v7 = ScriptBytecodeAdapter.compareLessThan(v3, v6);
            var3_3.record(v7, 28);
            if (v7) {
                v8 = true;
            } else lbl-1000:
            // 2 sources

            {
                v8 = false;
            }
            var3_3.record(v8, 19);
            if (v8) {
                var3_3.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert index >= 0 && index < buffers.size()", var3_3), null);
            }
        }
        catch (Throwable v9) {
            var3_3.clear();
            throw v9;
        }
        var2_2[16].call(this.buffers, index);
        if (DefaultTypeTransformation.booleanUnbox(var2_2[17].callGetProperty(this.log))) {
            var2_2[18].call((Object)this.log, new GStringImpl(new Object[]{index}, new String[]{"Deleted buffer with index: ", ""}));
        }
    }

    public int size() {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        return DefaultTypeTransformation.intUnbox(callSiteArray[19].call(this.buffers));
    }

    public void deleteSelected() {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[20].callCurrent((GroovyObject)this, this.selected);
            Object i = callSiteArray[21].call((Object)this.selected, 1);
            if (ScriptBytecodeAdapter.compareLessThan(i, 0)) {
                callSiteArray[22].callCurrent((GroovyObject)this, 0);
            } else {
                callSiteArray[23].callCurrent((GroovyObject)this, i);
            }
        } else {
            this.delete(this.selected);
            Integer i = this.selected - 1;
            if (ScriptBytecodeAdapter.compareLessThan(i, 0)) {
                this.select(0);
            } else {
                callSiteArray[24].callCurrent((GroovyObject)this, i);
            }
        }
    }

    public void clearSelected() {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[25].call(callSiteArray[26].callCurrent(this));
        } else {
            callSiteArray[27].call(this.current());
        }
    }

    public void updateSelected(List buffer) {
        CallSite[] callSiteArray = BufferManager.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List list = buffer;
            valueRecorder.record(list, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(list, null);
            valueRecorder.record(bl, 15);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        List list = buffer;
        callSiteArray[28].call(this.buffers, this.selected, list);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BufferManager.class) {
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

    public final List<List<String>> getBuffers() {
        return this.buffers;
    }

    public int getSelected() {
        return this.selected;
    }

    public void setSelected(int n) {
        this.selected = n;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "reset";
        stringArray[3] = "clear";
        stringArray[4] = "create";
        stringArray[5] = "debug";
        stringArray[6] = "isEmpty";
        stringArray[7] = "getAt";
        stringArray[8] = "size";
        stringArray[9] = "leftShift";
        stringArray[10] = "minus";
        stringArray[11] = "size";
        stringArray[12] = "select";
        stringArray[13] = "debugEnabled";
        stringArray[14] = "debug";
        stringArray[15] = "size";
        stringArray[16] = "remove";
        stringArray[17] = "debugEnabled";
        stringArray[18] = "debug";
        stringArray[19] = "size";
        stringArray[20] = "delete";
        stringArray[21] = "minus";
        stringArray[22] = "select";
        stringArray[23] = "select";
        stringArray[24] = "select";
        stringArray[25] = "clear";
        stringArray[26] = "current";
        stringArray[27] = "clear";
        stringArray[28] = "putAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[29];
        BufferManager.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BufferManager.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BufferManager.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

