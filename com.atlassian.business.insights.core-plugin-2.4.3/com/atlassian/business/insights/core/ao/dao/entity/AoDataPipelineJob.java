/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.business.insights.core.ao.dao.entity;

import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import javax.annotation.Nullable;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="DATA_PIPELINE_JOB")
@Preload
public interface AoDataPipelineJob
extends Entity {
    public static final String TABLE_NAME = "DATA_PIPELINE_JOB";
    public static final String ID_COLUMN = "ID";
    public static final String CREATED_COLUMN = "CREATED";
    public static final String EXPORT_FROM_COLUMN = "EXPORT_FROM";
    public static final String UPDATED_COLUMN = "UPDATED";
    public static final String SCHEMA_VERSION_COLUMN = "SCHEMA_VERSION";
    public static final String STATUS_COLUMN = "STATUS";
    public static final String METADATA_COLUMN = "METADATA";
    public static final String EXPORTED_ENTITIES_COLUMN = "EXPORTED_ENTITIES";
    public static final String WRITTEN_ROWS_COLUMN = "WRITTEN_ROWS";
    public static final String EXPORT_FORCED_COLUMN = "EXPORT_FORCED";
    public static final String ERRORS_COLUMN = "ERRORS";
    public static final String WARNINGS_COLUMN = "WARNINGS";
    public static final String EXPORT_PATH_COLUMN = "EXPORT_PATH";
    public static final String OPTED_OUT_ENTITY_IDENTIFIERS = "OPTED_OUT_ENTITY_IDENTIFIERS";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public int getID();

    @NotNull
    @Accessor(value="CREATED")
    public Long getCreated();

    public void setCreated(Long var1);

    @NotNull
    @Accessor(value="EXPORT_FROM")
    public Long getExportFrom();

    @NotNull
    @Accessor(value="UPDATED")
    public Long getUpdated();

    public void setUpdated(Long var1);

    @NotNull
    @Accessor(value="STATUS")
    public ExportProgressStatus getStatus();

    @Indexed
    public void setStatus(ExportProgressStatus var1);

    @NotNull
    @Accessor(value="SCHEMA_VERSION")
    public int getSchemaVersion();

    public void setSchemaVersion(int var1);

    @Nullable
    @Accessor(value="METADATA")
    public String getMetadata();

    public void setMetadata(String var1);

    @Nullable
    @Accessor(value="EXPORTED_ENTITIES")
    public Integer getExportedEntities();

    public void setExportedEntities(Integer var1);

    @Nullable
    @Accessor(value="WRITTEN_ROWS")
    public Integer getWrittenRows();

    public void setWrittenRows(Integer var1);

    @Accessor(value="EXPORT_FORCED")
    public boolean isExportForced();

    public void setExportForced(boolean var1);

    @Nullable
    @Accessor(value="ERRORS")
    @StringLength(value=-1)
    public String getErrors();

    public void setErrors(String var1);

    @Nullable
    @Accessor(value="WARNINGS")
    @StringLength(value=-1)
    public String getWarnings();

    public void setWarnings(String var1);

    @Nullable
    @Accessor(value="EXPORT_PATH")
    @StringLength(value=-1)
    public String getRootExportPath();

    public void setRootExportPath(String var1);

    @Nullable
    @Accessor(value="OPTED_OUT_ENTITY_IDENTIFIERS")
    @StringLength(value=-1)
    public String getOptedOutEntityIdentifiers();

    public void setOptedOutEntityIdentifiers(String var1);
}

