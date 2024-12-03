/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.List;
import javax.validation.ElementKind;

public interface Path
extends Iterable<Node> {
    public String toString();

    public static interface ContainerElementNode
    extends Node {
        public Class<?> getContainerClass();

        public Integer getTypeArgumentIndex();
    }

    public static interface PropertyNode
    extends Node {
        public Class<?> getContainerClass();

        public Integer getTypeArgumentIndex();
    }

    public static interface BeanNode
    extends Node {
        public Class<?> getContainerClass();

        public Integer getTypeArgumentIndex();
    }

    public static interface CrossParameterNode
    extends Node {
    }

    public static interface ParameterNode
    extends Node {
        public int getParameterIndex();
    }

    public static interface ReturnValueNode
    extends Node {
    }

    public static interface ConstructorNode
    extends Node {
        public List<Class<?>> getParameterTypes();
    }

    public static interface MethodNode
    extends Node {
        public List<Class<?>> getParameterTypes();
    }

    public static interface Node {
        public String getName();

        public boolean isInIterable();

        public Integer getIndex();

        public Object getKey();

        public ElementKind getKind();

        public <T extends Node> T as(Class<T> var1);

        public String toString();
    }
}

