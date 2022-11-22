package unit.test.demo.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhisong.guan
 */
public enum LockMode {
    W, R, NONE;

    public static LockMode getMode(String m) {
        for (LockMode l : values()) {
            if (StringUtils.equalsIgnoreCase(l.name(), m)) {
                return l;
            }
        }
        return NONE;
    }
}
