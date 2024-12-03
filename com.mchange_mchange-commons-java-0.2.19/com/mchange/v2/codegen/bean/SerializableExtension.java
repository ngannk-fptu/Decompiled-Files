/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SerializableExtension
implements GeneratorExtension {
    Set transientProperties;
    Map transientPropertyInitializers;

    public SerializableExtension(Set set, Map map) {
        this.transientProperties = set;
        this.transientPropertyInitializers = map;
    }

    public SerializableExtension() {
        this(Collections.EMPTY_SET, null);
    }

    @Override
    public Collection extraGeneralImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraSpecificImports() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("java.io.IOException");
        hashSet.add("java.io.Serializable");
        hashSet.add("java.io.ObjectOutputStream");
        hashSet.add("java.io.ObjectInputStream");
        return hashSet;
    }

    @Override
    public Collection extraInterfaceNames() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("Serializable");
        return hashSet;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        Object object;
        Property property;
        int n;
        indentedWriter.println("private static final long serialVersionUID = 1;");
        indentedWriter.println("private static final short VERSION = 0x0001;");
        indentedWriter.println();
        indentedWriter.println("private void writeObject( ObjectOutputStream oos ) throws IOException");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("oos.writeShort( VERSION );");
        int n2 = propertyArray.length;
        for (n = 0; n < n2; ++n) {
            property = propertyArray[n];
            if (this.transientProperties.contains(property.getName())) continue;
            object = classArray[n];
            if (object != null && ((Class)object).isPrimitive()) {
                if (object == Byte.TYPE) {
                    indentedWriter.println("oos.writeByte(" + property.getName() + ");");
                    continue;
                }
                if (object == Character.TYPE) {
                    indentedWriter.println("oos.writeChar(" + property.getName() + ");");
                    continue;
                }
                if (object == Short.TYPE) {
                    indentedWriter.println("oos.writeShort(" + property.getName() + ");");
                    continue;
                }
                if (object == Integer.TYPE) {
                    indentedWriter.println("oos.writeInt(" + property.getName() + ");");
                    continue;
                }
                if (object == Boolean.TYPE) {
                    indentedWriter.println("oos.writeBoolean(" + property.getName() + ");");
                    continue;
                }
                if (object == Long.TYPE) {
                    indentedWriter.println("oos.writeLong(" + property.getName() + ");");
                    continue;
                }
                if (object == Float.TYPE) {
                    indentedWriter.println("oos.writeFloat(" + property.getName() + ");");
                    continue;
                }
                if (object != Double.TYPE) continue;
                indentedWriter.println("oos.writeDouble(" + property.getName() + ");");
                continue;
            }
            this.writeStoreObject(property, (Class)object, indentedWriter);
        }
        this.generateExtraSerWriteStatements(classInfo, clazz, propertyArray, classArray, indentedWriter);
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        indentedWriter.println("private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("short version = ois.readShort();");
        indentedWriter.println("switch (version)");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("case VERSION:");
        indentedWriter.upIndent();
        n2 = propertyArray.length;
        for (n = 0; n < n2; ++n) {
            property = propertyArray[n];
            if (!this.transientProperties.contains(property.getName())) {
                object = classArray[n];
                if (object != null && ((Class)object).isPrimitive()) {
                    if (object == Byte.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readByte();");
                        continue;
                    }
                    if (object == Character.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readChar();");
                        continue;
                    }
                    if (object == Short.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readShort();");
                        continue;
                    }
                    if (object == Integer.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readInt();");
                        continue;
                    }
                    if (object == Boolean.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readBoolean();");
                        continue;
                    }
                    if (object == Long.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readLong();");
                        continue;
                    }
                    if (object == Float.TYPE) {
                        indentedWriter.println("this." + property.getName() + " = ois.readFloat();");
                        continue;
                    }
                    if (object != Double.TYPE) continue;
                    indentedWriter.println("this." + property.getName() + " = ois.readDouble();");
                    continue;
                }
                this.writeUnstoreObject(property, (Class)object, indentedWriter);
                continue;
            }
            object = (String)this.transientPropertyInitializers.get(property.getName());
            if (object == null) continue;
            indentedWriter.println("this." + property.getName() + " = " + (String)object + ';');
        }
        this.generateExtraSerInitializers(classInfo, clazz, propertyArray, classArray, indentedWriter);
        indentedWriter.println("break;");
        indentedWriter.downIndent();
        indentedWriter.println("default:");
        indentedWriter.upIndent();
        indentedWriter.println("throw new IOException(\"Unsupported Serialized Version: \" + version);");
        indentedWriter.downIndent();
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }

    protected void writeStoreObject(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("oos.writeObject( " + property.getName() + " );");
    }

    protected void writeUnstoreObject(Property property, Class clazz, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("this." + property.getName() + " = (" + property.getSimpleTypeName() + ") ois.readObject();");
    }

    protected void generateExtraSerWriteStatements(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
    }

    protected void generateExtraSerInitializers(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
    }
}

