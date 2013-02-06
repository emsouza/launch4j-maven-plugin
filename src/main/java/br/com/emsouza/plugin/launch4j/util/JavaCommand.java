package br.com.emsouza.plugin.launch4j.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineTimeOutException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author Eduardo Matos de Souza - SIC/NDS <br>
 *         D�gitro - 14/09/2012 <br>
 *         <a href="mailto:eduardo.souza@digitro.com.br">eduardo.souza@digitro.com.br</a>
 */
public class JavaCommand {

	private String className;

	private String baseDir;;

	private List<String> jvmargs = new ArrayList<String>();

	private List<File> classpath = new LinkedList<File>();

	private List<String> args = new ArrayList<String>();

	private Properties systemProperties = new Properties();

	private Properties env = new Properties();

	private String jvm;

	protected Log log;

	/**
	 * A plexus-util StreamConsumer to redirect messages to plugin log
	 */
	protected StreamConsumer out = new StreamConsumer() {
		@Override
		public void consumeLine(String line) {
			log.info(line);
		}
	};

	/**
	 * A plexus-util StreamConsumer to redirect errors to plugin log
	 */
	private StreamConsumer err = new StreamConsumer() {
		@Override
		public void consumeLine(String line) {
			log.error(line);
		}
	};

	public JavaCommand(Log log, String className) {
		this.log = log;
		this.className = className;
	}

	public void addClasspath(List<File> classpath) {
		for (File file : classpath) {
			this.classpath.add(file);
		}
	}

	public void addJVMArgs(List<String> jvmargs) {
		for (String arg : jvmargs) {
			this.jvmargs.add(arg);
		}
	}

	public void arg(String arg) {
		args.add(arg);
	}

	public void arg(String arg, String value) {
		args.add(arg);
		args.add(value);
	}

	public void systemProperty(String name, String value) {
		systemProperties.setProperty(name, value);
	}

	public void environment(String name, String value) {
		env.setProperty(name, value);
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public int execute() {

		List<String> command = new ArrayList<String>();

		command.addAll(jvmargs);

		command.add("-classpath");
		List<String> path = new ArrayList<String>(classpath.size());
		for (File file : classpath) {
			path.add(file.getAbsolutePath());
		}

		command.add(StringUtils.join(path.iterator(), File.pathSeparator));
		if (systemProperties != null) {
			for (Object entry : systemProperties.keySet()) {
				command.add("-D" + entry + "=" + systemProperties.get(entry));
			}
		}

		command.add(className);

		command.addAll(args);

		try {
			String[] arguments = command.toArray(new String[command.size()]);

			// On windows, the default Shell will fall into command line length limitation issue
			// On Unixes, not using a Shell breaks the classpath (NoClassDefFoundError: com/google/gwt/dev/Compiler).
			Commandline cmd = Os.isFamily(Os.FAMILY_WINDOWS) ? new Commandline(new JavaShell()) : new Commandline();

			cmd.setExecutable(getJavaCommand());

			cmd.setWorkingDirectory(getBaseDir());

			cmd.addArguments(arguments);

			if (env != null) {
				for (Object entry : env.keySet()) {
					cmd.addEnvironment((String) entry, (String) env.get(entry));
				}
			}

			return CommandLineUtils.executeCommandLine(cmd, out, err);

		} catch (CommandLineTimeOutException e) {
			err.consumeLine("Process Timeout.");
		} catch (CommandLineException e) {
			err.consumeLine("Process execution error.");
		}
		return 1;
	}

	public void withinClasspathFirst(File oophmJar) {
		classpath.add(0, oophmJar);
	}

	private String getJavaCommand() {
		if (StringUtils.isEmpty(jvm)) {
			// use the same JVM as the one used to run Maven (the "java.home" one)
			jvm = System.getProperty("java.home");
		}

		// does-it exists ? is-it a directory or a path to a java executable ?
		File jvmFile = new File(jvm);
		if (jvmFile.isDirectory()) {
			// it's a directory we construct the path to the java executable
			return jvmFile.getAbsolutePath() + File.separator + "bin" + File.separator + "java";
		}
		// log.debug( "use jvm " + jvm );
		return jvm;
	}
}