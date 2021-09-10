package core;

import java.lang.reflect.Method;

public class GoldenTestResult {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    private int numFailures;
    private int numErrors;
    private String finalOutput;

    public void addFailure(){
        numFailures++;
    }

    public void addError(){
        numErrors++;
    }

    public void addTestTraceFailure(Method meth, Exception e){
        finalOutput += ANSI_RED_BACKGROUND+meth.getName()+ANSI_RESET;
        finalOutput += e.toString();
    }

    public void addTestTraceError(Method meth, Exception e){
        finalOutput += ANSI_RED_BACKGROUND+meth.getName()+ANSI_RESET;
        finalOutput += e.toString();
    }

    public int getNumFailures() {
        return numFailures;
    }

    public void setNumFailures(int numFailures) {
        this.numFailures = numFailures;
    }

    public int getNumErrors() {
        return numErrors;
    }

    public void setNumErrors(int numErrors) {
        this.numErrors = numErrors;
    }

    public String getFinalOutput() {
        return finalOutput;
    }

    public void setFinalOutput(String finalOutput) {
        finalOutput = finalOutput;
    }
}
