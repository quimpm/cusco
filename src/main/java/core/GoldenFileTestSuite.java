package core;

import java.util.ArrayList;
import java.util.List;

public class GoldenFileTestSuite implements GoldenFileTest {

    List<GoldenFileTest> tests;
    List<GoldenTestResult> results;

    public GoldenFileTestSuite(){
        this.tests = new ArrayList<GoldenFileTest>();
        this.results = new ArrayList<GoldenTestResult>();
    }

    @Override
    public GoldenTestResult run(GoldenTestResult result, boolean update, String goldenFilesLocation) {
        for (GoldenFileTest test : this.tests){
            results.add(test.run(result, update, goldenFilesLocation));
        }
        return collectResults();
    }


    public void addTest(GoldenFileTest test){
        this.tests.add(test);
    }

    private GoldenTestResult collectResults(){
        GoldenTestResult testResult = new GoldenTestResult();
        for ( GoldenTestResult result : results){
            testResult.setNumErrors(testResult.getNumErrors()+result.getNumErrors());
            testResult.setNumFailures(testResult.getNumFailures()+result.getNumFailures());
            testResult.setFinalOutput(testResult.getFinalOutput()+result.getFinalOutput());
        }
        return testResult;
    }

}
