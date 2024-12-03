/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.JsonContentProperty$ContentPropertyBuilder
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty$SpacePropertyBuilder
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Space$SpaceBuilder
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.Version$VersionBuilder
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.SubjectType
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse$ContentRestrictionPageResponseBuilder
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.search.ContainerSummary
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.model.web.WebPanelView
 *  com.atlassian.confluence.api.model.web.WebPanelView$Builder
 *  com.atlassian.confluence.api.model.web.WebSectionView
 *  com.atlassian.confluence.api.model.web.WebSectionView$Builder
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.restapi.examples;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.ContainerSummary;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.model.web.WebPanelView;
import com.atlassian.confluence.api.model.web.WebSectionView;
import com.atlassian.confluence.plugins.restapi.enrich.StaticEnricherFilter;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContentExamples {
    private static final KnownUser KNOWN_USER = new KnownUser(null, "username", "Full Name", (UserKey)null);
    public static final Object CONTENT_REQUEST_BODY_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleCreateContent());
    public static final Object CONTENT_REQUEST_UPDATE_BODY_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleUpdateContent());
    public static final Object ATTACHMENT_REQUEST_UPDATE_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleUpdateAttachment());
    public static final Object CONTENT_LABEL_REQUEST_BODY_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleLabels());
    public static final Object CONTENT_PROPERTY_REQUEST_BODY_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.exampleContentPropertyBuilder().build());
    public static final Object SPACE_PROPERTY_REQUEST_BODY_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.exampleSpacePropertyBuilder().build());
    public static final Object CONTENT_PROPERTY_REQUEST_UPDATE_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleUpdateContentProperty());
    public static final Object SPACE_PROPERTY_REQUEST_UPDATE_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleUpdateSpaceProperty());
    public static final Object CONTENT_BODY_REQUEST_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleContentBody());
    public static final Object SPACE_REQUEST_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleCreateSpace());
    public static final Object CONTENT_RESTRICTION_REQUEST_UPDATE_EXAMPLE = StaticEnricherFilter.enrichRequest(ContentExamples.makeExampleUpdateContentRestrictionPageResponseAsRequest());
    public static final Object SPACE_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleSpace());
    public static final Object CONTENT_DOC_EXAMPLE_PAGE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExamplePage());
    public static final Object MACRO_BODY_EXAMPLE_PAGE = StaticEnricherFilter.enrichResponse(ContentExamples.makeMacroExamplePage());
    public static final Object CONTENT_DOC_EXAMPLE_ATTACHMENT = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleAttachment());
    public static final Object CONTENT_BODY_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleContentBody());
    public static final List CONTENT_DOC_EXAMPLE_REST_LIST_ATTACHMENTS;
    public static final Map CHILDREN_DOC_EXAMPLE;
    public static final Object CONTENT_PROPERTY_DOC_EXAMPLE;
    public static final Object SPACE_PROPERTY_DOC_EXAMPLE;
    public static final List CONTENT_PROPERTY_LIST_DOC_EXAMPLE;
    public static final List SPACE_PROPERTY_LIST_DOC_EXAMPLE;
    public static final Object SEARCH_RESULT_DOC_EXAMPLE;
    public static final Object CONTENT_RESTRICTION_PAGE_RESPONSE_EXAMPLE;
    public static final Object CONTENT_RESTRICTION_BOOLEAN_CHECK_RESPONSE_EXAMPLE;
    public static final Object CONTENT_VERSION_PAGE_RESPONSE_EXAMPLE;
    public static final Object CONTENT_VERSION_EXAMPLE;
    public static final Object WEB_SECTION_LIST;
    public static final Object WEB_ITEM_LIST;
    public static final Object WEB_PANEL_LIST;

    private static Object makeExampleUpdateContentRestrictionPageResponseAsRequest() {
        ContentRestriction singleUserUpdateContentRestriction = ContentRestriction.builder().operation(OperationKey.UPDATE).restrictions((Map)ImmutableMap.of((Object)SubjectType.USER, (Object)PageResponseImpl.fromSingle((Object)KNOWN_USER, (boolean)false).build(), (Object)SubjectType.GROUP, (Object)PageResponseImpl.empty((boolean)false))).build();
        return PageResponseImpl.fromSingle((Object)singleUserUpdateContentRestriction, (boolean)false).build();
    }

    private static Object makeExampleGetContentVersionPageResponse() {
        Version.VersionBuilder builder = Version.builder();
        Version version = builder.by((Person)KNOWN_USER).number(1).message("This is first version").minorEdit(false).syncRev("1234").when(OffsetDateTime.now()).build();
        return PageResponseImpl.fromSingle((Object)version, (boolean)false).build();
    }

    private static Object makeExampleGetContentVersion() {
        Version.VersionBuilder builder = Version.builder();
        Version version = builder.by((Person)KNOWN_USER).number(1).message("This is first version").minorEdit(false).syncRev("1234").when(OffsetDateTime.now()).content(Reference.to((Object)ContentExamples.makeExampleUpdateContent())).build();
        return version;
    }

    private static Object makeExampleGetContentRestrictionPageResponse() {
        ContentId contentId = ContentId.deserialise((String)"12345");
        Reference contentRef = Reference.collapsed((Object)Content.builder().id(contentId).build());
        ContentRestriction emptyReadContentRestriction = ContentRestriction.builder().content(contentRef).operation(OperationKey.READ).restrictions((Map)ImmutableMap.of((Object)SubjectType.GROUP, (Object)PageResponseImpl.empty((boolean)false), (Object)SubjectType.USER, (Object)PageResponseImpl.empty((boolean)false))).build();
        ContentRestriction emptyUpdateContentRestriction = ContentRestriction.builder().content(contentRef).operation(OperationKey.UPDATE).restrictions((Map)ImmutableMap.of((Object)SubjectType.GROUP, (Object)PageResponseImpl.empty((boolean)false), (Object)SubjectType.USER, (Object)PageResponseImpl.empty((boolean)false))).build();
        return ((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)ContentRestrictionsPageResponse.builder().withContentId(contentId).addAll(Arrays.asList(emptyReadContentRestriction, emptyUpdateContentRestriction))).withRestrictionsHash("this_is_hash").pageRequest((PageRequest)new SimplePageRequest(LimitedRequestImpl.create((int)PaginationLimits.restrictionSubjects())))).hasMore(false)).addLink(new Link(new LinkType("byOperation"), "http://example.com/some/path/byOperation")).build();
    }

    private static Space.SpaceBuilder emptySpaceBuilder() {
        return Space.builder().key("TST").type(null);
    }

    private static JsonContentProperty.ContentPropertyBuilder exampleContentPropertyBuilder() {
        return JsonContentProperty.builder().key("example-property-key").value(new JsonString("{\"anything\":\"goes\"}"));
    }

    private static JsonSpaceProperty.SpacePropertyBuilder exampleSpacePropertyBuilder() {
        return (JsonSpaceProperty.SpacePropertyBuilder)((JsonSpaceProperty.SpacePropertyBuilder)JsonSpaceProperty.builder().key("example-property-key")).value(new JsonString("{\"anything\":\"goes\"}"));
    }

    private static Space makeExampleSpace() {
        return ContentExamples.emptySpaceBuilder().id(11L).name("Example space").description(ContentRepresentation.PLAIN, "This is an example space").build();
    }

    private static Content makeExamplePage() {
        Space space = ContentExamples.makeExampleSpace();
        return Content.builder((ContentType)ContentType.PAGE, (long)1234L).title("Example Content title").body("<p><h1>Example</h1>Some example content body</p>", ContentRepresentation.VIEW).space(space).container((Container)space).parent(Content.builder((ContentType)ContentType.PAGE, (long)123L).build()).version(ContentExamples.makeExampleVersion()).build();
    }

    private static Content makeExampleAttachment() {
        ImmutableMap metadata = ImmutableMap.of((Object)"comment", (Object)"This is my File", (Object)"mediaType", (Object)"text/plain");
        return Content.builder((ContentType)ContentType.ATTACHMENT, (long)5678L).title("myfile.txt").container((Container)ContentExamples.makeExamplePage()).metadata((Map)metadata).version(ContentExamples.makeExampleVersion()).build();
    }

    private static Version makeExampleVersion() {
        return Version.builder().number(2).by((Person)KNOWN_USER).when(OffsetDateTime.now()).message("change message for this edit").minorEdit(false).build();
    }

    private static Content makeMacroExamplePage() {
        Space space = ContentExamples.makeExampleSpace();
        return Content.builder((ContentType)ContentType.PAGE, (long)1234L).title("Example Content title").body("<h1>Example</h1><ac:macro ac:name=\"macro\"><ac:rich-text-body><p>This is the body of a macro.</p></ac:rich-text-body></ac:macro>", ContentRepresentation.STORAGE).space(space).container((Container)space).version(Version.builder().number(1).by((Person)KNOWN_USER).when(OffsetDateTime.now()).message("initial edit").minorEdit(false).build()).build();
    }

    private static Map<ContentType, PageResponse<Content>> makeChildrenExample() {
        HashMap children = Maps.newHashMap();
        ArrayList childPages = Lists.newArrayList((Object[])new Content[]{ContentExamples.makeExamplePage()});
        children.put(ContentType.PAGE, RestList.newRestList((PageRequest)null).results((List)childPages, false).build());
        return children;
    }

    private static JsonContentProperty makeExampleContentProperty() {
        Content content = Content.builder((ContentType)ContentType.PAGE, (long)1234L).build();
        Version version = Version.builder().number(2).build();
        return ContentExamples.exampleContentPropertyBuilder().content(content).version(version).build();
    }

    private static JsonSpaceProperty makeExampleSpaceProperty() {
        Space space = ContentExamples.makeExampleCreateSpace();
        Version version = Version.builder().number(2).build();
        return ((JsonSpaceProperty.SpacePropertyBuilder)ContentExamples.exampleSpacePropertyBuilder().space(space).version(version)).build();
    }

    private static ContentBody makeExampleContentBody() {
        return ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(ContentRepresentation.STORAGE)).value("<p>Some example body in storage format</p>")).content(ContentSelector.fromId((ContentId)ContentId.of((ContentType)ContentType.PAGE, (long)3604482L))).build();
    }

    private static Space makeExampleCreateSpace() {
        return ContentExamples.emptySpaceBuilder().name("Example space").description(ContentRepresentation.PLAIN, "This is an example space").build();
    }

    private static Content makeExampleCreateContent() {
        return Content.builder((ContentType)ContentType.PAGE).status(null).space(ContentExamples.emptySpaceBuilder().build()).title("Example Content title").body("<p>This is a new page</p>", ContentRepresentation.STORAGE).build();
    }

    private static Content makeExampleUpdateContent() {
        return Content.builder((ContentType)ContentType.PAGE).id(ContentId.of((ContentType)ContentType.PAGE, (long)3604482L)).status(null).space(ContentExamples.emptySpaceBuilder().build()).title("Example Content title").body("<p>This is the updated text for the new page</p>", ContentRepresentation.STORAGE).version(Version.builder().number(2).build()).build();
    }

    private static Content makeExampleUpdateAttachment() {
        return Content.builder((ContentType)ContentType.ATTACHMENT, (long)5678L).title("new_file_name.txt").version(Version.builder().number(2).build()).build();
    }

    private static List<Label> makeExampleLabels() {
        return Lists.newArrayList((Object[])new Label[]{Label.builder((String)"label1").build(), Label.builder((String)"label2").build()});
    }

    private static JsonContentProperty makeExampleUpdateContentProperty() {
        return ContentExamples.exampleContentPropertyBuilder().version(Version.builder().number(2).build()).build();
    }

    private static JsonSpaceProperty makeExampleUpdateSpaceProperty() {
        return ((JsonSpaceProperty.SpacePropertyBuilder)ContentExamples.exampleSpacePropertyBuilder().version(Version.builder().number(2).build())).build();
    }

    private static User makeExampleUser() {
        return KnownUser.builder().displayName("Example User").username("euser").userKey("A123EDCE").profilePicture(new Icon("/some/download/url", 48, 48, false)).build();
    }

    private static Iterable makeSearchResultExamples() {
        Space exampleSpace = ContentExamples.makeExampleSpace();
        Content exampleContent = ContentExamples.makeExamplePage();
        User exampleUser = ContentExamples.makeExampleUser();
        ImmutableList results = ImmutableList.builder().add((Object)SearchResult.builder((Object)exampleSpace).bodyExcerpt(exampleSpace.getName()).title(exampleSpace.getName()).iconCssClass("space-css-class").url("/display/space/" + exampleSpace.getKey()).lastModified(OffsetDateTime.now()).build()).add((Object)SearchResult.builder((Object)exampleContent).bodyExcerpt(((ContentBody)exampleContent.getBody().get(ContentRepresentation.VIEW)).getValue()).title(exampleContent.getTitle()).resultGlobalContainer(ContainerSummary.builder().title(exampleSpace.getName()).displayUrl("/display/space/" + exampleSpace.getKey()).build()).iconCssClass("page-css-class").url("/display/" + exampleContent.getTitle()).lastModified(OffsetDateTime.now()).build()).add((Object)SearchResult.builder((Object)exampleUser).title(exampleUser.getDisplayName()).iconCssClass("user-css-class").url("/display/user/" + exampleUser.getUsername()).lastModified(OffsetDateTime.now()).build()).build();
        return SearchPageResponse.builder().cqlQuery("title ~ example").hasMore(true).pageRequest((PageRequest)new SimplePageRequest(0, 3)).searchDuration(50).totalSize(15).result((Iterable)results).build();
    }

    private static List makeExampleWebSectionList() {
        return ImmutableList.of((Object)ContentExamples.makeExampleWebSection("id1", "Section 1", ContentExamples.makeExampleWebItem1()), (Object)ContentExamples.makeExampleWebSection("id2", "Section 2", ContentExamples.makeExampleWebItem2()));
    }

    private static WebSectionView makeExampleWebSection(String id, String label, WebItemView webItem) {
        return new WebSectionView.Builder().setStyleClass("style-class").addItems(new WebItemView[]{webItem}).create(id, label);
    }

    private static List makeExampleWebItemList() {
        return ImmutableList.of((Object)ContentExamples.makeExampleWebItem1(), (Object)ContentExamples.makeExampleWebItem2());
    }

    private static WebItemView makeExampleWebItem1() {
        return WebItemView.builder().setId("addUserLink").setModuleKey("add-users-button").setCompleteKey("complete.key").setLabel("Invite Users").setUrl("/confluence/admin/users/inviteuser.action?from=db-button").setWeight(5).setUrlWithoutContextPath("/admin/users/inviteuser.action?from=db-button").setIcon(Optional.of(new Icon("/some/icon/path", 16, 16, false))).setParams((Map)ImmutableMap.of((Object)"iconClass", (Object)"space-default")).setStyleClass("some-class").setTooltip("An informative tooltip").build();
    }

    private static WebItemView makeExampleWebItem2() {
        return WebItemView.builder().setId("addSpaceLink").setModuleKey("add-space").setCompleteKey("complete.key").setLabel("Create Space").setUrl("/confluence/spaces/createspace-start.action").setWeight(6).setUrlWithoutContextPath("/spaces/createspace-start.action").setIcon(Optional.of(new Icon("/some/icon/path", 16, 16, false))).setParams((Map)ImmutableMap.of((Object)"iconClass", (Object)"space-default")).setStyleClass("").build();
    }

    private static List makeExampleWebPanelList() {
        return ImmutableList.of((Object)ContentExamples.makeExampleWebPanel1(), (Object)ContentExamples.makeExampleWebPanel2());
    }

    private static WebPanelView makeExampleWebPanel1() {
        return new WebPanelView.Builder().setModuleKey("notes").setCompleteKey("complete.key").setName("notes").setLocation("dashboard.notes").setLabel("Notes").setWeight(10).create("<i>Notes go here</i>");
    }

    private static WebPanelView makeExampleWebPanel2() {
        return new WebPanelView.Builder().setModuleKey("global-entities-panel").setCompleteKey("complete.key").setName("global-entities-panel").setLocation("atl.dashboard.secondary").setLabel("label").setWeight(8).create("<p>web panel content</p>");
    }

    static {
        CHILDREN_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeChildrenExample());
        CONTENT_PROPERTY_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleContentProperty());
        SPACE_PROPERTY_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleSpaceProperty());
        SEARCH_RESULT_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeSearchResultExamples());
        CONTENT_RESTRICTION_PAGE_RESPONSE_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleGetContentRestrictionPageResponse());
        CONTENT_RESTRICTION_BOOLEAN_CHECK_RESPONSE_EXAMPLE = StaticEnricherFilter.enrichResponse(Boolean.TRUE);
        CONTENT_VERSION_PAGE_RESPONSE_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleGetContentVersionPageResponse());
        CONTENT_VERSION_EXAMPLE = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleGetContentVersion());
        WEB_SECTION_LIST = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleWebSectionList());
        WEB_ITEM_LIST = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleWebItemList());
        WEB_PANEL_LIST = StaticEnricherFilter.enrichResponse(ContentExamples.makeExampleWebPanelList());
        ArrayList attachments = Lists.newArrayList((Object[])new Content[]{ContentExamples.makeExampleAttachment()});
        CONTENT_DOC_EXAMPLE_REST_LIST_ATTACHMENTS = StaticEnricherFilter.enrichResponse(RestList.newRestList((PageRequest)null).results((List)attachments, false).build());
        ArrayList contentProperties = Lists.newArrayList((Object[])new JsonContentProperty[]{ContentExamples.makeExampleContentProperty()});
        CONTENT_PROPERTY_LIST_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(RestList.newRestList((PageRequest)null).results((List)contentProperties, false).build());
        ArrayList spaceProperties = Lists.newArrayList((Object[])new JsonSpaceProperty[]{ContentExamples.makeExampleSpaceProperty()});
        SPACE_PROPERTY_LIST_DOC_EXAMPLE = StaticEnricherFilter.enrichResponse(RestList.newRestList((PageRequest)null).results((List)spaceProperties, false).build());
    }
}

