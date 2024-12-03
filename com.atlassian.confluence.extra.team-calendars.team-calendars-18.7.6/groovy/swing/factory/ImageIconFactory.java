/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Image;
import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;
import javax.swing.ImageIcon;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ImageIconFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ImageIconFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ImageIconFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = ImageIconFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call((Object)attributes, "image"))) {
                Object object;
                value = object = callSiteArray[1].call((Object)attributes, "image");
                if (!(value instanceof Image)) {
                    throw (Throwable)callSiteArray[2].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " image: attributes must be of type java.awt.Image"}));
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Object)attributes, "url"))) {
                Object object;
                value = object = callSiteArray[4].call((Object)attributes, "url");
                if (!(value instanceof URL)) {
                    throw (Throwable)callSiteArray[5].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " url: attributes must be of type java.net.URL"}));
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call((Object)attributes, "file"))) {
                Object object;
                value = object = callSiteArray[7].call((Object)attributes, "file");
                if (value instanceof GString) {
                    String string = (String)ScriptBytecodeAdapter.asType(value, String.class);
                    value = string;
                }
                if (value instanceof File) {
                    Object object2;
                    value = object2 = callSiteArray[8].call(value);
                } else if (!(value instanceof String)) {
                    throw (Throwable)callSiteArray[9].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " file: attributes must be of type java.io.File or a string"}));
                }
            }
        } else if (value instanceof GString) {
            String string = (String)ScriptBytecodeAdapter.asType(value, String.class);
            value = string;
        }
        Object resource = null;
        if (ScriptBytecodeAdapter.compareEqual(value, null) && DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call((Object)attributes, "resource"))) {
            Object object;
            resource = object = callSiteArray[11].call((Object)attributes, "resource");
        } else if (value instanceof String && !DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(callSiteArray[13].callConstructor(File.class, value)))) {
            Object object;
            resource = object = value;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(resource, null)) {
            Object object;
            Object klass = callSiteArray[14].callGetProperty(callSiteArray[15].callGroovyObjectGetProperty(builder));
            Object origValue = value;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[16].call((Object)attributes, "class"))) {
                Object object3;
                klass = object3 = callSiteArray[17].call((Object)attributes, "class");
            }
            if (ScriptBytecodeAdapter.compareEqual(klass, null)) {
                Class<ImageIconFactory> clazz = ImageIconFactory.class;
                klass = clazz;
            } else if (!(klass instanceof Class)) {
                Object object4;
                klass = object4 = callSiteArray[18].callGetProperty(klass);
            }
            value = object = callSiteArray[19].call(klass, resource);
            if (ScriptBytecodeAdapter.compareEqual(value, null)) {
                throw (Throwable)callSiteArray[20].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, origValue}, new String[]{"In ", " the value argument '", "' does not refer to a file or a class resource"}));
            }
        }
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            throw (Throwable)callSiteArray[21].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " has neither a value argument or one of image:, url:, file:, or resource:"}));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call((Object)attributes, "description"))) {
            return callSiteArray[23].callConstructor(ImageIcon.class, value, callSiteArray[24].call((Object)attributes, "description"));
        }
        return callSiteArray[25].callConstructor(ImageIcon.class, value);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ImageIconFactory.class) {
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
        stringArray[0] = "containsKey";
        stringArray[1] = "remove";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "containsKey";
        stringArray[4] = "remove";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "containsKey";
        stringArray[7] = "remove";
        stringArray[8] = "toURL";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "containsKey";
        stringArray[11] = "remove";
        stringArray[12] = "exists";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "owner";
        stringArray[15] = "context";
        stringArray[16] = "containsKey";
        stringArray[17] = "remove";
        stringArray[18] = "class";
        stringArray[19] = "getResource";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "containsKey";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "remove";
        stringArray[25] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[26];
        ImageIconFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ImageIconFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ImageIconFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

