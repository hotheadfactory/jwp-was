package webserver;

import db.DataBase;
import http.RequestBody;
import http.RequestHeader;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            router(br, dos);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    private void router(BufferedReader br, DataOutputStream dos) throws IOException, URISyntaxException {
        RequestHeader requestHeader = new RequestHeader(br);

        if (requestHeader.getMethod().equals("GET")) {
            resolveGet(requestHeader, dos);
        }
        if (requestHeader.getMethod().equals("POST")) {
            RequestBody requestBody = new RequestBody(br, requestHeader.getContentLength());
            resolvePost(requestHeader, requestBody, dos);
        }
    }

    private void resolveGet(RequestHeader requestHeader, DataOutputStream dos) throws IOException, URISyntaxException {
        byte[] body = FileIoUtils.loadFileFromClasspath("./templates" + requestHeader.getPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void resolvePost(RequestHeader requestHeader, RequestBody requestBody, DataOutputStream dos) {
        if (requestHeader.getPath().equals("/user/create")) {
            Map<String, String> params = requestBody.getParams();
            User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
            DataBase.addUser(user);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
