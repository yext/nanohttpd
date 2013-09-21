package fi.iki.elonen;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/16/13 at 11:58 PM
 */
public class CommandExec {
    public String exec(String command) {
        return "";
    }

    public String execJava(Class command, Map<String, List<String>> params) {
        if (params == null || params.size() == 0) {
            return execJavaWithoutParams(command);
        }
        return execJavaWithParams(command, params);
    }

    private String execJavaWithoutParams(Class command) {
        String returnValue = "";
        try {
            returnValue = command.newInstance().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private String execJavaWithParams(Class command, Map<String, List<String>> params) {
        String returnValue = "";
        try {
            Object obj = command.newInstance();
            if (params != null && params.size() > 0) {
                Field[] fields = command.getDeclaredFields();
                for (Field f : fields) {
                    List<String> values = params.get(f.getName());
                    if (values != null) {
                        String value = values.get(0);
                        try {
                            f.setAccessible(true);
                            f.set(obj, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            returnValue = obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
