module com.sun.xml.bind {
    requires java.xml.bind;
    requires java.compiler;
    requires java.desktop;
    requires java.logging;
    /* transitive */ requires java.activation;
    /* transitive */ requires java.xml;
    requires com.sun.xml.txw2;
    requires com.sun.xml.fastinfoset;
    requires org.jvnet.staxex;
    requires com.sun.istack.runtime;

    exports com.sun.xml.bind;
    exports com.sun.xml.bind.annotation;
    exports com.sun.xml.bind.api;
    exports com.sun.xml.bind.api.impl;
    exports com.sun.xml.bind.marshaller;
    exports com.sun.xml.bind.unmarshaller;
    exports com.sun.xml.bind.util;
    exports com.sun.xml.bind.v2;
    exports com.sun.xml.bind.v2.model.annotation;
    exports com.sun.xml.bind.v2.model.core;
    exports com.sun.xml.bind.v2.model.impl;
    exports com.sun.xml.bind.v2.model.nav;
    exports com.sun.xml.bind.v2.model.runtime;
    exports com.sun.xml.bind.v2.model.util;
    exports com.sun.xml.bind.v2.runtime;
    exports com.sun.xml.bind.v2.runtime.unmarshaller;
    exports com.sun.xml.bind.v2.schemagen;
    exports com.sun.xml.bind.v2.schemagen.episode;
    exports com.sun.xml.bind.v2.schemagen.xmlschema;
    exports com.sun.xml.bind.v2.util;

    opens com.sun.xml.bind.v2.model.nav to com.sun.tools.xjc;

}

