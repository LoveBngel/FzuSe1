import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rat403 {

    private static final HashMap<String,Integer> map = new HashMap<>();

    static {
        map.put("auto",0);
        map.put("break",0);
        map.put("case",0);
        map.put("char",0);
        map.put("const",0);
        map.put("continue",0);
        map.put("default",0);
        map.put("do",0);
        map.put("double",0);
        map.put("else",0);
        map.put("enum",0);
        map.put("extern",0);
        map.put("float",0);
        map.put("for",0);
        map.put("goto",0);
        map.put("if",0);
        map.put("int",0);
        map.put("long",0);
        map.put("register",0);
        map.put("return",0);
        map.put("short",0);
        map.put("signed",0);
        map.put("sizeof",0);
        map.put("static",0);
        map.put("struct",0);
        map.put("switch",0);
        map.put("typedef",0);
        map.put("union",0);
        map.put("unsigned",0);
        map.put("void",0);
        map.put("volatile",0);
        map.put("while",0);
    }

    @SuppressWarnings("all")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 输入这个  src//main/resources/test.cpp  4
        String fileName = scanner.next();
        int level = scanner.nextInt();
        long currTime = System.currentTimeMillis();
        StringBuilder stringBuilder = null;
        try {
            File file = new File(fileName);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String ck;
            stringBuilder = new StringBuilder();
            while((ck = reader.readLine()) != null){
                stringBuilder.append(ck);
            }
            streamReader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = stringBuilder.toString();
        String regularExpression = "\\w+|[{]+|[}]+";
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher((s));
        List<String> list = new LinkedList<>();
        List<Integer> switchCounts = new LinkedList<>();
        int currSwitch = 0;
        int flagSwitch = 0;
        int waitingSwitchLeft = 0;
        String last = "a";
        int ifElseIfCounts = 0;
        int ifElseCounts = 0;
        int currFlag = 0;
        int judgeElseIf = 0;
        Stack<Stack<Character>> stack = new Stack<>();
        Stack<Integer> stack1 = new Stack<>();
        Stack<Character> curr = new Stack<>();
        Stack<Character> switchStack = new Stack<>();
        while (matcher.find()){
            list.add(matcher.group());
            if(map.containsKey(matcher.group())){
                map.put(matcher.group(),map.get(matcher.group()) + 1);
            }
            //switch case
            if(matcher.group().equals("case") && flagSwitch == 1){
                currSwitch ++;
            }
            if(matcher.group().equals("switch")){
                flagSwitch = 1;
                waitingSwitchLeft = 0;
            }else if(flagSwitch == 1 && matcher.group().equals("}") && waitingSwitchLeft == 1){
                switchStack.pop();
                if(switchStack.isEmpty()){
                    flagSwitch = 0;
                    if(currSwitch != 0){
                        switchCounts.add(currSwitch);
                        currSwitch = 0;
                    }
                }
            }else if(flagSwitch == 1 && matcher.group().equals("{")){
                switchStack.push('{');
                waitingSwitchLeft = 1;
            }

            // if else
            if(!last.equals("else") && matcher.group().equals("if")){
                Stack<Character> stack2 = new Stack<>();
                stack.push(stack2);
                curr = stack2;
                stack1.push(judgeElseIf);
                currFlag = 1;
                judgeElseIf = 0;
                continue;
            }
            if(last.equals("else") && matcher.group().equals("if")){
                judgeElseIf = 1;
                stack1.pop();
                stack1.push(1);
            }
            if(currFlag == 1 && matcher.group().equals("}") && !curr.isEmpty()){
                curr.pop();
                continue;
            }else if(currFlag == 1 && matcher.group().equals("{")){
                curr.push('{');
            }
            if(currFlag == 1 && curr.isEmpty() && matcher.group().equals("}")){
                if(judgeElseIf == 1){
                    ifElseIfCounts ++;
                }else{
                    ifElseCounts ++;
                }
                if(stack1.isEmpty()){
                    judgeElseIf = 0;
                }else{
                    judgeElseIf = stack1.pop();
                }
                if(stack.isEmpty()){
                    curr = new Stack<>();
                    currFlag = 0;
                }else{
                    curr = stack.pop();
                    if(!curr.isEmpty()){
                        curr.pop();
                    }
                }
            }
            last = matcher.group();
        }
        /* 正则匹配后查看分词取消这里的注释就好了
        for(String x:list){
            System.out.println(x);
        }
         */
        while(!stack1.isEmpty()){
            Integer temp = stack1.pop();
            if(temp == 0){
                ifElseCounts ++;
            }else{
                ifElseIfCounts ++;
            }
        }
        int sum = 0;
        for(String x:map.keySet()){
            sum += map.get(x);
        }
        System.out.println("total num: " + sum);
        System.out.println("switch num: " + map.get("switch"));
        System.out.print("case num: ");
        for(Integer x:switchCounts){
            System.out.print(x + " ");
        }
        System.out.println();
        System.out.println("if-else num: " + ifElseCounts);
        System.out.println("if-elseif-else num: " + ifElseIfCounts);
        long currTime1 = System.currentTimeMillis();
        System.out.println("总耗时:" + (currTime1 - currTime) + "ms");
    }

}
