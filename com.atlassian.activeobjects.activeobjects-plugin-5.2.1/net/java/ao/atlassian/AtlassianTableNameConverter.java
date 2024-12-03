/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.RawEntity;
import net.java.ao.atlassian.ConverterUtils;
import net.java.ao.atlassian.PrefixedTableNameConverter;
import net.java.ao.atlassian.TablePrefix;
import net.java.ao.schema.Case;
import net.java.ao.schema.TableAnnotationTableNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.UnderscoreTableNameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AtlassianTableNameConverter
implements TableNameConverter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TableNameConverter tableNameConverter;

    public AtlassianTableNameConverter(TablePrefix prefix) {
        UnderscoreTableNameConverter baseConverter = new UnderscoreTableNameConverter(Case.UPPER);
        this.tableNameConverter = new PrefixedTableNameConverter(Objects.requireNonNull(prefix, "prefix can't be null"), new TableAnnotationTableNameConverter(baseConverter, baseConverter));
    }

    @Override
    public String getName(Class<? extends RawEntity<?>> entityClass) {
        String name = this.tableNameConverter.getName(entityClass);
        ConverterUtils.checkLength(name, "Invalid entity, generated table name (" + name + ") for '" + entityClass.getName() + "' is too long! It should be no longer than " + 30 + " chars.");
        this.logger.debug("Table name for '{}' is '{}'", (Object)entityClass.getName(), (Object)name);
        return name;
    }
}

