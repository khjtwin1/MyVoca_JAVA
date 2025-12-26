import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        ShareData.load();
        WrongWord wW = new WrongWord();
        wW.load();
        Textbook.load();
        new GUI();
    }
}

class Word {
    String word;
    String meaning;
    int rightCnt = 0;
    int wrongCnt = 0;
    int quizCnt = 0;
    public String toString(){
        return word+" - "+meaning+" (정답횟수: "+rightCnt+", 오답횟수: "+wrongCnt+")";
    }
}

class ShareData {
    static ArrayList<Word> words = new ArrayList<>();
    static void load() {
        try {
            File wFile = new File("words.txt");
            if (!wFile.exists()) wFile.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader("words.txt"));
            String line = br.readLine();
            while(line != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String[] parts = new String[5];
                for (int i = 0; i < 5 && st.hasMoreTokens(); i++) parts[i] = st.nextToken();
                Word w = new Word();
                w.word = parts[0];
                w.meaning = parts[1];
                w.rightCnt = Integer.parseInt(parts[2]);
                w.wrongCnt = Integer.parseInt(parts[3]);
                w.quizCnt = Integer.parseInt(parts[4]);
                words.add(w);
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) { e.printStackTrace(); }
    }
    static void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt"));
            for(Word w : words) {
                bw.write(w.word + "\t" + w.meaning + "\t" + w.rightCnt + "\t" + w.wrongCnt + "\t" + w.quizCnt + "\n");
            }
            bw.close();
        } catch(Exception e) { e.printStackTrace(); }
    }
}

class WordsDelete {
    WordsDelete(ArrayList<Word> list) {
        list.clear();
        if(list == ShareData.words){
            for(int i = 0; i<4; i++){
                Textbook.flag[i] = false;
            }
            WrongWord.wrongList.clear();
            ShareData.save();
            WrongWord.save();
            Textbook.save();
        } else if(list == WrongWord.wrongList){
            WrongWord.wrongList.clear();
            WrongWord.save();
        }
        JOptionPane.showMessageDialog(null, "모든 단어 삭제 완료!");
    }
}

class My extends JFrame {
    My(){
        this.setTitle("단어 추가");
        this.setSize(500,300);
        this.setLayout(new GridLayout(3,2));
        this.setLocationRelativeTo(null);
        JLabel wordL = new JLabel("단어 : ");
        JLabel meaningL = new JLabel("뜻 : ");
        JTextField word = new JTextField();
        JTextField meaning = new JTextField();
        JButton performBtn = new JButton("추가하기");
        this.add(wordL); this.add(word);
        this.add(meaningL); this.add(meaning);
        this.add(performBtn);
        performBtn.addActionListener(e -> {
            if(word.getText().equals("") || meaning.getText().equals(""))
                JOptionPane.showMessageDialog(null,"단어/뜻 입력을 다시 확인해주세요.");
            else check(word.getText(), meaning.getText());
        });
        this.setVisible(true);
    }
    public void check(String w, String m) {
        for (Word wd : ShareData.words) {
            if (wd.word.equals(w)) {
                JOptionPane.showMessageDialog(null,"이미 있는 단어입니다.");
                return;
            }
        }
        Word newW = new Word();
        newW.word = w;
        newW.meaning = m;
        ShareData.words.add(newW);
        ShareData.save();
        JOptionPane.showMessageDialog(null,"단어 추가 완료!");
    }
}

class Textbook extends JFrame {
    static boolean[] flag = new boolean[4];
    static void load() {
        try {
            File textbookF = new File("textbookFlag.txt");
            if (!textbookF.exists()) {
                textbookF.createNewFile();
                for (int i = 0; i < 4; i++) flag[i] = false;
            }
            BufferedReader br = new BufferedReader(new FileReader("textbookFlag.txt"));
            for(int i = 0; i<4; i++){
                String line = br.readLine();
                flag[i] = Boolean.parseBoolean(line);
            }
            br.close();
        } catch(Exception e) { e.printStackTrace(); }
    }
    static void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("textbookFlag.txt"));
            for(int i = 0; i<4; i++){
                bw.write(flag[i]+"\n");
            }
            bw.close();
        } catch(Exception e) { e.printStackTrace(); }
    }
    String[][] tbW = {{"apple", "pear", "peach", "grape"}, {"monkey", "camel", "lion", "crow"}, {"pencil", "glue", "book", "eraser"}, {"science", "english", "math", "society"}};
    String[][] tbM = {{"사과", "배", "복숭아", "포도"}, {"원숭이", "낙타", "사자", "까마귀"}, {"연필", "풀", "책", "지우개"}, {"과학", "영어", "수학", "사회"}};
    Textbook() {
        this.setTitle("교과서 단어 추가");
        this.setSize(500,300);
        this.setLayout(new GridLayout(4,1));
        this.setLocationRelativeTo(null);
        for(int i = 0; i<4; i++){
            int j = i;
            JButton btn = new JButton(i+1+"과 단어 추가하기");
            btn.addActionListener(e -> check(j));
            this.add(btn);
        }

        this.setVisible(true);
    }
    void check(int n){
        if (flag[n]) {
            JOptionPane.showMessageDialog(null,"이미 있는 단어입니다.");
            return;
        }
        for(int i = 0; i<tbW[n].length; i++) {
            Word w = new Word();
            w.word = tbW[n][i];
            w.meaning = tbM[n][i];
            ShareData.words.add(w);
        }
        flag[n] = true;
        Textbook.save();
        ShareData.save();
        JOptionPane.showMessageDialog(null,"단어 추가 완료!");
    }
}

class WrongWord {
    static ArrayList<Word> wrongList = new ArrayList<>();
    void load() {
        try {
            File file = new File("wrongWords.txt");
            if (!file.exists()) file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader("wrongWords.txt"));
            String line = br.readLine();
            while(line != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String[] parts = new String[5];
                for (int i = 0; i < 5 && st.hasMoreTokens(); i++) parts[i] = st.nextToken();
                Word w = new Word();
                w.word = parts[0];
                w.meaning = parts[1];
                w.rightCnt = Integer.parseInt(parts[2]);
                w.wrongCnt = Integer.parseInt(parts[3]);
                w.quizCnt = Integer.parseInt(parts[4]);
                wrongList.add(w);
                line = br.readLine();
            }
            br.close();
        } catch(Exception e){ e.printStackTrace(); }
    }
    static void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("wrongWords.txt"));
            for(Word w : wrongList){
                bw.write(w.word + "\t" + w.meaning + "\t" + w.rightCnt + "\t" + w.wrongCnt + "\t" + w.quizCnt + "\n");
            }
            bw.close();
        } catch(Exception e){ e.printStackTrace(); }
    }
    static void addWrong(Word w){
        for(Word wWrong : wrongList){
            if(wWrong.word.equals(w.word)) return;
        }
        Word nw = new Word();
        nw.word = w.word;
        nw.meaning = w.meaning;
        nw.rightCnt = w.rightCnt;
        nw.wrongCnt = w.wrongCnt;
        nw.quizCnt = w.quizCnt;
        wrongList.add(nw);
    }
}

class QuizStart extends JFrame {
    QuizStart(ArrayList<Word> list) {
        this.setTitle("퀴즈 종류 선택");
        this.setSize(300,150);
        this.setLayout(new GridLayout(2,1));
        this.setLocationRelativeTo(null);
        JButton btn1 = new JButton("단어 제시");
        JButton btn2 = new JButton("의미 제시");
        btn1.addActionListener(e -> {
            new QuizCount(new MeaningQ(list));
            this.dispose();
        });
        btn2.addActionListener(e -> {
            new QuizCount(new WordQ(list));
            this.dispose();
        });
        this.add(btn1);
        this.add(btn2);
        this.setVisible(true);
    }
}

class QuizCount extends JFrame {
    Quiz quiz;
    QuizCount(Quiz quiz) {
        this.quiz = quiz;
        this.setTitle("문제 개수 입력");
        this.setSize(300,150);
        this.setLayout(new GridLayout(3,1));
        this.setLocationRelativeTo(null);
        JLabel message = new JLabel("풀 문제 개수를 입력하세요", SwingConstants.CENTER);
        JTextField cntF = new JTextField();
        JButton startBtn = new JButton("시작");
        this.add(message);
        this.add(cntF);
        this.add(startBtn);
        startBtn.addActionListener(e -> {
            String s = cntF.getText();
            if(s.equals("")){
                JOptionPane.showMessageDialog(null, "숫자를 입력하세요.");
                return;
            }
            int n = Integer.parseInt(s);
            if(n <= 0){
                JOptionPane.showMessageDialog(null, "1 이상의 수를 입력하세요.");
                return;
            }
            this.dispose();
            quiz.startQuiz(n);
        });
        this.setVisible(true);
    }
}

abstract class Quiz extends JFrame {
    JLabel qL = new JLabel("", SwingConstants.CENTER);
    JTextField inputF = new JTextField();
    JButton submitBtn = new JButton("확인");
    JButton nextBtn = new JButton("다음");
    ArrayList<Word> list;
    Word nowWord;
    int remain;
    int mode;
    Quiz(ArrayList<Word> list) {
        this.list = list;
    }
    void startQuiz(int count) {
        remain = count;
        this.setTitle("Quiz");
        this.setSize(400,250);
        this.setLayout(new GridLayout(4,1));
        this.setLocationRelativeTo(null);
        this.add(qL);
        this.add(inputF);
        this.add(submitBtn);
        this.add(nextBtn);
        submitBtn.addActionListener(e -> checkAnswer());
        nextBtn.addActionListener(e -> nextQ());
        nextQ();
        this.setVisible(true);
    }
    void nextQ() {
        if(remain == 0) {
            JOptionPane.showMessageDialog(null, "퀴즈 종료!");
            this.dispose();
            return;
        }
        int idx = (int)(Math.random() * list.size());
        nowWord = list.get(idx);
        inputF.setText("");
        if(mode == 1) qL.setText("단어: " + nowWord.word);
        else qL.setText("뜻: " + nowWord.meaning);
        nowWord.quizCnt++;
        remain--;
    }
    void checkAnswer() {
        String input = inputF.getText();
        boolean correct;
        if(mode == 1) correct = nowWord.meaning.equals(input);
        else correct = nowWord.word.equals(input);
        if(correct) {
            nowWord.rightCnt++;
            JOptionPane.showMessageDialog(null, "정답입니다!");
        } else {
            nowWord.wrongCnt++;
            WrongWord.addWrong(nowWord);
            JOptionPane.showMessageDialog(null, "틀렸습니다\n정답: " + (mode == 1 ? nowWord.meaning : nowWord.word));
        }
        ShareData.save();
        WrongWord.save();
    }
}

class MeaningQ extends Quiz {
    MeaningQ(ArrayList<Word> list) {
        super(list);
        this.mode = 1;
    }
}

class WordQ extends Quiz {
    WordQ(ArrayList<Word> list) {
        super(list);
        this.mode = 2;
    }
}

class GUI extends JFrame {
    JPanel panel = new JPanel();
    GUI() {
        this.setTitle("영단어장 프로그램");
        this.setSize(500,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        JButton addWordBtn = new JButton("단어 추가");
        JButton addTextbookWordBtn = new JButton("교과서 단어 추가");
        JButton showWordListBtn = new JButton("단어 리스트");
        JButton wrongListBtn = new JButton("오답 단어 리스트");
        JButton exitBtn = new JButton("EXIT");
        this.add(panel);
        JLabel label = new JLabel("MY OWN WORDBOOK", SwingConstants.CENTER);
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.add(label);
        panel.add(addWordBtn);
        panel.add(addTextbookWordBtn);
        panel.add(showWordListBtn);
        panel.add(wrongListBtn);
        panel.add(exitBtn);
        addWordBtn.addActionListener(e -> new My());
        addTextbookWordBtn.addActionListener(e -> new Textbook());
        showWordListBtn.addActionListener(e -> new ShowWordList("단어 리스트",ShareData.words));
        wrongListBtn.addActionListener(e -> new ShowWordList("오답 리스트", WrongWord.wrongList));
        exitBtn.addActionListener(e -> {
            ShareData.save();
            WrongWord.save();
            Textbook.save();
            System.exit(0);
        });
        this.setVisible(true);
    }
}

class ShowWordList extends JFrame {
    ShowWordList(String title, ArrayList<Word> wordlist){
        this.setTitle(title);
        this.setSize(500,300);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        if(wordlist.isEmpty()){
            JOptionPane.showMessageDialog(null,"단어가 없습니다.");
            return;
        } else {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Word str : wordlist) {
                model.addElement(str.toString());
            }
            JList<String> list = new JList<>(model);
            JScrollPane scroll = new JScrollPane(list);
            this.add(scroll, BorderLayout.CENTER);
        }
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1,2));
        JButton deleteBtn = new JButton("전체 삭제");
        JButton quizBtn = new JButton("퀴즈 시작");
        deleteBtn.addActionListener(e -> new WordsDelete(wordlist));
        quizBtn.addActionListener(e -> {
            new QuizStart(wordlist);
            this.dispose();
        });
        btnPanel.add(deleteBtn);
        btnPanel.add(quizBtn);
        this.add(btnPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
