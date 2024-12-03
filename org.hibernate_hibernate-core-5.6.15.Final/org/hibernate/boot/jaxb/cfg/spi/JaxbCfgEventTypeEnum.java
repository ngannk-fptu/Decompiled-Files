/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="EventTypeEnum", namespace="http://www.hibernate.org/xsd/orm/cfg")
@XmlEnum
public enum JaxbCfgEventTypeEnum {
    AUTO_FLUSH("auto-flush"),
    CREATE("create"),
    CREATE_ONFLUSH("create-onflush"),
    DELETE("delete"),
    DIRTY_CHECK("dirty-check"),
    EVICT("evict"),
    FLUSH("flush"),
    FLUSH_ENTITY("flush-entity"),
    LOAD("load"),
    LOAD_COLLECTION("load-collection"),
    LOCK("lock"),
    MERGE("merge"),
    POST_COLLECTION_RECREATE("post-collection-recreate"),
    POST_COLLECTION_REMOVE("post-collection-remove"),
    POST_COLLECTION_UPDATE("post-collection-update"),
    POST_COMMIT_DELETE("post-commit-delete"),
    POST_COMMIT_INSERT("post-commit-insert"),
    POST_COMMIT_UPDATE("post-commit-update"),
    POST_DELETE("post-delete"),
    POST_INSERT("post-insert"),
    POST_LOAD("post-load"),
    POST_UPDATE("post-update"),
    PRE_COLLECTION_RECREATE("pre-collection-recreate"),
    PRE_COLLECTION_REMOVE("pre-collection-remove"),
    PRE_COLLECTION_UPDATE("pre-collection-update"),
    PRE_DELETE("pre-delete"),
    PRE_INSERT("pre-insert"),
    PRE_LOAD("pre-load"),
    PRE_UPDATE("pre-update"),
    REFRESH("refresh"),
    REPLICATE("replicate"),
    SAVE("save"),
    SAVE_UPDATE("save-update"),
    UPDATE("update");

    private final String value;

    private JaxbCfgEventTypeEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbCfgEventTypeEnum fromValue(String v) {
        for (JaxbCfgEventTypeEnum c : JaxbCfgEventTypeEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

