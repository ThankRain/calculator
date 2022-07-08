package net.bingyan.mobile.camp001;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Stack;

public class MainActivity extends Activity {
    private TextView scanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView[] num = new TextView[]{
                findViewById(R.id.num0),
                findViewById(R.id.num1),
                findViewById(R.id.num2),
                findViewById(R.id.num3),
                findViewById(R.id.num4),
                findViewById(R.id.num5),
                findViewById(R.id.num6),
                findViewById(R.id.num7),
                findViewById(R.id.num8),
                findViewById(R.id.num9),
        };//数字键盘，将所有数字绑定到一个数组中，第n个元素就是数字n
        TextView equal = findViewById(R.id.equal);//绑定 = 文本控件
        TextView plus = findViewById(R.id.plus);//绑定➕
        TextView minus = findViewById(R.id.minus);//绑定➖
        TextView times = findViewById(R.id.times);//绑定✖️
        TextView divide = findViewById(R.id.divide);//绑定➗
        TextView ac = findViewById(R.id.ac);//绑定 AC清除
        scanner = findViewById(R.id.scanner);//绑定显示算式的文本控件
        ac.setOnClickListener(view -> {//设置 AC 按钮的点击事件
            scanner.setText("");//清空算式的文本内容
        });
        for (int i = 0; i < num.length; i++) {//循环遍历数字键盘，为其绑定点击事件
            int finalI = i;//数字键盘对应的数字
            num[i].setOnClickListener(v -> {//设置点击事件
                scanner.setText(scanner.getText().toString() + finalI);//在算式末尾加上该数字
            });
        }
        plus.setOnClickListener(v -> {//设置➕的点击事件
            String value = scanner.getText().toString();//获取算式
            scanner.setText(trimSign(value) + "+");//调用自己实现的trimSign函数去除末尾的符号，再加上+号，
            // 避免重复点击运算符导致多个运算符在一块的问题
        });
        minus.setOnClickListener(v -> {//设置➖的点击事件
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "-");
        });
        times.setOnClickListener(v -> {//设置✖️的点击事件
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "*");
        });
        divide.setOnClickListener(v -> {//设置➗的点击事件
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "÷");
        });

        equal.setOnClickListener(v -> {//设置 = 的点击事件
            String formula = scanner.getText().toString();//获取算式文本
            scanner.setText(calculate(formula));//计算算式并显示结果
        });
    }

    //去除末尾的运算符
    private String trimSign(String input){
        char ends = input.charAt(input.length()-1);
        switch (ends) {
            case '+':
            case '-':
            case '*':
            case '÷':
                return input.substring(0,input.length()-1);
        }
        return input;
    }

    //各个运算符的优先级
    private int priority(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '÷':
                return 2;
            case '(':
            default:
                return 0;
        }
    }

    // 中缀表达式转逆波兰表达式
    private String calculate(String raw) {
        // 111 + 222 - 333 * 444 / 555
        // 采用逆波兰表达式计算
        raw = "0" + raw;
        Stack<String> numberStack = new Stack<>();//数字栈
        Stack<Character> operatorStack = new Stack<>();//操作符栈
        StringBuilder value = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '÷' || c == '(' || c == ')') {
                //c为运算符
                if (value.length() > 0)
                    numberStack.add(value.toString());
                value = new StringBuilder();//重置
                if (operatorStack.size() != 0) {
                    char last = operatorStack.get(operatorStack.size() - 1);//上一个操作符
                    if (c == ')') {
                        char pop = operatorStack.pop();
                        while (pop != '(') {
                            numberStack.push(String.valueOf(pop));
                            pop = operatorStack.pop();
                        }
                    } else {
                        while (priority(last) >= priority(c) && !operatorStack.empty()) {
                            //上一个操作符优先级高
                            //入栈到num栈中
                            operatorStack.pop();
                            numberStack.push(String.valueOf(last));
                        }
                    }
                }
                operatorStack.push(c);
            } else {
                value.append(c);
            }
        }
        numberStack.add(value.toString());
        while (!operatorStack.empty()) {
            numberStack.push(String.valueOf(operatorStack.pop()));
        }
        //调用逆波兰表达式计算函数，返回计算结果
        return calculateRPN(numberStack).toString();
    }

    // 计算逆波兰表达式并返回最终结果
    private Integer calculateRPN(Stack<String> stack) {
        Stack<Integer> numStack = new Stack<>();
        Log.i("Stack", "calculateRPN: " + stack);//打印逆波兰表达式
        for (String s : stack) {
            try {
                int v = Integer.parseInt(s);
                numStack.push(v);
            } catch (Exception e) {
                //不是数字，为运算符
                int b = numStack.pop();
                int a = numStack.pop();
                switch (s) {
                    case "+":
                        numStack.push(a + b);
                        break;
                    case "-":
                        numStack.push(a - b);
                        break;
                    case "*":
                        numStack.push(a * b);
                        break;
                    case "÷":
                        numStack.push(a / b);
                        break;
                }
            }
        }
        return numStack.pop();
    }
}
