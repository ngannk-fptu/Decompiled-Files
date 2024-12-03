/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.spi;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.engine.jdbc.env.internal.NormalizingIdentifierHelperImpl;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.jboss.logging.Logger;

public class IdentifierHelperBuilder {
    private static final Logger log = Logger.getLogger(IdentifierHelperBuilder.class);
    private final JdbcEnvironment jdbcEnvironment;
    private NameQualifierSupport nameQualifierSupport = NameQualifierSupport.BOTH;
    private final TreeSet<String> reservedWords = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    private boolean globallyQuoteIdentifiers = false;
    private boolean skipGlobalQuotingForColumnDefinitions = false;
    private boolean autoQuoteKeywords = true;
    private IdentifierCaseStrategy unquotedCaseStrategy = IdentifierCaseStrategy.UPPER;
    private IdentifierCaseStrategy quotedCaseStrategy = IdentifierCaseStrategy.MIXED;

    public static IdentifierHelperBuilder from(JdbcEnvironment jdbcEnvironment) {
        return new IdentifierHelperBuilder(jdbcEnvironment);
    }

    private IdentifierHelperBuilder(JdbcEnvironment jdbcEnvironment) {
        this.jdbcEnvironment = jdbcEnvironment;
    }

    public void applyReservedWords(DatabaseMetaData metaData) throws SQLException {
        if (metaData == null) {
            return;
        }
        if (!this.autoQuoteKeywords) {
            return;
        }
        this.reservedWords.addAll(IdentifierHelperBuilder.parseKeywords(metaData.getSQLKeywords()));
    }

    private static List<String> parseKeywords(String extraKeywordsString) {
        return StringHelper.parseCommaSeparatedString(extraKeywordsString);
    }

    public void applyIdentifierCasing(DatabaseMetaData metaData) throws SQLException {
        if (metaData == null) {
            return;
        }
        int unquotedAffirmatives = ArrayHelper.countTrue(metaData.storesLowerCaseIdentifiers(), metaData.storesUpperCaseIdentifiers(), metaData.storesMixedCaseIdentifiers());
        if (unquotedAffirmatives == 0) {
            log.debug((Object)"JDBC driver metadata reported database stores unquoted identifiers in neither upper, lower nor mixed case");
        } else {
            if (unquotedAffirmatives > 1) {
                log.debug((Object)"JDBC driver metadata reported database stores unquoted identifiers in more than one case");
            }
            this.unquotedCaseStrategy = metaData.storesUpperCaseIdentifiers() ? IdentifierCaseStrategy.UPPER : (metaData.storesLowerCaseIdentifiers() ? IdentifierCaseStrategy.LOWER : IdentifierCaseStrategy.MIXED);
        }
        int quotedAffirmatives = ArrayHelper.countTrue(metaData.storesLowerCaseQuotedIdentifiers(), metaData.storesUpperCaseQuotedIdentifiers(), metaData.storesMixedCaseQuotedIdentifiers());
        if (quotedAffirmatives == 0) {
            log.debug((Object)"JDBC driver metadata reported database stores quoted identifiers in neither upper, lower nor mixed case");
        } else {
            if (quotedAffirmatives > 1) {
                log.debug((Object)"JDBC driver metadata reported database stores quoted identifiers in more than one case");
            }
            this.quotedCaseStrategy = metaData.storesMixedCaseQuotedIdentifiers() ? IdentifierCaseStrategy.MIXED : (metaData.storesLowerCaseQuotedIdentifiers() ? IdentifierCaseStrategy.LOWER : IdentifierCaseStrategy.UPPER);
        }
    }

    public boolean isGloballyQuoteIdentifiers() {
        return this.globallyQuoteIdentifiers;
    }

    public void setGloballyQuoteIdentifiers(boolean globallyQuoteIdentifiers) {
        this.globallyQuoteIdentifiers = globallyQuoteIdentifiers;
    }

    public boolean isSkipGlobalQuotingForColumnDefinitions() {
        return this.skipGlobalQuotingForColumnDefinitions;
    }

    public void setSkipGlobalQuotingForColumnDefinitions(boolean skipGlobalQuotingForColumnDefinitions) {
        this.skipGlobalQuotingForColumnDefinitions = skipGlobalQuotingForColumnDefinitions;
    }

    public void setAutoQuoteKeywords(boolean autoQuoteKeywords) {
        this.autoQuoteKeywords = autoQuoteKeywords;
    }

    public NameQualifierSupport getNameQualifierSupport() {
        return this.nameQualifierSupport;
    }

    public void setNameQualifierSupport(NameQualifierSupport nameQualifierSupport) {
        this.nameQualifierSupport = nameQualifierSupport == null ? NameQualifierSupport.BOTH : nameQualifierSupport;
    }

    public IdentifierCaseStrategy getUnquotedCaseStrategy() {
        return this.unquotedCaseStrategy;
    }

    public void setUnquotedCaseStrategy(IdentifierCaseStrategy unquotedCaseStrategy) {
        this.unquotedCaseStrategy = unquotedCaseStrategy;
    }

    public IdentifierCaseStrategy getQuotedCaseStrategy() {
        return this.quotedCaseStrategy;
    }

    public void setQuotedCaseStrategy(IdentifierCaseStrategy quotedCaseStrategy) {
        this.quotedCaseStrategy = quotedCaseStrategy;
    }

    public void clearReservedWords() {
        this.reservedWords.clear();
    }

    public void applyReservedWords(Set<String> words) {
        if (!this.autoQuoteKeywords) {
            return;
        }
        this.reservedWords.addAll(words);
    }

    public void setReservedWords(Set<String> words) {
        this.clearReservedWords();
        this.applyReservedWords(words);
    }

    public IdentifierHelper build() {
        if (this.unquotedCaseStrategy == this.quotedCaseStrategy) {
            log.debugf("IdentifierCaseStrategy for both quoted and unquoted identifiers was set to the same strategy [%s]; that will likely lead to problems in schema update and validation if using quoted identifiers", (Object)this.unquotedCaseStrategy.name());
        }
        return new NormalizingIdentifierHelperImpl(this.jdbcEnvironment, this.nameQualifierSupport, this.globallyQuoteIdentifiers, this.skipGlobalQuotingForColumnDefinitions, this.autoQuoteKeywords, this.reservedWords, this.unquotedCaseStrategy, this.quotedCaseStrategy);
    }
}

