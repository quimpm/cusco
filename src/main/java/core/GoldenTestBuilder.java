package core;

import annotations.GoldenTest;
import org.reflections.Reflections;

import java.io.File;
import java.util.*;

public class GoldenTestBuilder <T>{


    public boolean update;
    public String testLocation;

    private GoldenTestBuilder(String testLocation, boolean update){
        this.testLocation = testLocation;
        this.update = update;
    }

    public static GoldenTestBuilder builder(String testLocation, boolean update){
        return new GoldenTestBuilder(testLocation, update);
    }

    public GoldenFileTestSuite createGoldenTests() {
        GoldenFileTestSuite test = new GoldenFileTestSuite();
        File pathFile = new File(this.testLocation);
        if (pathFile.isFile()){
            try {
                Class clss = Class.forName(pathFile.getName());
                if (clss.isAnnotationPresent(GoldenTest.class)) {
                    test.addTest((GoldenFileTest) clss.getConstructor().newInstance());
                }
            }catch (Exception e){
                System.out.println(e);
                System.exit(-1);
            }
        }
        if (pathFile.isDirectory()){
            List<File> files = Arrays.asList(pathFile.listFiles());
            test.addTest(recursiveDirectoryTestCreation(files, new GoldenFileTestSuite()));
        }
        return test;
    }

    public GoldenFileTestSuite recursiveDirectoryTestCreation(List<File> files, GoldenFileTestSuite testSuite){
        if (files.size() <= 0){
            return testSuite;
        }else{
            File file = files.remove(0);
            if (file.isDirectory()){
                testSuite.addTest(recursiveDirectoryTestCreation(Arrays.asList(file.listFiles()), testSuite));
            }else{
                try {
                    Class clss = Class.forName(file.getName());
                    if (clss.isAnnotationPresent(GoldenTest.class)) {
                        testSuite.addTest((GoldenFileTest) clss.getConstructor().newInstance());
                    }
                }catch(Exception e){
                    System.out.println(e);
                    System.exit(-1);
                }
            }
            return recursiveDirectoryTestCreation(files, testSuite);
        }
    }




}
