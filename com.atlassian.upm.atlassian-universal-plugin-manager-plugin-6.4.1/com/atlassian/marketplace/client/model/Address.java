/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import io.atlassian.fugue.Option;

public class Address {
    String line1;
    Option<String> line2;
    Option<String> city;
    Option<String> state;
    Option<String> postCode;
    Option<String> country;

    public String getLine1() {
        return this.line1;
    }

    public Option<String> getLine2() {
        return this.line2;
    }

    public Option<String> getCity() {
        return this.city;
    }

    public Option<String> getState() {
        return this.state;
    }

    public Option<String> getPostCode() {
        return this.postCode;
    }

    public Option<String> getCountry() {
        return this.country;
    }
}

