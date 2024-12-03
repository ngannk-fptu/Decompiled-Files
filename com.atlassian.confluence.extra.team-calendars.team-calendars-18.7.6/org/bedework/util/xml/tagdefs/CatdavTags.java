/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml.tagdefs;

import java.util.HashMap;
import javax.xml.namespace.QName;

public class CatdavTags {
    public static final String namespace = "http://www.bedework.org/ns/catdav";
    public static final HashMap<String, QName> qnames = new HashMap();
    public static final QName category = CatdavTags.makeQName("category");
    public static final QName categoryDescription = CatdavTags.makeQName("category-description");
    public static final QName categoriesHomeSet = CatdavTags.makeQName("categories-home-set");
    public static final QName addressbookCollectionLocationOk = CatdavTags.makeQName("addressbook-collection-location-ok");
    public static final QName allprop = CatdavTags.makeQName("allprop");
    public static final QName categoriesMultiget = CatdavTags.makeQName("categories-multiget");
    public static final QName categorieskQuery = CatdavTags.makeQName("categories-query");
    public static final QName categoryData = CatdavTags.makeQName("category-data");
    public static final QName categoryDataType = CatdavTags.makeQName("category-data-type");
    public static final QName descriptions = CatdavTags.makeQName("descriptions");
    public static final QName displayNames = CatdavTags.makeQName("display-names");
    public static final QName filter = CatdavTags.makeQName("filter");
    public static final QName isNotDefined = CatdavTags.makeQName("is-not-defined");
    public static final QName lastmod = CatdavTags.makeQName("lastmod");
    public static final QName limit = CatdavTags.makeQName("limit");
    public static final QName locale = CatdavTags.makeQName("locale");
    public static final QName localizedText = CatdavTags.makeQName("localized-text");
    public static final QName maxResourceSize = CatdavTags.makeQName("max-resource-size");
    public static final QName name = CatdavTags.makeQName("name");
    public static final QName noUidConflict = CatdavTags.makeQName("no-uid-conflict");
    public static final QName nresults = CatdavTags.makeQName("nresults");
    public static final QName paramFilter = CatdavTags.makeQName("param-filter");
    public static final QName principalAddress = CatdavTags.makeQName("principal-address");
    public static final QName prop = CatdavTags.makeQName("prop");
    public static final QName propFilter = CatdavTags.makeQName("prop-filter");
    public static final QName supportedCategoryData = CatdavTags.makeQName("supported-category-data");
    public static final QName supportedCollation = CatdavTags.makeQName("supported-collation");
    public static final QName validCategoryData = CatdavTags.makeQName("valid-category-data");
    public static final QName text = CatdavTags.makeQName("text");
    public static final QName textMatch = CatdavTags.makeQName("text-match");
    public static final QName uid = CatdavTags.makeQName("uid");

    private static QName makeQName(String name) {
        QName q = new QName(namespace, name);
        qnames.put(name, q);
        return q;
    }
}

