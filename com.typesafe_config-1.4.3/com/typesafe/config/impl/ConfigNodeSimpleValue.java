/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigReference;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.PathParser;
import com.typesafe.config.impl.SubstitutionExpression;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class ConfigNodeSimpleValue
extends AbstractConfigNodeValue {
    final Token token;

    ConfigNodeSimpleValue(Token value) {
        this.token = value;
    }

    @Override
    protected Collection<Token> tokens() {
        return Collections.singletonList(this.token);
    }

    protected Token token() {
        return this.token;
    }

    protected AbstractConfigValue value() {
        if (Tokens.isValue(this.token)) {
            return Tokens.getValue(this.token);
        }
        if (Tokens.isUnquotedText(this.token)) {
            return new ConfigString.Unquoted(this.token.origin(), Tokens.getUnquotedText(this.token));
        }
        if (Tokens.isSubstitution(this.token)) {
            List<Token> expression = Tokens.getSubstitutionPathExpression(this.token);
            Path path = PathParser.parsePathExpression(expression.iterator(), this.token.origin());
            boolean optional = Tokens.getSubstitutionOptional(this.token);
            return new ConfigReference(this.token.origin(), new SubstitutionExpression(path, optional));
        }
        throw new ConfigException.BugOrBroken("ConfigNodeSimpleValue did not contain a valid value token");
    }
}

