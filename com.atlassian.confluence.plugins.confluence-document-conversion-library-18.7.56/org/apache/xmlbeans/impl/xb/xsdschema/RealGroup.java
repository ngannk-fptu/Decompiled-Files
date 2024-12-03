/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.Group;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface RealGroup
extends Group {
    public static final DocumentFactory<RealGroup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "realgroup1f64type");
    public static final SchemaType type = Factory.getType();

    @Override
    public List<All> getAllList();

    @Override
    public All[] getAllArray();

    @Override
    public All getAllArray(int var1);

    @Override
    public int sizeOfAllArray();

    @Override
    public void setAllArray(All[] var1);

    @Override
    public void setAllArray(int var1, All var2);

    @Override
    public All insertNewAll(int var1);

    @Override
    public All addNewAll();

    @Override
    public void removeAll(int var1);

    @Override
    public List<ExplicitGroup> getChoiceList();

    @Override
    public ExplicitGroup[] getChoiceArray();

    @Override
    public ExplicitGroup getChoiceArray(int var1);

    @Override
    public int sizeOfChoiceArray();

    @Override
    public void setChoiceArray(ExplicitGroup[] var1);

    @Override
    public void setChoiceArray(int var1, ExplicitGroup var2);

    @Override
    public ExplicitGroup insertNewChoice(int var1);

    @Override
    public ExplicitGroup addNewChoice();

    @Override
    public void removeChoice(int var1);

    @Override
    public List<ExplicitGroup> getSequenceList();

    @Override
    public ExplicitGroup[] getSequenceArray();

    @Override
    public ExplicitGroup getSequenceArray(int var1);

    @Override
    public int sizeOfSequenceArray();

    @Override
    public void setSequenceArray(ExplicitGroup[] var1);

    @Override
    public void setSequenceArray(int var1, ExplicitGroup var2);

    @Override
    public ExplicitGroup insertNewSequence(int var1);

    @Override
    public ExplicitGroup addNewSequence();

    @Override
    public void removeSequence(int var1);
}

