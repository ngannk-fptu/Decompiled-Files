/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.troubleshooting.stp.salext;

import com.atlassian.sal.api.UrlMode;
import com.atlassian.sisyphus.SisyphusPatternSource;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.ApplicationVersionInfo;
import com.atlassian.troubleshooting.stp.hercules.ScanItem;
import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.request.FileSanitizer;
import com.atlassian.troubleshooting.stp.salext.license.ApplicationLicenseInfo;
import com.atlassian.troubleshooting.stp.salext.output.XmlSupportDataFormatter;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public interface SupportApplicationInfo
extends ApplicationVersionInfo {
    public static final String APPLICATION_BASE_URL = "stp.properties.application.base.url";
    public static final String APPLICATION_BUILD_NUMBER = "stp.properties.application.build.number";
    public static final String APPLICATION_BUILD_TIMESTAMP = "stp.properties.application.build.timestamp";
    public static final String APPLICATION_BUILD_VERSION = "stp.properties.application.build.version";
    public static final String APPLICATION_DARK_FEATURES_SYSTEM = "stp.properties.application.dark.features.system.enabled";
    public static final String APPLICATION_DARK_FEATURES_SITE = "stp.properties.application.dark.features.site.enabled";
    public static final String APPLICATION_DISPLAY_NAME = "stp.properties.application.display.name";
    public static final String APPLICATION_HOME = "stp.properties.application.home";
    public static final String APPLICATION_INFO = "stp.properties.application.info";
    public static final String APPLICATION_PROPERTIES = "stp.properties.application.properties";
    public static final String APPLICATION_SERVER = "stp.properties.application.server";
    public static final String APPLICATION_START_TIME = "stp.properties.application.start.time";
    public static final String APPLICATION_TIME_ZONE = "stp.properties.application.time.zone";
    public static final String APPLICATION_UPTIME = "stp.properties.application.uptime";
    public static final String APPLICATION_VERSION = "stp.properties.application.version";
    public static final String ATTACHMENT_DATA_STORE = "stp.properties.attachment.data.store";
    public static final String ATTACHMENT_MAX_SIZE = "stp.properties.attachment.max.size";
    public static final String ATTACHMENT_UI_MAX = "stp.properties.attachment.ui.max";
    public static final String BACKUP_DATE_FORMAT_PATTERN = "stp.properties.backup.date.format.pattern";
    public static final String BACKUP_FILE_PREFIX = "stp.properties.backup.file.prefix";
    public static final String BACKUP_PATH = "stp.properties.backup.path";
    public static final String CACHE_DIRECTORY = "stp.properties.cache.directory";
    public static final String CAPTCHA_ENABLED = "stp.properties.captcha.enabled";
    public static final String CAPTCHA_GROUPS = "stp.properties.captcha.groups";
    public static final String CONFIG_DIRECTORY = "stp.properties.config.directory";
    public static final String CONFIG_INFO = "stp.properties.config.info";
    public static final String CURRENT_DIRECTORY = "stp.properties.current.directory";
    public static final String DB = "stp.properties.db";
    public static final String DB_CONNECTION_TRANSACTION_ISOLATION = "stp.properties.db.connection.transaction.isolation";
    public static final String DB_CONNECTION_URL = "stp.properties.db.connection.url";
    public static final String DB_DIALECT = "stp.properties.db.dialect";
    public static final String DB_DRIVER_CLASS = "stp.properties.db.driver.class";
    public static final String DB_DRIVER_NAME = "stp.properties.db.driver.name";
    public static final String DB_DRIVER_VERSION = "stp.properties.db.driver.version";
    public static final String DB_EXAMPLE_LATENCY = "stp.properties.db.example.Latency";
    public static final String DB_NAME = "stp.properties.db.name";
    public static final String DB_STATISTICS = "stp.properties.db.statistics";
    public static final String DB_VERSION = "stp.properties.db.version";
    public static final String DEFAULT_ENCODING = "stp.properties.default.encoding";
    public static final String FILE_SYSTEM_ENCODING = "stp.properties.file.system.encoding";
    public static final String FREE_DISK_SPACE = "stp.properties.free.disk.space";
    public static final String GLOBAL_DEFAULT_LOCALE = "stp.properties.global.default.locale";
    public static final String HOST_NAME = "stp.properties.host.name";
    public static final String INDEX_SIZE = "stp.properties.index.size";
    public static final String INDEXING_LANGUAGE = "stp.properties.indexing.language";
    public static final String INSTALLED_LANGUAGES = "stp.properties.languages.installed";
    public static final String IP_ADDRESS = "stp.properties.ip.address";
    public static final String JAVA_HEAP_ALLOCATED = "stp.properties.java.heap.allocated";
    public static final String JAVA_HEAP_AVAILABLE = "stp.properties.java.heap.available";
    public static final String JAVA_HEAP_FREE_ALLOCATED = "stp.properties.java.heap.free.allocated";
    public static final String JAVA_HEAP_MAX = "stp.properties.java.heap.max";
    public static final String JAVA_HEAP_PERCENT_USED = "stp.properties.java.heap.percent.used";
    public static final String JAVA_HEAP_USED = "stp.properties.java.heap.used";
    public static final String JAVA_MEMORY_FREE = "stp.java.memory.free";
    public static final String JAVA_PERMGEN_AVAILABLE = "stp.properties.java.permgen.available";
    public static final String JAVA_PERMGEN_MAX = "stp.properties.java.permgen.max";
    public static final String JAVA_PERMGEN_PERCENT_USED = "stp.properties.java.permgen.percent.used";
    public static final String JAVA_PERMGEN_USED = "stp.properties.java.permgen.used";
    public static final String JAVA_RUNTIME = "stp.properties.java.runtime";
    public static final String JAVA_VENDOR = "stp.properties.java.vendor";
    public static final String JAVA_VERSION = "stp.properties.java.version";
    public static final String JAVA_VM = "stp.properties.java.vm";
    public static final String JAVA_VM_ARGUMENTS = "stp.properties.java.vm.arguments";
    public static final String JAVA_VM_VENDOR = "stp.properties.java.vm.vendor";
    public static final String JAVA_VM_VERSION = "stp.properties.java.vm.version";
    public static final String LANGUAGE_COUNTRY = "stp.properties.languages.language.country";
    public static final String LANGUAGE_NAME = "stp.properties.languages.language.name";
    public static final String LANGUAGE_ABBREVIATION = "stp.properties.languages.language.abbreviation";
    public static final String LANGUAGES_DEFAULT = "stp.properties.languages.default";
    public static final String LANGUAGES_LANGUAGE = "stp.properties.languages.language";
    public static final String LICENSE = "stp.properties.license";
    public static final String LICENSE_ACTIVE_USERS = "stp.properties.license.users.active";
    public static final String LICENSE_DESCRIPTION = "stp.properties.license.description";
    public static final String LICENSE_EDITION = "stp.properties.license.edition";
    public static final String LICENSE_EXPIRES = "stp.properties.license.expires";
    public static final String LICENSE_EXPIRES_NONE = "stp.properties.license.expires.none";
    public static final String LICENSE_INFO = "stp.properties.license.info";
    public static final String LICENSE_NEVER_EXPIRES = "stp.properties.license.never.expires";
    public static final String LICENSE_ORGANISATION = "stp.properties.license.organisation";
    public static final String LICENSE_OWNER = "stp.properties.license.owner";
    public static final String LICENSE_PARTNER = "stp.properties.license.partner";
    public static final String LICENSE_PRODUCT = "stp.properties.license.product";
    public static final String LICENSE_PURCHASED = "stp.properties.license.purchased";
    public static final String LICENSE_SEN = "stp.properties.license.sen";
    public static final String LICENSE_SERVER_ID = "stp.properties.license.server.id";
    public static final String LICENSE_SUPPORT_PERIOD = "stp.properties.license.period";
    public static final String LICENSE_TYPE = "stp.properties.license.type";
    public static final String LICENSE_UNLIMITED_USERS = "stp.properties.license.unlimited.users";
    public static final String LICENSE_USERS = "stp.properties.license.users";
    public static final String LINK = "stp.properties.links";
    public static final String LINK_DISPLAY_URL = "stp.properties.links.display.url";
    public static final String LINK_ID = "stp.properties.links.id";
    public static final String LINK_NAME = "stp.properties.links.name";
    public static final String LINK_PRIMARY = "stp.properties.links.primary";
    public static final String LINK_RPC_URL = "stp.properties.links.rpc.url";
    public static final String LINK_TYPE = "stp.properties.links.type";
    public static final String LISTENERS = "stp.properties.listeners";
    public static final String LISTENERS_LISTENER = "stp.properties.listeners.listener";
    public static final String LISTENERS_LISTENER_CLAZZ = "stp.properties.listeners.listener.clazz";
    public static final String LISTENERS_LISTENER_NAME = "stp.properties.listeners.listener.name";
    public static final String LOG_DIRECTORY = "stp.properties.log.directory";
    public static final String MAIL = "stp.properties.mail";
    public static final String MAIL_SERVER_ADDRESS = "stp.properties.mail.server.address";
    public static final String MAIL_SERVER_HOSTNAME = "stp.properties.mail.server.hostname";
    public static final String MAIL_SERVER_PORT = "stp.properties.mail.server.port";
    public static final String MAIL_USERNAME = "stp.properties.mail.username";
    public static final String MAIL_USE_TLS = "stp.properties.mail.use.tls";
    public static final String MEMORY = "stp.properties.memory";
    public static final String MEMORY_STATISTICS = "stp.properties.memory.statistics";
    public static final String MEMORY_TOTAL = "stp.properties.memory.total";
    public static final String MEMORY_USED = "stp.properties.memory.used";
    public static final String MODZ = "stp.properties.modz";
    public static final String MODZ_FILE = "stp.properties.modz.file";
    public static final String MODZ_MODIFIED = "stp.properties.modz.modified";
    public static final String MODZ_REMOVED = "stp.properties.modz.removed";
    public static final String NOT_SET = "stp.properties.not.set";
    public static final String PATCHES = "stp.properties.patches";
    public static final String PATCHES_PATCH = "stp.properties.patches.patch";
    public static final String PATCHES_PATCH_DESCRIPTION = "stp.properties.patches.patch.description";
    public static final String PATCHES_PATCH_KEY = "stp.properties.patches.patch.key";
    public static final String PATH_INFO = "stp.properties.path.info";
    public static final String PLUGIN_BUNDLED = "stp.properties.plugins.plugin.bundled";
    public static final String PLUGIN_CACHE_DIRECTORY = "stp.properties.plugin.cache.directory";
    public static final String PLUGIN_DIRECTORY = "stp.properties.plugin.directory";
    public static final String PLUGIN_FRAMEWORK_VERSION = "stp.properties.plugins.plugin.framework.version";
    public static final String PLUGIN_KEY = "stp.properties.plugins.plugin.key";
    public static final String PLUGIN_NAME = "stp.properties.plugins.plugin.name";
    public static final String PLUGIN_STATUS = "stp.properties.plugins.plugin.status";
    public static final String PLUGIN_PROVIDED = "stp.properties.plugins.plugin.provided";
    public static final String PLUGIN_USER_INSTALLED = "stp.properties.plugins.plugin.user.installed";
    public static final String PLUGIN_VENDOR = "stp.properties.plugins.plugin.vendor";
    public static final String PLUGIN_VENDOR_URL = "stp.properties.plugins.plugin.vendor.url";
    public static final String PLUGIN_VERSION = "stp.properties.plugins.plugin.version";
    public static final String PLUGINS = "stp.properties.plugins";
    public static final String PLUGINS_PLUGIN = "stp.properties.plugins.plugin";
    public static final String PROJECT = "stp.properties.projects.project";
    public static final String PROJECT_DESCRIPTION = "stp.properties.projects.project.description";
    public static final String PROJECT_KEY = "stp.properties.projects.project.key";
    public static final String PROJECT_NAME = "stp.properties.projects.project.name";
    public static final String QUICKNAV_MAX_REQUESTS = "stp.properties.quicknav.max.requests";
    public static final String REPOSITORIES = "stp.properties.repositories";
    public static final String REPOSITORIES_REPOSITORY = "stp.properties.repository";
    public static final String REPOSITORIES_REPOSITORY_NAME = "stp.properties.repository.name";
    public static final String REPOSITORIES_REPOSITORY_STATE = "stp.properties.repository.state";
    public static final String REPOSITORIES_REPOSITORY_TYPE = "stp.properties.repository.type";
    public static final String REPOSITORIES_REPOSITORY_SIZE = "stp.properties.repository.size";
    public static final String REPOSITORIES_REPOSITORY_SLUG = "stp.properties.repository.slug";
    public static final String REPOSITORIES_REPOSITORY_STATUS_MESSAGE = "stp.properties.repository.status-message";
    public static final String RESOURCE_LIMITS = "stp.properties.resource.limits";
    public static final String RSS_MAX_ITEMS = "stp.properties.rss.max.items";
    public static final String SERVICES = "stp.properties.services";
    public static final String SERVICES_SERVICE = "stp.properties.services.service";
    public static final String SERVICES_SERVICE_DELAY = "stp.properties.services.service.delay";
    public static final String SERVICES_SERVICE_DESCRIPTION = "stp.properties.services.service.description";
    public static final String SERVICES_SERVICE_LAST_RUN = "stp.properties.services.service.last.run";
    public static final String SERVICES_SERVICE_NAME = "stp.properties.services.service.name";
    public static final String SERVICES_SERVICE_STATUS = "stp.properties.services.service.status";
    public static final String SOURCE_CONTROL = "stp.properties.source.control";
    public static final String SSO_HEADER_CATEGORY = "stp.properties.sso";
    public static final String SSO_CONFIGURATION = "stp.properties.sso.configuration";
    public static final String SSO_TYPE = "stp.properties.sso.configuration.type";
    public static final String SSO_NAME = "stp.properties.sso.configuration.name";
    public static final String SSO_ENABLED = "stp.properties.sso.configuration.enabled";
    public static final String SSO_CUSTOMER_LOGINS = "stp.properties.sso.configuration.customer.logins";
    public static final String SSO_BUTTON_TEXT = "stp.properties.sso.configuration.button.text";
    public static final String SSO_LAST_UPDATED = "stp.properties.sso.configuration.last.updated";
    public static final String SSO_IDP_TYPE = "stp.properties.sso.configuration.idp.type";
    public static final String SSO_GENERIC_SHOW_LOGIN_FORM = "stp.properties.sso.generic.show.login.form";
    public static final String SSO_GENERIC_SHOW_LOGIN_FORM_JSM = "stp.properties.sso.generic.show.login.form.JSM";
    public static final String SSO_GENERIC_AUTH_FALLBACK = "stp.properties.sso.generic.auth.fallback";
    public static final String SSO_SETTINGS_ISSUER = "stp.properties.sso.settings.issuer";
    public static final String SSO_SETTINGS_URL = "stp.properties.sso.settings.identity.provider.url";
    public static final String SSO_SETTINGS_USERNAME_MAPPING = "stp.properties.sso.settings.username.mapping";
    public static final String SSO_ADDITIONAL_SCOPES = "stp.properties.sso.settings.additional.scopes";
    public static final String SSO_ADDITIONAL_SCOPE = "stp.properties.sso.settings.additional.scopes.scope";
    public static final String SSO_ADDITIONAL_SETTINGS_CATEGORY = "stp.properties.sso.additional.settings";
    public static final String SSO_ADDITIONAL_SETTINGS_ENABLED = "stp.properties.additional.settings.is.enabled";
    public static final String SSO_ADDITIONAL_SETTINGS_AUTHORIZATION_ENDPOINT = "stp.properties.sso.additional.settings.authorization.endpoint";
    public static final String SSO_ADDITIONAL_SETTINGS_TOKEN_ENDPOINT = "stp.properties.sso.additional.settings.token.endpoint";
    public static final String SSO_ADDITIONAL_SETTINGS_USERINFO_ENDPOINT = "stp.properties.sso.additional.settings.userinfo.endpoint";
    public static final String SSO_JIT_CATEGORY = "stp.properties.sso.jit.provisioning";
    public static final String SSO_JIT_ENABLED = "stp.properties.sso.jit.provisioning.enabled";
    public static final String SSO_JIT_NAME = "stp.properties.sso.jit.provisioning.display.name";
    public static final String SSO_JIT_EMAIL = "stp.properties.sso.jit.provisioning.email";
    public static final String SSO_JIT_GROUPS = "stp.properties.sso.jit.provisioning.groups";
    public static final String SSO_BEHAVIOUR_REMEMBER_LOGINS = "stp.properties.sso.behaviour.remember.user.logins";
    public static final String STATISTICS = "stp.properties.statistics";
    public static final String SYSTEM = "stp.properties.system";
    public static final String SYSTEM_AVAILABLE_PROCESSORS = "stp.properties.system.available.processors";
    public static final String SYSTEM_DATE = "stp.properties.system.date";
    public static final String SYSTEM_ENCODING = "stp.properties.system.encoding";
    public static final String SYSTEM_INFO = "stp.properties.system.info";
    public static final String SYSTEM_LANGUAGE = "stp.properties.system.language";
    public static final String SYSTEM_OS = "stp.properties.os";
    public static final String SYSTEM_OS_ARCH = "stp.properties.os.architecture";
    public static final String SYSTEM_OS_VERSION = "stp.properties.os.version";
    public static final String SYSTEM_OS_DISTRIBUTION = "stp.properties.os.distribution";
    public static final String SYSTEM_TIME = "stp.properties.system.time";
    public static final String SYSTEM_TIMEZONE = "stp.properties.system.time";
    public static final String SYSTEM_WORKING_DIRECTORY = "stp.properties.system.working.directory";
    public static final String TEMP_DIRECTORY = "stp.properties.system.temp.directory";
    public static final String TIMEZONE = "stp.properties.timezone";
    public static final String UPGRADE = "stp.properties.upgrade";
    public static final String UPGRADE_BUILD = "stp.properties.upgrade.build";
    public static final String UPGRADE_TIME = "stp.properties.upgrade.time";
    public static final String UPGRADE_VERSION = "stp.properties.upgrade.version";
    public static final String USAGE = "stp.properties.usage";
    public static final String USAGE_INDEX_SIZE = "stp.properties.usage.index.size";
    public static final String USAGE_LOCAL_GROUPS = "stp.properties.usage.local.groups";
    public static final String USAGE_LOCAL_USERS = "stp.properties.usage.local.users";
    public static final String USER_HOME = "stp.properties.user.home";
    public static final String USER_LOCALE = "stp.properties.user.locale";
    public static final String USER_MANAGEMENT = "stp.properties.user.management";
    public static final String USER_NAME = "stp.properties.user.name";
    public static final String USER_TIMEZONE = "stp.properties.user.timezone";
    public static final String ZIP_INCLUDE_MODZ = "stp.zip.include.modz";
    public static final String ZIP_INCLUDE_MODZ_DESCRIPTION = "stp.zip.include.modz.description";
    public static final String ZIP_INCLUDE_CLOUD_MIGRATION_LOGS = "stp.zip.include.cloud-migration.logs";
    public static final String ZIP_INCLUDE_CLOUD_MIGRATION_LOGS_DESCRIPTION = "stp.zip.include.cloud-migration.logs.description";
    public static final String UPGRADE_RECOVERY_FILES = "stp.properties.upgrade.recovery.files";
    public static final String UPGRADE_RECOVERY_FILE = "stp.properties.upgrade.recovery.file";
    public static final String UPGRADE_RECOVERY_FILE_NAME = "stp.properties.upgrade.recovery.file.name";
    public static final String UPGRADE_RECOVERY_FILE_DATE = "stp.properties.upgrade.recovery.file.date";
    public static final String UPGRADE_RECOVERY_FILE_SIZE = "stp.properties.upgrade.recovery.file.size";
    public static final String SYNCHRONY_CONFIGURATION = "stp.properties.synchrony.configuration";
    public static final String SYNCHRONY_ENABLED = "stp.properties.synchrony.enabled";
    public static final String SYNCHRONY_EXPLICITLY_DISABLED = "stp.properties.synchrony.explicitly.disabled";
    public static final String SYNCHRONY_EXTERNAL_URL = "stp.properties.synchrony.external.url";
    public static final String SYNCHRONY_INTERNAL_URL = "stp.properties.synchrony.internal.url";
    public static final String SYNCHRONY_INTERNAL_PORT = "stp.properties.synchrony.internal.port";
    public static final String SYNCHRONY_PROD_OVERRIDE = "stp.properties.synchrony.production.override";
    public static final String SYNCHRONY_PROXY_ENABLED = "stp.properties.synchrony.proxy.enabled";
    public static final String SYNCHRONY_PROXY_RUNNING = "stp.properties.synchrony.proxy.running";
    public static final String SYNCHRONY_RESOURCES_URL = "stp.properties.synchrony.resources.url";
    public static final String SYNCHRONY_SHARED_DRAFT_ENABLED = "stp.properties.synchrony.shared.drafts.enabled";
    public static final String SYNCHRONY_SHARED_DRAFT_EXPLICITLY_DISABLED = "stp.properties.synchrony.shared.drafts.explicitly.disabled";
    public static final String SYNCHRONY_ENVIRONMENT = "stp.properties.synchrony.environment";
    public static final String JDBC_URL = "stp.properties.synchrony.jdbc.url";
    public static final String JDBC_USER = "stp.properties.synchrony.jdbc.user";
    public static final String JDBC_PASSWORD = "stp.properties.synchrony.jdbc.password";
    public static final String JWT_PUBLIC_KEY = "stp.properties.synchrony.jwt.public.key";
    public static final String JWT_PRIVATE_KEY = "stp.properties.synchrony.jwt.private.key";
    public static final String SERVICE_URL = "stp.properties.synchrony.service.url";
    public static final String SYNCHRONY_PORT = "stp.properties.synchrony.port";
    public static final String CONTEXT_PATH = "stp.properties.synchrony.context.path";
    public static final String SYNCHRONY_MEMORY_MAX = "stp.properties.synchrony.memory.max";
    public static final String SYNCHRONY_EXTRACT_DIR = "stp.properties.synchrony.extract.dir";
    public static final String SYNCHRONY_WORKING_DIR = "stp.properties.synchrony.working.dir";
    public static final String CDN_CONFIGURATION = "stp.properties.cdn.configuration";
    public static final String CDN_ENABLED = "stp.properties.cdn.enabled";
    public static final String CDN_URL = "stp.properties.cdn.url";

    public List<ScanItem> getApplicationLogFilePaths();

    public File getPrimaryApplicationLog();

    public SisyphusPatternSource getPatternSource() throws IOException, ClassNotFoundException;

    public String getApplicationHome();

    public String getLocalApplicationHome();

    public String getApplicationName();

    @Nonnull
    public Optional<String> getInstanceTitle();

    public String getApplicationVersion();

    public String getApplicationSEN();

    public String getApplicationServerID();

    public Date getApplicationBuildDate();

    public String getText(String var1);

    public String getText(String var1, Serializable ... var2);

    public String getCreateSupportRequestEmail();

    @Nonnull
    public List<SupportZipBundle> getSupportZipBundles();

    @Nonnull
    public Set<String> getDefaultBundleKeys();

    public XmlSupportDataFormatter getXmlSupportDataFormatter();

    @Nonnull
    public List<SupportZipBundle> getSelectedSupportZipBundles(HttpServletRequest var1);

    public PropertyStore loadProperties(SupportDataDetail var1);

    public TemplateRenderer getTemplateRenderer();

    public String getMailQueueURL(HttpServletRequest var1);

    public boolean isMailExceptionAvailable();

    public String getBaseURL(HttpServletRequest var1);

    public String getBaseURL(UrlMode var1);

    public String getMailServerConfigurationURL(HttpServletRequest var1);

    public FileSanitizer getFileSanitizer();

    public String saveProperties(SupportDataDetail var1);

    public List<String> getSystemWarnings();

    @Nonnull
    public File getTempDirectory();

    @Nonnull
    public File getExportDirectory();

    @Nonnull
    public Optional<File> getExportFile(String var1);

    public String getApplicationLogDir();

    public String getFromAddress();

    public ApplicationLicenseInfo getLicenseInfo();

    public String getAdminLicenseUrl();

    public Pattern getApplicationRestartPattern();

    public String getPlatformId();

    public String getTimeZoneRelativeToGMT();

    public String getStpVersion();
}

