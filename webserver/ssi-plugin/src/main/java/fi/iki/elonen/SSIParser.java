package fi.iki.elonen;

import java.util.*;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/15/13 at 10:25 PM
 */
public class SSIParser {
    private Map<String, String> env;
    private CommandExec commandExec;

    public SSIParser(Map<String, String> env, CommandExec commandExec) {
        this.env = env;
        this.commandExec = commandExec;
    }

    public String parse(String source) {
        StringBuilder output = new StringBuilder();
        int start = 0;
        while (start < source.length()) {
            int command = source.indexOf("<!--#", start);
            if (command == -1) {
                command = source.length();
                output.append(source.substring(start, command));
                start = command;
                continue;
            }

            int endComment = source.indexOf("-->", command);
            if (endComment > command) {
                output.append(source.substring(start, command));
                SsiComment parsed = new SsiComment(source.substring(command+5, endComment));
                String commandString = parsed.getCommand();

                if ("exec".equals(commandString)) {
                    String externalExecutable = parsed.getParam("cmd");
                    if (externalExecutable != null) {
                        output.append(commandExec.exec(externalExecutable));
                    } else {
                        String javaExecutable = parsed.getParam("java");
                        try {
                            output.append(commandExec.execJava(Class.forName(javaExecutable), parsed.getParameters()));
                        } catch (ClassNotFoundException ignored) {}
                    }
                } else if ("set".equals(commandString)) {
                    String varname = parsed.getParam("var");
                    String varValue = parsed.getParam("value");

                    if (varname != null && varValue != null) {
                        env.put(varname, varValue);
                    }
                } else if ("echo".equals(commandString)) {
                    String varname = parsed.getParam("var");
                    String varValue = null;
                    if (varname != null) {
                        varValue = env.get(varname);
                    }

                    output.append(varValue != null ? varValue : "(none)");
                }
                start = endComment + 3;
            }
        }
        return output.toString();
    }

    private class SsiComment {
        private String command;
        private Map<String, List<String>> parameters;

        public SsiComment(String text) {
            parameters = new HashMap<String, List<String>>();
            StringTokenizer tok = new StringTokenizer(text, " \t\n");
            if (tok.hasMoreTokens()) {
                command = tok.nextToken();
            }
            while (tok.hasMoreTokens()) {
                String kvp = tok.nextToken();
                int equals = kvp.indexOf('=');
                if (equals == -1) {
                    continue;
                }
                String key = kvp.substring(0,equals);
                String value = kvp.substring(equals+2, kvp.length()-1);
                List<String> paramValue = parameters.get(key);
                if (paramValue == null) {
                    paramValue = new ArrayList<String>();
                    parameters.put(key, paramValue);
                }
                paramValue.add(value);
            }
        }

        private String getCommand() {
            return command;
        }

        public String getParam(String param) {
            List<String> value = parameters.get(param);
            if (value == null || value.size() == 0) {
                return null;
            }
            return value.get(0);
        }

        public List<String> getParamValues(String param) {
            return parameters.get(param);
        }

        public Map<String, List<String>> getParameters() {
            return parameters;
        }
    }
}
