/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.stream.JsonReader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.TwitterEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Component
public class TwitterEmoticonServiceImpl
implements TwitterEmoticonService {
    private static final Logger log = LoggerFactory.getLogger(TwitterEmoticonServiceImpl.class);
    private final WebResourceUrlProvider webResourceUrlProvider;

    public TwitterEmoticonServiceImpl(@ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public Collection<AtlaskitEmoticonModel> list() {
        JsonObject jsonObject;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("twemoji-mapping.json");
             InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
             JsonReader JsonReader2 = new JsonReader((Reader)inputStreamReader);){
            jsonObject = JsonParser.parseReader((JsonReader)JsonReader2).getAsJsonObject();
        }
        catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException while reading json file mapping twemoji", (Throwable)e);
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            log.error("IOException while reading json file mapping twemoji", (Throwable)e);
            throw new RuntimeException(e);
        }
        ArrayList<AtlaskitEmoticonModel> retVal = new ArrayList<AtlaskitEmoticonModel>();
        Gson gson = new Gson();
        JsonArray jsonArray = jsonObject.getAsJsonArray("emojis");
        for (int i = 0; i < jsonArray.size(); ++i) {
            JsonObject emoji = jsonArray.get(i).getAsJsonObject();
            try {
                JsonObject representation = emoji.getAsJsonObject("representation");
                representation.addProperty("imagePath", this.getFullPath(emoji.get("id").getAsString()));
                JsonArray skinVariations = emoji.getAsJsonArray("skinVariations");
                if (skinVariations != null) {
                    for (int j = 0; j < skinVariations.size(); ++j) {
                        JsonObject emojiSkin = skinVariations.get(j).getAsJsonObject();
                        JsonObject representationSkin = emojiSkin.getAsJsonObject("representation");
                        representationSkin.addProperty("imagePath", this.getFullPath(emojiSkin.get("id").getAsString()));
                    }
                }
                AtlaskitEmoticonModel atlaskitEmoticonModel = (AtlaskitEmoticonModel)gson.fromJson((JsonElement)emoji, AtlaskitEmoticonModel.class);
                retVal.add(atlaskitEmoticonModel);
                continue;
            }
            catch (Exception e) {
                log.error("Error while reading a property of JSON:", (Object)e, (Object)emoji);
            }
        }
        return retVal;
    }

    @Override
    public String getResourceUrl(AtlaskitEmoticonModel atlaskitEmoticonModel) {
        String fullImagePath = this.webResourceUrlProvider.getStaticPluginResourceUrl("com.atlassian.confluence.plugins.confluence-emoticons-plugin:twemoji-resources", atlaskitEmoticonModel.getId() + ".svg", UrlMode.RELATIVE);
        return fullImagePath;
    }

    private String getFullPath(String id) {
        return this.webResourceUrlProvider.getBaseUrl(UrlMode.RELATIVE) + "/plugins/servlet/twitterEmojiRedirector?id=" + id;
    }

    @Override
    public AtlaskitEmoticonModel findById(String id) {
        throw new UnsupportedOperationException("getImageFileContent not implemented");
    }

    @Override
    public String getImageFileContent(AtlaskitEmoticonModel model) {
        throw new UnsupportedOperationException("getImageFileContent not implemented");
    }
}

