/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.SimpleValue;

public class IdGeneratorResolverSecondPass
implements SecondPass {
    private SimpleValue id;
    private XProperty idXProperty;
    private String generatorType;
    private String generatorName;
    private MetadataBuildingContext buildingContext;
    private IdentifierGeneratorDefinition localIdentifierGeneratorDefinition;

    public IdGeneratorResolverSecondPass(SimpleValue id, XProperty idXProperty, String generatorType, String generatorName, MetadataBuildingContext buildingContext) {
        this.id = id;
        this.idXProperty = idXProperty;
        this.generatorType = generatorType;
        this.generatorName = generatorName;
        this.buildingContext = buildingContext;
    }

    public IdGeneratorResolverSecondPass(SimpleValue id, XProperty idXProperty, String generatorType, String generatorName, MetadataBuildingContext buildingContext, IdentifierGeneratorDefinition localIdentifierGeneratorDefinition) {
        this(id, idXProperty, generatorType, generatorName, buildingContext);
        this.localIdentifierGeneratorDefinition = localIdentifierGeneratorDefinition;
    }

    @Override
    public void doSecondPass(Map idGeneratorDefinitionMap) throws MappingException {
        BinderHelper.makeIdGenerator(this.id, this.idXProperty, this.generatorType, this.generatorName, this.buildingContext, this.localIdentifierGeneratorDefinition);
    }
}

