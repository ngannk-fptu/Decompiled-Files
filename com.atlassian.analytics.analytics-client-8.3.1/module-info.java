/* synthetic */ module com.fasterxml.jackson.dataformat.cbor {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.dataformat.cbor;
    exports com.fasterxml.jackson.dataformat.cbor.databind;

    provides JsonFactory with CBORFactory;

}

