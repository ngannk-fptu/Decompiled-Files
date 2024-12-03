/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml.tagdefs;

import java.util.HashMap;
import javax.xml.namespace.QName;

public class CarddavTags {
    public static final String namespace = "urn:ietf:params:xml:ns:carddav";
    public static final HashMap<String, QName> qnames = new HashMap();
    public static final QName addressbook = CarddavTags.makeQName("addressbook");
    public static final QName addressbookDescription = CarddavTags.makeQName("addressbook-description");
    public static final QName addressbookHomeSet = CarddavTags.makeQName("addressbook-home-set");
    public static final QName addressbookCollectionLocationOk = CarddavTags.makeQName("addressbook-collection-location-ok");
    public static final QName addressbookMultiget = CarddavTags.makeQName("addressbook-multiget");
    public static final QName addressbookQuery = CarddavTags.makeQName("addressbook-query");
    public static final QName addressData = CarddavTags.makeQName("address-data");
    public static final QName addressDataType = CarddavTags.makeQName("address-data-type");
    public static final QName allprop = CarddavTags.makeQName("allprop");
    public static final QName directory = CarddavTags.makeQName("directory");
    public static final QName filter = CarddavTags.makeQName("filter");
    public static final QName isNotDefined = CarddavTags.makeQName("is-not-defined");
    public static final QName limit = CarddavTags.makeQName("limit");
    public static final QName maxResourceSize = CarddavTags.makeQName("max-resource-size");
    public static final QName noUidConflict = CarddavTags.makeQName("no-uid-conflict");
    public static final QName nresults = CarddavTags.makeQName("nresults");
    public static final QName paramFilter = CarddavTags.makeQName("param-filter");
    public static final QName principalAddress = CarddavTags.makeQName("principal-address");
    public static final QName prop = CarddavTags.makeQName("prop");
    public static final QName propFilter = CarddavTags.makeQName("prop-filter");
    public static final QName supportedAddressData = CarddavTags.makeQName("supported-address-data");
    public static final QName supportedCollation = CarddavTags.makeQName("supported-collation");
    public static final QName validAddressData = CarddavTags.makeQName("valid-address-data");
    public static final QName textMatch = CarddavTags.makeQName("text-match");

    private static QName makeQName(String name) {
        QName q = new QName(namespace, name);
        qnames.put(name, q);
        return q;
    }
}

