package fi.iki.elonen;

import java.util.Map;

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
                String commandString = source.substring(command + 5, source.indexOf(' ', command));

                if ("exec".equals(commandString)) {
                    String externalExecutable = extractAttrValue(source, "cmd", command, endComment);
                    if (externalExecutable != null) {
                        output.append(commandExec.exec(externalExecutable));
                    } else {
                        String javaExecutable = extractAttrValue(source, "java", command, endComment);
                        String param = extractAttrValue(source, "param", command, endComment);
                        try {
                            output.append(commandExec.exec(Class.forName(javaExecutable), param));
                        } catch (ClassNotFoundException ignored) {}
                    }
                } else if ("set".equals(commandString)) {
                    String varname = extractAttrValue(source, "var", command, endComment);
                    String varValue = extractAttrValue(source, "value", command+varname.length()+6, endComment);

                    if (varname != null && varValue != null) {
                        env.put(varname, varValue);
                    }
                } else if ("echo".equals(commandString)) {
                    String varname = extractAttrValue(source, "var", command, endComment);
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

    private String extractAttrValue(String source, String attrName, int start, int end) {
        int attrNameLength = attrName.length();
        int var = source.indexOf(attrName +"=\"", start);
        String foo = null;
        if (var > -1) {
            int varEnd = source.indexOf("\"", var + attrNameLength + 2);
            if (varEnd < end && varEnd > -1) {
                foo = source.substring(var + attrNameLength + 2, varEnd);
            }
        }
        return foo;
    }
}
