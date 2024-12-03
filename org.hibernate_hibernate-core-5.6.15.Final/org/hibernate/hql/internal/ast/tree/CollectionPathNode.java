/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.ComponentJoin;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class CollectionPathNode
extends SqlNode {
    private final FromElement ownerFromElement;
    private final CollectionPersister collectionDescriptor;
    private final String collectionPropertyName;
    private final String collectionPropertyPath;
    private final String collectionQueryPath;
    private final HqlSqlWalker walker;

    public CollectionPathNode(FromElement ownerFromElement, CollectionPersister collectionDescriptor, String collectionPropertyName, String collectionQueryPath, String collectionPropertyPath, HqlSqlWalker walker) {
        this.ownerFromElement = ownerFromElement;
        this.collectionDescriptor = collectionDescriptor;
        this.collectionPropertyName = collectionPropertyName;
        this.collectionQueryPath = collectionQueryPath;
        this.collectionPropertyPath = collectionPropertyPath;
        this.walker = walker;
        walker.addQuerySpaces(collectionDescriptor.getCollectionSpaces());
        super.setType(79);
        super.setDataType(collectionDescriptor.getCollectionType());
        super.setText(collectionDescriptor.getRole());
    }

    public static CollectionPathNode from(AST qualifier, AST reference, HqlSqlWalker walker) {
        String referenceMappedPath;
        CollectionType collectionType;
        String referenceName = reference.getText();
        if (qualifier == null) {
            String referenceQueryPath = referenceName;
            FromElement byAlias = walker.getCurrentFromClause().getFromElement(referenceName);
            if (byAlias != null) {
                FromElement ownerRef = byAlias.getOrigin();
                QueryableCollection collectionDescriptor = byAlias.getQueryableCollection();
                return new CollectionPathNode(ownerRef, collectionDescriptor, referenceName, referenceQueryPath, referenceName, walker);
            }
            List fromElements = walker.getCurrentFromClause().getExplicitFromElements();
            if (fromElements.size() == 1) {
                FromElement ownerRef = (FromElement)fromElements.get(0);
                PropertyMapping collectionPropertyMapping = ownerRef.getPropertyMapping(referenceName);
                if (!CollectionType.class.isInstance(collectionPropertyMapping.getType())) {
                    throw new QueryException("Could not resolve identifier `" + referenceName + "` as plural-attribute");
                }
                CollectionType collectionType2 = (CollectionType)collectionPropertyMapping.getType();
                return new CollectionPathNode(ownerRef, walker.getSessionFactoryHelper().requireQueryableCollection(collectionType2.getRole()), referenceName, referenceQueryPath, referenceName, walker);
            }
            FromElement discoveredQualifier = null;
            for (int i = 0; i < fromElements.size(); ++i) {
                FromElement fromElement = (FromElement)fromElements.get(i);
                try {
                    PropertyMapping propertyMapping = fromElement.getPropertyMapping(referenceName);
                    if (!CollectionType.class.isInstance(propertyMapping.getType())) {
                        throw new QueryException("Could not resolve identifier `" + referenceName + "` as plural-attribute");
                    }
                    discoveredQualifier = fromElement;
                    break;
                }
                catch (Exception propertyMapping) {
                    continue;
                }
            }
            if (discoveredQualifier == null) {
                throw new QueryException("Could not resolve identifier `" + referenceName + "` as plural-attribute");
            }
            FromElement ownerRef = discoveredQualifier;
            PropertyMapping collectionPropertyMapping = ownerRef.getPropertyMapping(referenceName);
            if (!CollectionType.class.isInstance(collectionPropertyMapping.getType())) {
                throw new QueryException("Could not resolve identifier `" + referenceName + "` as plural-attribute");
            }
            CollectionType collectionType3 = (CollectionType)collectionPropertyMapping.getType();
            return new CollectionPathNode(ownerRef, walker.getSessionFactoryHelper().requireQueryableCollection(collectionType3.getRole()), referenceName, referenceQueryPath, referenceName, walker);
        }
        FromReferenceNode qualifierFromReferenceNode = (FromReferenceNode)qualifier;
        String qualifierQueryPath = ((FromReferenceNode)qualifier).getPath();
        String referenceQueryPath = qualifierQueryPath + "." + reference;
        try {
            qualifierFromReferenceNode.resolve(false, false);
        }
        catch (SemanticException e) {
            throw new QueryException("Unable to resolve collection-path qualifier : " + qualifierQueryPath, (Exception)((Object)e));
        }
        Type qualifierType = qualifierFromReferenceNode.getDataType();
        FromElement ownerRef = ((FromReferenceNode)qualifier).getFromElement();
        if (qualifierType instanceof CompositeType) {
            CompositeType qualifierCompositeType = (CompositeType)qualifierType;
            int collectionPropertyIndex = qualifierCompositeType.getPropertyIndex(referenceName);
            collectionType = (CollectionType)qualifierCompositeType.getSubtypes()[collectionPropertyIndex];
            referenceMappedPath = ownerRef instanceof ComponentJoin ? ((ComponentJoin)ownerRef).getComponentPath() + "." + referenceName : qualifierQueryPath.substring(qualifierQueryPath.indexOf(".") + 1);
        } else if (qualifierType instanceof EntityType) {
            EntityType qualifierEntityType = (EntityType)qualifierType;
            String entityName = qualifierEntityType.getAssociatedEntityName();
            EntityPersister entityPersister = walker.getSessionFactoryHelper().findEntityPersisterByName(entityName);
            int propertyIndex = entityPersister.getEntityMetamodel().getPropertyIndex(referenceName);
            collectionType = (CollectionType)entityPersister.getPropertyTypes()[propertyIndex];
            referenceMappedPath = referenceName;
        } else {
            throw new QueryException("Unexpected collection-path reference qualifier type : " + qualifier);
        }
        return new CollectionPathNode(((FromReferenceNode)qualifier).getFromElement(), walker.getSessionFactoryHelper().requireQueryableCollection(collectionType.getRole()), referenceName, referenceQueryPath, referenceMappedPath, walker);
    }

    public FromElement getCollectionOwnerFromElement() {
        return this.ownerFromElement;
    }

    public CollectionPersister getCollectionDescriptor() {
        return this.collectionDescriptor;
    }

    public String getCollectionPropertyName() {
        return this.collectionPropertyName;
    }

    public String getCollectionPropertyPath() {
        return this.collectionPropertyPath;
    }

    public String getCollectionQueryPath() {
        return this.collectionQueryPath;
    }

    public String[] resolveOwnerKeyColumnExpressions() {
        AST ast = this.walker.getAST();
        String ownerTableAlias = ast instanceof DeleteStatement || ast instanceof UpdateStatement ? this.ownerFromElement.getTableName() : this.ownerFromElement.getTableAlias();
        String lhsPropertyName = this.collectionDescriptor.getCollectionType().getLHSPropertyName();
        if (lhsPropertyName == null) {
            return StringHelper.qualify(ownerTableAlias, ((Joinable)((Object)this.collectionDescriptor.getOwnerEntityPersister())).getKeyColumnNames());
        }
        return this.ownerFromElement.toColumns(ownerTableAlias, lhsPropertyName, true);
    }
}

