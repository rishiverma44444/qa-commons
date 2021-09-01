package com.noonpay.qa.common.custom.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent = null;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal();

    private ExtentManager() {

    }

    public synchronized static ThreadLocal<ExtentTest> getTest() {
        return test;
    }

    public synchronized static void setTest(ExtentTest test) {
        getTest().set(test);
    }

    public synchronized static ExtentReports getInstance() {
        if (extent == null)
            createInstance();
        return extent;
    }

    public synchronized static ExtentReports createInstance() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("extentreport/" + "extentreport.html");
        sparkReporter.config().setDocumentTitle("Noon Automation Testing Report");
        sparkReporter.config().setReportName("Noon Automation Testing Report");
        sparkReporter.config().setTheme(Theme.DARK);
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        return extent;
    }
}



