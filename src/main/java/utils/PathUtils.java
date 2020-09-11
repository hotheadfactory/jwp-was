package utils;

public class PathUtils {
    public static String parsePath(String line) {
        if ("".equals(line)) {
            throw new IllegalArgumentException();
        }
        String[] tokens = line.split(" ");
        return tokens[1];
    }

    public static String parseMethod(String line) {
        if ("".equals(line)) {
            throw new IllegalArgumentException();
        }
        String[] tokens = line.split(" ");
        return tokens[0];
    }
}
