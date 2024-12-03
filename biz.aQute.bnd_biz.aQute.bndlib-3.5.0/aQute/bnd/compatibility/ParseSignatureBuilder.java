/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import aQute.bnd.compatibility.Access;
import aQute.bnd.compatibility.GenericType;
import aQute.bnd.compatibility.Kind;
import aQute.bnd.compatibility.Scope;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import java.io.InputStream;

public class ParseSignatureBuilder {
    final Scope root;

    public ParseSignatureBuilder(Scope root) {
        this.root = root;
    }

    public void add(Jar jar) throws Exception {
        for (Resource r : jar.getResources().values()) {
            InputStream in = r.openInputStream();
            Throwable throwable = null;
            try {
                this.parse(in);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (in == null) continue;
                if (throwable != null) {
                    try {
                        in.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                    continue;
                }
                in.close();
            }
        }
    }

    public Scope getRoot() {
        return this.root;
    }

    public void parse(InputStream in) throws Exception {
        try (Analyzer analyzer = new Analyzer();){
            Clazz clazz = new Clazz(analyzer, "", null);
            clazz.parseClassFile(in, new ClassDataCollector(){
                Scope s;
                Scope enclosing;
                Scope declaring;

                @Override
                public void classBegin(int access, Descriptors.TypeRef name) {
                    this.s = ParseSignatureBuilder.this.root.getScope(name.getBinary());
                    this.s.access = Access.modifier(access);
                    this.s.kind = Kind.CLASS;
                }

                @Override
                public void extendsClass(Descriptors.TypeRef name) {
                }

                @Override
                public void implementsInterfaces(Descriptors.TypeRef[] names) {
                    this.s.setParameterTypes(this.convert(names));
                }

                GenericType[] convert(Descriptors.TypeRef[] names) {
                    GenericType[] tss = new GenericType[names.length];
                    for (int i = 0; i < names.length; ++i) {
                    }
                    return tss;
                }

                @Override
                public void method(Clazz.MethodDef defined) {
                    Kind kind;
                    String descriptor;
                    if (defined.isConstructor()) {
                        descriptor = ":" + defined.getDescriptor();
                        kind = Kind.CONSTRUCTOR;
                    } else {
                        descriptor = defined.getName() + ":" + defined.getDescriptor();
                        kind = Kind.METHOD;
                    }
                    Scope m = this.s.getScope(descriptor);
                    m.access = Access.modifier(defined.getAccess());
                    m.kind = kind;
                    m.declaring = this.s;
                    this.s.add(m);
                }

                @Override
                public void field(Clazz.FieldDef defined) {
                    String descriptor = defined.getName() + ":" + defined.getDescriptor();
                    Kind kind = Kind.FIELD;
                    Scope m = this.s.getScope(descriptor);
                    m.access = Access.modifier(defined.getAccess());
                    m.kind = kind;
                    m.declaring = this.s;
                    this.s.add(m);
                }

                @Override
                public void classEnd() {
                    if (this.enclosing != null) {
                        this.s.setEnclosing(this.enclosing);
                    }
                    if (this.declaring != null) {
                        this.s.setDeclaring(this.declaring);
                    }
                }

                @Override
                public void enclosingMethod(Descriptors.TypeRef cName, String mName, String mDescriptor) {
                    this.enclosing = ParseSignatureBuilder.this.root.getScope(cName.getBinary());
                    if (mName != null) {
                        this.enclosing = this.enclosing.getScope(Scope.methodIdentity(mName, mDescriptor));
                    }
                }

                @Override
                public void innerClass(Descriptors.TypeRef innerClass, Descriptors.TypeRef outerClass, String innerName, int innerClassAccessFlags) {
                    if (outerClass != null && innerClass != null && innerClass.getBinary().equals(this.s.name)) {
                        this.declaring = ParseSignatureBuilder.this.root.getScope(outerClass.getBinary());
                    }
                }
            });
        }
    }
}

