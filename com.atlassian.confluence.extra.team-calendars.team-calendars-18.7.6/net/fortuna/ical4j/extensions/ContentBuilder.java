/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions;

import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import net.fortuna.ical4j.extensions.property.CalStartFactory;
import net.fortuna.ical4j.extensions.property.LicLocationFactory;
import net.fortuna.ical4j.extensions.property.WrAlarmIdFactory;
import net.fortuna.ical4j.extensions.property.WrCalDescFactory;
import net.fortuna.ical4j.extensions.property.WrCalNameFactory;
import net.fortuna.ical4j.extensions.property.WrTimezoneFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ContentBuilder
extends net.fortuna.ical4j.model.ContentBuilder {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ContentBuilder(boolean init) {
        CallSite[] callSiteArray = ContentBuilder.$getCallSiteArray();
        super(init);
    }

    public ContentBuilder() {
        CallSite[] callSiteArray = ContentBuilder.$getCallSiteArray();
        this(true);
    }

    public Object registerExtendedProperties() {
        CallSite[] callSiteArray = ContentBuilder.$getCallSiteArray();
        callSiteArray[0].callCurrent(this, "calstart", callSiteArray[1].callConstructor(CalStartFactory.class));
        callSiteArray[2].callCurrent(this, "liclocation", callSiteArray[3].callConstructor(LicLocationFactory.class));
        callSiteArray[4].callCurrent(this, "wralarmid", callSiteArray[5].callConstructor(WrAlarmIdFactory.class));
        callSiteArray[6].callCurrent(this, "wrcaldesc", callSiteArray[7].callConstructor(WrCalDescFactory.class));
        callSiteArray[8].callCurrent(this, "wrcalname", callSiteArray[9].callConstructor(WrCalNameFactory.class));
        return callSiteArray[10].callCurrent(this, "wrtimezone", callSiteArray[11].callConstructor(WrTimezoneFactory.class));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ContentBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$5$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "registerFactory";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "registerFactory";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "registerFactory";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "registerFactory";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "registerFactory";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "registerFactory";
        stringArray[11] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        ContentBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ContentBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ContentBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

