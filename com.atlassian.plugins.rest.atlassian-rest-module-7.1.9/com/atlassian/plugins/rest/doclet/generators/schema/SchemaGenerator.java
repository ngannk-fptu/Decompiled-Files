/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.plugins.rest.doclet.generators.schema.ModelClass;
import com.atlassian.plugins.rest.doclet.generators.schema.Property;
import com.atlassian.plugins.rest.doclet.generators.schema.RichClass;
import com.atlassian.plugins.rest.doclet.generators.schema.Schema;
import com.atlassian.rest.annotation.RestProperty;
import java.util.List;
import java.util.Set;

public class SchemaGenerator {
    private final RestProperty.Scope scope;
    private final Set<ModelClass> schemasWithDefinitions;
    private final ModelClass topLevelModel;

    private SchemaGenerator(RestProperty.Scope scope, RichClass topLevelClass) {
        this.scope = scope;
        this.topLevelModel = new ModelClass(topLevelClass, null);
        this.schemasWithDefinitions = this.topLevelModel.getSchemasReferencedMoreThanOnce(scope);
    }

    public static Schema generateSchema(RichClass modelClass, RestProperty.Scope scope) {
        return new SchemaGenerator(scope, modelClass).generateTopLevel();
    }

    private Schema generateTopLevel() {
        Schema.Builder topLevelSchema = this.generate(this.topLevelModel);
        String title = this.topLevelModel.getTopLevelTitle();
        if (title != null) {
            topLevelSchema.setId("https://docs.atlassian.com/jira/REST/schema/" + Schema.titleToId(title) + "#");
            topLevelSchema.setTitle(title);
        }
        for (ModelClass schemaThatNeedsDefinition : this.schemasWithDefinitions) {
            topLevelSchema.addDefinition(this.generate(schemaThatNeedsDefinition).build());
        }
        return topLevelSchema.build();
    }

    private Schema generateWithReferencing(ModelClass model) {
        if (this.schemasWithDefinitions.contains(model)) {
            return Schema.ref(model.getTitle());
        }
        return this.generate(model).build();
    }

    private Schema.Builder generate(ModelClass model) {
        List<ModelClass> subModels;
        Schema.Builder builder = Schema.builder();
        builder.setType(model.getType());
        builder.setTitle(model.getTitle());
        builder.setDescription(model.getDescription());
        if (model.isAbstract() && !(subModels = model.getSubModels()).isEmpty()) {
            subModels.forEach(subModel -> builder.addAnyOf(this.generateWithReferencing((ModelClass)subModel)));
            return builder;
        }
        model.getPatternedProperties().ifPresent(patternAndModel -> builder.addPatternProperty(patternAndModel.getPattern(), this.generateWithReferencing(patternAndModel.getValuesType())));
        model.getCollectionItemModel().ifPresent(modelClass -> builder.setItems(this.generateWithReferencing((ModelClass)modelClass)));
        for (Property property : model.getProperties(this.scope)) {
            builder.addProperty(property.name, this.generateWithReferencing(property.model));
            if (!property.required) continue;
            builder.addRequired(property.name);
        }
        if (model.getActualClass().isEnum()) {
            builder.setEnum(model.getActualClass());
        }
        return builder;
    }
}

