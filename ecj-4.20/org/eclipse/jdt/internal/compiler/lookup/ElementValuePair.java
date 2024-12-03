/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class ElementValuePair {
    char[] name;
    public Object value;
    public MethodBinding binding;

    public static Object getValue(Expression expression) {
        if (expression == null) {
            return null;
        }
        Constant constant = expression.constant;
        if (constant != null && constant != Constant.NotAConstant) {
            return constant;
        }
        if (expression instanceof Annotation) {
            return ((Annotation)expression).getCompilerAnnotation();
        }
        if (expression instanceof ArrayInitializer) {
            Expression[] exprs = ((ArrayInitializer)expression).expressions;
            int length = exprs == null ? 0 : exprs.length;
            Object[] values = new Object[length];
            int i = 0;
            while (i < length) {
                values[i] = ElementValuePair.getValue(exprs[i]);
                ++i;
            }
            return values;
        }
        if (expression instanceof ClassLiteralAccess) {
            return ((ClassLiteralAccess)expression).targetType;
        }
        if (expression instanceof Reference) {
            Binding binding;
            FieldBinding fieldBinding = null;
            if (expression instanceof FieldReference) {
                fieldBinding = ((FieldReference)expression).fieldBinding();
            } else if (expression instanceof NameReference && (binding = ((NameReference)expression).binding) != null && binding.kind() == 1) {
                fieldBinding = (FieldBinding)binding;
            }
            if (fieldBinding != null && (fieldBinding.modifiers & 0x4000) > 0) {
                return fieldBinding;
            }
        }
        return null;
    }

    public ElementValuePair(char[] name, Expression expression, MethodBinding binding) {
        this(name, ElementValuePair.getValue(expression), binding);
    }

    public ElementValuePair(char[] name, Object value, MethodBinding binding) {
        this.name = name;
        this.value = value;
        this.binding = binding;
    }

    public char[] getName() {
        return this.name;
    }

    public MethodBinding getMethodBinding() {
        return this.binding;
    }

    public Object getValue() {
        if (this.value instanceof UnresolvedEnumConstant) {
            this.value = ((UnresolvedEnumConstant)this.value).getResolved();
        } else if (this.value instanceof Object[]) {
            Object[] valueArray = (Object[])this.value;
            int i = 0;
            while (i < valueArray.length) {
                Object object = valueArray[i];
                if (object instanceof UnresolvedEnumConstant) {
                    valueArray[i] = ((UnresolvedEnumConstant)object).getResolved();
                }
                ++i;
            }
        }
        return this.value;
    }

    void setMethodBinding(MethodBinding binding) {
        this.binding = binding;
    }

    void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(5);
        buffer.append(this.name).append(" = ");
        buffer.append(this.value);
        return buffer.toString();
    }

    public static class UnresolvedEnumConstant {
        ReferenceBinding enumType;
        LookupEnvironment environment;
        char[] enumConstantName;

        UnresolvedEnumConstant(ReferenceBinding enumType, LookupEnvironment environment, char[] enumConstantName) {
            this.enumType = enumType;
            this.environment = environment;
            this.enumConstantName = enumConstantName;
        }

        FieldBinding getResolved() {
            if (this.enumType.isUnresolvedType()) {
                this.enumType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.enumType, this.environment, false);
            }
            return this.enumType.getField(this.enumConstantName, false);
        }

        public char[] getEnumConstantName() {
            return this.enumConstantName;
        }
    }
}

