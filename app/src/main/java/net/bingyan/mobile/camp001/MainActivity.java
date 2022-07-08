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
        };//数字键盘
        TextView equal = findViewById(R.id.equal);
        TextView plus = findViewById(R.id.plus);
        TextView minus = findViewById(R.id.minus);
        TextView times = findViewById(R.id.times);
        TextView divide = findViewById(R.id.divide);
        TextView ac = findViewById(R.id.ac);
        scanner = findViewById(R.id.scanner);
        ac.setOnClickListener(view -> {
            scanner.setText("");
        });
        for (int i = 0; i < num.length; i++) {
            int finalI = i;
            num[i].setOnClickListener(v -> {
                scanner.setText(scanner.getText().toString() + finalI);
            });
        }
        plus.setOnClickListener(v -> {
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "+");
        });
        minus.setOnClickListener(v -> {
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "-");
        });
        times.setOnClickListener(v -> {
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "*");
        });
        divide.setOnClickListener(v -> {
            String value = scanner.getText().toString();
            scanner.setText(trimSign(value) + "÷");
        });

        equal.setOnClickListener(v -> {
            scanner.setText(calculate(scanner.getText().toString()));
        });
    }

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
        return calculateRPN(numberStack).toString();
    }

    private Integer calculateRPN(Stack<String> stack) {
        Stack<Integer> numStack = new Stack<>();
        Log.i("Stack", "calculateRPN: " + stack);
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
