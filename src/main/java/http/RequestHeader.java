package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> params;

    public RequestHeader(BufferedReader br) throws IOException {
        params = new HashMap<>();
        String line = br.readLine();
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Line is empty.");
        }
        while ((line != null) && !line.isEmpty()) {
            String[] token = line.split(": ");
            if(token.length != 2) {
                throw new IllegalArgumentException("No value for the key: " + token[0]);
            }
            params.put(token[0].toLowerCase(), token[1]);
            line = br.readLine();
        }
    }

    public boolean containsValueOf(HeaderParam key, String value) {
        return this.params.get(key.getParamName()).contains(value);
    }

    public int getContentLength() {
        String contentLength = getHeaderParamValue(HeaderParam.CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getContentType() {
        return getHeaderParamValue(HeaderParam.CONTENT_TYPE);
    }

    private String getHeaderParamValue(HeaderParam headerParam) {
        return this.params.get(headerParam.getParamName());
    }
}
