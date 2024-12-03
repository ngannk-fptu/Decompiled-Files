/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterContainer;
import org.hibernate.hql.internal.ast.tree.TableReferenceNode;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.sql.JoinFragment;

public class SqlFragment
extends Node
implements ParameterContainer,
TableReferenceNode {
    private JoinFragment joinFragment;
    private FromElement fromElement;
    private String[] referencedTables;
    private List<ParameterSpecification> embeddedParameters;

    public void setJoinFragment(JoinFragment joinFragment) {
        this.joinFragment = joinFragment;
    }

    public boolean hasFilterCondition() {
        return this.joinFragment.hasFilterCondition();
    }

    public void setFromElement(FromElement fromElement) {
        this.fromElement = fromElement;
    }

    public FromElement getFromElement() {
        return this.fromElement;
    }

    @Override
    public void addEmbeddedParameter(ParameterSpecification specification) {
        if (this.embeddedParameters == null) {
            this.embeddedParameters = new ArrayList<ParameterSpecification>();
        }
        this.embeddedParameters.add(specification);
    }

    @Override
    public boolean hasEmbeddedParameters() {
        return this.embeddedParameters != null && !this.embeddedParameters.isEmpty();
    }

    @Override
    public ParameterSpecification[] getEmbeddedParameters() {
        return this.embeddedParameters.toArray(new ParameterSpecification[this.embeddedParameters.size()]);
    }

    @Override
    public String[] getReferencedTables() {
        return this.referencedTables;
    }

    public void setReferencedTables(String[] referencedTables) {
        this.referencedTables = referencedTables;
    }
}

