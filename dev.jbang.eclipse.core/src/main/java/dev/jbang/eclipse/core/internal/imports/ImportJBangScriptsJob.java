package dev.jbang.eclipse.core.internal.imports;

import java.nio.file.Path;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import dev.jbang.eclipse.core.JBangCorePlugin;
import dev.jbang.eclipse.core.internal.JBangFileUtils;
import dev.jbang.eclipse.core.internal.project.JBangProjectConfiguration;
import dev.jbang.eclipse.core.internal.runtime.JBangRuntimesDiscoveryJob;

public class ImportJBangScriptsJob extends WorkspaceJob	 {

	private Path[] scripts;

	public ImportJBangScriptsJob(Path[] scripts) {
		super("Import JBang scripts");
		this.scripts = scripts;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) {
		if (scripts == null || scripts.length == 0) {
			return Status.OK_STATUS;
		}
		try {
			Job.getJobManager().join(JBangRuntimesDiscoveryJob.class, monitor);
		} catch (Exception e) {
			//ignore
		}
		var projectManager = JBangCorePlugin.getJBangManager().getProjectConfigurationManager();
		var configuration = new JBangProjectConfiguration();
		for (Path script : scripts) {
			try {
				if (JBangFileUtils.isJBangFile(script) || JBangFileUtils.isJBangBuildFile(script)) {
					projectManager.createJBangProject(script, configuration, monitor);
				}
			} catch (Exception e) {
				return toStatus("Error configuring JBang Script", e);
			}
		}
		
		return Status.OK_STATUS;
	}

	private IStatus toStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, JBangCorePlugin.PLUGIN_ID, msg, e);
	}
	
	

}
