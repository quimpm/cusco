package core;

import java.lang.reflect.Method;

public class GoldenTestResult {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    private int numFailures;
    private int numErrors;
    private String trace;

    public GoldenTestResult(){
        numFailures = 0;
        numErrors = 0;
        trace = "";
    }

    public void addFailure(){
        numFailures++;
    }

    public void addError(){
        numErrors++;
    }

    public void addTestTraceFailure(Method meth, Exception e){
        trace += "\n";
        trace += ANSI_RED_BACKGROUND+meth.getName()+ANSI_RESET;
        trace += e.toString();
    }

    public void addTestTraceError(Method meth, Exception e){
        trace += "\n";
        trace += ANSI_RED_BACKGROUND+meth.getName()+ANSI_RESET;
        trace += e.toString();
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
        return trace;
    }

    public void setFinalOutput(String finalOutput) {
        finalOutput = finalOutput;
    }
}
