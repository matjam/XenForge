package net.stupendous.xf;

import java.util.logging.Logger;

public class XenForgeLogger {
    private final Logger log;
    public boolean debugging = false;

    public XenForgeLogger(Logger log) {
        this.log = log;
    }

    public void info(String msg) {
        log.info(String.format("%s", msg));
    }

    public void info(String format, Object... args) {
        this.info(String.format(format, args));
    }

    public void warning(String msg) {
        log.warning(String.format("%s", msg));
    }

    public void warning(String format, Object... args) {
        this.warning(String.format(format, args));
    }

    public void severe(String msg) {
        log.severe(String.format("%s", msg));
    }

    public void severe(String format, Object... args) {
        this.severe(String.format(format, args));
    }

    public void debug(String msg) {
        if (debugging)
            log.info(String.format("%s", msg));
    }

    public void debug(String format, Object... args) {
        if (debugging)
            this.info(String.format(format, args));
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }
}
