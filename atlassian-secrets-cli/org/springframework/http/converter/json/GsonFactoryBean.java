/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.lang.Nullable;

public class GsonFactoryBean
implements FactoryBean<Gson>,
InitializingBean {
    private boolean base64EncodeByteArrays = false;
    private boolean serializeNulls = false;
    private boolean prettyPrinting = false;
    private boolean disableHtmlEscaping = false;
    @Nullable
    private String dateFormatPattern;
    @Nullable
    private Gson gson;

    public void setBase64EncodeByteArrays(boolean base64EncodeByteArrays) {
        this.base64EncodeByteArrays = base64EncodeByteArrays;
    }

    public void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public void setPrettyPrinting(boolean prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    public void setDisableHtmlEscaping(boolean disableHtmlEscaping) {
        this.disableHtmlEscaping = disableHtmlEscaping;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    @Override
    public void afterPropertiesSet() {
        GsonBuilder builder;
        GsonBuilder gsonBuilder = builder = this.base64EncodeByteArrays ? GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays() : new GsonBuilder();
        if (this.serializeNulls) {
            builder.serializeNulls();
        }
        if (this.prettyPrinting) {
            builder.setPrettyPrinting();
        }
        if (this.disableHtmlEscaping) {
            builder.disableHtmlEscaping();
        }
        if (this.dateFormatPattern != null) {
            builder.setDateFormat(this.dateFormatPattern);
        }
        this.gson = builder.create();
    }

    @Override
    @Nullable
    public Gson getObject() {
        return this.gson;
    }

    @Override
    public Class<?> getObjectType() {
        return Gson.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

