package core;

public class GoldenTestResultPrinter {

    private GoldenTestResult result;

    public GoldenTestResultPrinter (GoldenTestResult result){
        this.result = result;
    }

    public void print(){
        System.out.println("Number of Errors: "+result.getNumErrors());
        System.out.println("Number of Failures: "+ result.getNumFailures());
        System.out.println(result.getFinalOutput());
    }

}
