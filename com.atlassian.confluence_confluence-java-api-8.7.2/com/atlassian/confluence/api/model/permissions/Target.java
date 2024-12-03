/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.permissions.TargetType;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ExperimentalApi
public abstract class Target {
    protected final TargetType targetType;

    private Target(TargetType targetType) {
        this.targetType = Objects.requireNonNull(targetType);
    }

    @Deprecated
    public static Target targetForModelObject(Object modelObject) {
        return new ModelObjectTarget(modelObject);
    }

    @Deprecated
    public static Target targetInContainer(Container container, TargetType childTargetType) {
        return new ContainerTarget(new ModelObjectTarget(container), childTargetType);
    }

    public static Target forModelObject(Object modelObject) {
        return new ModelObjectTarget(modelObject);
    }

    public static Target forChildrenOfContainer(Container container, TargetType childTargetType) {
        return new ContainerTarget(new ModelObjectTarget(container), childTargetType);
    }

    public static Target forContentId(ContentId id, TargetType type) {
        return new IdTarget(type, id);
    }

    public final @NonNull TargetType getTargetType() {
        return this.targetType;
    }

    static @NonNull TargetType getTargetType(Object targetInstance) {
        if (targetInstance instanceof Content) {
            ContentType contentType = ((Content)targetInstance).getType();
            if (contentType != null) {
                return Target.getTargetTypeForContentType(contentType);
            }
            throw new IllegalArgumentException("Could not determine TargetType for object: null content type: " + targetInstance);
        }
        if (targetInstance instanceof Space) {
            return TargetType.SPACE;
        }
        throw new IllegalArgumentException("Could not determine TargetType for object: " + targetInstance);
    }

    private static @NonNull TargetType getTargetTypeForContentType(ContentType contentType) {
        return TargetType.valueOf(contentType);
    }

    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !o.getClass().equals(this.getClass())) {
            return false;
        }
        Target t = (Target)o;
        return this.targetType.equals(t.targetType);
    }

    public int hashCode() {
        return Objects.hash(this.targetType);
    }

    @Internal
    public static class ContainerTarget
    extends Target {
        private final ModelObjectTarget container;

        private ContainerTarget(ModelObjectTarget container, TargetType childTargetType) {
            super(Objects.requireNonNull(childTargetType));
            this.container = Objects.requireNonNull(container);
        }

        public @NonNull ModelObjectTarget getContainer() {
            return this.container;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            return super.equals(o) && this.container.equals(((ContainerTarget)o).container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.targetType, this.container);
        }

        public String toString() {
            return "type: " + this.getTargetType() + " of container, container-type:" + this.container.getTargetType() + ", container-id:" + this.container.getId();
        }
    }

    @Internal
    public static class ModelObjectTarget
    extends Target {
        private final Object modelObject;

        private ModelObjectTarget(Object modelObject) {
            super(ModelObjectTarget.getTargetType(Objects.requireNonNull(modelObject)));
            this.modelObject = modelObject;
        }

        public @NonNull Object getModelObject() {
            return this.modelObject;
        }

        private String getId() {
            if (this.modelObject instanceof Content) {
                Content content = (Content)this.modelObject;
                if (content.getId() != null) {
                    return String.valueOf(content.getId().asLong());
                }
                return "null";
            }
            if (this.modelObject instanceof Space) {
                long id = ((Space)this.modelObject).getId();
                if (id == 0L) {
                    return "null";
                }
                return String.valueOf(id);
            }
            return "unknown";
        }

        public String toString() {
            return "type:" + this.getTargetType() + ", ID:" + this.getId();
        }

        @Override
        public boolean equals(@Nullable Object o) {
            return super.equals(o) && this.modelObject.equals(((ModelObjectTarget)o).modelObject);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.targetType, this.modelObject);
        }
    }

    @Internal
    public static class IdTarget
    extends Target {
        private ContentId targetId;

        private IdTarget(TargetType targetType, ContentId targetId) {
            super(targetType);
            this.targetId = targetId;
        }

        public ContentId getId() {
            return this.targetId;
        }

        public String toString() {
            return "type:" + this.getTargetType() + ", ID:" + this.getId();
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof IdTarget)) {
                return false;
            }
            return super.equals(o) && this.targetId.equals(((IdTarget)o).getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.targetType, this.targetId);
        }
    }
}

