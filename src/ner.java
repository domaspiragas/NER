import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class ner {
    //populate before creating Features
    public static ArrayList<String> Locations = new ArrayList<>();
    public static HashSet<String> trainingWords = new HashSet<>();
    public static HashSet<String> trainingPosTags = new HashSet<>();

    public static ArrayList<Features> trainingReadable = new ArrayList<>();
    public static ArrayList<Features> testReadable = new ArrayList<>();

    public static ArrayList<String> featureTypes = new ArrayList<>();

    public static HashMap<String, Integer> featureIds = new HashMap<>();

    public static void main(String[] args) {
        ParseLocationsFile(args[2]);
        ParseTrainFile(args[0]);
        if(args.length > 4){
            for(int i = 4; i < args.length; i++){
                featureTypes.add(args[i]);
            }
        }
        trainingWords.add("PHI");
        trainingWords.add("OMEGA");
        trainingPosTags.add("PHIPOS");
        trainingPosTags.add("OMEGAPOS");
        ParseTestFile(args[1]);
        removeFeatureTypes();
        createReadableFiles();
        generateFeatureIds();
        createVectorFiles();
    }

    private static void ParseLocationsFile(String fileName) {
        String line;
        try {

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                Locations.add(line);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not read file: " + fileName + ".");
        }
    }

    private static void ParseTrainFile(String fileName) {
        Features currFeatures;
        String prevLine = "";
        String currLine = "";
        String nextLine = "";
        String[] prev_ent_pos_word;
        String[] curr_ent_pos_word;
        String[] next_ent_pos_word;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((nextLine = bufferedReader.readLine()) != null) {
                currFeatures = new Features();
                if(prevLine.isEmpty() && !currLine.isEmpty() && !nextLine.isEmpty()){ //First Word
                    curr_ent_pos_word = currLine.split("\\s+");
                    next_ent_pos_word = nextLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos("PHIPOS")
                            .setPrevWord("PHI")
                            .setNextPos(next_ent_pos_word[1])
                            .setNextWord(next_ent_pos_word[2])
                            .findAbbr()
                            .findCap()
                            .findLoc();
                    trainingReadable.add(currFeatures);
                    trainingWords.add(curr_ent_pos_word[2]);
                    trainingPosTags.add(curr_ent_pos_word[1]);
                    prevLine = currLine;
                    currLine = nextLine;

                } else if(!prevLine.isEmpty() && !currLine.isEmpty() && nextLine.isEmpty()){ //Last Word
                    prev_ent_pos_word = prevLine.split("\\s+");
                    curr_ent_pos_word = currLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos(prev_ent_pos_word[1])
                            .setPrevWord(prev_ent_pos_word[2])
                            .setNextPos("OMEGAPOS")
                            .setNextWord("OMEGA")
                            .findAbbr()
                            .findCap()
                            .findLoc();
                    trainingReadable.add(currFeatures);
                    trainingWords.add(curr_ent_pos_word[2]);
                    trainingPosTags.add(curr_ent_pos_word[1]);
                    prevLine = currLine;
                    currLine = "";

                } else if (!prevLine.isEmpty() && !currLine.isEmpty() && !nextLine.isEmpty()){ //Middle Word
                    prev_ent_pos_word = prevLine.split("\\s+");
                    curr_ent_pos_word = currLine.split("\\s+");
                    next_ent_pos_word = nextLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos(prev_ent_pos_word[1])
                            .setPrevWord(prev_ent_pos_word[2])
                            .setNextPos(next_ent_pos_word[1])
                            .setNextWord(next_ent_pos_word[2])
                            .findAbbr()
                            .findCap()
                            .findLoc();
                    trainingReadable.add(currFeatures);
                    trainingWords.add(curr_ent_pos_word[2]);
                    trainingPosTags.add(curr_ent_pos_word[1]);
                    prevLine = currLine;
                    currLine = nextLine;

                } else if (currLine.isEmpty()) { //Next Sentence
                    prevLine = "";
                }
                currLine = nextLine;
            }
            // Last Word since NextLine == Null and loop ends but currLine may not have been handled
            if(!currLine.isEmpty()){
                currFeatures = new Features();
                prev_ent_pos_word = prevLine.split("\\s+");
                curr_ent_pos_word = currLine.split("\\s+");
                currFeatures
                        .setEntity(curr_ent_pos_word[0])
                        .setPos(curr_ent_pos_word[1])
                        .setWord(curr_ent_pos_word[2])
                        .setPrevPos(prev_ent_pos_word[1])
                        .setPrevWord(prev_ent_pos_word[2])
                        .setNextPos("OMEGAPOS")
                        .setNextWord("OMEGA")
                        .findAbbr()
                        .findCap()
                        .findLoc();
                trainingReadable.add(currFeatures);
                trainingWords.add(curr_ent_pos_word[2]);
                trainingPosTags.add(curr_ent_pos_word[1]);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not read file: " + fileName + ".");
        }
    }
    public static void ParseTestFile(String fileName){
        Features currFeatures;
        String prevLine = "";
        String currLine = "";
        String nextLine = "";
        String[] prev_ent_pos_word;
        String[] curr_ent_pos_word;
        String[] next_ent_pos_word;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((nextLine = bufferedReader.readLine()) != null) {
                currFeatures = new Features();
                if(prevLine.isEmpty() && !currLine.isEmpty() && !nextLine.isEmpty()){ //First Word
                    curr_ent_pos_word = currLine.split("\\s+");
                    next_ent_pos_word = nextLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos("PHIPOS")
                            .setPrevWord("PHI")
                            .setNextPos(next_ent_pos_word[1])
                            .setNextWord(next_ent_pos_word[2])
                            .findAbbr()
                            .findCap()
                            .findLoc()
                            .setUNKPosTag()
                            .setUNKWord();
                    testReadable.add(currFeatures);
                    prevLine = currLine;
                    currLine = nextLine;

                } else if(!prevLine.isEmpty() && !currLine.isEmpty() && nextLine.isEmpty()){ //Last Word
                    prev_ent_pos_word = prevLine.split("\\s+");
                    curr_ent_pos_word = currLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos(prev_ent_pos_word[1])
                            .setPrevWord(prev_ent_pos_word[2])
                            .setNextPos("OMEGAPOS")
                            .setNextWord("OMEGA")
                            .findAbbr()
                            .findCap()
                            .findLoc()
                            .setUNKPosTag()
                            .setUNKWord();
                    testReadable.add(currFeatures);
                    prevLine = currLine;
                    currLine = "";

                } else if (!prevLine.isEmpty() && !currLine.isEmpty() && !nextLine.isEmpty()){ //Middle Word
                    prev_ent_pos_word = prevLine.split("\\s+");
                    curr_ent_pos_word = currLine.split("\\s+");
                    next_ent_pos_word = nextLine.split("\\s+");
                    currFeatures
                            .setEntity(curr_ent_pos_word[0])
                            .setPos(curr_ent_pos_word[1])
                            .setWord(curr_ent_pos_word[2])
                            .setPrevPos(prev_ent_pos_word[1])
                            .setPrevWord(prev_ent_pos_word[2])
                            .setNextPos(next_ent_pos_word[1])
                            .setNextWord(next_ent_pos_word[2])
                            .findAbbr()
                            .findCap()
                            .findLoc()
                            .setUNKPosTag()
                            .setUNKWord();
                    testReadable.add(currFeatures);
                    prevLine = currLine;
                    currLine = nextLine;

                } else if (currLine.isEmpty()) { //Next Sentence
                    prevLine = "";
                }
                currLine = nextLine;
            }
            // Last Word since NextLine == Null and loop ends but currLine may not have been handled
            if(!currLine.isEmpty()) {
                currFeatures = new Features();
                prev_ent_pos_word = prevLine.split("\\s+");
                curr_ent_pos_word = currLine.split("\\s+");
                currFeatures
                        .setEntity(curr_ent_pos_word[0])
                        .setPos(curr_ent_pos_word[1])
                        .setWord(curr_ent_pos_word[2])
                        .setPrevPos(prev_ent_pos_word[1])
                        .setPrevWord(prev_ent_pos_word[2])
                        .setNextPos("OMEGAPOS")
                        .setNextWord("OMEGA")
                        .findAbbr()
                        .findCap()
                        .findLoc()
                        .setUNKPosTag()
                        .setUNKWord();
                testReadable.add(currFeatures);
            }
        } catch (IOException ex) {
            System.out.println("Error: Could not read file: " + fileName + ".");
        }
    }

    public static void removeFeatureTypes(){
        if(!featureTypes.contains("WORDCON")){
            for(Features train : trainingReadable){
                train.setPrevWord("n/a");
                train.setNextWord("");
            }
            for(Features test : testReadable){
                test.setPrevWord("n/a");
                test.setNextWord("");
            }
        }
        if(!featureTypes.contains("POS")){
            for(Features train : trainingReadable){
                train.setPos("n/a");
            }
            for(Features test : testReadable){
                test.setPos("n/a");
            }
        }
        if(!featureTypes.contains("POSCON")){
            for(Features train : trainingReadable){
                train.setPrevPos("n/a");
                train.setNextPos("");
            }
            for(Features test : testReadable){
                test.setPrevPos("n/a");
                test.setNextPos("");
            }
        }
        if(!featureTypes.contains("ABBR")){
            for(Features train : trainingReadable){
                train.setAbbr("n/a");
            }
            for(Features test : testReadable){
                test.setAbbr("n/a");
            }
        }
        if(!featureTypes.contains("CAP")){
            for(Features train : trainingReadable){
                train.setCap("n/a");
            }
            for(Features test : testReadable){
                test.setCap("n/a");
            }
        }
        if(!featureTypes.contains("LOCATION")){
            for(Features train : trainingReadable){
                train.setLoc("n/a");
            }
            for(Features test : testReadable){
                test.setLoc("n/a");
            }
        }
    }

    public static void generateFeatureIds(){
        int id = 1;

        for(Features train: trainingReadable){
            if(!featureIds.containsKey("word-"+train.getWord())){
                featureIds.put("word-"+train.getWord(), id++);
            }
        }
        featureIds.put("word-UNK", id++);


        if(featureTypes.contains("WORDCON")){
            for(Features train : trainingReadable){
                if(!featureIds.containsKey("prev-word-"+train.getWord())){
                    featureIds.put("prev-word-"+train.getWord(), id++);
                }
                if(!featureIds.containsKey("next-word-"+train.getWord())){
                    featureIds.put("next-word-"+train.getWord(), id++);
                }
            }
            featureIds.put("prev-word-UNK", id++);
            featureIds.put("next-word-UNK", id++);
            featureIds.put("prev-word-PHI", id++);
            featureIds.put("next-word-OMEGA", id++);
        }

        if(featureTypes.contains("POS")){
            for(Features train : trainingReadable){
                if(!featureIds.containsKey("pos-"+train.getPos())){
                    featureIds.put("pos-"+train.getPos(), id++);
                }
            }
            featureIds.put("pos-UNKPOS", id++);
        }

        if(featureTypes.contains("POSCON")){
            for(Features train : trainingReadable){
                if(!featureIds.containsKey("prev-pos-"+train.getPrevPos())){
                    featureIds.put("prev-pos-"+train.getPrevPos(), id++);
                }
                if(!featureIds.containsKey("next-pos-"+train.getNextPos())){
                    featureIds.put("next-pos-"+train.getNextPos(), id++);
                }
            }
            featureIds.put("prev-pos-UNKPOS", id++);
            featureIds.put("next-pos-UNKPOS", id++);
            featureIds.put("prev-pos-PHIPOS", id++);
            featureIds.put("next-pos-OMEGAPOS", id++);
        }

        if(featureTypes.contains("ABBR")){
            featureIds.put("ABBR", id++);
        }
        if(featureTypes.contains("CAP")){
            featureIds.put("CAP", id++);
        }
        if(featureTypes.contains("LOCATION")){
            featureIds.put("LOCATION", id++);
        }
    }

    public static int getBIO(String bio){
        switch (bio){
            case "O":
                return 0;
            case "B-PER":
                return 1;
            case "I-PER":
                return 2;
            case "B-LOC":
                return 3;
            case "I-LOC":
                return 4;
            case "B-ORG":
                return 5;
            case "I-ORG":
                return 6;
            default:
                return -1;
        }
    }
    private static void createReadableFiles(){
        try(PrintWriter out = new PrintWriter("resources/train.txt.readable")){
            for(Features  f: trainingReadable){
                out.println(f.toString());
            }
        }catch (Exception e){}

        try(PrintWriter out = new PrintWriter("resources/test.txt.readable")){
            for(Features  f: testReadable){
                out.println(f.toString());
            }
        }catch (Exception e){}
    }
    private static void createVectorFiles(){
        try(PrintWriter out = new PrintWriter("resources/train.txt.vector")){
            for(Features  f: trainingReadable){
                out.println(generateVectorString(f));
            }
        }catch (Exception e){
            System.out.println("Error generating training vector file");
        }

        try(PrintWriter out = new PrintWriter("resources/test.txt.vector")){
            for(Features  f: testReadable){
                out.println(generateVectorString(f));
            }
        }catch (Exception e){
            System.out.println("Error generating testing vector file");
        }
    }
    private static String generateVectorString(Features features){
        String returnString = "";
        ArrayList<Integer> ids = new ArrayList<>();
        returnString += getBIO(features.getEntity()) + " ";

        //word key guaranteed to exist
        ids.add(featureIds.get("word-"+features.getWord()));

        //if prev word not null, WORDCON
        if(!features.getPrevWord().equals("n/a")){
            ids.add(featureIds.get("prev-word-"+features.getPrevWord()));
            ids.add(featureIds.get("next-word-"+features.getNextWord()));
        }
        if(!features.getPos().equals("n/a")){
            ids.add(featureIds.get("pos-"+features.getPos()));
        }
        if(!features.getPrevPos().equals("n/a")){
            ids.add(featureIds.get("prev-pos-"+features.getPrevPos()));
            ids.add(featureIds.get("next-pos-"+features.getNextPos()));
        }
        if(!features.getAbbr().equals("n/a") && !features.getAbbr().equals("no")){
            ids.add(featureIds.get("ABBR"));
        }
        if(!features.getCap().equals("n/a") && !features.getCap().equals("no")){
            ids.add(featureIds.get("CAP"));
        }
        if(!features.getLoc().equals("n/a") && !features.getLoc().equals("no")){
            ids.add(featureIds.get("LOCATION"));
        }

        Collections.sort(ids);

        for(Integer id: ids){
            returnString += id +":1 ";
        }

        return returnString.trim();
    }
}
