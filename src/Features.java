public class Features {
    Features(){}

    String _entity, _word = "n/a", _prevWord = "n/a", _nextWord = "n/a", _pos = "n/a",
            _prevPos = "n/a", _nextPos = "n/a", _abbr = "n/a", _cap = "n/a", _loc = "n/a";

    public Features setEntity(String entity){
        _entity = entity;
        return this;
    }

    public String getEntity(){
        return _entity;
    }

    public Features setWord(String word){
        _word = word;
        return this;
    }

    public String getWord(){
        return _word;
    }

    public Features setPrevWord(String prevWord){
        _prevWord = prevWord;
        return this;
    }

    public String getPrevWord(){
        return _prevWord;
    }

    public Features setNextWord(String nextWord){
        _nextWord = nextWord;
        return this;
    }

    public String getNextWord(){
        return _nextWord;
    }

    public Features setPos(String pos){
        _pos = pos;
        return this;
    }

    public String getPos(){
        return _pos;
    }

    public Features setPrevPos(String prevPos){
        _prevPos = prevPos;
        return this;
    }

    public String getPrevPos(){
        return _prevPos;
    }

    public Features setNextPos(String nextPos){
        _nextPos = nextPos;
        return this;
    }

    public String getNextPos(){
        return _nextPos;
    }

    public Features findAbbr(){
        if(_word.endsWith(".") && _word.length() <= 4 && hasOnlyLettersOrPeriods(_word)){
            _abbr = "yes";
        } else{
            _abbr = "no";
        }
        return this;
    }

    public String getAbbr(){
        return _abbr;
    }

    public void setAbbr(String abbr){
        _abbr = abbr;
    }

    public Features findCap(){
        if(isCapitalized(_word)){
            _cap = "yes";
        } else{
            _cap = "no";
        }
        return this;
    }

    public String getCap(){
        return _cap;
    }

    public void setCap(String cap){
        _cap = cap;
    }

    public Features findLoc(){
        if(ner.Locations.contains(_word)) {
            _loc = "yes";
        } else {
            _loc = "no";
        }
        return this;
    }

    public String getLoc(){
        return _loc;
    }

    public void setLoc(String loc){
        _loc = loc;
    }

    public Features setUNKWord(){
        if(!ner.trainingWords.contains(_word)){
            _word = "UNK";
        }
        if(!ner.trainingWords.contains(_prevWord)){
            _prevWord = "UNK";
        }
        if(!ner.trainingWords.contains(_nextWord)){
            _nextWord = "UNK";
        }
        return this;
    }

    public Features setUNKPosTag(){
        if(!ner.trainingPosTags.contains(_pos)){
            _pos = "UNKPOS";
        }
        if(!ner.trainingPosTags.contains(_prevPos)){
            _prevPos = "UNKPOS";
        }
        if(!ner.trainingPosTags.contains(_nextPos)){
            _nextPos = "UNKPOS";
        }
        return this;
    }

    private boolean hasOnlyLettersOrPeriods(String word){
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c) && c != '.') {
                return false;
            }
        }
        return true;
    }

    private boolean isCapitalized(String word){
        char[] originalChars = word.toCharArray();
        char[] uppercaseChars = word.toUpperCase().toCharArray();
        if(Character.isLetter(originalChars[0]) && originalChars[0] == uppercaseChars[0]){
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        String returnString = "";
        returnString += "WORD: " + _word + "\n";
        returnString += "WORDCON: " + _prevWord + " " + _nextWord + "\n";
        returnString += "POS: " + _pos + "\n";
        returnString += "POSCON: " + _prevPos + " " + _nextPos + "\n";
        returnString += "ABBR: " + _abbr + "\n";
        returnString += "CAP: " + _cap + "\n";
        returnString += "LOCATION: " + _loc +"\n";

        return returnString;
    }
}
