package core;

import java.util.ArrayList;
import java.util.List;

public class GoldenFileTestSuite implements GoldenFileTest {

    List<GoldenFileTest> tests;

    public GoldenFileTestSuite(boolean update){
        this.tests = new ArrayList<GoldenFileTest>();
    }

    @Override
    public void run(GoldenTestResult result, boolean update, String goldenFilesLocation) {
        for (GoldenFileTest test : this.tests){
            test.run(result, update, goldenFilesLocation);
        }
    }

    public void addTest(GoldenFileTest test){
        this.tests.add(test);
    }

}
