package core;

import annotations.GoldenTest;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoldenTestBuilder <T>{

    public Reflections reflections;
    Set<Class<T>> annotatedGoldenTestClasses;
    boolean update;

    public GoldenTestBuilder(String testPackage, boolean update){
        this.reflections = new Reflections(testPackage);
        this.annotatedGoldenTestClasses = new HashSet<>();
    }

    public GoldenFileTestSuite createGoldenTests() throws Exception{
        GoldenFileTestSuite test = new GoldenFileTestSuite(this.update);
        for (Class clss : reflections.getTypesAnnotatedWith(GoldenTest.class)){
            try {
                test.addTest((GoldenFileTest) clss.getConstructor().newInstance());
            } catch (Exception e){
                throw e;
            }
        }
        return test;
    }

}
