/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.internal;

import java.util.Locale;
import java.util.TreeSet;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.model.naming.DatabaseIdentifier;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.jboss.logging.Logger;

public class NormalizingIdentifierHelperImpl
implements IdentifierHelper {
    private static final Logger log = Logger.getLogger(NormalizingIdentifierHelperImpl.class);
    private final JdbcEnvironment jdbcEnvironment;
    private final NameQualifierSupport nameQualifierSupport;
    private final boolean globallyQuoteIdentifiers;
    private final boolean globallyQuoteIdentifiersSkipColumnDefinitions;
    private final boolean autoQuoteKeywords;
    private final TreeSet<String> reservedWords;
    private final IdentifierCaseStrategy unquotedCaseStrategy;
    private final IdentifierCaseStrategy quotedCaseStrategy;

    public NormalizingIdentifierHelperImpl(JdbcEnvironment jdbcEnvironment, NameQualifierSupport nameQualifierSupport, boolean globallyQuoteIdentifiers, boolean globallyQuoteIdentifiersSkipColumnDefinitions, boolean autoQuoteKeywords, TreeSet<String> reservedWords, IdentifierCaseStrategy unquotedCaseStrategy, IdentifierCaseStrategy quotedCaseStrategy) {
        this.jdbcEnvironment = jdbcEnvironment;
        this.nameQualifierSupport = nameQualifierSupport;
        this.globallyQuoteIdentifiers = globallyQuoteIdentifiers;
        this.globallyQuoteIdentifiersSkipColumnDefinitions = globallyQuoteIdentifiersSkipColumnDefinitions;
        this.autoQuoteKeywords = autoQuoteKeywords;
        this.reservedWords = reservedWords;
        this.unquotedCaseStrategy = unquotedCaseStrategy == null ? IdentifierCaseStrategy.UPPER : unquotedCaseStrategy;
        this.quotedCaseStrategy = quotedCaseStrategy == null ? IdentifierCaseStrategy.MIXED : quotedCaseStrategy;
    }

    @Override
    public Identifier normalizeQuoting(Identifier identifier) {
        log.tracef("Normalizing identifier quoting [%s]", (Object)identifier);
        if (identifier == null) {
            return null;
        }
        if (identifier.isQuoted()) {
            return identifier;
        }
        if (this.globallyQuoteIdentifiers) {
            log.tracef("Forcing identifier [%s] to quoted for global quoting", (Object)identifier);
            return Identifier.toIdentifier(identifier.getText(), true);
        }
        if (this.autoQuoteKeywords && this.isReservedWord(identifier.getText())) {
            log.tracef("Forcing identifier [%s] to quoted as recognized reserved word", (Object)identifier);
            return Identifier.toIdentifier(identifier.getText(), true);
        }
        return identifier;
    }

    @Override
    public Identifier toIdentifier(String text) {
        return this.normalizeQuoting(Identifier.toIdentifier(text));
    }

    @Override
    public Identifier toIdentifier(String text, boolean quoted) {
        return this.normalizeQuoting(Identifier.toIdentifier(text, quoted));
    }

    @Override
    public Identifier applyGlobalQuoting(String text) {
        return Identifier.toIdentifier(text, this.globallyQuoteIdentifiers && !this.globallyQuoteIdentifiersSkipColumnDefinitions);
    }

    @Override
    public boolean isReservedWord(String word) {
        if (!this.autoQuoteKeywords) {
            throw new AssertionFailure("The reserved keywords map is only initialized if autoQuoteKeywords is true");
        }
        return this.reservedWords.contains(word);
    }

    @Override
    public String toMetaDataCatalogName(Identifier identifier) {
        log.tracef("Normalizing identifier quoting for catalog name [%s]", (Object)identifier);
        if (!this.nameQualifierSupport.supportsCatalogs()) {
            log.trace((Object)"Environment does not support catalogs; returning null");
            return null;
        }
        if (identifier == null) {
            if (this.jdbcEnvironment.getCurrentCatalog() == null) {
                return "";
            }
            identifier = this.jdbcEnvironment.getCurrentCatalog();
        }
        return this.toMetaDataText(identifier);
    }

    private String toMetaDataText(Identifier identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier cannot be null; bad usage");
        }
        if (identifier instanceof DatabaseIdentifier) {
            return identifier.getText();
        }
        if (identifier.isQuoted()) {
            switch (this.quotedCaseStrategy) {
                case UPPER: {
                    log.tracef("Rendering quoted identifier [%s] in upper case for use in DatabaseMetaData", (Object)identifier);
                    return identifier.getText().toUpperCase(Locale.ROOT);
                }
                case LOWER: {
                    log.tracef("Rendering quoted identifier [%s] in lower case for use in DatabaseMetaData", (Object)identifier);
                    return identifier.getText().toLowerCase(Locale.ROOT);
                }
            }
            log.tracef("Rendering quoted identifier [%s] in mixed case for use in DatabaseMetaData", (Object)identifier);
            return identifier.getText();
        }
        switch (this.unquotedCaseStrategy) {
            case MIXED: {
                log.tracef("Rendering unquoted identifier [%s] in mixed case for use in DatabaseMetaData", (Object)identifier);
                return identifier.getText();
            }
            case LOWER: {
                log.tracef("Rendering unquoted identifier [%s] in lower case for use in DatabaseMetaData", (Object)identifier);
                return identifier.getText().toLowerCase(Locale.ROOT);
            }
        }
        log.tracef("Rendering unquoted identifier [%s] in upper case for use in DatabaseMetaData", (Object)identifier);
        return identifier.getText().toUpperCase(Locale.ROOT);
    }

    @Override
    public String toMetaDataSchemaName(Identifier identifier) {
        log.tracef("Normalizing identifier quoting for schema name [%s]", (Object)identifier);
        if (!this.nameQualifierSupport.supportsSchemas()) {
            log.trace((Object)"Environment does not support catalogs; returning null");
            return null;
        }
        if (identifier == null) {
            if (this.jdbcEnvironment.getCurrentSchema() == null) {
                return "";
            }
            identifier = this.jdbcEnvironment.getCurrentSchema();
        }
        return this.toMetaDataText(identifier);
    }

    @Override
    public String toMetaDataObjectName(Identifier identifier) {
        log.tracef("Normalizing identifier quoting for object name [%s]", (Object)identifier);
        if (identifier == null) {
            throw new IllegalArgumentException("null was passed as an object name");
        }
        return this.toMetaDataText(identifier);
    }
}

