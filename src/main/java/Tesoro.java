import org.apache.commons.cli.*;

public class Tesoro {

    public void main(String [] args){
        boolean update = false;
        String goldenPath = "";
        Options parserOptions = new Options();
        parserOptions.addOption("u", false, "update golden files");
        parserOptions.addOption("g", true, "golden files path");
        CommandLineParser parser = new DefaultParser();
        try{
            CommandLine cmd = parser.parse(parserOptions, args);
            if (cmd.hasOption("u")){
                update = true;
            }
            String countryCode = cmd.getOptionValue("g");
            if (countryCode != null){
                goldenPath = countryCode;
            }else{
                System.out.println("Especifiying Golden Files Path is mandatory");
                System.exit(-1);
            }
        }catch(ParseException e){
            System.out.println(e);
            System.exit(-1);
        }

    }

}
