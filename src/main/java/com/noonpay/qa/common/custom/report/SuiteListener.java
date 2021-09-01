package com.noonpay.qa.common.custom.report;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class SuiteListener implements ISuiteListener {

    public void onStart(ISuite suite) {

        // this will contain some setup steps before start of test suite..
    }

     public void onFinish(ISuite suite) {

        // this will contain clean up activities..
    }

}
