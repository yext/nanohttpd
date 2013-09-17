package fi.iki.elonen;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/15/13 at 11:31 PM
 */
public class SSIParserFactory {
    public SSIParser create(final String uri, final File rootDir) {
        return new SSIParser(new HashMap<String, String>() {{
            File f = new File(rootDir, uri);
            put("DATE_LOCAL", getLocal().getTime().toString());
            put("DATE_GMT", getGmt().getTime().toString());
            put("DOCUMENT_URI", uri);
            put("DOCUMENT_NAME", f.getName());
            try {
                put("DOCUMENT_PATH", f.getCanonicalPath());
            } catch (IOException ignored) {}
        }}, new CommandExec());
    }

    public Calendar getGmt() {
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(System.currentTimeMillis());
        return gmt;
    }

    public Calendar getLocal() {
        Calendar local = Calendar.getInstance(TimeZone.getDefault());
        local.setTimeInMillis(System.currentTimeMillis());
        return local;
    }
}
