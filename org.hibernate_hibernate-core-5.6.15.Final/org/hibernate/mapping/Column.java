/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.sql.Template;
import org.hibernate.type.Type;

public class Column
implements Selectable,
Serializable,
Cloneable {
    public static final int DEFAULT_LENGTH = 255;
    public static final int DEFAULT_PRECISION = 19;
    public static final int DEFAULT_SCALE = 2;
    private int length = 255;
    private int precision = 19;
    private int scale = 2;
    private Value value;
    private int typeIndex;
    private String name;
    private boolean nullable = true;
    private boolean unique;
    private String sqlType;
    private Integer sqlTypeCode;
    private boolean quoted;
    int uniqueInteger;
    private String checkConstraint;
    private String comment;
    private String defaultValue;
    private String customWrite;
    private String customRead;

    public Column() {
    }

    public Column(String columnName) {
        this.setName(columnName);
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (StringHelper.isNotEmpty(name) && "`\"[".indexOf(name.charAt(0)) > -1) {
            this.quoted = true;
            this.name = name.substring(1, name.length() - 1);
        } else {
            this.name = name;
        }
    }

    public String getQuotedName() {
        return StringHelper.safeInterning(this.quoted ? "`" + this.name + "`" : this.name);
    }

    public String getQuotedName(Dialect d) {
        return StringHelper.safeInterning(this.quoted ? d.openQuote() + this.name + d.closeQuote() : this.name);
    }

    @Override
    public String getAlias(Dialect dialect) {
        boolean useRawName;
        int lastLetter = StringHelper.lastIndexOfLetter(this.name);
        String suffix = AliasConstantsHelper.get(this.uniqueInteger);
        String alias = this.name.toLowerCase(Locale.ROOT);
        if (lastLetter == -1) {
            alias = "column";
        } else if (alias.length() > lastLetter + 1) {
            alias = alias.substring(0, lastLetter + 1);
        }
        boolean bl = useRawName = this.name.length() + suffix.length() <= dialect.getMaxAliasLength() && !this.quoted && !this.name.toLowerCase(Locale.ROOT).equals("rowid");
        if (!useRawName) {
            if (suffix.length() >= dialect.getMaxAliasLength()) {
                throw new MappingException(String.format("Unique suffix [%s] length must be less than maximum [%d]", suffix, dialect.getMaxAliasLength()));
            }
            if (alias.length() + suffix.length() > dialect.getMaxAliasLength()) {
                alias = alias.substring(0, dialect.getMaxAliasLength() - suffix.length());
            }
        }
        return alias + suffix;
    }

    @Override
    public String getAlias(Dialect dialect, Table table) {
        return StringHelper.safeInterning(this.getAlias(dialect) + AliasConstantsHelper.get(table.getUniqueInteger()));
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getTypeIndex() {
        return this.typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public int hashCode() {
        return this.isQuoted() ? this.name.hashCode() : this.name.toLowerCase(Locale.ROOT).hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof Column && this.equals((Column)object);
    }

    public boolean equals(Column column) {
        if (null == column) {
            return false;
        }
        if (this == column) {
            return true;
        }
        return this.isQuoted() ? this.name.equals(column.name) : this.name.equalsIgnoreCase(column.name);
    }

    public int getSqlTypeCode(Mapping mapping) throws MappingException {
        Type type = this.getValue().getType();
        try {
            int sqlTypeCode = type.sqlTypes(mapping)[this.getTypeIndex()];
            if (this.getSqlTypeCode() != null && this.getSqlTypeCode() != sqlTypeCode) {
                throw new MappingException("SQLType code's does not match. mapped as " + sqlTypeCode + " but is " + this.getSqlTypeCode());
            }
            return sqlTypeCode;
        }
        catch (Exception e) {
            throw new MappingException("Could not determine type for column " + this.name + " of type " + type.getClass().getName() + ": " + e.getClass().getName(), e);
        }
    }

    public Integer getSqlTypeCode() {
        return this.sqlTypeCode;
    }

    public void setSqlTypeCode(Integer typeCode) {
        this.sqlTypeCode = typeCode;
    }

    public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
        if (this.sqlType == null) {
            this.sqlType = dialect.getTypeName(this.getSqlTypeCode(mapping), this.getLength(), this.getPrecision(), this.getScale());
        }
        return this.sqlType;
    }

    public String getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isQuoted() {
        return this.quoted;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getName() + ')';
    }

    public String getCheckConstraint() {
        return this.checkConstraint;
    }

    public void setCheckConstraint(String checkConstraint) {
        this.checkConstraint = checkConstraint;
    }

    public boolean hasCheckConstraint() {
        return this.checkConstraint != null;
    }

    @Override
    public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
        return StringHelper.safeInterning(this.hasCustomRead() ? Template.renderTransformerReadFragment(this.customRead, this.getQuotedName(dialect)) : "$PlaceHolder$." + this.getQuotedName(dialect));
    }

    public boolean hasCustomRead() {
        return this.customRead != null;
    }

    public String getReadExpr(Dialect dialect) {
        return this.hasCustomRead() ? this.customRead : this.getQuotedName(dialect);
    }

    public String getWriteExpr() {
        return this.customWrite != null && this.customWrite.length() > 0 ? this.customWrite : "?";
    }

    @Override
    public boolean isFormula() {
        return false;
    }

    @Override
    public String getText(Dialect d) {
        return this.getQuotedName(d);
    }

    @Override
    public String getText() {
        return this.getName();
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int scale) {
        this.precision = scale;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCustomWrite() {
        return this.customWrite;
    }

    public void setCustomWrite(String customWrite) {
        this.customWrite = StringHelper.safeInterning(customWrite);
    }

    public String getCustomRead() {
        return this.customRead;
    }

    public void setCustomRead(String customRead) {
        this.customRead = StringHelper.safeInterning(StringHelper.nullIfEmpty(customRead));
    }

    public String getCanonicalName() {
        return this.quoted ? this.name : this.name.toLowerCase(Locale.ROOT);
    }

    public Column clone() {
        Column copy = new Column();
        copy.setLength(this.length);
        copy.setScale(this.scale);
        copy.setValue(this.value);
        copy.setTypeIndex(this.typeIndex);
        copy.setName(this.getQuotedName());
        copy.setNullable(this.nullable);
        copy.setPrecision(this.precision);
        copy.setUnique(this.unique);
        copy.setSqlType(this.sqlType);
        copy.setSqlTypeCode(this.sqlTypeCode);
        copy.uniqueInteger = this.uniqueInteger;
        copy.setCheckConstraint(this.checkConstraint);
        copy.setComment(this.comment);
        copy.setDefaultValue(this.defaultValue);
        copy.setCustomRead(this.customRead);
        copy.setCustomWrite(this.customWrite);
        return copy;
    }
}

