/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class TypeVariablePattern
extends PatternNode {
    private static final String anything = "?";
    private String name;
    private TypePattern upperBound;
    private TypePattern[] interfaceBounds;
    private TypePattern lowerBound;

    public TypeVariablePattern(String variableName) {
        this.name = variableName;
        this.upperBound = new ExactTypePattern(UnresolvedType.OBJECT, false, false);
        this.lowerBound = null;
        this.interfaceBounds = null;
    }

    public TypeVariablePattern(String variableName, TypePattern upperBound) {
        this.name = variableName;
        this.upperBound = upperBound;
        this.lowerBound = null;
        this.interfaceBounds = null;
    }

    public TypeVariablePattern(String variableName, TypePattern upperLimit, TypePattern[] interfaceBounds, TypePattern lowerBound) {
        this.name = variableName;
        this.upperBound = upperLimit;
        if (this.upperBound == null) {
            this.upperBound = new ExactTypePattern(UnresolvedType.OBJECT, false, false);
        }
        this.interfaceBounds = interfaceBounds;
        this.lowerBound = lowerBound;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return this.name;
    }

    public boolean isAnythingPattern() {
        return this.name.equals(anything);
    }

    public TypePattern getRawTypePattern() {
        return this.upperBound;
    }

    public TypePattern getUpperBound() {
        return this.upperBound;
    }

    public boolean hasLowerBound() {
        return this.lowerBound != null;
    }

    public TypePattern getLowerBound() {
        return this.lowerBound;
    }

    public boolean hasAdditionalInterfaceBounds() {
        return this.interfaceBounds != null;
    }

    public TypePattern[] getAdditionalInterfaceBounds() {
        if (this.interfaceBounds != null) {
            return this.interfaceBounds;
        }
        return new TypePattern[0];
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TypeVariablePattern)) {
            return false;
        }
        TypeVariablePattern other = (TypeVariablePattern)obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (!this.upperBound.equals(other.upperBound)) {
            return false;
        }
        if (this.lowerBound != null) {
            if (other.lowerBound == null) {
                return false;
            }
            if (!this.lowerBound.equals(other.lowerBound)) {
                return false;
            }
        } else if (other.lowerBound != null) {
            return false;
        }
        if (this.interfaceBounds != null) {
            if (other.interfaceBounds == null) {
                return false;
            }
            if (this.interfaceBounds.length != other.interfaceBounds.length) {
                return false;
            }
            for (int i = 0; i < this.interfaceBounds.length; ++i) {
                if (this.interfaceBounds[i].equals(other.interfaceBounds[i])) continue;
                return false;
            }
        } else if (other.interfaceBounds != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 17 + 37 * this.name.hashCode();
        hashCode = hashCode * 37 + this.upperBound.hashCode();
        if (this.lowerBound != null) {
            hashCode = hashCode * 37 + this.lowerBound.hashCode();
        }
        if (this.interfaceBounds != null) {
            for (int i = 0; i < this.interfaceBounds.length; ++i) {
                hashCode = 37 * hashCode + this.interfaceBounds[i].hashCode();
            }
        }
        return hashCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append(this.getExtendsClause());
        if (this.interfaceBounds != null) {
            sb.append(" & ");
            for (int i = 0; i < this.interfaceBounds.length; ++i) {
                sb.append(this.interfaceBounds[i].toString());
                if (i >= this.interfaceBounds.length) continue;
                sb.append(",");
            }
        }
        if (this.lowerBound != null) {
            sb.append(" super ");
            sb.append(this.lowerBound.toString());
        }
        return sb.toString();
    }

    private String getExtendsClause() {
        if (this.upperBound instanceof ExactTypePattern) {
            ExactTypePattern bound = (ExactTypePattern)this.upperBound;
            if (bound.type == UnresolvedType.OBJECT) {
                return "";
            }
        }
        return " extends " + this.upperBound.toString();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeUTF(this.name);
        this.upperBound.write(s);
        if (this.interfaceBounds == null) {
            s.writeInt(0);
        } else {
            s.writeInt(this.interfaceBounds.length);
            for (int i = 0; i < this.interfaceBounds.length; ++i) {
                this.interfaceBounds[i].write(s);
            }
        }
        s.writeBoolean(this.hasLowerBound());
        if (this.hasLowerBound()) {
            this.lowerBound.write(s);
        }
        this.writeLocation(s);
    }

    public static TypeVariablePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        TypeVariablePattern tv = null;
        String name = s.readUTF();
        TypePattern upperBound = TypePattern.read(s, context);
        TypePattern[] additionalInterfaceBounds = null;
        int numInterfaceBounds = s.readInt();
        if (numInterfaceBounds > 0) {
            additionalInterfaceBounds = new TypePattern[numInterfaceBounds];
            for (int i = 0; i < additionalInterfaceBounds.length; ++i) {
                additionalInterfaceBounds[i] = TypePattern.read(s, context);
            }
        }
        boolean hasLowerBound = s.readBoolean();
        TypePattern lowerBound = null;
        if (hasLowerBound) {
            lowerBound = TypePattern.read(s, context);
        }
        tv = new TypeVariablePattern(name, upperBound, additionalInterfaceBounds, lowerBound);
        tv.readLocation(context, s);
        return tv;
    }
}

