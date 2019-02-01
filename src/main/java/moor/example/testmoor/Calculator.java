/*
 * Copyright (c) 2019.
 * @autor Kate Moor
 */

package moor.example.testmoor;

import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;

/**
 * Класс для расчета выражения
 */
public class Calculator {
    /**
     * выражение
     */
    private static String example;
    /**
     * текущая позиция
     */
    private static int currentPosition;

    /**
     * проверяет является ли символ разделителем
     * @param symbol символ для проверки
     * @return boolean
     */
    private static boolean isSeparator(char symbol) {
        return symbol == '.' || symbol == ',';
    }

    /**
     * проверяет является ли символ знаком +-
     * @param symbol
     * @return boolean
     */
    private static boolean isSign(char symbol) {
        return symbol == '+' || symbol == '-';
    }

    /**
     * проверяет является ли символ цифрой
     * @param symbol
     * @return boolean
     */
    private static boolean isDigit(char symbol) {
        return Character.isDigit(symbol);
    }

    /**
     * проверяет является ли символ знаком * и /
     * @param symbol
     * @return boolean
     */
    private static boolean isMultiply(char symbol) {
        return symbol == '*' || symbol == '/';
    }

    /**
     *  Достигнут ли конец выражения
     * @return boolean
     */
    private static boolean isNotEndString() {
        return currentPosition < example.length();
    }

    /**
     * Берет символ из выражения на текущей позиции
     * @return текущий символ
     */
    private static char currentSymbol() {
        return example.charAt(currentPosition);
    }

    /**
     *  Поиск числа с плавающей точкой
     *  выделено в отдельный метод для удобочитаемости
     */
    private static void findFloat() {
        if (currentSymbol() == ',') {
            example = example.replace(',', '.');
        }
        currentPosition++;
        if (!(isNotEndString() && isDigit(currentSymbol())))
            throw new NumberFormatException("В позиции:" + Integer.toString(currentPosition + 1) + ". Ожидалась цифра");
        while ((isNotEndString() && isDigit(currentSymbol()))) currentPosition++;
    }

    /**
     * Поиск числа в выражении
     * @return число
     */
    private static BigDecimal returnNumber() {
        int startPosition = currentPosition;

        if (isNotEndString() && isSign(currentSymbol())) currentPosition++;

        if (!(isNotEndString() && isDigit(currentSymbol())))
            throw new NumberFormatException("В позиции:" + Integer.toString(currentPosition + 1) + ". Ожидалась цифра");

        while ((isNotEndString() && isDigit(currentSymbol()))) currentPosition++;

        if (isNotEndString() && isSeparator(currentSymbol())) {
            findFloat();
        }
        return  new BigDecimal(example.substring(startPosition, currentPosition));
    }

    /**
     * Поиск простейшего выражения или выражения в скобках
     * @return результат расчета
     */
    private static BigDecimal parser() {
        BigDecimal answer;

        if (!(isNotEndString())) throw new NumberFormatException("Неверное выражение. Расчету не подлежит.");

        switch (currentSymbol()) {
            case '+':
                currentPosition++;
                answer = parser();
                break;
            case '-':
                currentPosition++;
                answer = parser().multiply(new BigDecimal(-1));
                break;
            case '(':
                currentPosition++;
                answer = expressionRangTwo();
                if (currentSymbol() != ')' || !isNotEndString())
                    throw new NumberFormatException("В выражении не хватает скобки )");
                currentPosition++;
                break;
            default:
                if (isDigit(currentSymbol())) answer = returnNumber();
                else
                    throw new NumberFormatException("Выражение содержит неверный символ");
        }

        return answer;
    }

    /**
     * Поиск выражения с операцией в наивысшем приоритете (умножение или деление)
     * @return результат расчета
     */
    private static BigDecimal expressionRangOne() {
        BigDecimal answer = parser();

        while (isNotEndString() && isMultiply(currentSymbol())) {
            char operation = currentSymbol();
            currentPosition++;
            if (operation == '*') {
                answer = answer.multiply(parser());
            } else if (operation == '/') {
                answer = answer.divide(parser());
            } else throw new NumberFormatException("Такого не может быть!");
        }
        return answer;
    }

    /**
     * Поиск выражения с операцией во втором приоритете (сложение и вычитание)
     * @return результат расчета
     */
    private static BigDecimal expressionRangTwo() {
        BigDecimal answer = expressionRangOne();
        while (isNotEndString() && isSign(currentSymbol())) {
            char operation = currentSymbol();
            currentPosition++;
            if (operation == '+') {
                answer = answer.add(expressionRangOne());
            } else if (operation == '-') {
                answer = answer.subtract(expressionRangOne());
            } else throw new NumberFormatException("Такого не может быть!");
        }
        return answer;
    }

    /**
     * расчет выражения
     * @param expression выражение в виде строки
     * @return результат расчета
     */
    public static String calculate(String expression) {
        try {
            example = StringUtils.deleteWhitespace(expression);
            currentPosition = 0;
            BigDecimal answer = expressionRangTwo();
            if (isNotEndString()) {
                return ("Неверное выражение. Расчету не подлежит.");
            } else {
                return answer.toString();
            }
        } catch (NumberFormatException e) {
            return e.getMessage();

        } catch (Exception e) {
            return "При расчете выражения " +
                    expression + ". Произошла непредвиденная ошибка. Обратитесь к разработчику @EkaterinaMoor";
        }
    }

    private Calculator() {
    }

}
