package com.example.calc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    @Test
    void add() {
        System.out.println("*** CalculatorTest.add");
        Calculator calculator = new Calculator();
        assertEquals(3, calculator.add(1, 2));
        assertEquals(3, calculator.add(2, 1));
    }

    @Test
    void subtract() {
        System.out.println("*** CalculatorTest.subtract");
        Calculator calculator = new Calculator();
        assertEquals(-1, calculator.subtract(1, 2));
        assertEquals(1, calculator.subtract(2, 1));
    }

}
