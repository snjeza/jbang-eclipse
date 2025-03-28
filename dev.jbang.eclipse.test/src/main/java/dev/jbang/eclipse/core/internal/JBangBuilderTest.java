package dev.jbang.eclipse.core.internal;

import static dev.jbang.eclipse.core.internal.utils.ClasspathHelpers.assertGenerateParameters;
import static dev.jbang.eclipse.core.internal.utils.ClasspathHelpers.assertJava;
import static dev.jbang.eclipse.core.internal.utils.ImportScriptUtils.importJBangScript;
import static dev.jbang.eclipse.core.internal.utils.JobHelpers.waitForJobsToComplete;
import static dev.jbang.eclipse.core.internal.utils.WorkspaceHelpers.assertNoErrors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.jbang.eclipse.core.internal.project.JBangProject;

public class JBangBuilderTest extends AbstractJBangTest {
	
	private JBangProject jbp;
	
	@BeforeEach
	public void importScript() throws Exception {
		jbp = importJBangScript("hello.java");
		assertNotNull(jbp);
		assertEquals("hello.java", jbp.getProject().getName());
		waitForJobsToComplete();
		assertNoErrors(jbp.getProject());
	}

	@Test
	public void enableParameters() throws Exception {
		IProject project = jbp.getProject();
		assertGenerateParameters(project, false);
		
		IFile script = jbp.getMainSourceFile();
		String content = ResourceUtil.getContent(script);
		String parametersOptions = "//JAVAC_OPTIONS -parameters\n";
		ResourceUtil.setContent(script, content.replace("//JAVA 11", "//JAVA 11\n"+parametersOptions));
		
		waitForJobsToComplete();
		assertNoErrors(project);
		assertGenerateParameters(project, true);
	}
	
	@Test
	public void changeJava() throws Exception {
		IProject project = jbp.getProject();
		assertJava(project, "11");
		
		IFile script = jbp.getMainSourceFile();
		String content = ResourceUtil.getContent(script);
		ResourceUtil.setContent(script, content.replace("//JAVA 11", "//JAVA 8"));
		
		waitForJobsToComplete();
		assertNoErrors(project);
		assertJava(project, "1.8");
	}
}
