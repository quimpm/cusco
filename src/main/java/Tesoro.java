import org.apache.commons.cli.*;

public class Tesoro {

    public void main(String [] args){
        boolean update = false;
        String goldenPath = "";
        Options parserOptions = new Options();
        parserOptions.addOption("u", false, "update golden files");
        parserOptions.addOption("g", true, "golden files path");
        parserOptions.addOption("p", true, "tests file path");
        CommandLineParser parser = new DefaultParser();
        try{
            CommandLine cmd = parser.parse(parserOptions, args);
            if (cmd.hasOption("u")){
                update = true;
            }
            String goldenPathg = cmd.getOptionValue("g");
            if (goldenPathg == null){
                System.out.println("Especifiying Golden Files Path is mandatory");
                System.exit(-1);
            }
            String testFilesPath = cmd.getOptionValue("p");
            if (testFilesPath == null){
                System.out.println("Especifiying Golden Files Path is mandatory");
                System.exit(-1);
            }

        }catch(ParseException e){
            System.out.println(e);
            System.exit(-1);
        }

    }

}
