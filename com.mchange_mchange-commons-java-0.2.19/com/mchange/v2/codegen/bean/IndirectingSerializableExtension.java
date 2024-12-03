/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.codegen.bean.SerializableExtension;
import com.mchange.v2.ser.IndirectPolicy;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public class IndirectingSerializableExtension
extends SerializableExtension {
    protected String findIndirectorExpr;
    protected String indirectorClassName;

    public IndirectingSerializableExtension(String string) {
        this.indirectorClassName = string;
        this.findIndirectorExpr = "new " + string + "()";
    }

    protected IndirectingSerializableExtension() {
    }

    @Override
    public Collection extraSpecificImports() {
        Collection collection = super.extraSpecificImports();
        collection.add(this.indirectorClassName);
        collection.add("com.mchange.v2.ser.IndirectlySerialized");
        collection.add("com.mchange.v2.ser.Indirector");
        collection.add("com.mchange.v2.ser.SerializableUtils");
        collection.add("java.io.NotSerializableException");
        return collection;
    }

    protected IndirectPolicy indirectingPolicy(Property property, Class clazz) {
        if (Serializable.class.isAssignableFrom(clazz)) {
            return IndirectPolicy.DEFINITELY_DIRECT;
        }
        return IndirectPolicy.INDIRECT_ON_EXCEPTION;
    }

    protected void writeInitializeIndirector(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
    }

    protected void writeExtraDeclarations(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        super.generate(classInfo, clazz, propertyArray, classArray, indentedWriter);
        this.writeExtraDeclarations(classInfo, clazz, propertyArray, classArray, indentedWriter);
    }

    @Override
    protected void writeStoreObject(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
        IndirectPolicy indirectPolicy = this.indirectingPolicy(property, clazz);
        if (indirectPolicy == IndirectPolicy.DEFINITELY_INDIRECT) {
            this.writeIndirectStoreObject(property, clazz, indentedWriter);
        } else if (indirectPolicy == IndirectPolicy.INDIRECT_ON_EXCEPTION) {
            indentedWriter.println("try");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            indentedWriter.println("//test serialize");
            indentedWriter.println("SerializableUtils.toByteArray(" + property.getName() + ");");
            super.writeStoreObject(property, clazz, indentedWriter);
            indentedWriter.downIndent();
            indentedWriter.println("}");
            indentedWriter.println("catch (NotSerializableException nse)");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            this.writeIndirectStoreObject(property, clazz, indentedWriter);
            indentedWriter.downIndent();
            indentedWriter.println("}");
        } else if (indirectPolicy == IndirectPolicy.DEFINITELY_DIRECT) {
            super.writeStoreObject(property, clazz, indentedWriter);
        } else {
            throw new InternalError("indirectingPolicy() overridden to return unknown policy: " + indirectPolicy);
        }
    }

    protected void writeIndirectStoreObject(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("try");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("Indirector indirector = " + this.findIndirectorExpr + ';');
        this.writeInitializeIndirector(property, clazz, indentedWriter);
        indentedWriter.println("oos.writeObject( indirector.indirectForm( " + property.getName() + " ) );");
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println("catch (IOException indirectionIOException)");
        indentedWriter.println("{ throw indirectionIOException; }");
        indentedWriter.println("catch (Exception indirectionOtherException)");
        indentedWriter.println("{ throw new IOException(\"Problem indirectly serializing " + property.getName() + ": \" + indirectionOtherException.toString() ); }");
    }

    @Override
    protected void writeUnstoreObject(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
        IndirectPolicy indirectPolicy = this.indirectingPolicy(property, clazz);
        if (indirectPolicy == IndirectPolicy.DEFINITELY_INDIRECT || indirectPolicy == IndirectPolicy.INDIRECT_ON_EXCEPTION) {
            indentedWriter.println("// we create an artificial scope so that we can use the name o for all indirectly serialized objects.");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            indentedWriter.println("Object o = ois.readObject();");
            indentedWriter.println("if (o instanceof IndirectlySerialized) o = ((IndirectlySerialized) o).getObject();");
            indentedWriter.println("this." + property.getName() + " = (" + property.getSimpleTypeName() + ") o;");
            indentedWriter.downIndent();
            indentedWriter.println("}");
        } else if (indirectPolicy == IndirectPolicy.DEFINITELY_DIRECT) {
            super.writeUnstoreObject(property, clazz, indentedWriter);
        } else {
            throw new InternalError("indirectingPolicy() overridden to return unknown policy: " + indirectPolicy);
        }
    }
}

