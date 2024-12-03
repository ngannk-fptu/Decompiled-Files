/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.troubleshooting.healthcheck.checks.eol.Product;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ProductReleaseDateManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultProductReleaseDateManager
implements ProductReleaseDateManager {
    private static final Type PRODUCT_LIST_TYPE = new TypeToken<List<Product>>(){}.getType();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nonnull
    public List<Product> readProducts(InputStream stream) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
        try (InputStreamReader inputStreamReader = new InputStreamReader(stream);){
            List list = (List)gson.fromJson((Reader)inputStreamReader, PRODUCT_LIST_TYPE);
            return list;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeProducts(List<Product> products, File jsonFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
        try (FileWriter writer = new FileWriter(jsonFile);){
            gson.toJson(products, (Appendable)writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

