package br.com.emsouza.plugin.launch4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import br.com.emsouza.plugin.launch4j.util.JavaCommand;

/**
 * Wraps a jar in a Windows executable.
 * 
 * @goal launch4j
 * @phase package
 */
public class Launch4jMojo extends AbstractMojo {

	/**
	 * The base of the current project.
	 * 
	 * @parameter default-value="${basedir}"
	 * @required
	 * @readonly
	 */
	private File basedir;

	/**
	 * The directory of Launch4j.
	 * 
	 * @parameter
	 */
	private String launch4jDir;

	/**
	 * The Launch4j config File.
	 * 
	 * @parameter
	 */
	private String launch4jConfig;

	@Override
	public void execute() throws MojoExecutionException {
		if (getLaunch4jAbsolutePath().exists()) {

			if (!FileUtils.fileExists(getAbsoluteConfigFile())) {
				throw new MojoExecutionException("The launch4j config file '" + launch4jConfig + "' cannot be read");
			} else {

				List<File> path = new ArrayList<File>();
				path.add(new File(launch4jDir, "launch4j.jar"));
				path.add(new File(launch4jDir, "lib/xstream.jar"));

				JavaCommand java = new JavaCommand(getLog(), "net.sf.launch4j.Main");

				java.setBaseDir(launch4jDir);

				java.addClasspath(path);

				java.arg(getAbsoluteConfigFile());

				java.execute();
			}

		} else {
			getLog().warn("The launch4j directory do not exist. Ignoring...");
		}
	}

	private File getLaunch4jAbsolutePath() {
		return new File(launch4jDir);
	}

	private String getAbsoluteConfigFile() {
		return new File(basedir, launch4jConfig).getAbsolutePath();
	}
}