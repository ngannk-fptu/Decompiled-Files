/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.bytecode.assign;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveTypeAwareAssigner;
import net.bytebuddy.implementation.bytecode.assign.primitive.VoidAwareAssigner;
import net.bytebuddy.implementation.bytecode.assign.reference.GenericTypeAwareAssigner;
import net.bytebuddy.implementation.bytecode.assign.reference.ReferenceTypeAwareAssigner;

@SuppressFBWarnings(value={"IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION"}, justification="Safe initialization is implied.")
public interface Assigner {
    public static final Assigner DEFAULT = new VoidAwareAssigner(new PrimitiveTypeAwareAssigner(ReferenceTypeAwareAssigner.INSTANCE));
    public static final Assigner GENERICS_AWARE = new VoidAwareAssigner(new PrimitiveTypeAwareAssigner(GenericTypeAwareAssigner.INSTANCE));

    public StackManipulation assign(TypeDescription.Generic var1, TypeDescription.Generic var2, Typing var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Refusing implements Assigner
    {
        INSTANCE;


        @Override
        public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Typing typing) {
            return StackManipulation.Illegal.INSTANCE;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EqualTypesOnly implements Assigner
    {
        GENERIC{

            public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Typing typing) {
                return (StackManipulation)((Object)(source.equals(target) ? StackManipulation.Trivial.INSTANCE : StackManipulation.Illegal.INSTANCE));
            }
        }
        ,
        ERASURE{

            public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Typing typing) {
                return (StackManipulation)((Object)(source.asErasure().equals(target.asErasure()) ? StackManipulation.Trivial.INSTANCE : StackManipulation.Illegal.INSTANCE));
            }
        };

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Typing {
        STATIC(false),
        DYNAMIC(true);

        private final boolean dynamic;

        private Typing(boolean dynamic) {
            this.dynamic = dynamic;
        }

        public static Typing of(boolean dynamic) {
            return dynamic ? DYNAMIC : STATIC;
        }

        public boolean isDynamic() {
            return this.dynamic;
        }
    }
}

