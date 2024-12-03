/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;

public class ClassDataCollector {
    public void classBegin(int access, Descriptors.TypeRef name) {
    }

    public boolean classStart(int access, Descriptors.TypeRef className) {
        this.classBegin(access, className);
        return true;
    }

    public boolean classStart(Clazz c) {
        this.classBegin(c.accessx, c.className);
        return true;
    }

    public void extendsClass(Descriptors.TypeRef zuper) throws Exception {
    }

    public void implementsInterfaces(Descriptors.TypeRef[] interfaces) throws Exception {
    }

    public void addReference(Descriptors.TypeRef ref) {
    }

    public void annotation(Annotation annotation) throws Exception {
    }

    public void parameter(int p) {
    }

    public void method(Clazz.MethodDef defined) {
    }

    public void field(Clazz.FieldDef defined) {
    }

    public void classEnd() throws Exception {
    }

    public void deprecated() throws Exception {
    }

    public void enclosingMethod(Descriptors.TypeRef cName, String mName, String mDescriptor) {
    }

    public void innerClass(Descriptors.TypeRef innerClass, Descriptors.TypeRef outerClass, String innerName, int innerClassAccessFlags) throws Exception {
    }

    public void signature(String signature) {
    }

    public void constant(Object object) {
    }

    public void memberEnd() {
    }

    public void version(int minor, int major) {
    }

    public void referenceMethod(int access, Descriptors.TypeRef className, String method, String descriptor) {
    }

    public void referTo(Descriptors.TypeRef typeRef, int modifiers) {
    }

    public void annotationDefault(Clazz.MethodDef last) {
    }

    public void annotationDefault(Clazz.MethodDef last, Object value) {
        this.annotationDefault(last);
    }
}

