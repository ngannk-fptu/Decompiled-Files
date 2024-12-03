/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class WrappedInputStream
extends InputStream
implements Closeable,
GroovyObject {
    private final InputStream wrapped;
    private ByteArrayInputStream inserted;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public WrappedInputStream(InputStream wrapped) {
        MetaClass metaClass;
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(ByteArrayInputStream.class);
        this.inserted = (ByteArrayInputStream)ScriptBytecodeAdapter.castToType(object, ByteArrayInputStream.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        InputStream inputStream = wrapped;
        this.wrapped = (InputStream)ScriptBytecodeAdapter.castToType(inputStream, InputStream.class);
    }

    @Override
    public int read() throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual(this.inserted, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[1].call(this.inserted), 0)) {
                return DefaultTypeTransformation.intUnbox(callSiteArray[2].call(this.inserted));
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual(this.inserted, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[3].call(this.inserted), 0)) {
            return DefaultTypeTransformation.intUnbox(callSiteArray[4].call(this.inserted));
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[5].call(this.wrapped));
    }

    public void insert(String chars) {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        callSiteArray[6].call(this.inserted);
        Object object = callSiteArray[7].callConstructor(ByteArrayInputStream.class, callSiteArray[8].call((Object)chars, "UTF-8"));
        this.inserted = (ByteArrayInputStream)ScriptBytecodeAdapter.castToType(object, ByteArrayInputStream.class);
    }

    @Override
    public int read(byte ... b) throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        Object insertb = callSiteArray[9].call((Object)this.inserted, (Object)b);
        if (ScriptBytecodeAdapter.compareGreaterThan(insertb, 0)) {
            return DefaultTypeTransformation.intUnbox(insertb);
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[10].call((Object)this.wrapped, (Object)b));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        Object insertb = callSiteArray[11].call(this.inserted, b, off, len);
        if (ScriptBytecodeAdapter.compareGreaterThan(insertb, 0)) {
            return DefaultTypeTransformation.intUnbox(insertb);
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[12].call(this.wrapped, b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        Object skipb = callSiteArray[13].call((Object)this.inserted, n);
        if (ScriptBytecodeAdapter.compareGreaterThan(skipb, 0)) {
            return DefaultTypeTransformation.longUnbox(skipb);
        }
        return DefaultTypeTransformation.longUnbox(callSiteArray[14].call((Object)this.wrapped, n));
    }

    @Override
    public int available() throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        int x = DefaultTypeTransformation.intUnbox(callSiteArray[15].call(this.inserted));
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? x > 0 : x > 0) {
            return x;
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[16].call(this.wrapped));
    }

    @Override
    public void close() throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        callSiteArray[17].call(this.wrapped);
        callSiteArray[18].call(this.inserted);
    }

    @Override
    public synchronized void mark(int readlimit) {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        throw (Throwable)callSiteArray[19].callConstructor(UnsupportedOperationException.class);
    }

    @Override
    public synchronized void reset() throws IOException {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        throw (Throwable)callSiteArray[20].callConstructor(UnsupportedOperationException.class);
    }

    @Override
    public boolean markSupported() {
        CallSite[] callSiteArray = WrappedInputStream.$getCallSiteArray();
        return false;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != WrappedInputStream.class) {
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

    public final InputStream getWrapped() {
        return this.wrapped;
    }

    public ByteArrayInputStream getInserted() {
        return this.inserted;
    }

    public void setInserted(ByteArrayInputStream byteArrayInputStream) {
        this.inserted = byteArrayInputStream;
    }

    public /* synthetic */ void super$2$close() {
        super.close();
    }

    public /* synthetic */ int super$2$read(byte[] byArray, int n, int n2) {
        return super.read(byArray, n, n2);
    }

    public /* synthetic */ int super$2$read(byte[] byArray) {
        return super.read(byArray);
    }

    public /* synthetic */ int super$2$available() {
        return super.available();
    }

    public /* synthetic */ void super$2$mark(int n) {
        super.mark(n);
    }

    public /* synthetic */ boolean super$2$markSupported() {
        return super.markSupported();
    }

    public /* synthetic */ void super$2$reset() {
        super.reset();
    }

    public /* synthetic */ long super$2$skip(long l) {
        return super.skip(l);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "available";
        stringArray[2] = "read";
        stringArray[3] = "available";
        stringArray[4] = "read";
        stringArray[5] = "read";
        stringArray[6] = "close";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "getBytes";
        stringArray[9] = "read";
        stringArray[10] = "read";
        stringArray[11] = "read";
        stringArray[12] = "read";
        stringArray[13] = "skip";
        stringArray[14] = "skip";
        stringArray[15] = "available";
        stringArray[16] = "available";
        stringArray[17] = "close";
        stringArray[18] = "close";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[21];
        WrappedInputStream.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(WrappedInputStream.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = WrappedInputStream.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

