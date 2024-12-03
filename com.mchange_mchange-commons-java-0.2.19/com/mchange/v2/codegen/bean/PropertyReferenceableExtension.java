/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.naming.JavaBeanObjectFactory;
import com.mchange.v2.naming.JavaBeanReferenceMaker;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class PropertyReferenceableExtension
implements GeneratorExtension {
    boolean explicit_reference_properties = false;
    String factoryClassName = JavaBeanObjectFactory.class.getName();
    String javaBeanReferenceMakerClassName = JavaBeanReferenceMaker.class.getName();

    public void setUseExplicitReferenceProperties(boolean bl) {
        this.explicit_reference_properties = bl;
    }

    public boolean getUseExplicitReferenceProperties() {
        return this.explicit_reference_properties;
    }

    public void setFactoryClassName(String string) {
        this.factoryClassName = string;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    @Override
    public Collection extraGeneralImports() {
        HashSet hashSet = new HashSet();
        return hashSet;
    }

    @Override
    public Collection extraSpecificImports() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("javax.naming.Reference");
        hashSet.add("javax.naming.Referenceable");
        hashSet.add("javax.naming.NamingException");
        hashSet.add("com.mchange.v2.naming.JavaBeanObjectFactory");
        hashSet.add("com.mchange.v2.naming.JavaBeanReferenceMaker");
        hashSet.add("com.mchange.v2.naming.ReferenceMaker");
        return hashSet;
    }

    @Override
    public Collection extraInterfaceNames() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("Referenceable");
        return hashSet;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("final static JavaBeanReferenceMaker referenceMaker = new " + this.javaBeanReferenceMakerClassName + "();");
        indentedWriter.println();
        indentedWriter.println("static");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("referenceMaker.setFactoryClassName( \"" + this.factoryClassName + "\" );");
        if (this.explicit_reference_properties) {
            int n = propertyArray.length;
            for (int i = 0; i < n; ++i) {
                indentedWriter.println("referenceMaker.addReferenceProperty(\"" + propertyArray[i].getName() + "\");");
            }
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.println("public Reference getReference() throws NamingException");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("return referenceMaker.createReference( this );");
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }
}

