/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package net.java.ao.schema;

import java.util.Objects;
import net.java.ao.RawEntity;
import net.java.ao.schema.CanonicalClassNameTableNameConverter;
import net.java.ao.schema.Table;
import net.java.ao.schema.TableNameConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class TableAnnotationTableNameConverter
implements TableNameConverter {
    public static final Class<Table> TABLE_ANNOTATION = Table.class;
    private final TableNameConverter delegateTableNameConverter;
    private final CanonicalClassNameTableNameConverter postProcessingTableNameConverter;

    public TableAnnotationTableNameConverter(TableNameConverter delegateTableNameConverter) {
        this(delegateTableNameConverter, new IdentityTableNameConverter());
    }

    public TableAnnotationTableNameConverter(TableNameConverter delegateTableNameConverter, CanonicalClassNameTableNameConverter postProcessingTableNameConverter) {
        this.delegateTableNameConverter = Objects.requireNonNull(delegateTableNameConverter, "delegateTableNameConverter can't be null");
        this.postProcessingTableNameConverter = Objects.requireNonNull(postProcessingTableNameConverter, "postProcessingTableNameConverter can't be null");
    }

    @Override
    public String getName(Class<? extends RawEntity<?>> entityClass) {
        if (entityClass.isAnnotationPresent(TABLE_ANNOTATION)) {
            return this.postProcessingTableNameConverter.getName(this.validate(entityClass.getAnnotation(TABLE_ANNOTATION).value()));
        }
        return this.delegateTableNameConverter.getName(entityClass);
    }

    private String validate(String value) {
        Validate.validState((boolean)StringUtils.isNotEmpty((CharSequence)value), (String)"Value %s for table annotation is not valid.", (Object[])new Object[]{value});
        return value;
    }

    private static class IdentityTableNameConverter
    extends CanonicalClassNameTableNameConverter {
        private IdentityTableNameConverter() {
        }

        @Override
        protected String getName(String entityClassCanonicalName) {
            return entityClassCanonicalName;
        }
    }
}

