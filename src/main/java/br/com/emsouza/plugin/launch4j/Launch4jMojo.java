package br.com.emsouza.plugin.launch4j;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineTimeOutException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

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

	/**
	 * A plexus-util StreamConsumer to redirect messages to plugin log
	 */
	protected StreamConsumer out = new StreamConsumer() {
		@Override
		public void consumeLine(String line) {
			getLog().info(line);
		}
	};

	/**
	 * A plexus-util StreamConsumer to redirect errors to plugin log
	 */
	private StreamConsumer err = new StreamConsumer() {
		@Override
		public void consumeLine(String line) {
			getLog().error(line);
		}
	};

	@Override
	public void execute() throws MojoExecutionException {
		if (getLaunch4jAbsolutePath().exists()) {

			if (!FileUtils.fileExists(getAbsoluteConfigFile())) {
				throw new MojoExecutionException("The launch4j config file '" + launch4jConfig + "' cannot be read");
			} else {

				try {
					Commandline cmd = new Commandline();

					cmd.setWorkingDirectory(getLaunch4jAbsolutePath());

					cmd.setExecutable("java");

					cmd.addArguments(new String[] { "-cp", "launch4j.jar;lib/xstream.jar", "net.sf.launch4j.Main", getAbsoluteConfigFile() });

					CommandLineUtils.executeCommandLine(cmd, out, err);

				} catch (CommandLineTimeOutException e) {
					throw new MojoExecutionException("Process Timeout.", e);
				} catch (CommandLineException e) {
					throw new MojoExecutionException("Process execution error.", e);
				}
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