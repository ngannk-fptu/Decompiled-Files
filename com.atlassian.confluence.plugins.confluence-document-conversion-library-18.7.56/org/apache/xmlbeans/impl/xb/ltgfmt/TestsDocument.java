/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.ltgfmt;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface TestsDocument
extends XmlObject {
    public static final DocumentFactory<TestsDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "tests5621doctype");
    public static final SchemaType type = Factory.getType();

    public Tests getTests();

    public void setTests(Tests var1);

    public Tests addNewTests();

    public static interface Tests
    extends XmlObject {
        public static final ElementFactory<Tests> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "tests9d6eelemtype");
        public static final SchemaType type = Factory.getType();

        public List<TestCase> getTestList();

        public TestCase[] getTestArray();

        public TestCase getTestArray(int var1);

        public int sizeOfTestArray();

        public void setTestArray(TestCase[] var1);

        public void setTestArray(int var1, TestCase var2);

        public TestCase insertNewTest(int var1);

        public TestCase addNewTest();

        public void removeTest(int var1);
    }
}

