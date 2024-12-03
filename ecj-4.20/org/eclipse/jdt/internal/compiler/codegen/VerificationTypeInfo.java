/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class VerificationTypeInfo {
    public static final int ITEM_TOP = 0;
    public static final int ITEM_INTEGER = 1;
    public static final int ITEM_FLOAT = 2;
    public static final int ITEM_DOUBLE = 3;
    public static final int ITEM_LONG = 4;
    public static final int ITEM_NULL = 5;
    public static final int ITEM_UNINITIALIZED_THIS = 6;
    public static final int ITEM_OBJECT = 7;
    public static final int ITEM_UNINITIALIZED = 8;
    public int tag;
    private int id;
    private TypeBinding binding;
    public int offset;
    private List<TypeBinding> bindings;

    public VerificationTypeInfo(int tag, TypeBinding binding) {
        this(binding);
        this.tag = tag;
    }

    public VerificationTypeInfo(TypeBinding binding) {
        if (binding == null) {
            return;
        }
        this.id = binding.id;
        this.binding = binding;
        switch (binding.id) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                this.tag = 1;
                break;
            }
            case 9: {
                this.tag = 2;
                break;
            }
            case 7: {
                this.tag = 4;
                break;
            }
            case 8: {
                this.tag = 3;
                break;
            }
            case 12: {
                this.tag = 5;
                break;
            }
            default: {
                this.tag = 7;
            }
        }
    }

    public void setBinding(TypeBinding binding) {
        int typeBindingId;
        this.id = typeBindingId = binding.id;
        switch (typeBindingId) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                this.tag = 1;
                break;
            }
            case 9: {
                this.tag = 2;
                break;
            }
            case 7: {
                this.tag = 4;
                break;
            }
            case 8: {
                this.tag = 3;
                break;
            }
            case 12: {
                this.tag = 5;
                break;
            }
            default: {
                this.tag = 7;
            }
        }
    }

    public int id() {
        return this.id;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        switch (this.tag) {
            case 6: {
                buffer.append("uninitialized_this(").append(this.readableName()).append(")");
                break;
            }
            case 8: {
                buffer.append("uninitialized(").append(this.readableName()).append(")");
                break;
            }
            case 7: {
                buffer.append(this.readableName());
                break;
            }
            case 3: {
                buffer.append('D');
                break;
            }
            case 2: {
                buffer.append('F');
                break;
            }
            case 1: {
                buffer.append('I');
                break;
            }
            case 4: {
                buffer.append('J');
                break;
            }
            case 5: {
                buffer.append("null");
                break;
            }
            case 0: {
                buffer.append("top");
            }
        }
        return String.valueOf(buffer);
    }

    public VerificationTypeInfo duplicate() {
        VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo(this.tag, this.binding);
        verificationTypeInfo.offset = this.offset;
        return verificationTypeInfo;
    }

    public boolean equals(Object obj) {
        if (obj instanceof VerificationTypeInfo) {
            VerificationTypeInfo info1 = (VerificationTypeInfo)obj;
            return info1.tag == this.tag && info1.offset == this.offset && CharOperation.equals(info1.constantPoolName(), this.constantPoolName());
        }
        return false;
    }

    public int hashCode() {
        return this.tag + this.offset + CharOperation.hashCode(this.constantPoolName());
    }

    public char[] constantPoolName() {
        return this.binding.constantPoolName();
    }

    public char[] readableName() {
        return this.constantPoolName();
    }

    public void replaceWithElementType() {
        ArrayBinding arrayBinding = (ArrayBinding)this.binding;
        this.binding = arrayBinding.elementsType();
        this.id = this.binding.id;
    }

    public VerificationTypeInfo merge(VerificationTypeInfo verificationTypeInfo, Scope scope) {
        if (this.binding.isBaseType() && verificationTypeInfo.binding.isBaseType()) {
            return this;
        }
        if (!this.binding.equals(verificationTypeInfo.binding)) {
            if (this.bindings == null) {
                this.bindings = new ArrayList<TypeBinding>();
                this.bindings.add(this.binding);
            }
            this.bindings.add(verificationTypeInfo.binding);
            this.binding = scope.lowerUpperBound(this.bindings.toArray(new TypeBinding[this.bindings.size()]));
            if (this.binding != null) {
                this.id = this.binding.id;
                switch (this.id) {
                    case 12: {
                        this.tag = 5;
                        break;
                    }
                    default: {
                        this.tag = 7;
                        break;
                    }
                }
            } else {
                this.binding = scope.getJavaLangObject();
                this.tag = 7;
            }
        }
        return this;
    }
}

