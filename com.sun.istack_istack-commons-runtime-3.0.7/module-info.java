module com.sun.istack.runtime {
    requires java.logging;
    /* transitive */ requires java.xml;
    /* transitive */ requires java.activation;

    exports com.sun.istack;
    exports com.sun.istack.localization;
    exports com.sun.istack.logging;

}

