/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Comparator;

public final class BeangenUtils {
    public static final Comparator PROPERTY_COMPARATOR = new Comparator(){

        public int compare(Object object, Object object2) {
            Property property = (Property)object;
            Property property2 = (Property)object2;
            return String.CASE_INSENSITIVE_ORDER.compare(property.getName(), property2.getName());
        }
    };

    public static String capitalize(String string) {
        char c = string.charAt(0);
        return Character.toUpperCase(c) + string.substring(1);
    }

    public static void writeExplicitDefaultConstructor(int n, ClassInfo classInfo, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.print(CodegenUtils.getModifierString(n));
        indentedWriter.println(' ' + classInfo.getClassName() + "()");
        indentedWriter.println("{}");
    }

    public static void writeArgList(Property[] propertyArray, boolean bl, IndentedWriter indentedWriter) throws IOException {
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                indentedWriter.print(", ");
            }
            if (bl) {
                indentedWriter.print(propertyArray[i].getSimpleTypeName() + ' ');
            }
            indentedWriter.print(propertyArray[i].getName());
        }
    }

    public static void writePropertyMember(Property property, IndentedWriter indentedWriter) throws IOException {
        BeangenUtils.writePropertyVariable(property, indentedWriter);
    }

    public static void writePropertyVariable(Property property, IndentedWriter indentedWriter) throws IOException {
        BeangenUtils.writePropertyVariable(property, property.getDefaultValueExpression(), indentedWriter);
    }

    public static void writePropertyMember(Property property, String string, IndentedWriter indentedWriter) throws IOException {
        BeangenUtils.writePropertyVariable(property, string, indentedWriter);
    }

    public static void writePropertyVariable(Property property, String string, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.print(CodegenUtils.getModifierString(property.getVariableModifiers()));
        indentedWriter.print(' ' + property.getSimpleTypeName() + ' ' + property.getName());
        String string2 = string;
        if (string2 != null) {
            indentedWriter.print(" = " + string2);
        }
        indentedWriter.println(';');
    }

    public static void writePropertyGetter(Property property, IndentedWriter indentedWriter) throws IOException {
        BeangenUtils.writePropertyGetter(property, property.getDefensiveCopyExpression(), indentedWriter);
    }

    public static void writePropertyGetter(Property property, String string, IndentedWriter indentedWriter) throws IOException {
        String string2 = "boolean".equals(property.getSimpleTypeName()) ? "is" : "get";
        indentedWriter.print(CodegenUtils.getModifierString(property.getGetterModifiers()));
        indentedWriter.println(' ' + property.getSimpleTypeName() + ' ' + string2 + BeangenUtils.capitalize(property.getName()) + "()");
        String string3 = string;
        if (string3 == null) {
            string3 = property.getName();
        }
        indentedWriter.println("{ return " + string3 + "; }");
    }

    public static void writePropertySetter(Property property, IndentedWriter indentedWriter) throws IOException {
        BeangenUtils.writePropertySetter(property, property.getDefensiveCopyExpression(), indentedWriter);
    }

    public static void writePropertySetter(Property property, String string, IndentedWriter indentedWriter) throws IOException {
        String string2 = string;
        if (string2 == null) {
            string2 = property.getName();
        }
        String string3 = "this." + property.getName();
        String string4 = "this." + property.getName() + " = " + string2 + ';';
        BeangenUtils.writePropertySetterWithGetExpressionSetStatement(property, string3, string4, indentedWriter);
    }

    public static void writePropertySetterWithGetExpressionSetStatement(Property property, String string, String string2, IndentedWriter indentedWriter) throws IOException {
        indentedWriter.print(CodegenUtils.getModifierString(property.getSetterModifiers()));
        indentedWriter.print(" void set" + BeangenUtils.capitalize(property.getName()) + "( " + property.getSimpleTypeName() + ' ' + property.getName() + " )");
        if (property.isConstrained()) {
            indentedWriter.println(" throws PropertyVetoException");
        } else {
            indentedWriter.println();
        }
        indentedWriter.println('{');
        indentedWriter.upIndent();
        if (BeangenUtils.changeMarked(property)) {
            String string3;
            indentedWriter.println(property.getSimpleTypeName() + " oldVal = " + string + ';');
            String string4 = "oldVal";
            String string5 = property.getName();
            String string6 = property.getSimpleTypeName();
            if (ClassUtils.isPrimitive(string6)) {
                Class clazz = ClassUtils.classForPrimitive(string6);
                if (clazz == Byte.TYPE) {
                    string4 = "new Byte( " + string4 + " )";
                    string5 = "new Byte( " + string5 + " )";
                } else if (clazz == Character.TYPE) {
                    string4 = "new Character( " + string4 + " )";
                    string5 = "new Character( " + string5 + " )";
                } else if (clazz == Short.TYPE) {
                    string4 = "new Short( " + string4 + " )";
                    string5 = "new Short( " + string5 + " )";
                } else if (clazz == Float.TYPE) {
                    string4 = "new Float( " + string4 + " )";
                    string5 = "new Float( " + string5 + " )";
                } else if (clazz == Double.TYPE) {
                    string4 = "new Double( " + string4 + " )";
                    string5 = "new Double( " + string5 + " )";
                }
                string3 = "oldVal != " + property.getName();
            } else {
                string3 = "! eqOrBothNull( oldVal, " + property.getName() + " )";
            }
            if (property.isConstrained()) {
                indentedWriter.println("if ( " + string3 + " )");
                indentedWriter.upIndent();
                indentedWriter.println("vcs.fireVetoableChange( \"" + property.getName() + "\", " + string4 + ", " + string5 + " );");
                indentedWriter.downIndent();
            }
            indentedWriter.println(string2);
            if (property.isBound()) {
                indentedWriter.println("if ( " + string3 + " )");
                indentedWriter.upIndent();
                indentedWriter.println("pcs.firePropertyChange( \"" + property.getName() + "\", " + string4 + ", " + string5 + " );");
                indentedWriter.downIndent();
            }
        } else {
            indentedWriter.println(string2);
        }
        indentedWriter.downIndent();
        indentedWriter.println('}');
    }

    public static boolean hasBoundProperties(Property[] propertyArray) {
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            if (!propertyArray[i].isBound()) continue;
            return true;
        }
        return false;
    }

    public static boolean hasConstrainedProperties(Property[] propertyArray) {
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            if (!propertyArray[i].isConstrained()) continue;
            return true;
        }
        return false;
    }

    private static boolean changeMarked(Property property) {
        return property.isBound() || property.isConstrained();
    }

    private BeangenUtils() {
    }
}

