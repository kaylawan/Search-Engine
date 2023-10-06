package assignment;
import java.util.*;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 */

public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    private transient Page currentPage = null;

    private Map<Page, String[]> totalPages = new HashMap<>();
    private Map<String, Map<Page, int[]>> dictionary = new HashMap<>();

    public void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
        int incrementedSize = totalPages.size()+1;
        currentPage.setID(incrementedSize);
    }

    //Search the given word or phrase query for associated set of pages
    public Set<Page> search(String query) {
        List<String> phrase = getPhrase(query);
        if (query.charAt(0) != '\"') {
            return searchWord(query);
        }
        else if (phrase.size() == 1) {
            return searchWord(phrase.get(0));
        }
        else return searchPhrase(phrase);
    }
    private List<String> getPhrase(String phrase) {
        int length = phrase.length();
        List<String> text = new LinkedList<>();
        StringBuilder lastWord = new StringBuilder();
        if (phrase.charAt(0) == '\"') {
            phrase = phrase.substring(1, length - 1);
        }
        int i=0;
        while(i<length){
            if (Character.isLetterOrDigit(phrase.charAt(i))) {
                lastWord.append(phrase.charAt(i));
            }
            else {
                int lengthLast = lastWord.length();
                if (lengthLast > 0)
                {
                    text.add(lastWord.toString());
                    lastWord = new StringBuilder();
                }
            }
            i++;
        }
        if (lastWord.length() > 0) {
            text.add(lastWord.toString());
        }
        return text;
    }

    //Search the given phrase for associated set of pages
    private Set<Page> searchPhrase(Collection<String> phrase) {
        //TODO
        HashSet blankSet = new HashSet();
        Iterator<String> phraseIt = phrase.iterator();
        if (phrase.isEmpty()==true) {
            return blankSet;
        }
        int size = phrase.size();
        if (size == 1) {
            return searchWord(phraseIt.next());
        }
        Map<Page, int[]> pageMap = dictionary.get(phraseIt.next());
        if (pageMap == null) {
            return blankSet;
        }
        Set<Page> pageSet = new HashSet<>(pageMap.keySet());
        //Find the intersection of the pages associated with each word in the phrase
        while (phraseIt.hasNext()) {
            Map<Page, int[]> nextMap = dictionary.get(phraseIt.next());
            if (nextMap == null) {
                return blankSet;
            }
            pageSet.retainAll(nextMap.keySet());
            int pageSize = pageSet.size();
            if (pageSize == 0) {
                return blankSet;
            }
        }
        pageSet.removeIf((Page p) -> !hasPhrase(totalPages.get(p), pageMap.get(p), phrase));
        return pageSet;
    }

    //Check whether a given set of nodes has at least one node representing the phrase
    private boolean hasPhrase(String[] wordList, int[] indexList, Collection<String> phrase) {
        //TODO
        main:
        for (int index : indexList) {
            if (index + phrase.size() > wordList.length) {
                return false;
            }
            index++;
            Iterator<String> it = phrase.iterator();
            it.next();
            while (it.hasNext()) {
                String word = it.next();
                if (!wordList[index].equals(word)) {
                    //This node doesn't match the given phrase
                    continue main;
                }
                index++;
            }
            return true;
        }
        //No nodes match the given phrase
        return false;
    }

    //Search the given word for associated set of pages
    public Set<Page> searchWord(String word) {
        String myWord = word.substring(1);
        char firstChar = word.charAt(0);
        if (firstChar == '!') {
            //return every page that doesn't contain this word
            return searchNotWord(myWord);
        }
        else if (dictionary.containsKey(word)==false) {
            return new HashSet<>();
        }
        //return copySet(dictionary.get(word).keySet());
        else return copySet(dictionary.get(word).keySet());
    }

    public Set<Page> searchNotWord(String word) {
        Set<Page> output = copySet(totalPages.keySet());
        output.removeAll(searchWord(word));
        return output;
    }

    //Provides a copy of a given set
    //Useful because sets are passed by reference
    private Set<Page> copySet(Set<Page> pageSet) {
        return new HashSet<>(pageSet);
    }

    //Inserts the given phrase into dictionary
    public void addPhrase(Queue<String> phrase) {
        if (phrase.isEmpty()) {
        }
        else {
            String[] phraseList = new String[phrase.size()];
            totalPages.put(currentPage, phraseList);
            int i = 0;
            while(i<phraseList.length){
                String word = getReference(phrase.remove());
                phraseList[i] = word;
                addToDictionary(word, i);
                i++;
            }
        }
    }

    private void addToDictionary(String word, int index) {
        if (dictionary.containsKey(word)==false) {
            Map<Page, int[]> pageMap = new HashMap<>();
            dictionary.put(word, pageMap);
            int[] indexList = new int[1];
            pageMap.put(currentPage, indexList);
            indexList[0] = index;
            return;
        }
        Map<Page, int[]> pageMap = dictionary.get(word);
        if (pageMap.containsKey(currentPage)) {
            int length = pageMap.get(currentPage).length;
            int[] indexList = new int[length+1];
            int i = 0;
            while(i<length){
                indexList[i] = pageMap.get(currentPage)[i];
                i++;
            }
            indexList[pageMap.get(currentPage).length] = index;
            pageMap.put(currentPage, indexList);
        }
        else{
            int[] indexList = new int[1];
            pageMap.put(currentPage, indexList);
            indexList[0] = index;
        }
    }

    //Returns a pointer to a given String if it already exists in the structure
    //Useful for saving space
    private String getReference(String word) {
        if (dictionary.containsKey(word) == true) {
            Iterator<Page> pageIt = dictionary.get(word).keySet().iterator();
            Page current = pageIt.next();
            return totalPages.get(current)[dictionary.get(word).get(current)[0]];
        }
        else return word;
    }


    public Set<Page> inverse(Set<Page> input) {
        //TODO
        Set<Page> output = copySet(totalPages.keySet());
        output.removeAll(input);
        return output;
    }


    public Set<Page> searchPhraseAdd(String phrase, Set<Page> intersection) {
        return searchPhraseAdd(getPhrase(phrase), intersection);
    }

    private Set<Page> searchPhraseAdd(Collection<String> phrase, Set<Page> intersection) {
        //TODO
        Iterator<String> phraseIt = phrase.iterator();
        Map<Page, int[]> pageMap = dictionary.get(phraseIt.next());
        intersection.retainAll(pageMap.keySet());
        if (phrase.size() == 0) {
            return new HashSet<>();
        }
        else if (phrase.size() == 1) {
            Set<Page> temp = searchWord(phraseIt.next());
            temp.retainAll(intersection);
            return temp;
        }
        else if (pageMap == null) {
            return new HashSet<>();
        }
        if (intersection.isEmpty()) {
            return new HashSet<>();
        }
        else {
            while (phraseIt.hasNext()==true) {
            Map<Page, int[]> nextMap = dictionary.get(phraseIt.next());
            if (nextMap == null) {
                return new HashSet<>();
            }
            intersection.retainAll(nextMap.keySet());
            if (intersection.isEmpty()==true) {
                return new HashSet<>();
            }
        }
        intersection.removeIf((Page p) -> !hasPhrase(totalPages.get(p), pageMap.get(p), phrase));
        return intersection;
        }
    }

    public Set<Page> searchPhraseRemove(String phrase, Set<Page> remove) {
        return searchPhraseRemove(getPhrase(phrase), remove);
    }

    public Set<Page> searchPhraseRemove(Collection<String> phrase, Set<Page> remove) {
        Iterator<String> phraseIt = phrase.iterator();
        Map<Page, int[]> pageMap = dictionary.get(phraseIt.next());
        if (phrase.isEmpty()==true) {
            return new HashSet<>();
        }
        else if (phrase.size() == 1) {
            Set<Page> temp = searchWord(phraseIt.next());
            temp.removeAll(remove);
            return temp;
        }
        else if (pageMap == null) {
            return new HashSet<>();
        }
        Set<Page> pageSet = new HashSet<>(pageMap.keySet());
        pageSet.removeAll(remove);
        if (pageSet.isEmpty()) {
            return new HashSet<>();
        }
        while (phraseIt.hasNext()) {
            Map<Page, int[]> nextMap = dictionary.get(phraseIt.next());
            if (nextMap == null) {
                return new HashSet<>();
            }
            pageSet.retainAll(nextMap.keySet());
            if (pageSet.isEmpty()) {
                return new HashSet<>();
            }
        }
        pageSet.removeIf((Page p) -> !hasPhrase(totalPages.get(p), pageMap.get(p), phrase));
        return pageSet;
    }
}