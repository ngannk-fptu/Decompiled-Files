/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Util;

public class AnnotationValueImpl
implements AnnotationValue,
TypeIds {
    private static final int T_AnnotationMirror = -1;
    private static final int T_EnumConstant = -2;
    private static final int T_ClassObject = -3;
    private static final int T_ArrayType = -4;
    private final BaseProcessingEnvImpl _env;
    private final Object _value;
    private final int _kind;

    public AnnotationValueImpl(BaseProcessingEnvImpl env, Object value, TypeBinding type) {
        this._env = env;
        int[] kind = new int[1];
        if (type == null) {
            this._value = this.convertToMirrorType(value, type, kind);
            this._kind = kind[0];
        } else if (type.isArrayType()) {
            ArrayList<AnnotationValueImpl> convertedValues = null;
            TypeBinding valueType = ((ArrayBinding)type).elementsType();
            if (value instanceof Object[]) {
                Object[] values = (Object[])value;
                convertedValues = new ArrayList(values.length);
                Object[] objectArray = values;
                int n = values.length;
                int n2 = 0;
                while (n2 < n) {
                    Object oneValue = objectArray[n2];
                    convertedValues.add(new AnnotationValueImpl(this._env, oneValue, valueType));
                    ++n2;
                }
            } else {
                convertedValues = new ArrayList<AnnotationValueImpl>(1);
                convertedValues.add(new AnnotationValueImpl(this._env, value, valueType));
            }
            this._value = Collections.unmodifiableList(convertedValues);
            this._kind = -4;
        } else {
            this._value = this.convertToMirrorType(value, type, kind);
            this._kind = kind[0];
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Object convertToMirrorType(Object value, TypeBinding type, int[] kind) {
        block30: {
            block29: {
                if (type == null) {
                    kind[0] = 11;
                    return "<error>";
                }
                if (!(type instanceof BaseTypeBinding) && type.id != 11) break block29;
                if (value == null) {
                    if (type instanceof BaseTypeBinding || type.id == 11) {
                        kind[0] = 11;
                        return "<error>";
                    }
                    if (type.isAnnotationType()) {
                        kind[0] = -1;
                        return this._env.getFactory().newAnnotationMirror(null);
                    }
                    break block30;
                } else if (value instanceof Constant) {
                    if (type instanceof BaseTypeBinding) {
                        kind[0] = ((BaseTypeBinding)type).id;
                    } else {
                        if (type.id != 11) {
                            kind[0] = 11;
                            return "<error>";
                        }
                        kind[0] = ((Constant)value).typeID();
                    }
                    switch (kind[0]) {
                        case 5: {
                            return ((Constant)value).booleanValue();
                        }
                        case 3: {
                            return ((Constant)value).byteValue();
                        }
                        case 2: {
                            return Character.valueOf(((Constant)value).charValue());
                        }
                        case 8: {
                            return ((Constant)value).doubleValue();
                        }
                        case 9: {
                            return Float.valueOf(((Constant)value).floatValue());
                        }
                        case 10: {
                            try {
                                if (!(value instanceof LongConstant || value instanceof DoubleConstant || value instanceof FloatConstant)) {
                                    return ((Constant)value).intValue();
                                }
                                kind[0] = 11;
                                return "<error>";
                            }
                            catch (ShouldNotImplement shouldNotImplement) {
                                kind[0] = 11;
                                return "<error>";
                            }
                        }
                        case 11: {
                            return ((Constant)value).stringValue();
                        }
                        case 7: {
                            return ((Constant)value).longValue();
                        }
                        case 4: {
                            return ((Constant)value).shortValue();
                        }
                    }
                }
                break block30;
            }
            if (type.isEnum()) {
                if (value instanceof FieldBinding) {
                    kind[0] = -2;
                    return this._env.getFactory().newElement((FieldBinding)value);
                }
                kind[0] = 11;
                return "<error>";
            }
            if (type.isAnnotationType()) {
                if (value instanceof AnnotationBinding) {
                    kind[0] = -1;
                    return this._env.getFactory().newAnnotationMirror((AnnotationBinding)value);
                }
            } else if (value instanceof TypeBinding) {
                kind[0] = -3;
                return this._env.getFactory().newTypeMirror((TypeBinding)value);
            }
        }
        kind[0] = 11;
        return "<error>";
    }

    @Override
    public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p) {
        switch (this._kind) {
            case 5: {
                return v.visitBoolean((Boolean)this._value, p);
            }
            case 3: {
                return v.visitByte((Byte)this._value, p);
            }
            case 2: {
                return v.visitChar(((Character)this._value).charValue(), p);
            }
            case 8: {
                return v.visitDouble((Double)this._value, p);
            }
            case 9: {
                return v.visitFloat(((Float)this._value).floatValue(), p);
            }
            case 10: {
                return v.visitInt((Integer)this._value, p);
            }
            case 11: {
                return v.visitString((String)this._value, p);
            }
            case 7: {
                return v.visitLong((Long)this._value, p);
            }
            case 4: {
                return v.visitShort((Short)this._value, p);
            }
            case -2: {
                return v.visitEnumConstant((VariableElement)this._value, p);
            }
            case -3: {
                return v.visitType((TypeMirror)this._value, p);
            }
            case -1: {
                return v.visitAnnotation((AnnotationMirror)this._value, p);
            }
            case -4: {
                return v.visitArray((List)this._value, p);
            }
        }
        return null;
    }

    @Override
    public Object getValue() {
        return this._value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AnnotationValueImpl) {
            return this._value.equals(((AnnotationValueImpl)obj)._value);
        }
        return false;
    }

    public int hashCode() {
        return this._value.hashCode() + this._kind;
    }

    @Override
    public String toString() {
        if (this._value == null) {
            return "null";
        }
        if (this._value instanceof String) {
            String value = (String)this._value;
            StringBuffer sb = new StringBuffer();
            sb.append('\"');
            int i = 0;
            while (i < value.length()) {
                Util.appendEscapedChar(sb, value.charAt(i), true);
                ++i;
            }
            sb.append('\"');
            return sb.toString();
        }
        if (this._value instanceof Character) {
            StringBuffer sb = new StringBuffer();
            sb.append('\'');
            Util.appendEscapedChar(sb, ((Character)this._value).charValue(), false);
            sb.append('\'');
            return sb.toString();
        }
        if (this._value instanceof VariableElement) {
            VariableElement enumDecl = (VariableElement)this._value;
            return String.valueOf(enumDecl.asType().toString()) + "." + enumDecl.getSimpleName();
        }
        if (this._value instanceof Collection) {
            Collection values = (Collection)this._value;
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;
            for (AnnotationValue annoValue : values) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(annoValue.toString());
            }
            sb.append('}');
            return sb.toString();
        }
        if (this._value instanceof TypeMirror) {
            return String.valueOf(this._value.toString()) + ".class";
        }
        return this._value.toString();
    }
}

