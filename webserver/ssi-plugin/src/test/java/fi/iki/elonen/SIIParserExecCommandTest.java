package fi.iki.elonen;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/15/13 at 10:22 PM
 */
public class SIIParserExecCommandTest {

    private SSIParser parser;
    private Map<String,String> env;
    private Calendar local;
    private Calendar gmt;
    private SSIParserFactory parserFactory;
    private CommandExec exec;

    @Before
    public void setUp() throws Exception {
        env = new HashMap<String, String>();
        gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(System.currentTimeMillis());
        local = Calendar.getInstance(TimeZone.getDefault());
        local.setTimeInMillis(System.currentTimeMillis());

        env.put("DATE_GMT", gmt.getTime().toString());
        env.put("DATE_LOCAL", local.getTime().toString());
        env.put("DOCUMENT_NAME", "foo");
        env.put("DOCUMENT_URI", "/foo/index.shtml");

        parserFactory = mock(SSIParserFactory.class);
        exec = mock(CommandExec.class);
        when(parserFactory.getGmt()).thenReturn(gmt);
        when(parserFactory.getLocal()).thenReturn(local);
        when(parserFactory.create(anyString(), any(File.class))).thenReturn(new SSIParser(env, exec));

        parser = parserFactory.create("/foo/index.shtml", new File("."));
    }

    @Test
    public void testExecIsCalledForCommandsVariable() {
        when(exec.exec("foo")).thenReturn("bar");

        String source = "<html><head></head><body><!--#exec cmd=\"foo\" --></body></html>";
        String expected = "<html><head></head><body>bar</body></html>";

        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testExecIsCalledForJavaCommand() {
        when(exec.execJava(eq(java.lang.Integer.class), any(Map.class))).thenReturn("100");

        String source = "<html><head></head><body><!--#exec java=\"java.lang.Integer\" param=\"100\" --></body></html>";
        String expected = "<html><head></head><body>100</body></html>";

        String output = parser.parse(source);

        assertEquals(expected, output);
    }

}
