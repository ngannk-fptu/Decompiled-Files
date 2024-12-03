/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.MDC
 */
package org.hibernate.loader.plan.build.internal;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.CollectionReturnImpl;
import org.hibernate.loader.plan.build.internal.returns.EntityReturnImpl;
import org.hibernate.loader.plan.build.internal.spaces.QuerySpacesImpl;
import org.hibernate.loader.plan.build.spi.ExpandingEntityIdentifierDescription;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.build.spi.LoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.spi.LoadPlanBuildingContext;
import org.hibernate.loader.plan.spi.AttributeFetch;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.loader.plan.spi.CompositeFetch;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

public abstract class AbstractLoadPlanBuildingAssociationVisitationStrategy
implements LoadPlanBuildingAssociationVisitationStrategy,
LoadPlanBuildingContext {
    private static final Logger log = Logger.getLogger(AbstractLoadPlanBuildingAssociationVisitationStrategy.class);
    private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
    private final SessionFactoryImplementor sessionFactory;
    private final QuerySpacesImpl querySpaces;
    private final PropertyPathStack propertyPathStack = new PropertyPathStack();
    private final ArrayDeque<ExpandingFetchSource> fetchSourceStack = new ArrayDeque();
    private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque();
    protected PropertyPath currentPropertyPath = new PropertyPath("");
    private Map<AssociationKey, FetchSource> fetchedAssociationKeySourceMap = new HashMap<AssociationKey, FetchSource>();

    protected AbstractLoadPlanBuildingAssociationVisitationStrategy(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.querySpaces = new QuerySpacesImpl(sessionFactory);
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public ExpandingQuerySpaces getQuerySpaces() {
        return this.querySpaces;
    }

    private void pushToStack(ExpandingFetchSource fetchSource) {
        log.trace((Object)("Pushing fetch source to stack : " + fetchSource));
        this.propertyPathStack.push(fetchSource.getPropertyPath());
        this.fetchSourceStack.addFirst(fetchSource);
    }

    private ExpandingFetchSource popFromStack() {
        ExpandingFetchSource last = this.fetchSourceStack.removeFirst();
        log.trace((Object)("Popped fetch owner from stack : " + last));
        this.propertyPathStack.pop();
        return last;
    }

    protected ExpandingFetchSource currentSource() {
        return this.fetchSourceStack.peekFirst();
    }

    @Override
    public void start() {
        if (!this.fetchSourceStack.isEmpty()) {
            throw new WalkingException("Fetch owner stack was not empty on start; be sure to not use LoadPlanBuilderStrategy instances concurrently");
        }
        this.propertyPathStack.push(new PropertyPath());
    }

    @Override
    public void finish() {
        this.propertyPathStack.pop();
        MDC.remove((String)MDC_KEY);
        this.fetchSourceStack.clear();
    }

    protected abstract void addRootReturn(Return var1);

    protected boolean supportsRootEntityReturns() {
        return true;
    }

    @Override
    public void startingEntity(EntityDefinition entityDefinition) {
        boolean isRoot = this.fetchSourceStack.isEmpty();
        if (!isRoot) {
            return;
        }
        log.tracef("%s Starting root entity : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)entityDefinition.getEntityPersister().getEntityName());
        if (!this.supportsRootEntityReturns()) {
            throw new HibernateException("This strategy does not support root entity returns");
        }
        EntityReturnImpl entityReturn = new EntityReturnImpl(entityDefinition, this.querySpaces);
        this.addRootReturn(entityReturn);
        this.pushToStack(entityReturn);
        Joinable entityPersister = (Joinable)((Object)entityDefinition.getEntityPersister());
        this.associationKeyRegistered(new AssociationKey(entityPersister.getTableName(), entityPersister.getKeyColumnNames()));
    }

    @Override
    public void finishingEntity(EntityDefinition entityDefinition) {
        boolean isRoot;
        ExpandingFetchSource currentSource = this.currentSource();
        boolean bl = isRoot = EntityReturn.class.isInstance(currentSource) && entityDefinition.getEntityPersister().equals(((EntityReturn)EntityReturn.class.cast(currentSource)).getEntityPersister());
        if (!isRoot) {
            return;
        }
        ExpandingFetchSource popped = this.popFromStack();
        this.checkPoppedEntity(popped, entityDefinition);
        log.tracef("%s Finished root entity : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)entityDefinition.getEntityPersister().getEntityName());
    }

    private void checkPoppedEntity(ExpandingFetchSource fetchSource, EntityDefinition entityDefinition) {
        if (!EntityReference.class.isInstance(fetchSource)) {
            throw new WalkingException(String.format("Mismatched FetchSource from stack on pop.  Expecting EntityReference(%s), but found %s", entityDefinition.getEntityPersister().getEntityName(), fetchSource));
        }
        EntityReference entityReference = (EntityReference)((Object)fetchSource);
        if (!entityReference.getEntityPersister().equals(entityDefinition.getEntityPersister())) {
            throw new WalkingException("Mismatched FetchSource from stack on pop");
        }
    }

    @Override
    public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
        log.tracef("%s Starting entity identifier : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName());
        EntityReference entityReference = (EntityReference)((Object)this.currentSource());
        if (!entityReference.getEntityPersister().equals(entityIdentifierDefinition.getEntityDefinition().getEntityPersister())) {
            throw new WalkingException(String.format("Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]", entityReference.getEntityPersister().getEntityName(), entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()));
        }
        if (ExpandingEntityIdentifierDescription.class.isInstance(entityReference.getIdentifierDescription())) {
            this.pushToStack((ExpandingEntityIdentifierDescription)entityReference.getIdentifierDescription());
        }
    }

    @Override
    public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
        ExpandingFetchSource currentSource = this.currentSource();
        if (!ExpandingEntityIdentifierDescription.class.isInstance(currentSource)) {
            if (!EntityReference.class.isInstance(currentSource)) {
                throw new WalkingException("Unexpected state in FetchSource stack");
            }
            EntityReference entityReference = (EntityReference)((Object)currentSource);
            if (entityReference.getEntityPersister().getEntityKeyDefinition() != entityIdentifierDefinition) {
                throw new WalkingException(String.format("Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]", entityReference.getEntityPersister().getEntityName(), entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()));
            }
            return;
        }
        ExpandingEntityIdentifierDescription identifierDescription = (ExpandingEntityIdentifierDescription)this.popFromStack();
        ExpandingFetchSource entitySource = this.currentSource();
        if (!EntityReference.class.isInstance(entitySource)) {
            throw new WalkingException("Unexpected state in FetchSource stack");
        }
        EntityReference entityReference = (EntityReference)((Object)entitySource);
        if (entityReference.getIdentifierDescription() != identifierDescription) {
            throw new WalkingException(String.format("Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]", entityReference.getEntityPersister().getEntityName(), entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()));
        }
        log.tracef("%s Finished entity identifier : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName());
    }

    private void pushToCollectionStack(CollectionReference collectionReference) {
        log.trace((Object)("Pushing collection reference to stack : " + collectionReference));
        this.propertyPathStack.push(collectionReference.getPropertyPath());
        this.collectionReferenceStack.addFirst(collectionReference);
    }

    private CollectionReference popFromCollectionStack() {
        CollectionReference last = this.collectionReferenceStack.removeFirst();
        log.trace((Object)("Popped collection reference from stack : " + last));
        this.propertyPathStack.pop();
        return last;
    }

    private CollectionReference currentCollection() {
        return this.collectionReferenceStack.peekFirst();
    }

    @Override
    public void startingCollection(CollectionDefinition collectionDefinition) {
        boolean isRoot = this.fetchSourceStack.isEmpty();
        if (!isRoot) {
            return;
        }
        log.tracef("%s Starting root collection : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)collectionDefinition.getCollectionPersister().getRole());
        if (!this.supportsRootCollectionReturns()) {
            throw new HibernateException("This strategy does not support root collection returns");
        }
        CollectionReturnImpl collectionReturn = new CollectionReturnImpl(collectionDefinition, this.querySpaces);
        this.pushToCollectionStack(collectionReturn);
        this.addRootReturn(collectionReturn);
        this.associationKeyRegistered(new AssociationKey(((Joinable)((Object)collectionDefinition.getCollectionPersister())).getTableName(), ((Joinable)((Object)collectionDefinition.getCollectionPersister())).getKeyColumnNames()));
    }

    protected boolean supportsRootCollectionReturns() {
        return true;
    }

    @Override
    public void finishingCollection(CollectionDefinition collectionDefinition) {
        boolean isRoot;
        boolean bl = isRoot = this.fetchSourceStack.isEmpty() && this.collectionReferenceStack.size() == 1;
        if (!isRoot) {
            return;
        }
        CollectionReference popped = this.popFromCollectionStack();
        this.checkedPoppedCollection(popped, collectionDefinition);
        log.tracef("%s Finished root collection : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)collectionDefinition.getCollectionPersister().getRole());
    }

    private void checkedPoppedCollection(CollectionReference poppedCollectionReference, CollectionDefinition collectionDefinition) {
        if (!poppedCollectionReference.getCollectionPersister().equals(collectionDefinition.getCollectionPersister())) {
            throw new WalkingException("Mismatched CollectionReference from stack on pop");
        }
    }

    @Override
    public void startingCollectionIndex(CollectionIndexDefinition indexDefinition) {
        Type indexType = indexDefinition.getType();
        log.tracef("%s Starting collection index graph : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)indexDefinition.getCollectionDefinition().getCollectionPersister().getRole());
        CollectionReference collectionReference = this.currentCollection();
        CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
        if (indexType.isEntityType() || indexType.isComponentType()) {
            if (indexGraph == null) {
                throw new WalkingException("CollectionReference did not return an expected index graph : " + indexDefinition.getCollectionDefinition().getCollectionPersister().getRole());
            }
            if (!indexType.isAnyType()) {
                this.pushToStack((ExpandingFetchSource)((Object)indexGraph));
            }
        } else if (indexGraph != null) {
            throw new WalkingException("CollectionReference returned an unexpected index graph : " + indexDefinition.getCollectionDefinition().getCollectionPersister().getRole());
        }
    }

    @Override
    public void finishingCollectionIndex(CollectionIndexDefinition indexDefinition) {
        ExpandingFetchSource fetchSource;
        Type indexType = indexDefinition.getType();
        if (!indexType.isAnyType() && (indexType.isEntityType() || indexType.isComponentType()) && !CollectionFetchableIndex.class.isInstance(fetchSource = this.popFromStack())) {
            throw new WalkingException("CollectionReference did not return an expected index graph : " + indexDefinition.getCollectionDefinition().getCollectionPersister().getRole());
        }
        log.tracef("%s Finished collection index graph : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)indexDefinition.getCollectionDefinition().getCollectionPersister().getRole());
    }

    @Override
    public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
        Type elementType = elementDefinition.getType();
        log.tracef("%s Starting collection element graph : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)elementDefinition.getCollectionDefinition().getCollectionPersister().getRole());
        CollectionReference collectionReference = this.currentCollection();
        CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
        if (elementType.isAssociationType() || elementType.isComponentType()) {
            if (elementGraph == null) {
                throw new IllegalStateException("CollectionReference did not return an expected element graph : " + elementDefinition.getCollectionDefinition().getCollectionPersister().getRole());
            }
            if (!elementType.isAnyType()) {
                this.pushToStack((ExpandingFetchSource)((Object)elementGraph));
            }
        } else if (elementGraph != null) {
            throw new IllegalStateException("CollectionReference returned an unexpected element graph : " + elementDefinition.getCollectionDefinition().getCollectionPersister().getRole());
        }
    }

    @Override
    public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
        ExpandingFetchSource popped;
        Type elementType = elementDefinition.getType();
        if (!elementType.isAnyType() && (elementType.isComponentType() || elementType.isAssociationType()) && !CollectionFetchableElement.class.isInstance(popped = this.popFromStack())) {
            throw new WalkingException("Mismatched FetchSource from stack on pop");
        }
        log.tracef("%s Finished collection element graph : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)elementDefinition.getCollectionDefinition().getCollectionPersister().getRole());
    }

    @Override
    public void startingComposite(CompositionDefinition compositionDefinition) {
        log.tracef("%s Starting composite : %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)compositionDefinition.getName());
        if (this.fetchSourceStack.isEmpty() && this.collectionReferenceStack.isEmpty()) {
            throw new HibernateException("A component cannot be the root of a walk nor a graph");
        }
        ExpandingFetchSource currentSource = this.currentSource();
        if (!(CompositeFetch.class.isInstance(currentSource) || CollectionFetchableElement.class.isInstance(currentSource) || CollectionFetchableIndex.class.isInstance(currentSource) || ExpandingEntityIdentifierDescription.class.isInstance(currentSource))) {
            throw new WalkingException("Mismatched FetchSource from stack on pop");
        }
    }

    @Override
    public void finishingComposite(CompositionDefinition compositionDefinition) {
        log.tracef("%s Finishing composite : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)compositionDefinition.getName());
    }

    @Override
    public boolean startingAttribute(AttributeDefinition attributeDefinition) {
        log.tracef("%s Starting attribute %s", (Object)StringHelper.repeat(">>", this.fetchSourceStack.size()), (Object)attributeDefinition);
        Type attributeType = attributeDefinition.getType();
        boolean isComponentType = attributeType.isComponentType();
        boolean isAssociationType = attributeType.isAssociationType();
        boolean isBasicType = !isComponentType && !isAssociationType;
        this.currentPropertyPath = this.currentPropertyPath.append(attributeDefinition.getName());
        if (isBasicType) {
            return true;
        }
        if (isAssociationType) {
            return this.handleAssociationAttribute((AssociationAttributeDefinition)attributeDefinition);
        }
        return this.handleCompositeAttribute(attributeDefinition);
    }

    @Override
    public void finishingAttribute(AttributeDefinition attributeDefinition) {
        Type attributeType = attributeDefinition.getType();
        if (attributeType.isAssociationType()) {
            AssociationAttributeDefinition associationAttributeDefinition = (AssociationAttributeDefinition)attributeDefinition;
            if (!attributeType.isAnyType()) {
                CollectionReference currentCollection;
                if (attributeType.isEntityType()) {
                    ExpandingFetchSource source = this.currentSource();
                    if (AttributeFetch.class.isInstance(source) && associationAttributeDefinition.equals(((AttributeFetch)AttributeFetch.class.cast(source)).getFetchedAttributeDefinition())) {
                        ExpandingFetchSource popped = this.popFromStack();
                        this.checkPoppedEntity(popped, associationAttributeDefinition.toEntityDefinition());
                    }
                } else if (attributeType.isCollectionType() && AttributeFetch.class.isInstance(currentCollection = this.currentCollection()) && associationAttributeDefinition.equals(((AttributeFetch)AttributeFetch.class.cast(currentCollection)).getFetchedAttributeDefinition())) {
                    CollectionReference popped = this.popFromCollectionStack();
                    this.checkedPoppedCollection(popped, associationAttributeDefinition.toCollectionDefinition());
                }
            }
        } else if (attributeType.isComponentType()) {
            ExpandingFetchSource popped = this.popFromStack();
            if (!CompositeAttributeFetch.class.isInstance(popped)) {
                throw new WalkingException(String.format("Mismatched FetchSource from stack on pop; expected: CompositeAttributeFetch; actual: [%s]", popped));
            }
            CompositeAttributeFetch poppedAsCompositeAttributeFetch = (CompositeAttributeFetch)((Object)popped);
            if (!attributeDefinition.equals(poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition())) {
                throw new WalkingException(String.format("Mismatched CompositeAttributeFetch from stack on pop; expected fetch for attribute: [%s]; actual: [%s]", attributeDefinition, poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition()));
            }
        }
        log.tracef("%s Finishing up attribute : %s", (Object)StringHelper.repeat("<<", this.fetchSourceStack.size()), (Object)attributeDefinition);
        this.currentPropertyPath = this.currentPropertyPath.getParent();
    }

    @Override
    public boolean isDuplicateAssociationKey(AssociationKey associationKey) {
        return this.fetchedAssociationKeySourceMap.containsKey(associationKey);
    }

    @Override
    public void associationKeyRegistered(AssociationKey associationKey) {
        log.tracef("%s Registering AssociationKey : %s -> %s", (Object)StringHelper.repeat("..", this.fetchSourceStack.size()), (Object)associationKey, (Object)this.currentSource());
        this.fetchedAssociationKeySourceMap.put(associationKey, this.currentSource());
    }

    @Override
    public FetchSource registeredFetchSource(AssociationKey associationKey) {
        return this.fetchedAssociationKeySourceMap.get(associationKey);
    }

    @Override
    public void foundCircularAssociation(AssociationAttributeDefinition attributeDefinition) {
        ExpandingFetchSource currentSource;
        Joinable currentEntityPersister;
        AssociationKey currentEntityReferenceAssociationKey;
        FetchSource registeredFetchSource;
        FetchStrategy fetchStrategy = this.determineFetchStrategy(attributeDefinition);
        AssociationKey associationKey = attributeDefinition.getAssociationKey();
        if (attributeDefinition.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY && (registeredFetchSource = this.registeredFetchSource(associationKey)) != null && !associationKey.equals(currentEntityReferenceAssociationKey = new AssociationKey((currentEntityPersister = (Joinable)((Object)(currentSource = this.currentSource()).resolveEntityReference().getEntityPersister())).getTableName(), currentEntityPersister.getKeyColumnNames()))) {
            currentSource.buildBidirectionalEntityReference(attributeDefinition, fetchStrategy, registeredFetchSource.resolveEntityReference());
        }
    }

    @Override
    public void foundAny(AnyMappingDefinition anyDefinition) {
    }

    protected boolean handleCompositeAttribute(AttributeDefinition attributeDefinition) {
        CompositeAttributeFetch compositeFetch = this.currentSource().buildCompositeAttributeFetch(attributeDefinition);
        this.pushToStack((ExpandingFetchSource)((Object)compositeFetch));
        return true;
    }

    protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
        FetchStrategy fetchStrategy = this.determineFetchStrategy(attributeDefinition);
        ExpandingFetchSource currentSource = this.currentSource();
        currentSource.validateFetchPlan(fetchStrategy, attributeDefinition);
        AssociationAttributeDefinition.AssociationNature nature = attributeDefinition.getAssociationNature();
        if (nature == AssociationAttributeDefinition.AssociationNature.ANY) {
            currentSource.buildAnyAttributeFetch(attributeDefinition, fetchStrategy);
            return false;
        }
        if (nature == AssociationAttributeDefinition.AssociationNature.ENTITY) {
            EntityFetch fetch = currentSource.buildEntityAttributeFetch(attributeDefinition, fetchStrategy);
            if (FetchStrategyHelper.isJoinFetched(fetchStrategy)) {
                this.pushToStack((ExpandingFetchSource)((Object)fetch));
                return true;
            }
            return false;
        }
        CollectionAttributeFetch fetch = currentSource.buildCollectionAttributeFetch(attributeDefinition, fetchStrategy);
        if (FetchStrategyHelper.isJoinFetched(fetchStrategy)) {
            this.pushToCollectionStack(fetch);
            return true;
        }
        return false;
    }

    protected abstract FetchStrategy determineFetchStrategy(AssociationAttributeDefinition var1);

    protected int currentDepth() {
        return this.fetchSourceStack.size();
    }

    protected boolean isTooManyCollections() {
        return false;
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory();
    }

    public static class PropertyPathStack {
        private ArrayDeque<PropertyPath> pathStack = new ArrayDeque();

        public void push(PropertyPath path) {
            this.pathStack.addFirst(path);
            MDC.put((String)AbstractLoadPlanBuildingAssociationVisitationStrategy.MDC_KEY, (Object)this.extractFullPath(path));
        }

        private String extractFullPath(PropertyPath path) {
            return path == null ? "<no-path>" : path.getFullPath();
        }

        public void pop() {
            this.pathStack.removeFirst();
            PropertyPath newHead = this.pathStack.peekFirst();
            MDC.put((String)AbstractLoadPlanBuildingAssociationVisitationStrategy.MDC_KEY, (Object)this.extractFullPath(newHead));
        }
    }
}

