package core;

public class GoldenTestResultPrinter {

    public static void print(GoldenTestResult result){
        System.out.println("Number of Errors: "+result.getNumErrors());
        System.out.println("Number of Failures: "+ result.getNumFailures());
        System.out.println(result.getFinalOutput());
    }

}
