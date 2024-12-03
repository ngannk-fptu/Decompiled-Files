/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAuxiliaryDatabaseObjectType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmClassRenameType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdentifierGeneratorDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmResultSetMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnionSubclassEntityType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"identifierGenerator", "typedef", "filterDef", "_import", "clazz", "subclass", "joinedSubclass", "unionSubclass", "resultset", "query", "sqlQuery", "fetchProfile", "databaseObject"})
@XmlRootElement(name="hibernate-mapping", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmHibernateMapping
extends JaxbHbmToolingHintContainer
implements Serializable {
    @XmlElement(name="identifier-generator", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmIdentifierGeneratorDefinitionType> identifierGenerator;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmTypeDefinitionType> typedef;
    @XmlElement(name="filter-def", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFilterDefinitionType> filterDef;
    @XmlElement(name="import", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmClassRenameType> _import;
    @XmlElement(name="class", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmRootEntityType> clazz;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmDiscriminatorSubclassEntityType> subclass;
    @XmlElement(name="joined-subclass", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmJoinedSubclassEntityType> joinedSubclass;
    @XmlElement(name="union-subclass", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmUnionSubclassEntityType> unionSubclass;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmResultSetMappingType> resultset;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedQueryType> query;
    @XmlElement(name="sql-query", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedNativeQueryType> sqlQuery;
    @XmlElement(name="fetch-profile", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFetchProfileType> fetchProfile;
    @XmlElement(name="database-object", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmAuxiliaryDatabaseObjectType> databaseObject;
    @XmlAttribute(name="auto-import")
    protected Boolean autoImport;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="default-access")
    protected String defaultAccess;
    @XmlAttribute(name="default-cascade")
    protected String defaultCascade;
    @XmlAttribute(name="default-lazy")
    protected Boolean defaultLazy;
    @XmlAttribute(name="package")
    protected String _package;
    @XmlAttribute(name="schema")
    protected String schema;

    public List<JaxbHbmIdentifierGeneratorDefinitionType> getIdentifierGenerator() {
        if (this.identifierGenerator == null) {
            this.identifierGenerator = new ArrayList<JaxbHbmIdentifierGeneratorDefinitionType>();
        }
        return this.identifierGenerator;
    }

    public List<JaxbHbmTypeDefinitionType> getTypedef() {
        if (this.typedef == null) {
            this.typedef = new ArrayList<JaxbHbmTypeDefinitionType>();
        }
        return this.typedef;
    }

    public List<JaxbHbmFilterDefinitionType> getFilterDef() {
        if (this.filterDef == null) {
            this.filterDef = new ArrayList<JaxbHbmFilterDefinitionType>();
        }
        return this.filterDef;
    }

    public List<JaxbHbmClassRenameType> getImport() {
        if (this._import == null) {
            this._import = new ArrayList<JaxbHbmClassRenameType>();
        }
        return this._import;
    }

    public List<JaxbHbmRootEntityType> getClazz() {
        if (this.clazz == null) {
            this.clazz = new ArrayList<JaxbHbmRootEntityType>();
        }
        return this.clazz;
    }

    public List<JaxbHbmDiscriminatorSubclassEntityType> getSubclass() {
        if (this.subclass == null) {
            this.subclass = new ArrayList<JaxbHbmDiscriminatorSubclassEntityType>();
        }
        return this.subclass;
    }

    public List<JaxbHbmJoinedSubclassEntityType> getJoinedSubclass() {
        if (this.joinedSubclass == null) {
            this.joinedSubclass = new ArrayList<JaxbHbmJoinedSubclassEntityType>();
        }
        return this.joinedSubclass;
    }

    public List<JaxbHbmUnionSubclassEntityType> getUnionSubclass() {
        if (this.unionSubclass == null) {
            this.unionSubclass = new ArrayList<JaxbHbmUnionSubclassEntityType>();
        }
        return this.unionSubclass;
    }

    public List<JaxbHbmResultSetMappingType> getResultset() {
        if (this.resultset == null) {
            this.resultset = new ArrayList<JaxbHbmResultSetMappingType>();
        }
        return this.resultset;
    }

    public List<JaxbHbmNamedQueryType> getQuery() {
        if (this.query == null) {
            this.query = new ArrayList<JaxbHbmNamedQueryType>();
        }
        return this.query;
    }

    public List<JaxbHbmNamedNativeQueryType> getSqlQuery() {
        if (this.sqlQuery == null) {
            this.sqlQuery = new ArrayList<JaxbHbmNamedNativeQueryType>();
        }
        return this.sqlQuery;
    }

    public List<JaxbHbmFetchProfileType> getFetchProfile() {
        if (this.fetchProfile == null) {
            this.fetchProfile = new ArrayList<JaxbHbmFetchProfileType>();
        }
        return this.fetchProfile;
    }

    public List<JaxbHbmAuxiliaryDatabaseObjectType> getDatabaseObject() {
        if (this.databaseObject == null) {
            this.databaseObject = new ArrayList<JaxbHbmAuxiliaryDatabaseObjectType>();
        }
        return this.databaseObject;
    }

    public boolean isAutoImport() {
        if (this.autoImport == null) {
            return true;
        }
        return this.autoImport;
    }

    public void setAutoImport(Boolean value) {
        this.autoImport = value;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String value) {
        this.catalog = value;
    }

    public String getDefaultAccess() {
        if (this.defaultAccess == null) {
            return "property";
        }
        return this.defaultAccess;
    }

    public void setDefaultAccess(String value) {
        this.defaultAccess = value;
    }

    public String getDefaultCascade() {
        if (this.defaultCascade == null) {
            return "none";
        }
        return this.defaultCascade;
    }

    public void setDefaultCascade(String value) {
        this.defaultCascade = value;
    }

    public boolean isDefaultLazy() {
        if (this.defaultLazy == null) {
            return true;
        }
        return this.defaultLazy;
    }

    public void setDefaultLazy(Boolean value) {
        this.defaultLazy = value;
    }

    public String getPackage() {
        return this._package;
    }

    public void setPackage(String value) {
        this._package = value;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String value) {
        this.schema = value;
    }
}

