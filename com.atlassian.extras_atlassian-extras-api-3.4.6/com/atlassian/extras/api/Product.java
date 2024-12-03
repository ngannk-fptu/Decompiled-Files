/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class Product {
    private static final String JIRA_NS = "jira";
    private static final String CONF_NS = "conf";
    private static final String BAMBOO_NS = "bamboo";
    private static final String CROWD_NS = "crowd";
    private static final String CLOVER_NS = "clover";
    private static final String FISHEYE_NS = "fisheye";
    private static final String CRUCIBLE_NS = "crucible";
    private static final String STASH_NS = "stash";
    private static final String EDIT_LIVE_PLUGIN_NS = "edit_live_plugin";
    private static final String PERFORCE_PLUGIN_NS = "perforce_plugin";
    private static final String SHAREPOINT_PLUGIN_NS = "sharepoint_plugin";
    private static final String GREENHOPPER_NS = "greenhopper";
    private static final String TEAM_CALENDARS_NS = "team_calendars";
    private static final String BONFIRE_NS = "bonfire";
    private static final String VSS_PLUGIN_NS = "vss_plugin";
    private static final String CONF_QUESTIONS_NS = "conf_questions";
    public static Product JIRA = new Product("JIRA", "jira");
    public static Product CONFLUENCE = new Product("Confluence", "conf");
    public static Product BAMBOO = new Product("Bamboo", "bamboo");
    public static Product CROWD = new Product("Crowd", "crowd");
    public static Product CLOVER = new Product("Clover", "clover");
    public static Product FISHEYE = new Product("FishEye", "fisheye");
    public static Product CRUCIBLE = new Product("Crucible", "crucible");
    public static Product STASH = new Product("Stash", "stash");
    public static Product BITBUCKET_SERVER = new Product("Bitbucket Server", "stash");
    public static Product EDIT_LIVE_PLUGIN = new Product("Edit Live Plugin", "edit_live_plugin", true);
    public static Product PERFORCE_PLUGIN = new Product("Perforce Plugin", "perforce_plugin", true);
    public static Product SHAREPOINT_PLUGIN = new Product("Sharepoint Plugin", "sharepoint_plugin", true);
    public static Product GREENHOPPER = new Product("GreenHopper", "greenhopper", true);
    public static Product TEAM_CALENDARS = new Product("Team Calendars", "team_calendars", true);
    public static Product BONFIRE = new Product("Bonfire", "bonfire", true);
    public static Product VSS_PLUGIN = new Product("VSS Plugin", "vss_plugin", true);
    public static Product CONFLUENCE_QUESTIONS = new Product("Confluence Questions", "conf_questions");
    public static Iterable<Product> ATLASSIAN_PRODUCTS = Collections.unmodifiableList(Arrays.asList(JIRA, CONFLUENCE, BAMBOO, CROWD, CLOVER, FISHEYE, CRUCIBLE, STASH, BITBUCKET_SERVER, EDIT_LIVE_PLUGIN, PERFORCE_PLUGIN, SHAREPOINT_PLUGIN, GREENHOPPER, TEAM_CALENDARS, BONFIRE, VSS_PLUGIN, CONFLUENCE_QUESTIONS));
    public static Product ALL_PLUGINS = new Product("All Plugins", "plugins.*", true);
    private final String name;
    private final String namespace;
    private final boolean plugin;

    public static Product[] getAtalssianProductsAsArray() {
        ArrayList<Product> products = new ArrayList<Product>();
        for (Product p : ATLASSIAN_PRODUCTS) {
            products.add(p);
        }
        return products.toArray(new Product[0]);
    }

    public Product(String name, String namespace) {
        this(name, namespace, false);
    }

    public Product(String name, String namespace, boolean plugin) {
        this.name = name;
        this.plugin = plugin;
        this.namespace = "com.atlassian.bonfire.plugin".equals(namespace) ? BONFIRE_NS : ("com.atlassian.confluence.extra.team-calendars".equals(namespace) ? TEAM_CALENDARS_NS : ("com.pyxis.greenhopper.jira".equals(namespace) ? GREENHOPPER_NS : ("com.atlassian.confluence.extra.sharepoint".equals(namespace) ? SHAREPOINT_PLUGIN_NS : ("com.atlassian.jira.plugin.ext.vss".equals(namespace) ? VSS_PLUGIN_NS : namespace))));
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public boolean isPlugin() {
        return this.plugin;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Product p = (Product)o;
        return !(this.name != null ? !this.name.equalsIgnoreCase(p.name) : p.name != null);
    }

    public int hashCode() {
        return 527 + this.name.hashCode();
    }

    public String toString() {
        return "product <" + this.getName() + ">";
    }

    public static Product fromNamespace(String ns) {
        if (JIRA_NS.equals(ns)) {
            return JIRA;
        }
        if (CONF_NS.equals(ns)) {
            return CONFLUENCE;
        }
        if (BAMBOO_NS.equals(ns)) {
            return BAMBOO;
        }
        if (CROWD_NS.equals(ns)) {
            return CROWD;
        }
        if (CLOVER_NS.equals(ns)) {
            return CLOVER;
        }
        if (FISHEYE_NS.equals(ns)) {
            return FISHEYE;
        }
        if (CRUCIBLE_NS.equals(ns)) {
            return CRUCIBLE;
        }
        if (STASH_NS.equals(ns)) {
            return STASH;
        }
        if (EDIT_LIVE_PLUGIN_NS.equals(ns)) {
            return EDIT_LIVE_PLUGIN;
        }
        if (PERFORCE_PLUGIN_NS.equals(ns)) {
            return PERFORCE_PLUGIN;
        }
        if (SHAREPOINT_PLUGIN_NS.equals(ns)) {
            return SHAREPOINT_PLUGIN;
        }
        if (GREENHOPPER_NS.equals(ns)) {
            return GREENHOPPER;
        }
        if (TEAM_CALENDARS_NS.equals(ns)) {
            return TEAM_CALENDARS;
        }
        if (BONFIRE_NS.equals(ns)) {
            return BONFIRE;
        }
        if (VSS_PLUGIN_NS.equals(ns)) {
            return VSS_PLUGIN;
        }
        if (CONF_QUESTIONS_NS.equals(ns)) {
            return CONFLUENCE_QUESTIONS;
        }
        return new Product(ns, ns, true);
    }
}

