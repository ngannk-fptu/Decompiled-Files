/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.LicensedOnly
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.json.JSONException
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.model.JiraDateField;
import com.atlassian.confluence.extra.calendar3.model.JqlValidationResult;
import com.atlassian.confluence.extra.calendar3.model.QueryOptions;
import com.atlassian.confluence.extra.calendar3.model.SearchFilter;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.JiraLinksResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.param.ValidateJQLParam;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sun.jersey.api.core.InjectParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

@LicensedOnly
@Path(value="calendar/jira")
@InterceptorChain(value={TransactionInterceptor.class})
public class JiraCalendarResource
extends AbstractResource {
    private final JiraAccessor jiraAccessor;

    public JiraCalendarResource(CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, JiraAccessor jiraAccessor, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, UserAccessor userAccessor) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.jiraAccessor = jiraAccessor;
    }

    @Path(value="jiraLinks")
    @GET
    @Produces(value={"application/json"})
    public Response getJiraLinks() throws JSONException {
        return Response.ok((Object)new JiraLinksResponseEntity(this.jiraAccessor.getLinkedJiraApplications()).toJson().toString()).build();
    }

    @Path(value="{applicationId}/query/options")
    @GET
    @Produces(value={"application/json"})
    public Response getJiraQueryOptions(@PathParam(value="applicationId") String applicationId) throws CredentialsRequiredException, ResponseException {
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
        if (null != jiraLink) {
            return Response.ok((Object)this.jiraAccessor.getQueryOptions(jiraLink).toJson().toString()).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    @Path(value="{applicationId}/date-fields/jql")
    @GET
    @Produces(value={"application/json"})
    public Response getDateFieldsByJql(@PathParam(value="applicationId") String applicationId, @QueryParam(value="query") String jql) throws IOException, CredentialsRequiredException, ResponseException {
        JqlValidationResult jqlValidationResult;
        List<JiraDateField> jiraDateFields = Collections.emptyList();
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
        if (null != jiraLink && StringUtils.isNotBlank(jql) && (jqlValidationResult = this.jiraAccessor.validateJql(jiraLink, jql)).isValid()) {
            jiraDateFields = this.getDateFieldsNormalized(jiraLink, this.jiraAccessor.getDateFields(jiraLink, jql));
        }
        return Response.ok((Object)this.toJsonArray(jiraDateFields.toArray(new JiraDateField[jiraDateFields.size()])).toString()).build();
    }

    @Path(value="{applicationId}/date-fields/project/{projectKey}")
    @GET
    @Produces(value={"application/json"})
    public Response getDateFieldsByProject(@PathParam(value="applicationId") String applicationId, @PathParam(value="projectKey") String projectKey) throws CredentialsRequiredException, IOException, ResponseException {
        return this.getDateFieldsByJql(applicationId, String.format("project = \"%s\"", projectKey));
    }

    @Path(value="{applicationId}/date-fields/filter/{searchFilterId}")
    @GET
    @Produces(value={"application/json"})
    public Response getDateFieldsBySearchFilter(@PathParam(value="applicationId") String applicationId, @PathParam(value="searchFilterId") long searchFilterId) throws CredentialsRequiredException, ResponseException, IOException {
        List<JiraDateField> jiraDateFields = Collections.emptyList();
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
        if (null != jiraLink) {
            QueryOptions queryOptions = this.jiraAccessor.getQueryOptions(jiraLink);
            List<SearchFilter> searchFilters = queryOptions.getSearchFilters();
            jiraDateFields = null != searchFilters && Collections2.filter(searchFilters, searchFilter -> searchFilterId == searchFilter.getId()).size() == 1 ? this.getDateFieldsNormalized(jiraLink, this.jiraAccessor.getDateFields(jiraLink, searchFilterId)) : this.getDateFieldsNormalized(jiraLink, jiraDateFields);
        }
        return Response.ok((Object)this.toJsonArray(jiraDateFields.toArray(new JiraDateField[jiraDateFields.size()])).toString()).build();
    }

    @Path(value="{applicationId}/jql/validate")
    @PUT
    @Produces(value={"application/json"})
    public Response validateJql(@PathParam(value="applicationId") String applicationId, @InjectParam ValidateJQLParam param) throws IOException, CredentialsRequiredException, ResponseException {
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
        if (null != jiraLink) {
            return Response.ok((Object)this.jiraAccessor.validateJql(jiraLink, param.getJql()).toJson().toString()).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    @Path(value="{applicationId}/jql/autocomplete")
    @GET
    @Produces(value={"application/json"})
    public Response getJiraAutocomplete(@PathParam(value="applicationId") String applicationId, @QueryParam(value="fieldName") String fieldName, @QueryParam(value="fieldValue") String fieldValue) throws IOException, CredentialsRequiredException, ResponseException {
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
        if (null != jiraLink) {
            return Response.ok((Object)this.jiraAccessor.getAutoComplete(jiraLink, fieldName, fieldValue).toJson().toString()).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    private List<JiraDateField> getDateFieldsNormalized(ApplicationLink jiraLink, Collection<JiraDateField> unsortedDateFields) {
        ArrayList<JiraDateField> normalizedDateFields = new ArrayList<JiraDateField>();
        normalizedDateFields.addAll(Collections2.filter((Collection)Collections2.transform(unsortedDateFields, (Function)new JiraDataFieldTranslationTransformer(this.getI18nBean())), jiraDateField -> !StringUtils.equals(jiraDateField.getKey(), "updated")));
        if (normalizedDateFields.isEmpty()) {
            normalizedDateFields.add(new JiraDateField("duedate", this.getText("calendar3.jira.fields.duedate.name")));
        }
        normalizedDateFields.add(new JiraDateField("versiondue", this.getText("calendar3.jira.fields.versiondue.name")));
        Collections.sort(normalizedDateFields, (leftField, rightField) -> {
            int result = StringUtils.defaultString(leftField.getName()).compareTo(StringUtils.defaultString(rightField.getName()));
            return 0 == result ? StringUtils.defaultString(leftField.getKey()).compareTo(StringUtils.defaultString(rightField.getKey())) : result;
        });
        this.setDateFieldOrder(normalizedDateFields, "duedate", 0);
        this.setDateFieldOrder(normalizedDateFields, "versiondue", 1);
        if (this.jiraAccessor.isGreenHopperSprintDatesSupported(jiraLink)) {
            normalizedDateFields.add(2, new JiraDateField("sprint", this.getText("calendar3.jira.fields.greenhoppersprint.name")));
        }
        return normalizedDateFields;
    }

    private void setDateFieldOrder(List<JiraDateField> jiraDateFields, String fieldKey, int newIndexInList) {
        if (newIndexInList < 0 || newIndexInList >= jiraDateFields.size()) {
            throw new IllegalArgumentException(String.format("New position %d out of range.", newIndexInList));
        }
        int index = -1;
        int j = jiraDateFields.size();
        for (int i = 0; i < j; ++i) {
            if (!StringUtils.equals(fieldKey, jiraDateFields.get(i).getKey())) continue;
            index = i;
            break;
        }
        if (index >= 0) {
            jiraDateFields.add(newIndexInList, jiraDateFields.remove(index));
        }
    }

    private static class JiraDataFieldTranslationTransformer
    implements Function<JiraDateField, JiraDateField> {
        private final I18NBean i18NBean;
        private final StringBuilder i18nKeyBuilder;

        private JiraDataFieldTranslationTransformer(I18NBean i18NBean) {
            this.i18NBean = i18NBean;
            this.i18nKeyBuilder = new StringBuilder();
        }

        public JiraDateField apply(JiraDateField jiraDateField) {
            this.i18nKeyBuilder.setLength(0);
            String i18nKey = this.i18nKeyBuilder.append("calendar3.jira.fields.").append(jiraDateField.getKey()).append(".name").toString();
            String translatedName = this.i18NBean.getText(i18nKey);
            if (!StringUtils.equals(i18nKey, translatedName)) {
                jiraDateField.setName(translatedName);
            }
            return jiraDateField;
        }
    }
}

