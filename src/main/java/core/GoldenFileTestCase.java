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
    public void run(GoldenTestResult result, boolean update, String goldenFilesLocation) {
        Reflections reflections = new Reflections(this.getClass());
        Set<Method> methodsGoldenVsFile = getMethodsGoldenVsFile(reflections);
        Set<Method> methodsGoldenVsString = getMethodsGoldenVsString(reflections);
        beforeAll();
        //Canviar el try/catch d'aquí
        try {
            if (update) {
                updateGoldenFiles(methodsGoldenVsFile, methodsGoldenVsString, goldenFilesLocation);
            } else {
                runTest(methodsGoldenVsFile, methodsGoldenVsString, goldenFilesLocation);
            }
        }catch (GoldenFileDoesntMatch e){
            result.addFailure();
            System.out.println(e);
        }catch (Exception e){
            result.addError();
            System.out.println(e);
        }a
        afterAll();
    }

    private void updateGoldenFiles(Set<Method> methodsGoldenVsFile,
                                   Set<Method> methodsGoldenVsString,
                                   String goldenFilesLocation)
            throws OutputFileNotSpecified, InvocationTargetException, IllegalAccessException, IOException {
        updateGoldenFilesfromFile(methodsGoldenVsFile, goldenFilesLocation);
        updateGoldenFilesFromString(methodsGoldenVsString, goldenFilesLocation);
    }

    private void updateGoldenFilesfromFile(Set<Method> methodsGoldenVsFile, String goldenFilesLocation)
            throws OutputFileNotSpecified, InvocationTargetException, IllegalAccessException, IOException {
        for (Method testMethod : methodsGoldenVsFile){
            try {
                beforeEach();
                testMethod.invoke(this);
                afterEach();
                Files.copy(Paths.get(getOutputFilePath(testMethod)), Paths.get(goldenFilesLocation+"/"+testMethod.getName()));
            }catch (Exception e){
                BufferedWriter writer = new BufferedWriter(new FileWriter(goldenFilesLocation+"/"+testMethod.getName()));
                writer.write(e.toString());
                writer.close();
            }
        }
    }

    private void updateGoldenFilesFromString(Set<Method> methodsGoldenVsString, String goldenFilesLocation) throws InvocationTargetException, IllegalAccessException, IOException {
        for (Method testMethod : methodsGoldenVsString){
            try {
                beforeEach();
                String output = (String) testMethod.invoke(this);
                afterEach();
                BufferedWriter writer = new BufferedWriter(new FileWriter(goldenFilesLocation+"/"+testMethod.getName()));
                writer.write(output);
                writer.close();
            }catch (Exception e){
                BufferedWriter writer = new BufferedWriter(new FileWriter(goldenFilesLocation+"/"+testMethod.getName()));
                writer.write(e.toString());
                writer.close();
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

    private void runTest(Set<Method> methodsGoldenVsFile, Set<Method> methodsGoldenVsString, String goldenFileLocations) throws GoldenFileDoesntMatch, IOException, OutputFileNotSpecified, InvocationTargetException, IllegalAccessException {
        checkGoldenVsFileTests(methodsGoldenVsFile, goldenFileLocations);
        checkGoldenVsStringTests(methodsGoldenVsFile, goldenFileLocations);
    }

    private void checkGoldenVsFileTests(Set<Method> methodsGoldenVsFile, String goldenFileLocations) throws InvocationTargetException, IllegalAccessException, OutputFileNotSpecified, IOException, GoldenFileDoesntMatch {
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
            }catch (GoldenFileDoesntMatch e){

            }
        }
    }

    private void checkGoldenVsStringTests(Set<Method> methodsGoldenVsString, String goldenFileLocations) throws InvocationTargetException, IllegalAccessException, IOException, GoldenFileDoesntMatch {
        for(Method testMethod : methodsGoldenVsString){
            beforeEach();
            String output = (String) testMethod.invoke(this);
            afterEach();
            byte [] byteOutput =  output.getBytes();
            byte [] goldenFile = Files.readAllBytes(Paths.get(goldenFileLocations+"/"+testMethod.getName()));
            if (!Arrays.equals(byteOutput, goldenFile)){
                throw new GoldenFileDoesntMatch("The Output and the value stored in the Golden File doesn't Match");
            }
        }
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
