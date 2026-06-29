// Scientific Calculator - MainActivity.java
// Course: SEN214/SEN104/CYB104/CSC104
// Language: Java (NOT Kotlin)
// Package matches your actual project

package com.Fasiku Richard.scientificcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings({"all"})
public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("all")
    // Holds the expression the user is building e.g. "sin(30)+5"
    private String expression = "";

    // Holds the last calculated answer (used by Ans button)
    private String lastAnswer = "";

    // Display TextViews
    private TextView tvExpression;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect display views
        tvExpression = findViewById(R.id.tvExpression);
        tvResult     = findViewById(R.id.tvResult);

        // ── Number buttons ──────────────────────────────────────
        findViewById(R.id.btn0).setOnClickListener(v -> append("0"));
        findViewById(R.id.btn1).setOnClickListener(v -> append("1"));
        findViewById(R.id.btn2).setOnClickListener(v -> append("2"));
        findViewById(R.id.btn3).setOnClickListener(v -> append("3"));
        findViewById(R.id.btn4).setOnClickListener(v -> append("4"));
        findViewById(R.id.btn5).setOnClickListener(v -> append("5"));
        findViewById(R.id.btn6).setOnClickListener(v -> append("6"));
        findViewById(R.id.btn7).setOnClickListener(v -> append("7"));
        findViewById(R.id.btn8).setOnClickListener(v -> append("8"));
        findViewById(R.id.btn9).setOnClickListener(v -> append("9"));

        // ── Decimal ─────────────────────────────────────────────
        findViewById(R.id.btnDot).setOnClickListener(v -> append("."));

        // ── Basic operators ──────────────────────────────────────
        findViewById(R.id.btnPlus).setOnClickListener(v     -> append("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v    -> append("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> append("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v   -> append("/"));

        // ── Brackets ─────────────────────────────────────────────
        findViewById(R.id.btnOpenBracket).setOnClickListener(v  -> append("("));
        findViewById(R.id.btnCloseBracket).setOnClickListener(v -> append(")"));

        // ── Percentage ───────────────────────────────────────────
        findViewById(R.id.btnPercent).setOnClickListener(v -> append("/100"));

        // ── Power ────────────────────────────────────────────────
        findViewById(R.id.btnPower).setOnClickListener(v -> append("^"));

        // ── Scientific functions ─────────────────────────────────
        findViewById(R.id.btnSin).setOnClickListener(v  -> wrapFunction("sin"));
        findViewById(R.id.btnCos).setOnClickListener(v  -> wrapFunction("cos"));
        findViewById(R.id.btnTan).setOnClickListener(v  -> wrapFunction("tan"));
        findViewById(R.id.btnLog).setOnClickListener(v  -> wrapFunction("log"));
        findViewById(R.id.btnLn).setOnClickListener(v   -> wrapFunction("ln"));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> wrapFunction("sqrt"));

        // ── Constants ────────────────────────────────────────────
        findViewById(R.id.btnPi).setOnClickListener(v -> append(String.valueOf(Math.PI)));
        findViewById(R.id.btnE).setOnClickListener(v  -> append(String.valueOf(Math.E)));

        // ── Ans button ───────────────────────────────────────────
        findViewById(R.id.btnAns).setOnClickListener(v -> {
            if (!lastAnswer.isEmpty()) {
                append(lastAnswer);
            }
        });

        // ── +/- toggle ───────────────────────────────────────────
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> {
            if (!expression.isEmpty()) {
                expression = "-(" + expression + ")";
                tvExpression.setText(expression);
            }
        });

        // ── DEL: delete last character ───────────────────────────
        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
                tvExpression.setText(expression);
                if (expression.isEmpty()) {
                    tvResult.setText("0");
                }
            }
        });

        // ── AC: clear everything ─────────────────────────────────
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            expression = "";
            tvExpression.setText("");
            tvResult.setText("0");
        });

        // ── Equals: calculate result ─────────────────────────────
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
    }

    // ── Append a value to the expression ────────────────────────
    private void append(String value) {
        expression += value;
        tvExpression.setText(expression);
        showLivePreview();
    }

    // ── Wrap current expression in a function call ───────────────
    // e.g. if expression = "30", wrapFunction("sin") → "sin(30)"
    private void wrapFunction(String funcName) {
        if (!expression.isEmpty()) {
            expression = funcName + "(" + expression + ")";
        } else {
            expression = funcName + "(";
        }
        tvExpression.setText(expression);
        showLivePreview();
    }

    // ── Show a live preview while the user is typing ─────────────
    private void showLivePreview() {
        try {
            double result = evaluate(expression);
            tvResult.setText(formatResult(result));
        } catch (Exception e) {
            // Ignore errors while user is still typing
        }
    }

    // ── Calculate and display the final result ───────────────────
    @SuppressLint("SetTextI18n")
    private void calculateResult() {
        if (expression.isEmpty()) return;
        try {
            double result = evaluate(expression);
            String formatted = formatResult(result);
            tvResult.setText(formatted);
            lastAnswer = formatted;         // Save for Ans button
            tvExpression.setText(expression);
            expression = formatted;         // Allow chaining
        } catch (Exception e) {
            tvResult.setText("Error");
            expression = "";
        }
    }

    // ── Format result: strip unnecessary .0 ─────────────────────
    private String formatResult(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);  // e.g. 6.0 → "6"
        } else {
            // Up to 8 decimal places, strip trailing zeros
            String s = String.format("%.8f", value);
            s = s.replaceAll("0+$", "");
            s = s.replaceAll("\\.$", "");
            return s;
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── Expression Evaluator ─────────────────────────────────────
    // Evaluates math expressions safely without eval().
    // Handles: +, -, *, /, ^, sin, cos, tan, log, ln, sqrt, (, )
    // Trig functions use DEGREES (standard calculator behaviour).
    // ════════════════════════════════════════════════════════════
    private double evaluate(String expr) {
        return new Parser(expr.trim()).parse();
    }

    // ── Recursive Descent Parser (inner class) ───────────────────
    private class Parser {
        public final String input;
        private int pos = 0;

        Parser(String input) {
            this.input = input;
        }

        // Entry point
        double parse() {
            double result;
            result = parseAddSubtract();
            return result;
        }

        // Handles + and -
        private double parseAddSubtract() {
            double result = parseMultiplyDivide();
            while (pos < input.length()) {
                char ch = input.charAt(pos);
                if (ch == '+') { pos++; result += parseMultiplyDivide(); }
                else if (ch == '-') { pos++; result -= parseMultiplyDivide(); }
                else break;
            }
            return result;
        }

        // Handles * and /
        private double parseMultiplyDivide() {
            double result = parsePower();
            while (pos < input.length()) {
                char ch = input.charAt(pos);
                if (ch == '*') {
                    pos++;
                    result *= parsePower();
                } else if (ch == '/') {
                    pos++;
                    double divisor = parsePower();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    result /= divisor;
                } else break;
            }
            return result;
        }

        // Handles ^ (power/exponent)
        private double parsePower() {
            double result = parseUnary();
            while (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                double exponent = parseUnary();
                result = Math.pow(result, exponent);
            }
            return result;
        }

        // Handles unary minus e.g. -5
        private double parseUnary() {
            if (pos < input.length() && input.charAt(pos) == '-') {
                pos++;
                return -parsePrimary();
            }
            if (pos < input.length() && input.charAt(pos) == '+') {
                pos++;
            }
            return parsePrimary();
        }

        // Handles numbers, functions, constants, brackets
        private double parsePrimary() {
            skipSpaces();

            // Handle parentheses: (expression)
            if (pos < input.length() && input.charAt(pos) == '(') {
                pos++; // skip (
                double result = parseAddSubtract();
                if (pos < input.length() && input.charAt(pos) == ')') pos++; // skip )
                return result;
            }

            // Handle function names and constants
            if (pos < input.length() && Character.isLetter(input.charAt(pos))) {
                StringBuilder name = new StringBuilder();
                while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
                    name.append(input.charAt(pos++));
                }
                skipSpaces();
                String funcName = name.toString().toLowerCase();

                // Function with bracketed argument
                if (pos < input.length() && input.charAt(pos) == '(') {
                    pos++; // skip (
                    double arg = parseAddSubtract();
                    if (pos < input.length() && input.charAt(pos) == ')') pos++; // skip )

                    switch (funcName) {
                        case "sin":  return Math.sin(Math.toRadians(arg));
                        case "cos":  return Math.cos(Math.toRadians(arg));
                        case "tan":  return Math.tan(Math.toRadians(arg));
                        case "log":
                            if (arg <= 0) throw new ArithmeticException("log of non-positive");
                            return Math.log10(arg);
                        case "ln":
                            if (arg <= 0) throw new ArithmeticException("ln of non-positive");
                            return Math.log(arg);
                        case "sqrt":
                            if (arg < 0) throw new ArithmeticException("sqrt of negative");
                            return Math.sqrt(arg);
                        default:
                            throw new IllegalArgumentException("Unknown function: " + funcName);
                    }
                }

                // Constants (no brackets needed)
                switch (funcName) {
                    case "pi": return Math.PI;
                    case "e":  return Math.E;
                    default:   throw new IllegalArgumentException("Unknown constant: " + funcName);
                }
            }

            // Handle numeric literals: 3.14, 2.718, 1.5E10 etc.
            if (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                StringBuilder numStr = new StringBuilder();
                while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                    numStr.append(input.charAt(pos++));
                }
                // Handle scientific notation e.g. 1.5E10
                if (pos < input.length() && (input.charAt(pos) == 'E' || input.charAt(pos) == 'e')) {
                    numStr.append(input.charAt(pos++));
                    if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) {
                        numStr.append(input.charAt(pos++));
                    }
                    while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                        numStr.append(input.charAt(pos++));
                    }
                }
                return Double.parseDouble(numStr.toString());
            }

            throw new IllegalArgumentException("Unexpected character at pos " + pos);
        }

        private void skipSpaces() {
            while (pos < input.length() && input.charAt(pos) == ' ') pos++;
        }
    }
}