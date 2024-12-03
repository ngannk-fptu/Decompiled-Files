/* synthetic */ module com.fasterxml.jackson.datatype.joda {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.joda.time;

    exports com.fasterxml.jackson.datatype.joda;
    exports com.fasterxml.jackson.datatype.joda.cfg;
    exports com.fasterxml.jackson.datatype.joda.deser;
    exports com.fasterxml.jackson.datatype.joda.deser.key;
    exports com.fasterxml.jackson.datatype.joda.ser;

    provides Module with JodaModule;

}

