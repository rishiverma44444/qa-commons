package com.noonpay.qa.common.custom.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.*;
import java.util.*;

public class CustomReporter implements ITestListener {

    @Override
    public synchronized void onStart(ITestContext context) {
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
        for (ITestResult temp : skippedTests) {
            ITestNGMethod method = temp.getMethod();
            if (context.getFailedTests().getResults(method).size() > 0) {
                skippedTests.remove(temp);
            } else {
                if (context.getPassedTests().getResults(method).size() > 0) {
                    skippedTests.remove(temp);
                }
            }
        }
        for (String s : Reporter.getOutput()) {
            ExtentManager.getInstance().setTestRunnerOutput(s);
        }

        ExtentManager.getInstance().flush();
    }


    @Override
    public synchronized void onTestStart(ITestResult result) {
        String methodName = getMethodNameWithParams(result);
        String className = result.getMethod().getRealClass().getSimpleName();
        String qualifiedName = className + "." + methodName;

        ExtentTest extentTest = ExtentManager.getInstance().createTest(qualifiedName, result.getMethod().getDescription());
        ExtentManager.setTest(extentTest);
        addParametersInReport(result);
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        Reporter.log(result.getName() + " = [Pass]<br>");
        String className = result.getMethod().getRealClass().getSimpleName();
        ExtentManager.getTest().get().assignCategory(className);
        ExtentManager.getTest().get().pass(MarkupHelper.createLabel("Test passed", ExtentColor.GREEN));
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        Reporter.log(result.getName() + " = [Fail]<br>");
        String className = result.getMethod().getRealClass().getSimpleName();
        ExtentManager.getTest().get().assignCategory(className);
        ExtentManager.getTest().get().fail(result.getThrowable());
        ExtentManager.getTest().get().fail(MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (null != throwable && throwable.getMessage().contains("depends on not successfully finished methods")) {
            ExtentTest extentTest = ExtentManager.getInstance().createTest(result.getName(), result.getMethod().getDescription());
            ExtentManager.setTest(extentTest);
        }
        Reporter.log(result.getName() + " = [Skip]<br>");
        String className = result.getMethod().getRealClass().getSimpleName();
        ExtentManager.getTest().get().assignCategory(className);
        ExtentManager.getTest().get().skip(result.getThrowable());
        ExtentManager.getTest().get().skip(MarkupHelper.createLabel("Test Skipped", ExtentColor.ORANGE));
    }

    @Override
    public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }



    private void addParametersInReport(ITestResult result) {
        if (result.getParameters().length > 0 && result.getParameters()[0] instanceof HashMap) {
            ExtentManager.getTest().get().log(Status.PASS, MarkupHelper.createTable(getParameterArray((HashMap<String, String>) result.getParameters()[0])));
        }
    }

    private String[][] getParameterArray(HashMap<String, String> hm) {
        String[][] parameters = new String[hm.size()][2];
        int row = 0;
        int column = 0;
        for (String str : hm.keySet()) {
            parameters[row][column] = "<b>" + str + "</b>";
            column++;
            parameters[row][column] = hm.get(str);
            row++;
            column = 0;
        }
        return parameters;
    }

    private String getMethodNameWithParams(ITestResult result) {
        String methodName = result.getName();
        String nextLineCharacter = "<br>";
        if (result.getParameters().length > 0 && null != result.getParameters()[0]) {
            String paramName = result.getParameters()[0].toString();
            methodName = methodName + nextLineCharacter + "(" + paramName + ")";
        }
        return methodName;
    }





}

