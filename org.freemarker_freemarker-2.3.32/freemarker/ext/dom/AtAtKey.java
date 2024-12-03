/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

enum AtAtKey {
    MARKUP("@@markup"),
    NESTED_MARKUP("@@nested_markup"),
    ATTRIBUTES_MARKUP("@@attributes_markup"),
    TEXT("@@text"),
    START_TAG("@@start_tag"),
    END_TAG("@@end_tag"),
    QNAME("@@qname"),
    NAMESPACE("@@namespace"),
    LOCAL_NAME("@@local_name"),
    ATTRIBUTES("@@"),
    PREVIOUS_SIBLING_ELEMENT("@@previous_sibling_element"),
    NEXT_SIBLING_ELEMENT("@@next_sibling_element");

    private final String key;

    public String getKey() {
        return this.key;
    }

    private AtAtKey(String key) {
        this.key = key;
    }

    public static boolean containsKey(String key) {
        for (AtAtKey item : AtAtKey.values()) {
            if (!item.getKey().equals(key)) continue;
            return true;
        }
        return false;
    }
}

