package top.hequehua.swagger.config;

/**
 */
public enum RequestMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static RequestMethod[] all = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE};

    public static RequestMethod[] all() {
        return all;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}
