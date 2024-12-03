module io.github.classgraph {
    requires java.xml;
    requires jdk.unsupported;
    requires java.management;
    requires java.logging;
    /* static phase */ requires io.github.toolfactory.narcissus;

    exports io.github.classgraph;

}

