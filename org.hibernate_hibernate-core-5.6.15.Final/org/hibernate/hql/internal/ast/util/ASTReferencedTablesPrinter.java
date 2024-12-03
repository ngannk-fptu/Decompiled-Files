/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.IdentNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.hql.internal.ast.util.ASTPrinter;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;

public class ASTReferencedTablesPrinter
extends ASTPrinter {
    public ASTReferencedTablesPrinter(Class tokenTypeConstants) {
        super(tokenTypeConstants);
    }

    @Override
    public String nodeToString(AST ast) {
        if (ast == null) {
            return "{node:null}";
        }
        return ast.getClass().getSimpleName();
    }

    @Override
    public LinkedHashMap<String, Object> createNodeProperties(AST node) {
        LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
        if (node instanceof FromReferenceNode) {
            FromReferenceNode frn = (FromReferenceNode)node;
            FromElement fromElement = frn.getFromElement();
            EntityPersister entityPersister = fromElement != null ? fromElement.getEntityPersister() : null;
            String entityPersisterStr = entityPersister != null ? entityPersister.toString() : null;
            props.put("persister", entityPersisterStr);
        }
        if (node instanceof DotNode) {
            DotNode dn = (DotNode)node;
            props.put("path", dn.getPath());
        }
        if (node instanceof IdentNode) {
            IdentNode in = (IdentNode)node;
            props.put("originalText", in.getOriginalText());
        }
        if (node instanceof SelectClause) {
            SelectClause sc = (SelectClause)node;
            for (Object element : sc.getFromElementsForLoad()) {
                FromElement fromElement = (FromElement)element;
                EntityPersister entityPersister = fromElement.getEntityPersister();
                if (entityPersister == null || !(entityPersister instanceof AbstractEntityPersister)) continue;
                AbstractEntityPersister aep = (AbstractEntityPersister)entityPersister;
                String entityClass = aep.getMappedClass().getSimpleName();
                String tables = Arrays.toString(aep.getTableNames());
                props.put(String.format("referencedTables(entity %s)", entityClass), tables);
            }
        }
        return props;
    }
}

