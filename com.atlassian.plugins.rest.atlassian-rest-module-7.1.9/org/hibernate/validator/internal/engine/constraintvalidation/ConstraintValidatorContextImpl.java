/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ClockProvider
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeBuilderCustomizableContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeBuilderDefinedContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeContextBuilder
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeBuilderCustomizableContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeBuilderDefinedContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeContextBuilder
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$NodeBuilderCustomizableContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$NodeBuilderDefinedContext
 *  javax.validation.ConstraintValidatorContext$ConstraintViolationBuilder$NodeContextBuilder
 *  javax.validation.ElementKind
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ElementKind;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ConstraintValidatorContextImpl
implements HibernateConstraintValidatorContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private Map<String, Object> messageParameters;
    private Map<String, Object> expressionVariables;
    private final List<String> methodParameterNames;
    private final ClockProvider clockProvider;
    private final PathImpl basePath;
    private final ConstraintDescriptor<?> constraintDescriptor;
    private List<ConstraintViolationCreationContext> constraintViolationCreationContexts;
    private boolean defaultDisabled;
    private Object dynamicPayload;
    private final Object constraintValidatorPayload;

    public ConstraintValidatorContextImpl(List<String> methodParameterNames, ClockProvider clockProvider, PathImpl propertyPath, ConstraintDescriptor<?> constraintDescriptor, Object constraintValidatorPayload) {
        this.methodParameterNames = methodParameterNames;
        this.clockProvider = clockProvider;
        this.basePath = propertyPath;
        this.constraintDescriptor = constraintDescriptor;
        this.constraintValidatorPayload = constraintValidatorPayload;
    }

    public final void disableDefaultConstraintViolation() {
        this.defaultDisabled = true;
    }

    public final String getDefaultConstraintMessageTemplate() {
        return this.constraintDescriptor.getMessageTemplate();
    }

    public final ConstraintValidatorContext.ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate) {
        return new ConstraintViolationBuilderImpl(this.methodParameterNames, messageTemplate, PathImpl.createCopy(this.basePath));
    }

    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(HibernateConstraintValidatorContext.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    @Override
    public HibernateConstraintValidatorContext addExpressionVariable(String name, Object value) {
        Contracts.assertNotNull(name, "null is not a valid value for an expression variable name");
        if (this.expressionVariables == null) {
            this.expressionVariables = new HashMap<String, Object>();
        }
        this.expressionVariables.put(name, value);
        return this;
    }

    @Override
    public HibernateConstraintValidatorContext addMessageParameter(String name, Object value) {
        Contracts.assertNotNull(name, "null is not a valid value for a parameter name");
        if (this.messageParameters == null) {
            this.messageParameters = new HashMap<String, Object>();
        }
        this.messageParameters.put(name, value);
        return this;
    }

    public ClockProvider getClockProvider() {
        return this.clockProvider;
    }

    @Override
    public HibernateConstraintValidatorContext withDynamicPayload(Object violationContext) {
        this.dynamicPayload = violationContext;
        return this;
    }

    @Override
    public <C> C getConstraintValidatorPayload(Class<C> type) {
        if (this.constraintValidatorPayload != null && type.isAssignableFrom(this.constraintValidatorPayload.getClass())) {
            return type.cast(this.constraintValidatorPayload);
        }
        return null;
    }

    public final ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    public final List<ConstraintViolationCreationContext> getConstraintViolationCreationContexts() {
        if (this.defaultDisabled) {
            if (this.constraintViolationCreationContexts == null || this.constraintViolationCreationContexts.size() == 0) {
                throw LOG.getAtLeastOneCustomMessageMustBeCreatedException();
            }
            return CollectionHelper.toImmutableList(this.constraintViolationCreationContexts);
        }
        if (this.constraintViolationCreationContexts == null || this.constraintViolationCreationContexts.size() == 0) {
            return Collections.singletonList(this.getDefaultConstraintViolationCreationContext());
        }
        ArrayList<ConstraintViolationCreationContext> returnedConstraintViolationCreationContexts = new ArrayList<ConstraintViolationCreationContext>(this.constraintViolationCreationContexts.size() + 1);
        returnedConstraintViolationCreationContexts.addAll(this.constraintViolationCreationContexts);
        returnedConstraintViolationCreationContexts.add(this.getDefaultConstraintViolationCreationContext());
        return CollectionHelper.toImmutableList(returnedConstraintViolationCreationContexts);
    }

    private ConstraintViolationCreationContext getDefaultConstraintViolationCreationContext() {
        return new ConstraintViolationCreationContext(this.getDefaultConstraintMessageTemplate(), this.basePath, this.messageParameters != null ? new HashMap<String, Object>(this.messageParameters) : Collections.emptyMap(), (Map<String, Object>)(this.expressionVariables != null ? new HashMap<String, Object>(this.expressionVariables) : Collections.emptyMap()), this.dynamicPayload);
    }

    public List<String> getMethodParameterNames() {
        return this.methodParameterNames;
    }

    private class DeferredNodeBuilder
    extends NodeBuilderBase
    implements ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext,
    ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext,
    ConstraintValidatorContext.ConstraintViolationBuilder.NodeContextBuilder,
    ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeContextBuilder,
    ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext,
    ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeContextBuilder {
        private final String leafNodeName;
        private final ElementKind leafNodeKind;
        private final Class<?> leafNodeContainerType;
        private final Integer leafNodeTypeArgumentIndex;

        private DeferredNodeBuilder(String template, PathImpl path, String nodeName, ElementKind leafNodeKind) {
            super(template, path);
            this.leafNodeName = nodeName;
            this.leafNodeKind = leafNodeKind;
            this.leafNodeContainerType = null;
            this.leafNodeTypeArgumentIndex = null;
        }

        private DeferredNodeBuilder(String template, PathImpl path, String nodeName, Class<?> leafNodeContainerType, Integer leafNodeTypeArgumentIndex) {
            super(template, path);
            this.leafNodeName = nodeName;
            this.leafNodeKind = ElementKind.CONTAINER_ELEMENT;
            this.leafNodeContainerType = leafNodeContainerType;
            this.leafNodeTypeArgumentIndex = leafNodeTypeArgumentIndex;
        }

        public DeferredNodeBuilder inIterable() {
            this.propertyPath.makeLeafNodeIterable();
            return this;
        }

        public DeferredNodeBuilder inContainer(Class<?> containerClass, Integer typeArgumentIndex) {
            this.propertyPath.setLeafNodeTypeParameter(containerClass, typeArgumentIndex);
            return this;
        }

        public NodeBuilder atKey(Object key) {
            this.propertyPath.makeLeafNodeIterableAndSetMapKey(key);
            this.addLeafNode();
            return new NodeBuilder(this.messageTemplate, this.propertyPath);
        }

        public NodeBuilder atIndex(Integer index) {
            this.propertyPath.makeLeafNodeIterableAndSetIndex(index);
            this.addLeafNode();
            return new NodeBuilder(this.messageTemplate, this.propertyPath);
        }

        @Deprecated
        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String name) {
            return this.addPropertyNode(name);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String name) {
            this.addLeafNode();
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, ElementKind.PROPERTY);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name, Class<?> containerType, Integer typeArgumentIndex) {
            this.addLeafNode();
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, containerType, typeArgumentIndex);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode() {
            this.addLeafNode();
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, null, ElementKind.BEAN);
        }

        @Override
        public ConstraintValidatorContext addConstraintViolation() {
            this.addLeafNode();
            return super.addConstraintViolation();
        }

        private void addLeafNode() {
            switch (this.leafNodeKind) {
                case BEAN: {
                    this.propertyPath.addBeanNode();
                    break;
                }
                case PROPERTY: {
                    this.propertyPath.addPropertyNode(this.leafNodeName);
                    break;
                }
                case CONTAINER_ELEMENT: {
                    this.propertyPath.setLeafNodeTypeParameter(this.leafNodeContainerType, this.leafNodeTypeArgumentIndex);
                    this.propertyPath.addContainerElementNode(this.leafNodeName);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unsupported node kind: " + this.leafNodeKind);
                }
            }
        }
    }

    private class NodeBuilder
    extends NodeBuilderBase
    implements ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext,
    ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderDefinedContext,
    ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderDefinedContext {
        private NodeBuilder(String template, PathImpl path) {
            super(template, path);
        }

        @Deprecated
        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String name) {
            return this.addPropertyNode(name);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String name) {
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, ElementKind.PROPERTY);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode() {
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, null, ElementKind.BEAN);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name, Class<?> containerType, Integer typeArgumentIndex) {
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, containerType, typeArgumentIndex);
        }
    }

    private class ConstraintViolationBuilderImpl
    extends NodeBuilderBase
    implements ConstraintValidatorContext.ConstraintViolationBuilder {
        private final List<String> methodParameterNames;

        private ConstraintViolationBuilderImpl(List<String> methodParameterNames, String template, PathImpl path) {
            super(template, path);
            this.methodParameterNames = methodParameterNames;
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext addNode(String name) {
            this.dropLeafNodeIfRequired();
            this.propertyPath.addPropertyNode(name);
            return new NodeBuilder(this.messageTemplate, this.propertyPath);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String name) {
            this.dropLeafNodeIfRequired();
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, ElementKind.PROPERTY);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode() {
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, null, ElementKind.BEAN);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext addParameterNode(int index) {
            if (this.propertyPath.getLeafNode().getKind() != ElementKind.CROSS_PARAMETER) {
                throw LOG.getParameterNodeAddedForNonCrossParameterConstraintException(this.propertyPath);
            }
            this.dropLeafNodeIfRequired();
            this.propertyPath.addParameterNode(this.methodParameterNames.get(index), index);
            return new NodeBuilder(this.messageTemplate, this.propertyPath);
        }

        public ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name, Class<?> containerType, Integer typeArgumentIndex) {
            this.dropLeafNodeIfRequired();
            return new DeferredNodeBuilder(this.messageTemplate, this.propertyPath, name, containerType, typeArgumentIndex);
        }

        private void dropLeafNodeIfRequired() {
            if (this.propertyPath.getLeafNode().getKind() == ElementKind.BEAN || this.propertyPath.getLeafNode().getKind() == ElementKind.CROSS_PARAMETER) {
                this.propertyPath = this.propertyPath.getPathWithoutLeafNode();
            }
        }
    }

    private abstract class NodeBuilderBase {
        protected final String messageTemplate;
        protected PathImpl propertyPath;

        protected NodeBuilderBase(String template, PathImpl path) {
            this.messageTemplate = template;
            this.propertyPath = path;
        }

        public ConstraintValidatorContext addConstraintViolation() {
            if (ConstraintValidatorContextImpl.this.constraintViolationCreationContexts == null) {
                ConstraintValidatorContextImpl.this.constraintViolationCreationContexts = CollectionHelper.newArrayList(3);
            }
            ConstraintValidatorContextImpl.this.constraintViolationCreationContexts.add(new ConstraintViolationCreationContext(this.messageTemplate, this.propertyPath, ConstraintValidatorContextImpl.this.messageParameters != null ? new HashMap<String, Object>(ConstraintValidatorContextImpl.this.messageParameters) : Collections.emptyMap(), ConstraintValidatorContextImpl.this.expressionVariables != null ? new HashMap(ConstraintValidatorContextImpl.this.expressionVariables) : Collections.emptyMap(), ConstraintValidatorContextImpl.this.dynamicPayload));
            return ConstraintValidatorContextImpl.this;
        }
    }
}

