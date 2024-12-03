/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.service.reporter.Reporter;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

class ClassDataCollectors
implements Closeable {
    final List<ClassDataCollector> delegates = new ArrayList<ClassDataCollector>();
    final List<ClassDataCollector> shortlist = new ArrayList<ClassDataCollector>();
    final Reporter reporter;

    ClassDataCollectors(Reporter reporter) {
        this.reporter = reporter;
    }

    void add(ClassDataCollector cd) {
        this.delegates.add(cd);
    }

    void parse(Clazz clazz) throws Exception {
        clazz.parseClassFileWithCollector(new Collectors(clazz));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void with(Clazz clazz, ClassDataCollector cd) throws Exception {
        this.delegates.add(cd);
        try {
            this.parse(clazz);
        }
        finally {
            this.delegates.remove(cd);
        }
    }

    @Override
    public void close() {
        for (ClassDataCollector cd : this.delegates) {
            try {
                if (!(cd instanceof Closeable)) continue;
                ((Closeable)((Object)cd)).close();
            }
            catch (Exception e) {
                this.reporter.exception(e, "Failure on call close[%s]", cd);
            }
        }
        this.delegates.clear();
        this.shortlist.clear();
    }

    private class Collectors
    extends ClassDataCollector {
        private final Clazz clazz;

        Collectors(Clazz clazz) {
            this.clazz = clazz;
        }

        @Override
        public void classBegin(int access, Descriptors.TypeRef name) {
            for (ClassDataCollector cd : ClassDataCollectors.this.delegates) {
                try {
                    cd.classBegin(access, name);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call classBegin[%s] for %s", this.clazz, cd);
                }
            }
        }

        @Override
        public boolean classStart(int access, Descriptors.TypeRef className) {
            boolean start = false;
            for (ClassDataCollector cd : ClassDataCollectors.this.delegates) {
                try {
                    if (!cd.classStart(access, className)) continue;
                    ClassDataCollectors.this.shortlist.add(cd);
                    start = true;
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call classStart[%s]", this.clazz, cd);
                }
            }
            return start;
        }

        @Override
        public boolean classStart(Clazz clazz) {
            boolean start = false;
            for (ClassDataCollector cd : ClassDataCollectors.this.delegates) {
                try {
                    if (!cd.classStart(clazz)) continue;
                    ClassDataCollectors.this.shortlist.add(cd);
                    start = true;
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call classStart[%s]", clazz, cd);
                }
            }
            return start;
        }

        @Override
        public void extendsClass(Descriptors.TypeRef zuper) throws Exception {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.extendsClass(zuper);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call extendsClass[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void implementsInterfaces(Descriptors.TypeRef[] interfaces) throws Exception {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.implementsInterfaces(interfaces);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call implementsInterfaces[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void addReference(Descriptors.TypeRef ref) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.addReference(ref);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call addReference[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void annotation(Annotation annotation) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.annotation(annotation);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call annotation[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void parameter(int p) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.parameter(p);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call parameter[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void method(Clazz.MethodDef defined) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.method(defined);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call method[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void field(Clazz.FieldDef defined) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.field(defined);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call field[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void classEnd() throws Exception {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.classEnd();
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call classEnd[%s]", this.clazz, cd);
                }
            }
            ClassDataCollectors.this.shortlist.clear();
        }

        @Override
        public void deprecated() throws Exception {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.deprecated();
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call deprecated[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void enclosingMethod(Descriptors.TypeRef cName, String mName, String mDescriptor) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.enclosingMethod(cName, mName, mDescriptor);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call enclosingMethod[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void innerClass(Descriptors.TypeRef innerClass, Descriptors.TypeRef outerClass, String innerName, int innerClassAccessFlags) throws Exception {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.innerClass(innerClass, outerClass, innerName, innerClassAccessFlags);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call innerClass[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void signature(String signature) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.signature(signature);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call signature[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void constant(Object object) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.constant(object);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call constant[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void memberEnd() {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.memberEnd();
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call memberEnd[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void version(int minor, int major) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.version(minor, major);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call version[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void referenceMethod(int access, Descriptors.TypeRef className, String method, String descriptor) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.referenceMethod(access, className, method, descriptor);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call referenceMethod[%s]", this.clazz, cd);
                }
            }
        }

        @Override
        public void referTo(Descriptors.TypeRef typeRef, int modifiers) {
            for (ClassDataCollector cd : ClassDataCollectors.this.shortlist) {
                try {
                    cd.referTo(typeRef, modifiers);
                }
                catch (Exception e) {
                    ClassDataCollectors.this.reporter.exception(e, "Failure for %s on call referTo[%s]", this.clazz, cd);
                }
            }
        }
    }
}

