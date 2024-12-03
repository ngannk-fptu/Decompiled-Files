open /* synthetic */ module org.apache.felix.framework {
    requires jdk.unsupported;

    exports org.apache.felix.framework.ext;
    exports org.osgi.dto;
    exports org.osgi.framework;
    exports org.osgi.framework.connect;
    exports org.osgi.framework.dto;
    exports org.osgi.framework.hooks.bundle;
    exports org.osgi.framework.hooks.resolver;
    exports org.osgi.framework.hooks.service;
    exports org.osgi.framework.hooks.weaving;
    exports org.osgi.framework.launch;
    exports org.osgi.framework.namespace;
    exports org.osgi.framework.startlevel;
    exports org.osgi.framework.startlevel.dto;
    exports org.osgi.framework.wiring;
    exports org.osgi.framework.wiring.dto;
    exports org.osgi.resource;
    exports org.osgi.resource.dto;
    exports org.osgi.service.condition;
    exports org.osgi.service.packageadmin;
    exports org.osgi.service.resolver;
    exports org.osgi.service.startlevel;
    exports org.osgi.service.url;
    exports org.osgi.util.tracker;

    provides FrameworkFactory with org.apache.felix.framework.FrameworkFactory;
    provides ConnectFrameworkFactory with org.apache.felix.framework.FrameworkFactory;

}

