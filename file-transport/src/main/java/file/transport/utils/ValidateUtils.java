package file.transport.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.util.InetAddressUtils;

public class ValidateUtils {

    public static boolean isIPv4(String ipv4) {
        return InetAddressUtils.isIPv4Address(ipv4);
    }

    public static boolean isNotIPv4(String ipv4) {
        return !isIPv4(ipv4);
    }

    public static boolean isIPv6(String ipv6) {
        return InetAddressUtils.isIPv6Address(ipv6);
    }

    public static boolean isNotIPv6(String ipv6) {
        return !isIPv6(ipv6);
    }

    public static boolean isNumeric(String str) {
        return StringUtils.isNumeric(str);
    }

    public static boolean isNotNumeric(String str) {
        return !isNumeric(str);
    }
}