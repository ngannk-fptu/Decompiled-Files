/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.mapping.BasicKeyValuePersistentEntity
 *  org.springframework.data.keyvalue.core.mapping.KeySpaceResolver
 *  org.springframework.data.util.TypeInformation
 */
package org.springframework.vault.repository.mapping;

import org.springframework.data.keyvalue.core.mapping.BasicKeyValuePersistentEntity;
import org.springframework.data.keyvalue.core.mapping.KeySpaceResolver;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.vault.repository.mapping.Secret;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

public class BasicVaultPersistentEntity<T>
extends BasicKeyValuePersistentEntity<T, VaultPersistentProperty>
implements VaultPersistentEntity<T> {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private final String backend;
    @Nullable
    private final Expression backendExpression;

    public BasicVaultPersistentEntity(TypeInformation<T> information, KeySpaceResolver fallbackKeySpaceResolver) {
        super(information, fallbackKeySpaceResolver);
        Secret annotation = (Secret)this.findAnnotation(Secret.class);
        if (annotation != null && StringUtils.hasText(annotation.backend())) {
            this.backend = annotation.backend();
            this.backendExpression = BasicVaultPersistentEntity.detectExpression(this.backend);
        } else {
            this.backend = "secret";
            this.backendExpression = null;
        }
    }

    @Nullable
    private static Expression detectExpression(String potentialExpression) {
        Expression expression = PARSER.parseExpression(potentialExpression, ParserContext.TEMPLATE_EXPRESSION);
        return expression instanceof LiteralExpression ? null : expression;
    }

    public String getKeySpace() {
        return String.format("%s/%s", this.getSecretBackend(), super.getKeySpace());
    }

    @Override
    public String getSecretBackend() {
        return this.backendExpression == null ? this.backend : this.backendExpression.getValue(this.getEvaluationContext(null), String.class);
    }
}

