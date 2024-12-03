/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.plugins.search.actions;

import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchActionParameterMigrator {
    private final Function<SearchQueryParameters, String> convertToTextField = s -> Strings.isNullOrEmpty((String)s.getQuery()) ? null : "siteSearch ~ " + this.quote(s.getQuery());
    private final Function<SearchQueryParameters, String> convertToSpaceField = s -> Strings.isNullOrEmpty((String)s.getSpaceKey()) ? null : "space = " + this.quote(s.getSpaceKey());
    private final Function<SearchQueryParameters, String> convertToSpaceTypeField = s -> {
        String spaceType;
        if (s.getSpaceCategory() == null) {
            return null;
        }
        switch (s.getSpaceCategory()) {
            case GLOBAL: {
                spaceType = "global";
                break;
            }
            case PERSONAL: {
                spaceType = "personal";
                break;
            }
            case FAVOURITES: {
                spaceType = "favourite";
                break;
            }
            case ALL: {
                return null;
            }
            default: {
                throw new NotImplementedServiceException("Space category type not implemented: " + s.getSpaceCategory());
            }
        }
        return "space.type = \"" + spaceType + "\"";
    };
    private final Function<SearchQueryParameters, String> convertToTypeField = s -> {
        ContentTypeEnum contentType = s.getContentType();
        if (contentType != null) {
            String typeStr = contentType == ContentTypeEnum.SPACE_DESCRIPTION || contentType == ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION ? "space" : (contentType == ContentTypeEnum.PERSONAL_INFORMATION ? "user" : contentType.toString());
            return "type = " + this.quote(typeStr);
        }
        ContentTypeSearchDescriptor pluginContentType = s.getPluginContentType();
        if (pluginContentType != null) {
            return "type = " + this.quote(pluginContentType.getIdentifier());
        }
        return null;
    };
    private final Function<SearchQueryParameters, String> convertToLastModifiedField = s -> {
        String nowFunctionArgument;
        if (s.getLastModified() == null) {
            return null;
        }
        switch (s.getLastModified()) {
            case LASTDAY: {
                nowFunctionArgument = "-1d";
                break;
            }
            case LASTTWODAYS: {
                nowFunctionArgument = "-2d";
                break;
            }
            case LASTWEEK: {
                nowFunctionArgument = "-1w";
                break;
            }
            case LASTMONTH: {
                nowFunctionArgument = "-1M";
                break;
            }
            case LASTSIXMONTHS: {
                nowFunctionArgument = "-6M";
                break;
            }
            case LASTYEAR: {
                nowFunctionArgument = "-1y";
                break;
            }
            case LASTTWOYEARS: {
                nowFunctionArgument = "-2y";
                break;
            }
            default: {
                throw new NotImplementedServiceException("No implementation for date range: " + s.getLastModified().toString());
            }
        }
        return "lastmodified >= now('" + nowFunctionArgument + "')";
    };
    private final Function<SearchQueryParameters, String> convertToContributorField = s -> s.getContributor() == null ? null : "contributor = " + this.quote(s.getContributor());
    private final Function<SearchQueryParameters, String> convertToLabelsField = s -> {
        Set<String> labels = s.getLabels();
        if (labels == null || labels.isEmpty()) {
            return null;
        }
        return labels.stream().filter(label -> !Strings.isNullOrEmpty((String)label)).map(label -> "label = \"" + label + "\"").collect(Collectors.joining(" AND "));
    };

    private SearchActionParameterMigrator() {
    }

    private String quote(String stringToQuote) {
        return "\"" + stringToQuote.replaceAll("\"", "\\\\\"") + "\"";
    }

    public static String migrate(SearchQueryParameters searchQueryParameters) {
        SearchActionParameterMigrator searchActionParameterMigrator = new SearchActionParameterMigrator();
        List<Function> parameterConverters = Arrays.asList(searchActionParameterMigrator.convertToTextField, searchActionParameterMigrator.convertToSpaceField, searchActionParameterMigrator.convertToSpaceTypeField, searchActionParameterMigrator.convertToTypeField, searchActionParameterMigrator.convertToLastModifiedField, searchActionParameterMigrator.convertToContributorField, searchActionParameterMigrator.convertToLabelsField);
        return parameterConverters.stream().map(f -> (String)f.apply(searchQueryParameters)).filter(Objects::nonNull).collect(Collectors.joining(" AND "));
    }
}

