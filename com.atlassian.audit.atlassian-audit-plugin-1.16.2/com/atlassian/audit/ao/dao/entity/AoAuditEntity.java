/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.audit.ao.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="AUDIT_ENTITY")
@Preload
@Indexes(value={@Index(name="timestampAndId", methodNames={"getTimestamp", "getId"}), @Index(name="userIdAndTimestamp", methodNames={"getUserId", "getTimestamp"}), @Index(name="primaryResourceAndTimestamp", methodNames={"getPrimaryResourceId", "getPrimaryResourceType", "getTimestamp"}), @Index(name="secondaryResourceAndTimestamp", methodNames={"getSecondaryResourceId", "getSecondaryResourceType", "getTimestamp"}), @Index(name="thirdResourceAndTimestamp", methodNames={"getResourceId3", "getResourceType3", "getTimestamp"}), @Index(name="fourthResourceAndTimestamp", methodNames={"getResourceId4", "getResourceType4", "getTimestamp"}), @Index(name="fifthResourceAndTimestamp", methodNames={"getResourceId5", "getResourceType5", "getTimestamp"}), @Index(name="category", methodNames={"getCategory"}), @Index(name="action", methodNames={"getAction"})})
public interface AoAuditEntity
extends RawEntity<Integer> {
    public static final String TABLE_NAME = "AUDIT_ENTITY";
    public static final String ACTION_COLUMN = "ACTION";
    public static final String ACTION_TKEY_COLUMN = "ACTION_T_KEY";
    public static final String CATEGORY_COLUMN = "CATEGORY";
    public static final String CATEGORY_TKEY_COLUMN = "CATEGORY_T_KEY";
    public static final String LEVEL_COLUMN = "LEVEL";
    public static final String AREA_COLUMN = "AREA";
    public static final String TIMESTAMP_COLUMN = "ENTITY_TIMESTAMP";
    public static final String ID_COLUMN = "ID";
    public static final String METHOD_COLUMN = "METHOD";
    public static final String SOURCE_COLUMN = "SOURCE";
    public static final String USER_ID_COLUMN = "USER_ID";
    public static final String USER_NAME_COLUMN = "USER_NAME";
    public static final String USER_TYPE_COLUMN = "USER_TYPE";
    public static final String CHANGE_VALUES_COLUMN = "CHANGE_VALUES";
    public static final String ATTRIBUTES_COLUMN = "ATTRIBUTES";
    public static final String RESOURCES_COLUMN = "RESOURCES";
    public static final String SEARCH_STRING_COLUMN = "SEARCH_STRING";
    public static final String SYSTEM_COLUMN = "SYSTEM_INFO";
    public static final String NODE_COLUMN = "NODE";
    public static final String RESOURCE_ID_COLUMN_1 = "PRIMARY_RESOURCE_ID";
    public static final String RESOURCE_TYPE_COLUMN_1 = "PRIMARY_RESOURCE_TYPE";
    public static final String RESOURCE_ID_COLUMN_2 = "SECONDARY_RESOURCE_ID";
    public static final String RESOURCE_TYPE_COLUMN_2 = "SECONDARY_RESOURCE_TYPE";
    public static final String RESOURCE_ID_COLUMN_3 = "RESOURCE_ID_3";
    public static final String RESOURCE_TYPE_COLUMN_3 = "RESOURCE_TYPE_3";
    public static final String RESOURCE_ID_COLUMN_4 = "RESOURCE_ID_4";
    public static final String RESOURCE_TYPE_COLUMN_4 = "RESOURCE_TYPE_4";
    public static final String RESOURCE_ID_COLUMN_5 = "RESOURCE_ID_5";
    public static final String RESOURCE_TYPE_COLUMN_5 = "RESOURCE_TYPE_5";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public Long getId();

    @NotNull
    @Accessor(value="ACTION")
    public String getAction();

    @Accessor(value="ACTION_T_KEY")
    public String getActionI18nKey();

    @NotNull
    @Accessor(value="LEVEL")
    public String getLevel();

    @NotNull
    @Accessor(value="AREA")
    public String getArea();

    @Accessor(value="RESOURCES")
    @StringLength(value=-1)
    public String getResources();

    @Accessor(value="CHANGE_VALUES")
    @StringLength(value=-1)
    public String getChangedValues();

    @Accessor(value="ATTRIBUTES")
    @StringLength(value=-1)
    public String getAttributes();

    @Accessor(value="SOURCE")
    public String getSource();

    @NotNull
    @Accessor(value="ENTITY_TIMESTAMP")
    public Long getTimestamp();

    @Accessor(value="USER_ID")
    public String getUserId();

    @Accessor(value="USER_NAME")
    public String getUsername();

    @Accessor(value="USER_TYPE")
    public String getUserType();

    @Accessor(value="METHOD")
    public String getMethod();

    @Accessor(value="CATEGORY")
    public String getCategory();

    @Accessor(value="CATEGORY_T_KEY")
    public String getCategoryI18nKey();

    @Accessor(value="PRIMARY_RESOURCE_ID")
    public String getPrimaryResourceId();

    @Accessor(value="PRIMARY_RESOURCE_TYPE")
    public String getPrimaryResourceType();

    @Accessor(value="SECONDARY_RESOURCE_ID")
    public String getSecondaryResourceId();

    @Accessor(value="SECONDARY_RESOURCE_TYPE")
    public String getSecondaryResourceType();

    @Accessor(value="RESOURCE_ID_3")
    public String getResourceId3();

    @Accessor(value="RESOURCE_TYPE_3")
    public String getResourceType3();

    @Accessor(value="RESOURCE_ID_4")
    public String getResourceId4();

    @Accessor(value="RESOURCE_TYPE_4")
    public String getResourceType4();

    @Accessor(value="RESOURCE_ID_5")
    public String getResourceId5();

    @Accessor(value="RESOURCE_TYPE_5")
    public String getResourceType5();

    @Accessor(value="SEARCH_STRING")
    @StringLength(value=-1)
    public String getSearchString();

    @Accessor(value="SYSTEM_INFO")
    public String getSystem();

    @Accessor(value="NODE")
    public String getNode();
}

