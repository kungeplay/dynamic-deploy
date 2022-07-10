package org.example.udf.calculator.impl;

import org.example.calculator.Calculator;
import org.example.udf.CalculatorCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculatorImpl implements Calculator {

    @Autowired
    private CalculatorCore calculatorCore;

    /**
     * 注解方式
     * @param a
     * @param b
     * @return
     */
    @Override
    public int calculate(int a, int b) {
        int c = calculatorCore.add(a, b);
        return c;
    }

    /**
     * 反射方式
     * @param a
     * @param b
     * @return
     */
    @Override
    public int add(int a, int b) {
        return new CalculatorCore().add(a, b);
    }
}
