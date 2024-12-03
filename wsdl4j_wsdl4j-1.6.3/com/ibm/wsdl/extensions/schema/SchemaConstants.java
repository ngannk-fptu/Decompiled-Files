/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;

public class SchemaConstants {
    public static final String ATTR_ID = "id";
    public static final String ATTR_SCHEMA_LOCATION = "schemaLocation";
    public static final String ELEM_SCHEMA = "schema";
    public static final String ELEM_INCLUDE = "include";
    public static final String ELEM_REDEFINE = "redefine";
    public static final String NS_URI_XSD_1999 = "http://www.w3.org/1999/XMLSchema";
    public static final String NS_URI_XSD_2000 = "http://www.w3.org/2000/10/XMLSchema";
    public static final String NS_URI_XSD_2001 = "http://www.w3.org/2001/XMLSchema";
    public static final QName Q_ELEM_XSD_1999 = new QName("http://www.w3.org/1999/XMLSchema", "schema");
    public static final QName Q_ELEM_XSD_2000 = new QName("http://www.w3.org/2000/10/XMLSchema", "schema");
    public static final QName Q_ELEM_XSD_2001 = new QName("http://www.w3.org/2001/XMLSchema", "schema");
    public static final List XSD_QNAME_LIST = Arrays.asList(Q_ELEM_XSD_1999, Q_ELEM_XSD_2000, Q_ELEM_XSD_2001);
    public static final QName Q_ELEM_IMPORT_XSD_1999 = new QName("http://www.w3.org/1999/XMLSchema", "import");
    public static final QName Q_ELEM_IMPORT_XSD_2000 = new QName("http://www.w3.org/2000/10/XMLSchema", "import");
    public static final QName Q_ELEM_IMPORT_XSD_2001 = new QName("http://www.w3.org/2001/XMLSchema", "import");
    public static final List XSD_IMPORT_QNAME_LIST = Arrays.asList(Q_ELEM_IMPORT_XSD_1999, Q_ELEM_IMPORT_XSD_2000, Q_ELEM_IMPORT_XSD_2001);
    public static final QName Q_ELEM_INCLUDE_XSD_1999 = new QName("http://www.w3.org/1999/XMLSchema", "include");
    public static final QName Q_ELEM_INCLUDE_XSD_2000 = new QName("http://www.w3.org/2000/10/XMLSchema", "include");
    public static final QName Q_ELEM_INCLUDE_XSD_2001 = new QName("http://www.w3.org/2001/XMLSchema", "include");
    public static final List XSD_INCLUDE_QNAME_LIST = Arrays.asList(Q_ELEM_INCLUDE_XSD_1999, Q_ELEM_INCLUDE_XSD_2000, Q_ELEM_INCLUDE_XSD_2001);
    public static final QName Q_ELEM_REDEFINE_XSD_1999 = new QName("http://www.w3.org/1999/XMLSchema", "redefine");
    public static final QName Q_ELEM_REDEFINE_XSD_2000 = new QName("http://www.w3.org/2000/10/XMLSchema", "redefine");
    public static final QName Q_ELEM_REDEFINE_XSD_2001 = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
    public static final List XSD_REDEFINE_QNAME_LIST = Arrays.asList(Q_ELEM_REDEFINE_XSD_1999, Q_ELEM_REDEFINE_XSD_2000, Q_ELEM_REDEFINE_XSD_2001);
}

