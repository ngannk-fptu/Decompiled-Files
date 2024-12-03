/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.build;

import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.Version;
import aQute.service.reporter.Messages;
import java.io.File;
import java.util.List;
import java.util.SortedMap;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ProjectMessages
extends Messages {
    public Messages.ERROR InvalidStrategy(String var1, String[] var2);

    public Messages.ERROR RepoTooFewArguments(String var1, String[] var2);

    public Messages.ERROR AddingNonExistentFileToClassPath_(File var1);

    public Messages.ERROR Deploying(Exception var1);

    public Messages.ERROR DeployingFile_On_Exception_(File var1, String var2, Exception var3);

    public Messages.ERROR MissingPom();

    public Messages.ERROR FoundVersions_ForStrategy_ButNoProvider(SortedMap<Version, RepositoryPlugin> var1, Strategy var2);

    public Messages.ERROR NoSuchProject(String var1, String var2);

    public Messages.ERROR CircularDependencyContext_Message_(String var1, String var2);

    public Messages.ERROR IncompatibleHandler_For_(String var1, String var2);

    public Messages.ERROR NoOutputDirectory_(File var1);

    public Messages.ERROR MissingDependson_(String var1);

    public Messages.ERROR NoNameForReleaseRepository();

    public Messages.ERROR ReleaseRepository_NotFoundIn_(String var1, List<RepositoryPlugin> var2);

    public Messages.ERROR Release_Into_Exception_(String var1, RepositoryPlugin var2, Exception var3);

    public Messages.ERROR NoScripters_(String var1);

    public Messages.ERROR SettingPackageInfoException_(Exception var1);

    public Messages.ERROR ConfusedNoContainerFile();
}

