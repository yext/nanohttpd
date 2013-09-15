package fi.iki.elonen;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/13/13 at 4:01 AM
 */
public class SSIWebServerPluginInfo implements WebServerPluginInfo {
    @Override public String[] getMimeTypes() {
        return new String[]{"text/x-server-parsed-html"};
    }

    @Override public String[] getIndexFilesForMimeType(String mime) {
        return new String[]{"index.shtml"};
    }

    @Override public WebServerPlugin getWebServerPlugin(String mimeType) {
        return new SSIWebServerPlugin();
    }
}
