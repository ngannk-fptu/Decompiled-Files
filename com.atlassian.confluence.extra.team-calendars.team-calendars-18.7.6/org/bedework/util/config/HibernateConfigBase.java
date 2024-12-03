/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.util.List;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.config.HibernateConfigI;
import org.bedework.util.misc.ToString;

public class HibernateConfigBase<T extends ConfigBase>
extends ConfigBase<T>
implements HibernateConfigI {
    private List<String> hibernateProperties;

    @Override
    public void setHibernateProperties(List<String> val) {
        this.hibernateProperties = val;
    }

    @Override
    @ConfInfo(collectionElementName="hibernateProperty", elementType="java.lang.String")
    public List<String> getHibernateProperties() {
        return this.hibernateProperties;
    }

    @Override
    public void setHibernateDialect(String val) {
        this.setHibernateProperty("hibernate.dialect", val);
    }

    @Override
    @ConfInfo(dontSave=true)
    public String getHibernateDialect() {
        return this.getHibernateProperty("hibernate.dialect");
    }

    @Override
    public void addHibernateProperty(String name, String val) {
        this.setHibernateProperties(this.addListProperty(this.getHibernateProperties(), name, val));
    }

    @Override
    @ConfInfo(dontSave=true)
    public String getHibernateProperty(String name) {
        return this.getProperty(this.getHibernateProperties(), name);
    }

    @Override
    public void removeHibernateProperty(String name) {
        this.removeProperty(this.getHibernateProperties(), name);
    }

    @Override
    @ConfInfo(dontSave=true)
    public void setHibernateProperty(String name, String val) {
        this.setHibernateProperties(this.setListProperty(this.getHibernateProperties(), name, val));
    }

    @Override
    public void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        ts.append("hibernateProperties", this.getHibernateProperties());
    }

    @Override
    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

