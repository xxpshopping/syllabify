package syllabify.model;

import java.util.ArrayList;
import java.util.Collections;

public class English implements ILanguage {
  private ArrayList<String> nuclei;
  private ArrayList<String> onsets;
  // private ArrayList<String> codas;

  public English() {
    nuclei = new ArrayList<>();
    onsets = new ArrayList<>();
    Collections.addAll(nuclei, "a", "i", "o", "e", "u", "ue", "ai", "oa", "ou");
    Collections.addAll(onsets, "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n",
            "p", "q", "r", "s", "t", "v", "w", "x", "y", "z", "sk", "bl", "cl", "gl", "", "sn",
            "sl", "sm", "rh", "spr");
    // Collections.addAll(codas, "g", "m");
  }

  @Override
  public void nucleusPass(Word word) {
    String w = word.getWord();
    int startSyll = 0;
    for (int i = 0; i < w.length(); i++) {
      String vowel = "";
      if (nuclei.contains(vowel + w.charAt(i)) && !(i == w.length() - 1 && w.charAt(i) == 'e')) {
        String onset = w.substring(startSyll, i);
        String nucleus;
        if (i < w.length() - 1 &&
                nuclei.contains(vowel + w.charAt(i) + w.charAt(i + 1))) {
          nucleus = w.substring(i, i + 2);
          startSyll = i + 2;
          i++;
        } else {
          nucleus = w.substring(i, i + 1);
          startSyll = i + 1;
        }

        Syllable toAdd = new Syllable(onset, nucleus);
        word.addSyllable(toAdd);
      }
    }
    if (startSyll < w.length()) {
      ISyllable s = new Syllable();
      s.setOnsetTo(w.substring(startSyll));
      word.addSyllable(s);
    }
  }

  private void yPass(Word word) {
    ArrayList<ISyllable> sylls = word.getSyllables();
    ArrayList<ISyllable> newsylls = new ArrayList<>();
    for (ISyllable syl : sylls) {
      String onset = syl.getOnset();
      if (!onsets.contains(onset) && onset.contains("y")) {
        int yIndex = onset.indexOf("y");
        String newOnset = onset.substring(0, yIndex);
        newsylls.add(new Syllable(newOnset, "y"));

        if (yIndex < onset.length() - 1) {
          newsylls.add(new Syllable(onset.substring(yIndex + 1), syl.getNucleus()));
        } else if (!syl.getNucleus().equals("")) {
          newsylls.add(new Syllable("", syl.getNucleus()));
        }
      } else {
        newsylls.add(syl);
      }
    }
    word.setSyllables(newsylls);
  }

  @Override
  public void onsetPass(Word word) {
    try {
      this.yPass(word);
      ArrayList<ISyllable> sylls = word.getSyllables();

      for (ISyllable syl : sylls) {
        int wordPosn = syl.getWordPosn();
        String onset = syl.getOnset();

        if (!onsets.contains(onset) && !syl.getNucleus().equals("")) {
          ISyllable prevSy = word.getSyllable(wordPosn - 1);
          int divide = this.getSegmentNum(onset);
          syl.setOnsetTo(onset.substring(divide));
          prevSy.addCodaSounds(onset.substring(0, divide));
        }
      }
      ISyllable lastSyllable = sylls.get(sylls.size() - 1);
      if (lastSyllable.getNucleus().equals("")) {
        ISyllable prevSy = word.getSyllable(sylls.size() - 2);
        prevSy.addCodaSounds(lastSyllable.getOnset());
        word.removeSyllable(sylls.size() - 1);
      }
    } catch (IndexOutOfBoundsException e) {
      System.out.print("Not a legal word in English");
      System.exit(0);
    }


  }


  @Override
  public boolean codaPass(Word word) {
    for (ISyllable sylls : word.getSyllables()) {
    }
    return false;
  }

  private int getSegmentNum(String onset) {
    int length = onset.length();
    int divider = 0;
    while (!onsets.contains(onset) && onset.length() > 1) {
      onset = onset.substring(1);
      divider += 1;
    }
    if (onsets.contains(onset)) {
      return divider;
    } else {
      return length;
    }
  }
}
