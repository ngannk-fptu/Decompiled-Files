/* synthetic */ module com.ctc.wstx {
    /* transitive */ requires java.xml;
    /* transitive */ requires org.codehaus.stax2;

    exports com.ctc.wstx.api;
    exports com.ctc.wstx.cfg;
    exports com.ctc.wstx.dom;
    exports com.ctc.wstx.dtd;
    exports com.ctc.wstx.ent;
    exports com.ctc.wstx.evt;
    exports com.ctc.wstx.exc;
    exports com.ctc.wstx.io;
    exports com.ctc.wstx.msv;
    exports com.ctc.wstx.sax;
    exports com.ctc.wstx.sr;
    exports com.ctc.wstx.stax;
    exports com.ctc.wstx.sw;
    exports com.ctc.wstx.util;

    provides XMLEventFactory with WstxEventFactory;
    provides XMLInputFactory with WstxInputFactory;
    provides XMLOutputFactory with WstxOutputFactory;
    provides DatatypeLibraryFactory with DataTypeLibraryImpl;
    provides VerifierFactoryLoader with FactoryLoaderImpl;
    provides XMLValidationSchemaFactory.dtd with DTDSchemaFactory;
    provides XMLValidationSchemaFactory.relaxng with RelaxNGSchemaFactory;
    provides XMLValidationSchemaFactory.w3c with W3CSchemaFactory;

}

