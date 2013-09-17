package fi.iki.elonen;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/15/13 at 10:22 PM
 */
public class SIIParserEchoCommandTest {

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
    public void testSourceWithNoImports() {
        String source = "<html><head></head><body></body></html>";

        String output = parser.parse(source);

        assertEquals(source, output);
    }

    @Test
    public void testSourceWithCommentsButNoIncludes() {
        String source = "<html><head></head><body><!-- foo --></body></html>";

        String output = parser.parse(source);

        assertEquals(source, output);
    }

    @Test
    public void testEchoNonExistentVariable() {
        String source = "<html><head></head><body><!--#echo var=\"foo\" --></body></html>";
        String expected = "<html><head></head><body>(none)</body></html>";

        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testEchoDocumentName() {
        String source = "<html><head></head><body><!--#echo var=\"DOCUMENT_NAME\" --></body></html>";
        String expected = "<html><head></head><body>foo</body></html>";
        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testEchoDocumentUri() {
        String source = "<html><head></head><body><!--#echo var=\"DOCUMENT_URI\" --></body></html>";
        String expected = "<html><head></head><body>/foo/index.shtml</body></html>";
        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testEchoDateGMT() {
        String source = "<html><head></head><body><!--#echo var=\"DATE_GMT\" --></body></html>";
        String expected = "<html><head></head><body>"+gmt.getTime().toString()+"</body></html>";
        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testEchoDateLocal() {
        String source = "<html><head></head><body><!--#echo var=\"DATE_LOCAL\" --></body></html>";
        String expected = "<html><head></head><body>"+local.getTime().toString()+"</body></html>";
        String output = parser.parse(source);

        assertEquals(expected, output);
    }

    @Test
    public void testMultipleEchoStatements() {
        String source = "<html><head></head><body><!--#echo var=\"DOCUMENT_URI\" --> xx <!--#echo var=\"DATE_LOCAL\" --></body></html>";
        String expected = "<html><head></head><body>/foo/index.shtml xx "+local.getTime().toString()+"</body></html>";
        String output = parser.parse(source);

        assertEquals(expected, output);
    }

}
