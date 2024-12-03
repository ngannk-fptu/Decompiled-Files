/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.metadata.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.StringHelper;

public class MetaConstraint<A extends Annotation> {
    private final ConstraintTree<A> constraintTree;
    private final ConstraintLocation location;
    private final ValueExtractionPathNode valueExtractionPath;
    private final int hashCode;
    private final boolean isDefinedForOneGroupOnly;

    MetaConstraint(ConstraintDescriptorImpl<A> constraintDescriptor, ConstraintLocation location, List<ContainerClassTypeParameterAndExtractor> valueExtractionPath, Type validatedValueType) {
        this.constraintTree = ConstraintTree.of(constraintDescriptor, validatedValueType);
        this.location = location;
        this.valueExtractionPath = MetaConstraint.getValueExtractionPath(valueExtractionPath);
        this.hashCode = MetaConstraint.buildHashCode(constraintDescriptor, location);
        this.isDefinedForOneGroupOnly = constraintDescriptor.getGroups().size() <= 1;
    }

    private static ValueExtractionPathNode getValueExtractionPath(List<ContainerClassTypeParameterAndExtractor> valueExtractionPath) {
        switch (valueExtractionPath.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return new SingleValueExtractionPathNode(valueExtractionPath.iterator().next());
            }
        }
        return new LinkedValueExtractionPathNode(null, valueExtractionPath);
    }

    public final Set<Class<?>> getGroupList() {
        return this.constraintTree.getDescriptor().getGroups();
    }

    public final boolean isDefinedForOneGroupOnly() {
        return this.isDefinedForOneGroupOnly;
    }

    public final ConstraintDescriptorImpl<A> getDescriptor() {
        return this.constraintTree.getDescriptor();
    }

    public final ElementType getElementType() {
        return this.constraintTree.getDescriptor().getElementType();
    }

    public boolean validateConstraint(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext) {
        boolean success = true;
        if (this.valueExtractionPath != null) {
            Object valueToValidate = valueContext.getCurrentValidatedValue();
            if (valueToValidate != null) {
                TypeParameterValueReceiver receiver = new TypeParameterValueReceiver(validationContext, valueContext, this.valueExtractionPath);
                ValueExtractorHelper.extractValues(this.valueExtractionPath.getValueExtractorDescriptor(), valueToValidate, receiver);
                success = receiver.isSuccess();
            }
        } else {
            success = this.doValidateConstraint(validationContext, valueContext);
        }
        return success;
    }

    private boolean doValidateConstraint(ValidationContext<?> executionContext, ValueContext<?, ?> valueContext) {
        valueContext.setElementType(this.getElementType());
        boolean validationResult = this.constraintTree.validateConstraints(executionContext, valueContext);
        return validationResult;
    }

    public ConstraintLocation getLocation() {
        return this.location;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MetaConstraint that = (MetaConstraint)o;
        if (!this.constraintTree.getDescriptor().equals(that.constraintTree.getDescriptor())) {
            return false;
        }
        return this.location.equals(that.location);
    }

    private static int buildHashCode(ConstraintDescriptorImpl<?> constraintDescriptor, ConstraintLocation location) {
        int prime = 31;
        int result = 1;
        result = 31 * result + constraintDescriptor.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MetaConstraint");
        sb.append("{constraintType=").append(StringHelper.toShortString(this.constraintTree.getDescriptor().getAnnotation().annotationType()));
        sb.append(", location=").append(this.location);
        sb.append(", valueExtractionPath=").append(this.valueExtractionPath);
        sb.append("}");
        return sb.toString();
    }

    private static final class LinkedValueExtractionPathNode
    implements ValueExtractionPathNode {
        private final ValueExtractionPathNode previous;
        private final ValueExtractionPathNode next;
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        private LinkedValueExtractionPathNode(ValueExtractionPathNode previous, List<ContainerClassTypeParameterAndExtractor> elements) {
            ContainerClassTypeParameterAndExtractor first = elements.get(0);
            this.containerClass = first.containerClass;
            this.typeParameter = first.typeParameter;
            this.typeParameterIndex = first.typeParameterIndex;
            this.valueExtractorDescriptor = first.valueExtractorDescriptor;
            this.previous = previous;
            this.next = elements.size() == 1 ? null : new LinkedValueExtractionPathNode(this, elements.subList(1, elements.size()));
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public ValueExtractionPathNode getPrevious() {
            return this.previous;
        }

        @Override
        public ValueExtractionPathNode getNext() {
            return this.next;
        }

        @Override
        public Class<?> getContainerClass() {
            return this.containerClass;
        }

        @Override
        public TypeVariable<?> getTypeParameter() {
            return this.typeParameter;
        }

        @Override
        public Integer getTypeParameterIndex() {
            return this.typeParameterIndex;
        }

        @Override
        public ValueExtractorDescriptor getValueExtractorDescriptor() {
            return this.valueExtractorDescriptor;
        }

        public String toString() {
            return "LinkedValueExtractionPathNode [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }

    private static final class SingleValueExtractionPathNode
    implements ValueExtractionPathNode {
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        public SingleValueExtractionPathNode(ContainerClassTypeParameterAndExtractor typeParameterAndExtractor) {
            this.containerClass = typeParameterAndExtractor.containerClass;
            this.typeParameter = typeParameterAndExtractor.typeParameter;
            this.typeParameterIndex = typeParameterAndExtractor.typeParameterIndex;
            this.valueExtractorDescriptor = typeParameterAndExtractor.valueExtractorDescriptor;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public ValueExtractionPathNode getPrevious() {
            throw new NoSuchElementException();
        }

        @Override
        public ValueExtractionPathNode getNext() {
            throw new NoSuchElementException();
        }

        @Override
        public Class<?> getContainerClass() {
            return this.containerClass;
        }

        @Override
        public TypeVariable<?> getTypeParameter() {
            return this.typeParameter;
        }

        @Override
        public Integer getTypeParameterIndex() {
            return this.typeParameterIndex;
        }

        @Override
        public ValueExtractorDescriptor getValueExtractorDescriptor() {
            return this.valueExtractorDescriptor;
        }

        public String toString() {
            return "SingleValueExtractionPathNode [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }

    private static interface ValueExtractionPathNode {
        public boolean hasNext();

        public ValueExtractionPathNode getPrevious();

        public ValueExtractionPathNode getNext();

        public Class<?> getContainerClass();

        public TypeVariable<?> getTypeParameter();

        public Integer getTypeParameterIndex();

        public ValueExtractorDescriptor getValueExtractorDescriptor();
    }

    static final class ContainerClassTypeParameterAndExtractor {
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        ContainerClassTypeParameterAndExtractor(Class<?> containerClass, TypeVariable<?> typeParameter, Integer typeParameterIndex, ValueExtractorDescriptor valueExtractorDescriptor) {
            this.containerClass = containerClass;
            this.typeParameter = typeParameter;
            this.typeParameterIndex = typeParameterIndex;
            this.valueExtractorDescriptor = valueExtractorDescriptor;
        }

        public String toString() {
            return "ContainerClassTypeParameterAndExtractor [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", typeParameterIndex=" + this.typeParameterIndex + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }

    private final class TypeParameterValueReceiver
    implements ValueExtractor.ValueReceiver {
        private final ValidationContext<?> validationContext;
        private final ValueContext<?, Object> valueContext;
        private boolean success = true;
        private ValueExtractionPathNode currentValueExtractionPathNode;

        public TypeParameterValueReceiver(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext, ValueExtractionPathNode currentValueExtractionPathNode) {
            this.validationContext = validationContext;
            this.valueContext = valueContext;
            this.currentValueExtractionPathNode = currentValueExtractionPathNode;
        }

        public void value(String nodeName, Object object) {
            this.doValidate(object, nodeName);
        }

        public void iterableValue(String nodeName, Object value) {
            this.valueContext.markCurrentPropertyAsIterable();
            this.doValidate(value, nodeName);
        }

        public void indexedValue(String nodeName, int index, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetIndex(index);
            this.doValidate(value, nodeName);
        }

        public void keyedValue(String nodeName, Object key, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetKey(key);
            this.doValidate(value, nodeName);
        }

        private void doValidate(Object value, String nodeName) {
            ValueContext.ValueState<Object> originalValueState = this.valueContext.getCurrentValueState();
            Class<?> containerClass = this.currentValueExtractionPathNode.getContainerClass();
            if (containerClass != null) {
                this.valueContext.setTypeParameter(containerClass, this.currentValueExtractionPathNode.getTypeParameterIndex());
            }
            if (nodeName != null) {
                this.valueContext.appendTypeParameterNode(nodeName);
            }
            this.valueContext.setCurrentValidatedValue(value);
            if (this.currentValueExtractionPathNode.hasNext()) {
                if (value != null) {
                    this.currentValueExtractionPathNode = this.currentValueExtractionPathNode.getNext();
                    ValueExtractorDescriptor valueExtractorDescriptor = this.currentValueExtractionPathNode.getValueExtractorDescriptor();
                    ValueExtractorHelper.extractValues(valueExtractorDescriptor, value, this);
                    this.currentValueExtractionPathNode = this.currentValueExtractionPathNode.getPrevious();
                }
            } else {
                this.success &= MetaConstraint.this.doValidateConstraint(this.validationContext, this.valueContext);
            }
            this.valueContext.resetValueState(originalValueState);
        }

        public boolean isSuccess() {
            return this.success;
        }
    }
}

