/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.google.common.collect.Sets
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.extras.api.Product;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public enum Application {
    Confluence(Product.CONFLUENCE),
    JIRA(Product.JIRA),
    FECRU(Product.FISHEYE, Product.CRUCIBLE),
    Crowd(Product.CROWD),
    Bamboo(Product.BAMBOO),
    Bitbucket(new Product("Stash", "stash"), new Product("Bitbucket Server", "stash"), new Product("Bitbucket", "stash")),
    Plugin(Product.ALL_PLUGINS),
    Unknown(new Product[0]);

    private final Set<Product> products;

    private Application(Product ... products) {
        this.products = Collections.unmodifiableSet(Sets.newHashSet((Object[])products));
    }

    public static Application byProduct(Product product) {
        return Arrays.stream(Application.values()).filter(a -> a.products.contains(product)).findAny().orElse(Unknown);
    }

    public static Application byAppDisplayName(String displayName) {
        return Arrays.stream(Application.values()).filter(val -> val.products.stream().anyMatch(p -> p.getName().equals(displayName))).findAny().orElse(Unknown);
    }
}

