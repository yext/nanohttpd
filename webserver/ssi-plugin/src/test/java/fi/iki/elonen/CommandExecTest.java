package fi.iki.elonen;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/21/13 at 6:54 AM
 */
public class CommandExecTest {

    private CommandExec commandExec;

    @Before
    public void setUp() throws Exception {
        commandExec = new CommandExec();
    }

    @Test
    public void testSimpleJavaExecPassingNull() {
        String output = commandExec.execJava(SimpleJavaCommand.class, null);
        assertEquals("foo", output);
    }

    @Test
    public void testSimpleJavaExecPassingEmptyParams() {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String output = commandExec.execJava(SimpleJavaCommand.class, params);
        assertEquals("foo", output);
    }

    @Test
    public void testJavaExecPassingParamsExactlyMatchingObject() {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("foo", Arrays.asList("123"));
        params.put("bar", Arrays.asList("456"));
        String output = commandExec.execJava(JavaCommandWithParameters.class, params);
        assertEquals("foo=123, bar=456", output);
    }

    @Test
    public void testJavaExecPassingMoreParamsThanObject() {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("foo", Arrays.asList("123"));
        params.put("bar", Arrays.asList("456"));
        params.put("baz", Arrays.asList("789"));
        String output = commandExec.execJava(JavaCommandWithParameters.class, params);
        assertEquals("foo=123, bar=456", output);
    }

    @Test
    public void testJavaExecPassingLessParamsThanObject() {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("foo", Arrays.asList("123"));
        String output = commandExec.execJava(JavaCommandWithParameters.class, params);
        assertEquals("foo=123, bar=", output);
    }

    public static class SimpleJavaCommand {
        public String toString() {
            return "foo";
        }
    }

    public static class JavaCommandWithParameters {
        String foo = "";
        String bar = "";

        public String toString() {
            return "foo="+foo+", bar="+bar;
        }
    }
}
