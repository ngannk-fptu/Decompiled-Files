/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeGroupExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.GroupDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class XMLSchemaSchema
implements Serializable {
    public static final String XMLSchemaInstanceNamespace = "http://www.w3.org/2001/XMLSchema-instance";
    public final String targetNamespace;
    public final ExpressionPool pool;
    public Expression topLevel;
    public final SimpleTypeContainer simpleTypes = new SimpleTypeContainer();
    public final ComplexTypeContainer complexTypes = new ComplexTypeContainer();
    public final AttributeGroupContainer attributeGroups = new AttributeGroupContainer();
    public final AttributeDeclContainer attributeDecls = new AttributeDeclContainer();
    public final ElementDeclContainer elementDecls = new ElementDeclContainer();
    public final GroupDeclContainer groupDecls = new GroupDeclContainer();
    public final IdentityConstraintContainer identityConstraints = new IdentityConstraintContainer();
    private static final long serialVersionUID = 1L;

    public XMLSchemaSchema(String targetNamespace, XMLSchemaGrammar parent) {
        this.pool = parent.pool;
        this.targetNamespace = targetNamespace;
        parent.schemata.put(targetNamespace, this);
    }

    public final class IdentityConstraintContainer
    implements Serializable {
        private final Map storage = new HashMap();

        public IdentityConstraint get(String name) {
            return (IdentityConstraint)this.storage.get(name);
        }

        public void add(String name, IdentityConstraint idc) {
            this.storage.put(name, idc);
        }
    }

    public final class GroupDeclContainer
    extends ReferenceContainer {
        public GroupDeclExp getOrCreate(String name) {
            return (GroupDeclExp)super._getOrCreate(name);
        }

        public GroupDeclExp get(String name) {
            return (GroupDeclExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new GroupDeclExp(name);
        }
    }

    public final class ElementDeclContainer
    extends ReferenceContainer {
        public ElementDeclExp getOrCreate(String name) {
            return (ElementDeclExp)super._getOrCreate(name);
        }

        public ElementDeclExp get(String name) {
            return (ElementDeclExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new ElementDeclExp(XMLSchemaSchema.this, name);
        }
    }

    public final class AttributeDeclContainer
    extends ReferenceContainer {
        public AttributeDeclExp getOrCreate(String name) {
            return (AttributeDeclExp)super._getOrCreate(name);
        }

        public AttributeDeclExp get(String name) {
            return (AttributeDeclExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new AttributeDeclExp(name);
        }
    }

    public final class AttributeGroupContainer
    extends ReferenceContainer {
        public AttributeGroupExp getOrCreate(String name) {
            return (AttributeGroupExp)super._getOrCreate(name);
        }

        public AttributeGroupExp get(String name) {
            return (AttributeGroupExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new AttributeGroupExp(name);
        }
    }

    public final class ComplexTypeContainer
    extends ReferenceContainer {
        public ComplexTypeExp getOrCreate(String name) {
            return (ComplexTypeExp)super._getOrCreate(name);
        }

        public ComplexTypeExp get(String name) {
            return (ComplexTypeExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new ComplexTypeExp(XMLSchemaSchema.this, name);
        }
    }

    public final class SimpleTypeContainer
    extends ReferenceContainer {
        public SimpleTypeExp getOrCreate(String name) {
            return (SimpleTypeExp)super._getOrCreate(name);
        }

        public SimpleTypeExp get(String name) {
            return (SimpleTypeExp)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new SimpleTypeExp(name);
        }
    }
}

