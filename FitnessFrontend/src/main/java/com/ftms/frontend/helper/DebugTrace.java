package com.ftms.frontend.helper;

public final class DebugTrace {
    private DebugTrace() {
    }

    public static long begin(String traceId, String step) {
        long start = System.currentTimeMillis();
        log(traceId, step + " START");
        return start;
    }

    public static void mark(String traceId, String step, long start) {
        log(traceId, step + " OK in " + elapsed(start) + " ms");
    }

    public static void log(String traceId, String message) {
        System.out.println("[FTMS-DEBUG][" + traceId + "] " + message);
    }

    public static void error(String traceId, String step, long start, Throwable ex) {
        System.out.println("[FTMS-DEBUG][" + traceId + "] " + step + " FAILED after "
                + elapsed(start) + " ms: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace(System.out);
    }

    public static String newTraceId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    private static long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }
}
