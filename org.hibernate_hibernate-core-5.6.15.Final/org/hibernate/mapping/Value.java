/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public interface Value
extends Serializable {
    public int getColumnSpan();

    public Iterator<Selectable> getColumnIterator();

    public Type getType() throws MappingException;

    public FetchMode getFetchMode();

    public Table getTable();

    public boolean hasFormula();

    public boolean isAlternateUniqueKey();

    public boolean isNullable();

    public boolean[] getColumnUpdateability();

    public boolean[] getColumnInsertability();

    public void createForeignKey() throws MappingException;

    public boolean isSimpleValue();

    public boolean isValid(Mapping var1) throws MappingException;

    public void setTypeUsingReflection(String var1, String var2) throws MappingException;

    public Object accept(ValueVisitor var1);

    public boolean isSame(Value var1);

    public ServiceRegistry getServiceRegistry();
}

