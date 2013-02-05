package br.com.emsouza.plugin.launch4j.util;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.Shell;

/**
 * @author Eduardo Matos de Souza - SIC/NDS <br>
 *         D�gitro - 14/09/2012 <br>
 *         <a href="mailto:eduardo.souza@digitro.com.br">eduardo.souza@digitro.com.br</a>
 */
public class JavaShell extends Shell {

	@Override
	protected List<String> getRawCommandLine(String executable, String[] arguments) {

		List<String> commandLine = new ArrayList<String>();
		if (executable != null) {
			commandLine.add(executable);
		}

		for (String arg : arguments) {

			if (isQuotedArgumentsEnabled()) {
				char[] escapeChars = getEscapeChars(isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped());
				commandLine.add(StringUtils.quoteAndEscape(arg, getArgumentQuoteDelimiter(), escapeChars, getQuotingTriggerChars(), '\\', false));
			} else {
				commandLine.add(arg);
			}
		}
		return commandLine;
	}
}