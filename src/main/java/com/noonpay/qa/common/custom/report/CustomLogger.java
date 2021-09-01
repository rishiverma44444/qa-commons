package com.noonpay.qa.common.custom.report;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class CustomLogger {

    public synchronized static void logPass(String log) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.PASS, log);
    }

    public synchronized static void logFail(String log) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.FAIL, log);
    }

    public synchronized static void logSkip(String log) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.SKIP, MarkupHelper.createLabel(log, ExtentColor.ORANGE));
    }

    public synchronized static void logDebug(String log) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.DEBUG, MarkupHelper.createLabel(log, ExtentColor.LIME));
    }

    public synchronized static void logDebug(String log, ExtentColor color) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.DEBUG, MarkupHelper.createLabel(log, color));
    }

    public synchronized static void logWarning(String log) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(Status.WARNING, log);
    }

    public synchronized static void logException(Status status, Throwable throwable) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().log(status, throwable);
    }

    public synchronized static void logCategory(String category) {
        if (ExtentManager.getTest().get() != null)
            ExtentManager.getTest().get().assignCategory(category);
    }

}
