/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.headers;

public enum Category {
    custom("Custom", "A cetegory not listed here. This 'custom' is not listed in the manifest header"),
    adoption("Adoption", "Standard bundles adopted for OSGi"),
    bus("Busses", "Interfaces to busses like USB, CEBus ..."),
    clients("Clients", "Interfaces to external clients"),
    communication("Communication", "Bundles that provide communication facilities"),
    cryptography("Cryptography", "Public key, certificates"),
    database("Database", "Persistence"),
    device("Device", "Implementations of the device specification"),
    development("Development", "Tools for development"),
    distributed("Distributed", "processing\tDistributed computing like CORBA, RMI, DCE"),
    discovery("Discovery protocols", "JINI, UPnP, SLP, Salutation"),
    ecommerce("E-commerce", "Electronic shopping"),
    example("Example/tutorial", "Material for courses and tutorials"),
    framework("Framework", "Directly related to the framework"),
    games("Games", "Entertainment related, including proxies for games"),
    language("Language", "Translations, locale"),
    management("Management", "Management of the box"),
    messaging("Messaging", "Mail, message queues"),
    mobility("Mobility", "Functions like positioning"),
    network("Networking", "Implementations of network protocols"),
    nursery("Nursery", "Example bundles for the standardization"),
    osgi("OSGi standardization", "Related to OSGi standardization effort"),
    payment("Payment", "Electronic payments"),
    preferences("Preferences", "Preferences"),
    publishing("Publishing", "Mechanisms and tools to publish information"),
    reliability("Reliability", "Fault management, performance management"),
    robotic("Robotic", "Robotic control"),
    scripting("Scripting", "Script languages like python, vb, javascript"),
    security("Security", "Authorization and authentication"),
    testing("Testing", "OSGi test cases"),
    tools("Tools", "Tools that help building bundles"),
    users("User management", "User repository, user preferences"),
    utility("Utilities", "Support bundles providing some utiltity"),
    vehicle("Vehicle", "Automobile related"),
    wireless("Wireless", "802.11, Bluetooth"),
    xmls("XML, HTML, WML, ...", "Parsing and processing of ?ML information"),
    json("JSON", "JSON codecs"),
    enroute("OSGi enRoute", "A project to show how easy it is to use OSGi");

    public final String name;
    public final String desc;

    private Category(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}

