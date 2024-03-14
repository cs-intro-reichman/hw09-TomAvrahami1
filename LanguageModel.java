import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of character data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
    private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }
    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        char character;
        StringBuilder stringBuilder = new StringBuilder();
        In inputFile = new In(fileName);
        for (int i = 0; i < windowLength; i=i+1) {
            stringBuilder.append(inputFile.readChar());
        }
        while (inputFile.isEmpty()==false) {
            character = inputFile.readChar();
            List newList = CharDataMap.get(stringBuilder.toString());
            if (newList == null)
            {
                newList = new List();
                CharDataMap.put(stringBuilder.toString(),newList);
            }
                newList.update(character);
                stringBuilder.append(character);
                stringBuilder = new StringBuilder(stringBuilder.substring(1));
            }
        for (List probabilities : CharDataMap.values()) {
            calculateProbabilities(probabilities);
        }
        }


    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public  void calculateProbabilities(List probabilities) {
		int count = 0;
        double cp = 0;
        for (int i = 0; i < probabilities.getSize(); i= i+1){
            count += probabilities.get(i).count;
        }
        for (int i = 0; i < probabilities.getSize(); i= i+1) {
            probabilities.get(i).p = (double) ((double) probabilities.get(i).count) /((double) count);
            probabilities.get(i).cp = cp + probabilities.get(i).p;
            cp = probabilities.get(i).cp;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probabilities) {
        char randomCharacter;
        double randomDouble = randomGenerator.nextDouble();

        for (int i = 0; i < probabilities.getSize(); i= i+1)
        {
            if (probabilities.get(i).cp > randomDouble) 
            {
                randomCharacter = probabilities.get(i).chr;
                return randomCharacter;
            }
            else continue;
        }
        return ' '; 
    }


    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param textLength - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if(initialText.length() < windowLength) return initialText;
        
        StringBuilder preResult = new StringBuilder(initialText.substring(initialText.length() - windowLength));
        StringBuilder finalResult = preResult;

        while (finalResult.length() < (textLength + windowLength)){
            List currentList = CharDataMap.get(preResult.toString());
            if (currentList == null) break;
            else
            {
                finalResult.append(getRandomChar(currentList));
                preResult = new StringBuilder(finalResult.substring(finalResult.length() - windowLength));
            }
        }
        String generatedText = finalResult.toString();
        return generatedText;
        
        }
	

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {

    }
}