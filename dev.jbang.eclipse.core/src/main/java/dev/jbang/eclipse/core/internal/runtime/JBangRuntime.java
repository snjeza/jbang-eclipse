package dev.jbang.eclipse.core.internal.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class JBangRuntime {
	
	public static final String SYSTEM = "System";

	private static final IPath EXECUTABLE;

	static {
		if (System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).contains("windows")) {
			EXECUTABLE = new Path("jbang.cmd");
		} else {
			EXECUTABLE = new Path("jbang");
		}
	}

	private IPath location;
	private String version;

	private String name;

	public JBangRuntime() {
		name = SYSTEM;
	}

	public JBangRuntime(String name, String path) {
		this(name, path, null);
	}
	
	public JBangRuntime(String name, String path, String version) {
		this(path);
		this.name = name;
		this.version = version;
	}
	
	public JBangRuntime(String path) {
		this();
		if (path != null && !path.isBlank()) {
			if (path.startsWith("~")) {
				path = System.getProperty("user.home") + path.substring(1);
			}
			this.location = new Path(path);
		}
	}

	public JBangRuntime(IPath location) {
		this.location = location;
	}

	public IPath getLocation() {
		return location;
	}

	public IPath getExecutable() {
		return location == null ? EXECUTABLE : location.append("bin").append(EXECUTABLE);
	}

	@Override
	public String toString() {
		return getExecutable().toOSString();
	}
	
	public boolean isValid() {
		//FIXME Doesn't make sense for jbang found on the PATH
		return Files.isExecutable(getExecutable().toFile().toPath());
	}
	
	public String detectVersion(IProgressMonitor monitor) {
		var versionFile = java.nio.file.Path.of(location.toOSString(), "version.txt"); 
		//Since JBang 0.83.0
		if (Files.exists(versionFile)) {
			try {
				var v = Files.readString(versionFile);
				if (v != null) {
					version = v;
					return version;
				}
			} catch (IOException O_o) {
				//ignore
			}
		}
		// Can't read version.txt for some reason (likely older than 0.83.0)
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(getExecutable().toOSString(), "version");
			var env = processBuilder.environment();
			var processJavaHome = env.get("JAVA_HOME");
			if (processJavaHome == null || processJavaHome.isBlank()) {
				String javaHome = System.getProperty("java.home");
				if (javaHome != null) {
					env.put("JAVA_HOME", javaHome);
				}
			}
			
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			try (BufferedReader processOutputReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));) {
				String v = processOutputReader.readLine();
				process.waitFor();
				if (!v.toLowerCase().contains("error")) {
					version = v;					
				}
				
			}
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
		return version;
	}
	
	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}
	
	public boolean isEditable() {
		return !SYSTEM.equals(this.name);
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(location, name, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JBangRuntime other = (JBangRuntime) obj;
		return Objects.equals(location, other.location) && Objects.equals(name, other.name)
				&& Objects.equals(version, other.version);
	}
	
}
