package core;

import annotations.GoldenTest;
import annotations.GoldenVsFile;
import annotations.GoldenVsString;
import exceptions.GoldenFileDoesntMatch;
import exceptions.OutputFileNotSpecified;
import org.reflections.Reflections;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

public abstract class GoldenFileTestCase implements GoldenFileTest {

    @Override
    public GoldenTestResult run(GoldenTestResult result, boolean update, String goldenFilesLocation) {
        Reflections reflections = new Reflections(this.getClass());
        Set<Method> methodsGoldenVsFile = getMethodsGoldenVsFile(reflections);
        Set<Method> methodsGoldenVsString = getMethodsGoldenVsString(reflections);
        beforeAll();
        if (update) {
            updateGoldenFiles(methodsGoldenVsFile, methodsGoldenVsString, goldenFilesLocation);
        } else {
            result = runTest(methodsGoldenVsFile, methodsGoldenVsString, goldenFilesLocation, result);
        }
        afterAll();
        return result;
    }

    private void updateGoldenFiles(Set<Method> methodsGoldenVsFile,
                                   Set<Method> methodsGoldenVsString,
                                   String goldenFilesLocation)  {
        updateGoldenFilesfromFile(methodsGoldenVsFile, goldenFilesLocation);
        updateGoldenFilesFromString(methodsGoldenVsString, goldenFilesLocation);
    }

    private void updateGoldenFilesfromFile(Set<Method> methodsGoldenVsFile, String goldenFilesLocation) {
        for (Method testMethod : methodsGoldenVsFile){
            try {
                beforeEach();
                testMethod.invoke(this);
                afterEach();
                Files.copy(Paths.get(getOutputFilePath(testMethod)), Paths.get(goldenFilesLocation+"/"+testMethod.getName()));
            }catch (Exception e){
                System.out.println(e);
                System.exit(-1);
            }
        }
    }

    private void updateGoldenFilesFromString(Set<Method> methodsGoldenVsString, String goldenFilesLocation) {
        for (Method testMethod : methodsGoldenVsString){
            try {
                beforeEach();
                String output = (String) testMethod.invoke(this);
                afterEach();
                BufferedWriter writer = new BufferedWriter(new FileWriter(goldenFilesLocation+"/"+testMethod.getName()));
                writer.write(output);
                writer.close();
            }catch (Exception e){
                System.out.println(e);

            }
        }
    }

    private String getPathOutputFile(Method testMethod){
        for (Annotation ann : testMethod.getParameterAnnotations()[0]){
            if (GoldenVsFile.class.isInstance(ann)){
                return ((GoldenVsFile) ann).output();
            }
        }
        return "";
    }

    private GoldenTestResult runTest(Set<Method> methodsGoldenVsFile, Set<Method> methodsGoldenVsString, String goldenFileLocations, GoldenTestResult result)  {
        result = checkGoldenVsFileTests(methodsGoldenVsFile, goldenFileLocations, result);
        return checkGoldenVsStringTests(methodsGoldenVsFile, goldenFileLocations,result);
    }

    private GoldenTestResult checkGoldenVsFileTests(Set<Method> methodsGoldenVsFile, String goldenFileLocations, GoldenTestResult result) {
        for(Method testMethod : methodsGoldenVsFile){
            try {
                beforeEach();
                testMethod.invoke(this);
                afterEach();
                byte[] output = Files.readAllBytes(Paths.get(getOutputFilePath(testMethod)));
                byte[] goldenFile = Files.readAllBytes(Paths.get(goldenFileLocations + "/" + testMethod.getName()));
                if (!Arrays.equals(output, goldenFile)) {
                    throw new GoldenFileDoesntMatch("The Output and the value stored in the Golden File doesn't Match");
                }
                System.out.print(".");
            }catch (GoldenFileDoesntMatch e){
                System.out.print("F");
                result.addFailure();
                result.addTestTraceFailure(testMethod, e);
            }catch (Exception e){
                System.out.print("E");
                result.addError();
                result.addTestTraceError(testMethod, e);
            }
        }
        return result;
    }

    private GoldenTestResult checkGoldenVsStringTests(Set<Method> methodsGoldenVsString, String goldenFileLocations, GoldenTestResult result) {
        for(Method testMethod : methodsGoldenVsString){
            try{
                beforeEach();
                String output = (String) testMethod.invoke(this);
                afterEach();
                byte [] byteOutput =  output.getBytes();
                byte [] goldenFile = Files.readAllBytes(Paths.get(goldenFileLocations+"/"+testMethod.getName()));
                if (!Arrays.equals(byteOutput, goldenFile)){
                    throw new GoldenFileDoesntMatch("The Output and the value stored in the Golden File doesn't Match");
                }
                System.out.print(".");
            }catch (GoldenFileDoesntMatch e){
                System.out.print("F");
                result.addFailure();
                result.addTestTraceFailure(testMethod, e);
            }catch (Exception e){
                System.out.print("E");
                result.addError();
                result.addTestTraceError(testMethod, e);
            }
        }
        return result;
    }

    private String getOutputFilePath(Method testMethod) throws OutputFileNotSpecified {
        String outputFilePath = getPathOutputFile(testMethod);
        if (outputFilePath == ""){
            throw new OutputFileNotSpecified("Output File Not Specified in GoldenVsFile annotation");
        }
        return outputFilePath;
    }

    private Set<Method> getMethodsGoldenVsFile(Reflections reflections){
        return reflections.getMethodsAnnotatedWith(GoldenVsFile.class);
    }

    private Set<Method> getMethodsGoldenVsString(Reflections reflections){
        return reflections.getMethodsAnnotatedWith(GoldenVsString.class);
    }

    public abstract void beforeAll();

    public abstract void beforeEach();

    public abstract void afterAll();

    public abstract void afterEach();

}
